const assert = require("node:assert/strict");
const { chromium } = require("playwright");

const baseline = process.argv.includes("--baseline");
const origin = baseline ? "http://127.0.0.1:5174" : "http://127.0.0.1:5173";
const password = "Synthetic-Session15-Only!";
const marker = `S15-${Date.now()}-${process.pid}`;
const api = "/api";
const requestMatch = (path, method) => (response) =>
  new URL(response.url()).pathname === api + path && response.request().method() === method;
const status = async (response, code) => assert.equal(response.status(), code, response.url());
const heading = (page, name) => page.getByRole("heading", { name, exact: true });
const button = (page, name) => page.getByRole("button", { name, exact: true });

async function cookie(context, name) {
  const match = (await context.cookies()).find((value) => value.name === name);
  assert.ok(match, `Expected ${name} cookie`);
  return match.value;
}

async function mutation(context, path, data, method = "POST") {
  return context.request.fetch(origin + api + path, {
    method,
    headers: { "X-XSRF-TOKEN": decodeURIComponent(await cookie(context, "XSRF-TOKEN")) },
    data,
  });
}

async function login(browser, label, viewport, register = true) {
  const context = await browser.newContext({ viewport, baseURL: origin });
  const user = { email: `${label.toLowerCase()}-${marker.toLowerCase()}@example.test`, displayName: label, password };
  await status(await context.request.get(origin + api + "/health"), 200);
  if (register) await status(await mutation(context, "/auth/register", user), 202);
  const page = await context.newPage();
  page.setDefaultTimeout(12000);
  await page.goto("/login", { waitUntil: "domcontentloaded" });
  await page.getByLabel("Email").fill(user.email);
  await page.getByLabel("Password").fill(password);
  const response = page.waitForResponse(requestMatch("/auth/login", "POST"));
  await button(page, "Sign in").click();
  await status(await response, 200);
  await heading(page, `Welcome, ${label}`).waitFor();
  return { page, context };
}

async function navigation(browser) {
  for (const viewport of [{ width: 1280, height: 800 }, { width: 390, height: 844 }]) {
    const { page, context } = await login(browser, `Nav${viewport.width}`, viewport);
    const name = `${marker} navigation ${viewport.width}`;
    const created = await mutation(context, "/engagements", { name, description: "Synthetic navigation" });
    await status(created, 200);
    const { id } = await created.json();
    await page.reload({ waitUntil: "domcontentloaded" });
    await page.getByRole("main").getByRole("button", { name: new RegExp(name) }).press("Enter");
    await page.waitForURL(origin + `/engagements/${id}`);
    await heading(page, name).waitFor();
    await page.getByRole("main").getByRole("button", { name: /View and manage engagement logs/ }).press("Enter");
    await page.waitForURL(origin + `/engagements/${id}/logs`);
    await page.getByText("No logs yet", { exact: true }).waitFor();
    if (viewport.width === 390) {
      await page.evaluate(() => document.activeElement.blur());
      await page.keyboard.press("Tab");
      const focusInAside = await page.evaluate(() => !!document.activeElement.closest("aside"));
      const menu = button(page, "Toggle navigation");
      if (baseline) {
        assert.equal(await menu.count(), 0);
        console.log(`JR-143 BASELINE mobile toggle has no accessible name; focus landed in off-canvas sidebar: ${focusInAside}`);
        await page.locator("header button").click();
      } else {
        assert.equal(focusInAside, false);
        await menu.press("Enter");
        assert.equal(await menu.getAttribute("aria-expanded"), "true");
        assert.equal(await page.evaluate(() => !!document.activeElement.closest("aside")), true);
        await page.keyboard.press("Escape");
        assert.equal(await menu.getAttribute("aria-expanded"), "false");
        assert.equal(await menu.evaluate((node) => document.activeElement === node), true);
        await menu.press("Space");
      }
    }
    const logout = page.waitForResponse(requestMatch("/auth/logout", "POST"));
    const oldCookie = await cookie(context, "JSESSIONID");
    await button(page, "Sign out").press("Enter");
    await status(await logout, 200);
    await page.waitForURL(origin + "/login");
    const replay = await browser.newContext({ extraHTTPHeaders: { Cookie: "JSESSIONID=" + oldCookie } });
    await status(await replay.request.get(origin + api + "/auth/me"), 403);
    await replay.close();
    console.log(`JR-143 PASS ${viewport.width}x${viewport.height}: dashboard → detail → logs; logout 200, replay 403`);
    await context.close();
  }
}

async function expiry(browser) {
  const { page, context } = await login(browser, "IdleUser", { width: 1280, height: 800 });
  const created = await mutation(context, "/engagements", { name: marker, description: "Fresh idle-session fixture" });
  await status(created, 200);
  const { id } = await created.json();
  await page.reload({ waitUntil: "domcontentloaded" });
  await page.getByRole("main").getByRole("button", { name: new RegExp(marker) }).click();
  await page.waitForURL(origin + `/engagements/${id}`);
  await heading(page, marker).waitFor();
  const session = await cookie(context, "JSESSIONID");
  const auth = await context.request.get(origin + api + "/auth/me");
  await status(auth, 200);
  const logIdleRequest = (request) => {
    const path = new URL(request.url()).pathname;
    if (path.startsWith(api + "/")) idleRequests.push(path);
  };
  const idleRequests = [];
  await page.waitForLoadState("networkidle");
  page.on("request", logIdleRequest);
  await new Promise((resolve) => setTimeout(resolve, 65000));
  page.off("request", logIdleRequest);
  assert.deepEqual(idleRequests, [], "The open tab made API requests during the idle interval");
  assert.equal(await cookie(context, "JSESSIONID"), session, "Browser retained the elapsed server session cookie");
  await page.addInitScript((secretFixture) => {
    window.protectedTextSeen = false;
    new MutationObserver(() => {
      window.protectedTextSeen ||= document.body?.innerText.includes(secretFixture) ?? false;
    }).observe(document, { childList: true, subtree: true, characterData: true });
  }, marker);
  const paths = [];
  page.on("request", (request) => paths.push(new URL(request.url()).pathname));
  const me = page.waitForResponse(requestMatch("/auth/me", "GET"));
  await page.reload({ waitUntil: "domcontentloaded" });
  await status(await me, 403);
  await page.waitForURL(origin + "/login");
  await button(page, "Sign in").waitFor();
  assert.equal(await page.evaluate(() => window.protectedTextSeen), false);
  assert.equal(paths.some((path) => path.startsWith("/api/engagements")), false);
  const denied = await context.request.get(origin + api + `/engagements/${id}`);
  await status(denied, 403);
  assert.equal((await denied.text()).includes(marker), false);
  await status(await mutation(context, `/engagements/${id}`, { name: "Denied edit" }, "PUT"), 403);
  const operator = await login(browser, "IdleUser", { width: 1280, height: 800 }, false);
  const retained = await operator.context.request.get(origin + api + `/engagements/${id}`);
  await status(retained, 200);
  assert.equal((await retained.json()).name, marker);
  await operator.context.close();
  await context.close();
  console.log("JR-144 PASS: elapsed 65 s with original JSESSIONID, /auth/me and detail 403, redirected to /login, no protected DOM or query and PUT denied");
}

async function engagement(browser) {
  const viewport = { width: 390, height: 844 };
  const lead = await login(browser, "SyntheticLeader", viewport);
  const member = await login(browser, "SyntheticMember", viewport);
  const outsider = await login(browser, "SyntheticOutsider", viewport);
  const name = `${marker} engagement`;
  const target = "scope.example.test";
  await button(lead.page, "Manage Engagements").click();
  await heading(lead.page, "No engagements yet").waitFor();
  await button(lead.page, "New Engagement").click();
  await lead.page.getByLabel("Engagement name").fill(name);
  await lead.page.getByLabel("Scope & description").fill("Synthetic engagement");
  const create = lead.page.waitForResponse(requestMatch("/engagements", "POST"));
  await button(lead.page, "Create Engagement").click();
  const createResponse = await create;
  await status(createResponse, 200);
  const created = await createResponse.json();
  assert.equal(created.role, "LEADER");
  const path = `/engagements/${created.id}`;
  await lead.page.getByRole("main").getByRole("button", { name: new RegExp(name) }).click();
  await button(lead.page, "Edit").click();
  await lead.page.getByLabel("In-scope targets (one per line)").fill(target);
  const update = lead.page.waitForResponse(requestMatch(path, "PUT"));
  await button(lead.page, "Save changes").click();
  await status(await update, 200);
  await button(lead.page, "Scope & RoE").click();
  await lead.page.getByText(target, { exact: true }).waitFor();
  const codeResponse = lead.page.waitForResponse(requestMatch(path + "/join-code", "POST"));
  await button(lead.page, "Join code").click();
  const code = await codeResponse;
  await status(code, 200);
  await button(member.page, "Manage Engagements").click();
  await member.page.getByPlaceholder(/Enter join code/).fill((await code.json()).code);
  const join = member.page.waitForResponse(requestMatch("/join", "POST"));
  await button(member.page, "Join").click();
  const joined = await join;
  await status(joined, 200);
  assert.equal((await joined.json()).role, "MEMBER");
  await member.page.getByRole("main").getByRole("button", { name: new RegExp(name) }).click();
  await button(member.page, "Scope & RoE").click();
  await member.page.getByText(target, { exact: true }).waitFor();
  await button(member.page, "Team").click();
  await heading(member.page, "Team (2)").waitFor();
  await member.page.reload({ waitUntil: "domcontentloaded" });
  await button(member.page, "Scope & RoE").click();
  await member.page.getByText(target, { exact: true }).waitFor();
  await button(member.page, "Team").click();
  await heading(member.page, "Team (2)").waitFor();
  for (const label of ["SyntheticLeader", "SyntheticMember"]) {
    await member.page.getByRole("main").getByText(label, { exact: true }).waitFor();
  }
  if (!baseline) assert.equal(await button(member.page, "Team").getAttribute("aria-pressed"), "true");
  assert.equal(await button(member.page, "Edit").count(), 0);
  assert.equal(await button(member.page, "Audit").count(), 0);
  console.log("JR-148 PASS: UI create, scope update, code issuance and join all 200; LEADER/MEMBER and two-person roster persisted after reload");

  const before = await lead.context.request.get(origin + api + path + "/audit");
  await status(before, 200);
  const deniedDetail = await outsider.context.request.get(origin + api + path);
  await status(deniedDetail, 404);
  assert.equal("members" in await deniedDetail.json(), false);
  const unavailableDetail = outsider.page.waitForResponse(requestMatch("/engagements/0", "GET"));
  await outsider.page.goto("/engagements/0", { waitUntil: "domcontentloaded" });
  await status(await unavailableDetail, 404);
  if (baseline) {
    await outsider.page.getByText("Engagement not found", { exact: true }).waitFor();
    assert.equal(await outsider.page.getByRole("alert").count(), 0);
  } else {
    await outsider.page.getByRole("alert").filter({ hasText: "Engagement not found" }).waitFor();
  }
  assert.equal(await heading(outsider.page, name).count(), 0);
  assert.equal(await button(outsider.page, "Team").count(), 0);
  await outsider.page.goto("/engagements", { waitUntil: "domcontentloaded" });
  await outsider.page.getByPlaceholder(/Enter join code/).fill("PL0G-AAAAAA");
  const rejected = outsider.page.waitForResponse(requestMatch("/join", "POST"));
  await button(outsider.page, "Join").click();
  await status(await rejected, 400);
  if (baseline) {
    await outsider.page.getByText("Invalid join code", { exact: true }).waitFor();
    assert.equal(await outsider.page.getByRole("alert").count(), 0);
    console.log("JR-149 BASELINE: detail 404 and join 400 shown visually without an alert");
  } else {
    await outsider.page.getByRole("alert").filter({ hasText: "Invalid join code" }).waitFor();
  }
  assert.equal(await outsider.page.getByText("Joined engagement", { exact: true }).count(), 0);
  assert.deepEqual(await (await outsider.context.request.get(origin + api + "/engagements")).json(), []);
  const after = await lead.context.request.get(origin + api + path + "/audit");
  await status(after, 200);
  assert.deepEqual(await after.json(), await before.json());
  const retained = await lead.context.request.get(origin + api + path);
  await status(retained, 200);
  const detail = await retained.json();
  assert.equal(detail.members.length, 2);
  assert.deepEqual(detail.inScopeTargets, [target]);
  console.log("JR-149 PASS HTTP and state: detail 404; join 400; no disclosed roster, false success, outsider membership or audit mutation");
  await Promise.all([lead.context.close(), member.context.close(), outsider.context.close()]);
}

(async () => {
  const browser = await chromium.launch({
    executablePath: process.env.PLOGIT_CHROMIUM,
    args: ["--no-sandbox"],
  });
  try {
    console.log("Chromium", browser.version(), "fixture", marker);
    const checks = { navigation, engagement, expiry };
    const selected = process.env.PLOGIT_TEST_CASE;
    assert.ok(!selected || Object.hasOwn(checks, selected), "Unknown browser check");
    for (const [name, check] of Object.entries(checks)) {
      if (!selected || name === selected) await check(browser);
    }
  } finally {
    await browser.close();
  }
})().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
