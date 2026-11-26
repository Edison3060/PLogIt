const assert = require("node:assert/strict");
const { chromium } = require("playwright");

const origin = "http://127.0.0.1:5173";
const marker = `S16-${Date.now()}`;
const password = "Synthetic-Session16-Only!";
const button = (page, name) => page.getByRole("button", { name, exact: true });
const heading = (page, name) => page.getByRole("heading", { name, exact: true });
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

async function fixture(browser, viewport) {
  const leader = await signIn(browser, "ReviewLeader", viewport);
  const author = await signIn(browser, "DraftAuthor", viewport);
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

async function logJourney(browser) {
  const { leader, author, path } = await fixture(browser, { width: 1280, height: 800 });
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
  assert.equal(await button(page, "Approve").count(), 0);

  await openBoard(leader.page);
  await keyboardActivate(leader.page, button(leader.page, "Review Queue"));
  await keyboardActivate(leader.page, leader.page.getByRole("link", { name: title, exact: true }));
  await heading(leader.page, title).waitFor();
  const approval = watch(leader.page, logPath + "/transition", "POST");
  await keyboardActivate(leader.page, button(leader.page, "Approve"));
  assert.equal((await (await status(await approval, 200)).json()).reviewState, "APPROVED");
  await leader.page.getByText("Approved", { exact: true }).waitFor();
  const audit = await (await status(await request(leader.context, path + "/audit"), 200)).json();
  assert.ok(JSON.stringify(audit).includes(draft.id));
  assert.ok(JSON.stringify(audit).includes("APPROVE"));
  console.log(`${marker} JR-153: Tab/Enter create, open, submit, filter and approve passed. Server returned DRAFT → SUBMITTED → APPROVED. Rendered Markdown and approval audit verified.`);
  await Promise.all([leader.context.close(), author.context.close()]);
}

(async () => {
  const browser = await chromium.launch({
    executablePath: process.env.PLOGIT_CHROMIUM,
    args: ["--no-sandbox", "--disable-dev-shm-usage", "--no-zygote", "--single-process"],
  });
  try {
    console.log(`Chromium ${browser.version()}`);
    await logJourney(browser);
  } finally {
    await browser.close();
  }
})().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
