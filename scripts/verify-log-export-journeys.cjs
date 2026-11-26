const assert = require("node:assert/strict");
const { readFile } = require("node:fs/promises");
const { chromium } = require("playwright");

const origin = "http://127.0.0.1:5173";
const marker = `S16-${Date.now()}`;
const password = "Synthetic-Session16-Only!";
const button = (page, name) => page.getByRole("button", { name, exact: true });
const heading = (page, name) => page.getByRole("main").getByRole("heading", { name, exact: true });
const watch = (page, path, method) => page.waitForResponse((response) =>
  new URL(response.url()).pathname === "/api" + path && response.request().method() === method
);

async function status(response, expected) {
  assert.equal(response.status(), expected, response.url());
  return response;
}

async function request(context, path, method = "GET", data) {
  const cookies = await context.cookies();
  const csrf = cookies.find((cookie) => cookie.name === "XSRF-TOKEN");
  const headers = method === "GET" ? {} : { "X-XSRF-TOKEN": decodeURIComponent(csrf.value) };
  return context.request.fetch(origin + "/api" + path, { method, data, headers });
}

async function signIn(browser, name, viewport) {
  const context = await browser.newContext({ viewport, baseURL: origin });
  await status(await request(context, "/health"), 200);
  const email = `${name.toLowerCase()}-${marker.toLowerCase()}@example.test`;
  await status(await request(context, "/auth/register", "POST", { email, password, displayName: name }), 202);
  const page = await context.newPage();
  page.setDefaultTimeout(10000);
  await page.goto("/login", { waitUntil: "domcontentloaded" });
  await page.getByLabel("Email").fill(email);
  await page.getByLabel("Password").fill(password);
  await button(page, "Sign in").press("Enter");
  await heading(page, `Welcome, ${name}`).waitFor();
  return { context, page };
}

async function fixture(browsers, viewport) {
  const leader = await signIn(browsers[0], "ReviewLeader", viewport);
  const author = await signIn(browsers[1], "DraftAuthor", viewport);
  const name = `${marker} journey`;
  const created = await status(await request(leader.context, "/engagements", "POST", {
    name, description: "Synthetic browser verification",
  }), 200);
  const { id } = await created.json();
  const path = `/engagements/${id}`;
  const code = await status(await request(leader.context, path + "/join-code", "POST"), 200);
  const joined = await status(await request(author.context, "/join", "POST", { code: (await code.json()).code }), 200);
  assert.equal((await joined.json()).role, "MEMBER");
  for (const { page } of [leader, author]) {
    await page.reload({ waitUntil: "domcontentloaded" });
    await page.getByRole("main").getByRole("button", { name: new RegExp(name) }).press("Enter");
    await heading(page, name).waitFor();
  }
  return { leader, author, path };
}

async function openBoard(page) {
  await page.getByRole("main").getByRole("button", { name: /View and manage engagement logs/ }).press("Enter");
  await heading(page, "Activity Logs").waitFor();
}

async function tabTo(page, control) {
  await control.waitFor();
  for (let i = 0; i < 45; i += 1) {
    if (await control.evaluate((node) => document.activeElement === node)) return;
    await page.keyboard.press("Tab");
  }
  assert.fail("Control is not reachable in the tab order");
}

async function keyboardActivate(page, control) {
  await tabTo(page, control);
  await page.keyboard.press("Enter");
}

async function logJourney(browsers) {
  const { leader, author, path } = await fixture(browsers, { width: 1280, height: 800 });
  const { page } = author;
  const title = `${marker} keyboard draft`;
  await openBoard(page);
  await keyboardActivate(page, button(page, "New Log"));
  await heading(page, "New Log").waitFor();
  await tabTo(page, page.getByLabel("Title *"));
  await page.keyboard.insertText(title);
  await tabTo(page, page.getByLabel("Description *"));
  await page.keyboard.insertText("**Synthetic Markdown evidence**");
  await tabTo(page, page.getByLabel("Result *"));
  await page.keyboard.insertText("Captured for browser verification.");
  const created = watch(page, path + "/logs", "POST");
  await keyboardActivate(page, button(page, "Create log"));
  const draft = await (await status(await created, 200)).json();
  assert.equal(draft.reviewState, "DRAFT");
  await heading(page, "Activity Logs").waitFor();
  await keyboardActivate(page, page.getByRole("link", { name: title, exact: true }));
  await heading(page, title).waitFor();
  assert.equal(await page.locator("strong").filter({ hasText: "Synthetic Markdown evidence" }).count(), 1);
  const logPath = `/logs/${draft.id}`;
  const submitted = watch(page, logPath + "/transition", "POST");
  await keyboardActivate(page, button(page, "Submit for review"));
  assert.equal((await (await status(await submitted, 200)).json()).reviewState, "SUBMITTED");
  await page.getByText("Submitted", { exact: true }).waitFor();
  assert.equal(await button(page, "Approve").count(), 0);
  assert.equal(await button(page, "Reject").count(), 0);
  const auditBeforeDenial = await (await status(await request(leader.context, path + "/audit"), 200)).json();
  await status(await request(author.context, logPath + "/transition", "POST", { action: "APPROVE" }), 403);
  assert.deepEqual(await (await status(await request(leader.context, path + "/audit"), 200)).json(), auditBeforeDenial);

  await openBoard(leader.page);
  await keyboardActivate(leader.page, button(leader.page, "Review Queue"));
  await keyboardActivate(leader.page, leader.page.getByRole("link", { name: title, exact: true }));
  await heading(leader.page, title).waitFor();
  await keyboardActivate(leader.page, button(leader.page, "Reject"));
  await tabTo(leader.page, leader.page.getByLabel("Rejection comment"));
  await leader.page.keyboard.insertText("Please expand the synthetic evidence.");
  const rejection = watch(leader.page, logPath + "/transition", "POST");
  await keyboardActivate(leader.page, button(leader.page, "Send back to draft"));
  assert.equal((await (await status(await rejection, 200)).json()).reviewState, "DRAFT");
  await page.reload({ waitUntil: "domcontentloaded" });
  await page.getByText("Please expand the synthetic evidence.", { exact: true }).waitFor();
  const resubmitted = watch(page, logPath + "/transition", "POST");
  await keyboardActivate(page, button(page, "Submit for review"));
  assert.equal((await (await status(await resubmitted, 200)).json()).reviewState, "SUBMITTED");
  await leader.page.reload({ waitUntil: "domcontentloaded" });
  const approval = watch(leader.page, logPath + "/transition", "POST");
  await keyboardActivate(leader.page, button(leader.page, "Approve"));
  assert.equal((await (await status(await approval, 200)).json()).reviewState, "APPROVED");
  await leader.page.getByText("Approved", { exact: true }).waitFor();
  const audit = await (await status(await request(leader.context, path + "/audit"), 200)).json();
  assert.ok(JSON.stringify(audit).includes(draft.id));
  assert.ok(JSON.stringify(audit).includes("APPROVE"));
  console.log(`${marker} JR-153: Tab/Enter create, open, submit, filter, reject, resubmit and approve passed. Rendered Markdown and approval audit verified.`);

  const raceTitle = `${marker} concurrent review`;
  const race = await (await status(await request(author.context, path + "/logs", "POST", {
    title: raceTitle, activityType: "RECONNAISSANCE", description: "Stored draft", result: "Synthetic result", outcome: "IN_PROGRESS",
  }), 200)).json();
  await page.goBack({ waitUntil: "domcontentloaded" });
  await heading(page, "Activity Logs").waitFor();
  await keyboardActivate(page, page.getByRole("link", { name: raceTitle, exact: true }));
  await keyboardActivate(page, button(page, "Edit"));
  const editedTitle = "Unsaved draft text";
  await page.getByLabel("Title *").fill(editedTitle);
  const racePath = `/logs/${race.id}`;
  const raceHistory = await (await status(await request(leader.context, racePath + "/history"), 200)).json();
  await status(await request(leader.context, racePath + "/transition", "POST", { action: "SUBMIT" }), 200);
  const collided = watch(page, racePath, "PUT");
  await keyboardActivate(page, button(page, "Save changes"));
  await status(await collided, 409);
  await page.getByRole("alert").filter({ hasText: "Only draft logs can be edited" }).waitFor();
  assert.equal(await page.getByLabel("Title *").inputValue(), editedTitle);
  const kept = await (await status(await request(leader.context, racePath), 200)).json();
  assert.equal(kept.title, raceTitle);
  assert.equal(kept.reviewState, "SUBMITTED");
  assert.deepEqual(await (await status(await request(leader.context, racePath + "/history"), 200)).json(), raceHistory);
  console.log(`${marker} JR-154: member approval denied with 403 and unchanged audit. Concurrent submit blocked stale edit with 409. Browser text, stored title and version history retained.`);
  return { leader, author, path, logPath, racePath, title };
}

async function checkDialog(dialog) {
  const measurements = await dialog.evaluate((element) => {
    const toLuminance = (color) => {
      const rgb = color.match(/[0-9.]+/g).slice(0, 3).map(Number);
      const [r, g, b] = rgb.map((component) => {
        const value = component / 255;
        return value <= 0.04045 ? value / 12.92 : ((value + 0.055) / 1.055) ** 2.4;
      });
      return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    };
    const controls = [...element.querySelectorAll("h2, button, fieldset p, label span, p")];
    const contrasts = controls.map((control) => {
      let background = control;
      while (getComputedStyle(background).backgroundColor === "rgba(0, 0, 0, 0)") {
        background = background.parentElement;
      }
      const foreground = getComputedStyle(control).color;
      const surfaceColor = getComputedStyle(background).backgroundColor;
      const text = toLuminance(foreground);
      const surface = toLuminance(surfaceColor);
      return {
        contrast: (Math.max(text, surface) + 0.05) / (Math.min(text, surface) + 0.05),
        label: control.textContent.trim(), foreground, surfaceColor,
      };
    });
    const bounds = element.getBoundingClientRect();
    contrasts.sort((a, b) => a.contrast - b.contrast);
    return { left: bounds.left, right: bounds.right, width: innerWidth, overflow: element.scrollWidth - element.clientWidth, minimum: contrasts[0] };
  });
  assert.equal(measurements.width, 320);
  assert.ok(measurements.left >= 0 && measurements.right <= 320, JSON.stringify(measurements));
  assert.equal(measurements.overflow, 0);
  assert.ok(measurements.minimum.contrast >= 4.5, JSON.stringify(measurements));
  return measurements.minimum.contrast.toFixed(2);
}

async function exportJourney({ leader, author, path, logPath, racePath, title }) {
  const { page } = leader;
  await page.setViewportSize({ width: 320, height: 640 });
  await page.goto("/", { waitUntil: "domcontentloaded" });
  await page.getByRole("main").getByRole("button", { name: new RegExp(marker) }).press("Enter");
  const opener = button(page, "Export");
  await keyboardActivate(page, opener);
  const dialog = page.getByRole("dialog", { name: "Export evidence pack" });
  await dialog.waitFor();
  assert.equal(await dialog.getByRole("button", { name: "Close export dialog" }).evaluate((node) => node === document.activeElement), true);
  assert.ok(await dialog.getByRole("button", { name: "Close export dialog" }).evaluate((node) => getComputedStyle(node).outlineStyle === "solid"));
  assert.equal(await page.getByRole("main", { includeHidden: true }).evaluate((node) => node.scrollWidth - node.clientWidth), 0);
  await dialog.getByText(/Draft and submitted logs are excluded/).waitFor();
  const lightContrast = await checkDialog(dialog);
  for (let i = 0; i < 12; i += 1) {
    await page.keyboard.press("Tab");
    assert.ok(await dialog.evaluate((node) => document.activeElement === document.body || node.contains(document.activeElement)));
  }
  await page.keyboard.press("Escape");
  await dialog.waitFor({ state: "hidden" });
  assert.ok(await opener.evaluate((node) => node === document.activeElement));

  await page.goto("/settings", { waitUntil: "domcontentloaded" });
  await button(page, "Dark Mode").press("Enter");
  await page.goto("/", { waitUntil: "domcontentloaded" });
  assert.equal(await page.locator("html").evaluate((node) => node.classList.contains("dark")), true);
  await page.getByRole("main").getByRole("button", { name: new RegExp(marker) }).press("Enter");

  const mimeTypes = { PDF: "application/pdf", JSON: "application/json", CSV: "text/csv" };
  for (const format of ["PDF", "JSON", "CSV"]) {
    await keyboardActivate(page, opener);
    await dialog.waitFor();
    const darkContrast = await checkDialog(dialog);
    await keyboardActivate(page, dialog.getByRole("button", { name: format, exact: true }));
    assert.equal(await dialog.getByRole("button", { name: format, exact: true }).getAttribute("aria-pressed"), "true");
    if (format !== "PDF") {
      const include = dialog.getByRole("checkbox", { name: /Include already-exported/ });
      if (!(await include.isChecked())) {
        await tabTo(page, include);
        await page.keyboard.press("Space");
      }
    }
    const result = watch(page, path + "/export", "POST");
    const downloadEvent = page.waitForEvent("download");
    await keyboardActivate(page, dialog.getByRole("button", { name: `Download ${format}`, exact: true }));
    const response = await status(await result, 200);
    const download = await downloadEvent;
    assert.equal(await download.failure(), null);
    assert.ok(download.suggestedFilename().endsWith("." + format.toLowerCase()));
    assert.ok((await response.headerValue("content-type")).startsWith(mimeTypes[format]));
    assert.deepEqual(response.request().postDataJSON(), { format, includeExported: format !== "PDF" });
    const body = await readFile(await download.path());
    if (format === "PDF") assert.equal(body.subarray(0, 5).toString(), "%PDF-");
    else if (format === "JSON") assert.ok(JSON.parse(body.toString()).logs.some((log) => log.title === title));
    else assert.ok(body.toString().includes(title));
    await dialog.waitFor({ state: "hidden" });
    assert.equal((await (await status(await request(leader.context, logPath), 200)).json()).reviewState, "EXPORTED");
    console.log(`${marker} JR-158: ${format} download and EXPORTED state verified. Dialog minimum sampled contrast light ${lightContrast}:1, dark ${darkContrast}:1.`);
  }

  const authorId = (await (await status(await request(author.context, "/auth/me"), 200)).json()).id;
  await keyboardActivate(page, opener);
  await dialog.waitFor();
  await status(await request(leader.context, path + "/transfer-leadership", "POST", { newLeaderId: authorId }), 204);
  const auditBefore = await (await status(await request(author.context, path + "/audit"), 200)).json();
  let downloads = 0;
  page.on("download", () => { downloads += 1; });
  const denied = watch(page, path + "/export", "POST");
  await keyboardActivate(page, dialog.getByRole("button", { name: "Download CSV", exact: true }));
  await status(await denied, 403);
  await dialog.getByRole("alert").filter({ hasText: "Only engagement leaders can perform this action" }).waitFor();
  assert.equal(downloads, 0);
  assert.equal(await page.getByText(/^Exported CSV/).count(), 0);
  assert.deepEqual(await (await status(await request(author.context, path + "/audit"), 200)).json(), auditBefore);
  assert.equal((await (await status(await request(author.context, logPath), 200)).json()).reviewState, "EXPORTED");
  assert.equal((await (await status(await request(author.context, racePath), 200)).json()).reviewState, "SUBMITTED");
  console.log(`${marker} JR-159: stale leader download denied with 403, modal announced error, no download or extra audit, evidence states retained.`);
}

(async () => {
  const browsers = [];
  try {
    for (let i = 0; i < 2; i += 1) {
      browsers.push(await chromium.launch({
        executablePath: process.env.PLOGIT_CHROMIUM,
        args: ["--no-sandbox", "--disable-dev-shm-usage", ...(process.env.PLOGIT_SINGLE_PROCESS ? ["--no-zygote", "--single-process"] : [])],
      }));
    }
    console.log(`Chromium ${browsers[0].version()}`);
    const fixtureData = await logJourney(browsers);
    if (process.env.PLOGIT_TEST_CASE !== "logs") await exportJourney(fixtureData);
  } finally {
    await Promise.all(browsers.map((browser) => browser.close()));
  }
})().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
