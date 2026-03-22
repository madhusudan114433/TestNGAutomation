# TestNGAutomation

## Playwright + TestNG Java Setup

Project files added:
- `pom.xml`
- `testng.xml`
- `src/test/java/tests/PlaywrightTest.java`

### Run

1. Install Playwright dependencies:
   - `mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"`
   - `sudo mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install-deps"`

2. Run tests:
   - `mvn test`

3. Reports:
   - `target/surefire-reports/index.html`
   - `target/surefire-reports/testng-results.xml`

## Interview-focused Playwright Use Cases

Added new test class: `src/test/java/tests/PlaywrightInterviewUsecases.java`

Use cases covered:
- Data-driven navigation plus title and element text assertions (`@DataProvider navigationData`)
- Data-driven navigation from Excel (`@DataProvider excelData` reading `src/test/resources/testdata.xlsx`)
- Data-driven navigation using java collection map from Excel (`@DataProvider excelDataViaCollection`)
- Data-driven form fill/submit with different first/last name combinations (`@DataProvider formData`)
- Captcha handling strategy (detect and wait for challenge resolution)
- File upload using `setInputFiles` plus temp file creation
- iFrame within iFrame traversal and nested frame lookup
- New tab opened from hyperlink with parent return and URL validation
- Network request interception + mocked API response
- File download lifecycle + validation
- Cross-browser testing across Chromium, Firefox, and WebKit (`@DataProvider browsers`)
- Headless vs headed mode comparison with screenshot capture (`@DataProvider headlessModes`)
- Advanced browser configs with user agents, viewports, dynamic iframes, and conditional network mocking (`@DataProvider browserConfigs`)

Run with `mvn test` and inspect results in `target/surefire-reports`

## Page Object Model (POM) implementation

- `pageobjects/base/BaseTest.java`: base test setup/teardown and shared context.
- `pageobjects/pages/PlaywrightPage.java`: page actions and reusable methods for navigation, form, iframe, popup, API mocking, file upload/download, and Excel collection-driven data mapping.
- `tests/PlaywrightInterviewUsecases.java`: uses `BaseTest` + `PlaywrightPage` methods for all scripted use cases.

## Data-driven framework notes

- TestNG `@DataProvider` drives test combinations for UI validation.
- `testng.xml` includes both `PlaywrightTest` and `PlaywrightInterviewUsecases`.
- Reports are generated in `target/surefire-reports` (standard TestNG HTML + XML).

## Learning Guide: Playwright Use Cases

This guide documents all implemented use cases with steps, code snippets, and learning objectives. Each use case demonstrates key Playwright concepts for interviews and real-world automation.

### 1. Basic Navigation and Assertion

**Objective:** Learn basic page navigation and element assertions.

**Code Location:** `dataDrivenNavigation()` in `PlaywrightInterviewUsecases.java`

**Steps:**
1. Navigate to `https://example.com`.
2. Assert page title equals "Example Domain".
3. Assert H1 element is visible.
4. Assert H1 text equals "Example Domain".

**Learning Points:**
- Use `page.navigate(url)` for loading pages.
- `page.title()` gets the page title.
- `page.isVisible(selector)` checks element visibility.
- `page.textContent(selector)` extracts text.

**Run:** `mvn test -Dtest=PlaywrightInterviewUsecases#dataDrivenNavigation`

### 2. Data-Driven Navigation from Excel (Array)

**Objective:** Implement data-driven testing with Excel using arrays.

**Code Location:** `dataDrivenNavigationFromExcel()` with `@DataProvider excelData`

**Steps:**
1. Read `src/test/resources/testdata.xlsx` using Apache POI.
2. Parse rows into Object[][].
3. For each row: navigate, assert title, check selector visibility, assert text.

**Learning Points:**
- `@DataProvider` for parameterized tests.
- Apache POI for Excel reading.
- Pros: Simple, fast for small datasets.
- Cons: Hardcoded file path, limited to arrays.

**Run:** `mvn test -Dtest=PlaywrightInterviewUsecases#dataDrivenNavigationFromExcel`

### 3. Data-Driven Navigation from Excel (Collections)

**Objective:** Use Java collections (List<Map>) for flexible data handling.

**Code Location:** `dataDrivenNavigationFromExcelWithCollection()` with `@DataProvider excelDataViaCollection`

**Steps:**
1. Load Excel into `List<Map<String, String>>`.
2. Each map has keys: "url", "title", "selector", "expected".
3. Test method accesses data via map.get(key).

**Learning Points:**
- Collections for dynamic data structures.
- Map-based access for readable test code.
- Scalable for complex data models.

**Run:** `mvn test -Dtest=PlaywrightInterviewUsecases#dataDrivenNavigationFromExcelWithCollection`

### 4. Form Fill and Submit

**Objective:** Automate form interactions.

**Code Location:** `dataDrivenFormSubmit()` with `@DataProvider formData`

**Steps:**
1. Navigate to form page.
2. Fill "firstname" and "lastname" fields.
3. Click submit button.
4. Assert URL contains success indicator.

**Learning Points:**
- `page.fill(selector, value)` for input fields.
- `page.click(selector)` for buttons.
- Post-submit validation via URL or elements.

**Run:** `mvn test -Dtest=PlaywrightInterviewUsecases#dataDrivenFormSubmit`

### 5. Captcha Handling

**Objective:** Detect and handle captcha challenges.

**Code Location:** `captchaUsecase()`

**Steps:**
1. Navigate to captcha demo page.
2. Assert iframe with "recaptcha" is visible.
3. Wait for manual resolution (in real scenarios, integrate solver API).

**Learning Points:**
- Identify captcha via iframe selectors.
- `page.waitForSelector()` for dynamic waits.
- Strategy: Detect, alert, or integrate third-party solvers.

**Run:** `mvn test -Dtest=PlaywrightInterviewUsecases#captchaUsecase`

### 6. File Upload

**Objective:** Automate file upload interactions.

**Code Location:** `fileUploadUsecase()`

**Steps:**
1. Create temporary file.
2. Navigate to upload page.
3. Use `page.setInputFiles(selector, path)`.
4. Assert file input value.

**Learning Points:**
- `setInputFiles()` for file inputs.
- Java `Files` for temp file creation.
- Cleanup with `Files.deleteIfExists()`.

**Run:** `mvn test -Dtest=PlaywrightInterviewUsecases#fileUploadUsecase`

### 7. iFrame Traversal

**Objective:** Handle nested iframes.

**Code Location:** `iframeWithinIframeUsecase()`

**Steps:**
1. Navigate to iframe demo.
2. Get outer frame by URL pattern.
3. Access inner frame via `childFrames()`.
4. Assert frames are not null.

**Learning Points:**
- `page.frameByUrl(pattern)` for outer frames.
- `frame.childFrames()` for nested access.
- Dynamic iframe loading requires waits.

**Run:** `mvn test -Dtest=PlaywrightInterviewUsecases#iframeWithinIframeUsecase`

### 8. New Tab Handling

**Objective:** Manage popups and return to parent.

**Code Location:** `newTabAndBackToParentUsecase()`

**Steps:**
1. Navigate to link page.
2. Click link with `waitForPopup()`.
3. Switch to popup, load, close.
4. Bring parent to front, assert URL.

**Learning Points:**
- `page.waitForPopup(() -> action)` for new tabs.
- `popup.close()` and `page.bringToFront()`.
- URL validation for context switching.

**Run:** `mvn test -Dtest=PlaywrightInterviewUsecases#newTabAndBackToParentUsecase`

### 9. Network Interception

**Objective:** Mock API responses.

**Code Location:** `networkInterceptAndAssert()`

**Steps:**
1. Route URL pattern with `page.route()`.
2. Fulfill with mock JSON.
3. Navigate and assert mocked content.

**Learning Points:**
- `page.route(pattern, handler)` for interception.
- `route.fulfill(options)` for mocking.
- Useful for testing without real APIs.

**Run:** `mvn test -Dtest=PlaywrightInterviewUsecases#networkInterceptAndAssert`

### 10. File Download

**Objective:** Handle download triggers.

**Code Location:** `fileDownload()`

**Steps:**
1. Navigate to download page.
2. `waitForDownload(() -> click)`.
3. Assert downloaded file path ends with ".zip".

**Learning Points:**
- `page.waitForDownload(action)` for async downloads.
- `download.path()` for file verification.
- Handle different file types.

**Run:** `mvn test -Dtest=PlaywrightInterviewUsecases#fileDownload`

### 11. Cross-Browser Testing

**Objective:** Run tests across browsers.

**Code Location:** `crossBrowserNavigation()` with `@DataProvider browsers`

**Steps:**
1. Data provider: "chromium", "firefox", "webkit".
2. Launch browser based on type.
3. Navigate and assert (same as basic).

**Learning Points:**
- Switch expression for browser selection.
- Headless mode for all.
- Matrix testing for compatibility.

**Run:** `mvn test -Dtest=PlaywrightInterviewUsecases#crossBrowserNavigation`

### 12. Headless vs Headed

**Objective:** Compare execution modes.

**Code Location:** `headlessUsecase()` with `@DataProvider headlessModes`

**Steps:**
1. Data provider: true/false for headless.
2. Launch with mode, navigate, screenshot.
3. Assert visibility (headless check).

**Learning Points:**
- `setHeadless(boolean)` toggle.
- Screenshots for evidence.
- Headless for CI, headed for debug.

**Run:** `mvn test -Dtest=PlaywrightInterviewUsecases#headlessUsecase`

### 13. Advanced Browser Configs

**Objective:** Handle user agents, viewports, dynamic elements, conditional mocking.

**Code Location:** `advancedBrowserTest()` with `@DataProvider browserConfigs`

**Steps:**
1. Data: browser, userAgent, width, height.
2. Set context with configs.
3. Route with conditional mocking (check postData).
4. Wait for dynamic iframe, interact.

**Learning Points:**
- `BrowserContext` with userAgent/viewport.
- Conditional `route.fulfill()` vs `route.resume()`.
- Thread-safe: Isolated per test.

**Run:** `mvn test -Dtest=PlaywrightInterviewUsecases#advancedBrowserTest`

## General Guidance

- **Setup:** Install Maven, Java 17+, Playwright deps.
- **Run All:** `mvn test`
- **Reports:** Open `target/surefire-reports/index.html`
- **Debug:** Set headless=false in BaseTest for visual.
- **Extend:** Add more data to providers or new methods.

This framework covers 13+ interview-ready use cases. Study each for deep Playwright expertise!
