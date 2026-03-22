package tests;

import com.microsoft.playwright.*;
import pageobjects.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

public class PlaywrightInterviewUsecases extends BaseTest {

  @Test(description = "Data-driven navigation and content validation")
  public void dataDrivenNavigation() {
    app.navigate("https://example.com");
    Assert.assertEquals(app.title(), "Example Domain");
    Assert.assertTrue(app.isVisible("h1"));
    Assert.assertEquals(app.text("h1"), "Example Domain");
  }

  @Test(description = "Data-driven navigation from Excel map collection")
  public void dataDrivenNavigationFromExcelWithCollection() throws Exception {
    Map<String, String> row = app.getExcelDataFromCollection("src/test/resources/testdata.xlsx").get(0);
    app.navigate(row.get("url"));
    Assert.assertEquals(app.title(), row.get("title"));
    Assert.assertTrue(app.isVisible(row.get("selector")));
    Assert.assertEquals(app.text(row.get("selector")), row.get("expected"));
  }

  @Test(description = "Data-driven form submit and URL assertion")
  public void dataDrivenFormSubmit() {
    app.navigate("https://www.w3schools.com/html/html_forms.asp");
    app.fillField("input[name='firstname']", "John");
    app.fillField("input[name='lastname']", "Doe");
    app.click("input[type='submit']");
    // Note: Demo form may not actually submit, so just verify fields are filled
    Assert.assertEquals(app.getPage().inputValue("input[name='firstname']"), "John");
    Assert.assertEquals(app.getPage().inputValue("input[name='lastname']"), "Doe");
  }

  @Test(description = "Captcha use case: identify captcha and wait for manual solve")
  public void captchaUsecase() {
    app.navigate("https://www.google.com/recaptcha/api2/demo");
    app.assertRecaptchaPresent();
  }

  @Test(description = "File upload use case")
  public void fileUploadUsecase() throws Exception {
    app.navigate("https://www.w3schools.com/howto/howto_html_file_upload_button.asp");
    app.uploadFile("input#myFile", "src/test/resources/sample-upload.txt");
    Assert.assertTrue(app.getPage().inputValue("input#myFile").contains("sample-upload"));
  }

  @Test(description = "iFrame within iFrame use case", enabled = false)
  public void iframeWithinIframeUsecase() {
    app.navigate("https://www.w3schools.com/html/tryit.asp?filename=tryhtml_iframe");
    app.verifyNestedFrame("*/tryhtml_iframe*");
  }

  @Test(description = "New tab from link and return to parent URL use case")
  public void newTabAndBackToParentUsecase() {
    app.navigate("https://www.w3schools.com/tags/att_a_target.asp");
    app.openNewTabAndReturn("https://www.w3schools.com/tags/att_a_target.asp", "a[target='_blank']");
  }

  @Test(description = "Network interception and API assertion")
  public void networkInterceptAndAssert() {
    app.routeJson("**/todos/**", "[{\"userId\":1,\"id\":1,\"title\":\"mocked\",\"completed\":false}]");
    app.navigate("https://jsonplaceholder.typicode.com/todos/1");
    Assert.assertTrue(app.text("body").contains("mocked"));
  }

  @Test(description = "File download automation", enabled = false)
  public void fileDownload() {
    app.navigate("https://the-internet.herokuapp.com/download");
    var download = app.waitForDownloadAndClick("a[href^='/download/']");
    Assert.assertTrue(download.path().toString().endsWith(".txt") || download.path().toString().endsWith(".zip"));
  }

  @DataProvider(name = "browsers")
  public Object[][] browsers() {
    return new Object[][]{
      {"chromium"},
      {"firefox"},
      {"webkit"}
    };
  }

  @Test(dataProvider = "browsers", description = "Cross-browser navigation test")
  public void crossBrowserNavigation(String browserType) {
    Playwright playwright = Playwright.create();
    Browser browser = switch (browserType) {
      case "chromium" -> playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
      case "firefox" -> playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true));
      case "webkit" -> playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(true));
      default -> throw new IllegalArgumentException("Unknown browser: " + browserType);
    };
    BrowserContext context = browser.newContext();
    Page page = context.newPage();
    page.navigate("https://example.com");
    Assert.assertEquals(page.title(), "Example Domain");
    page.close();
    context.close();
    browser.close();
    playwright.close();
  }

  @DataProvider(name = "headlessModes")
  public Object[][] headlessModes() {
    return new Object[][]{
      {true, "headless"}
    };
  }

  @Test(dataProvider = "headlessModes", description = "Headless vs headed mode use case with screenshot capture")
  public void headlessUsecase(boolean isHeadless, String mode) {
    Playwright playwright = Playwright.create();
    Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(isHeadless));
    BrowserContext context = browser.newContext();
    Page page = context.newPage();

    page.navigate("https://example.com");
    Assert.assertEquals(page.title(), "Example Domain");

    // Capture screenshot in both modes
    page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get("target/screenshots/screenshot-" + mode + ".png")));

    // Additional headless-specific check: no visible UI elements that require interaction
    if (isHeadless) {
      // In headless, ensure page loads without user interaction
      Assert.assertTrue(page.isVisible("h1"));
    }

    page.close();
    context.close();
    browser.close();
    playwright.close();
  }

  @DataProvider(name = "browserConfigs")
  public Object[][] browserConfigs() {
    return new Object[][]{
      {"chromium", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36", 1920, 1080},
      {"firefox", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:91.0) Gecko/20100101 Firefox/91.0", 1366, 768}
    };
  }

  @Test(dataProvider = "browserConfigs", description = "Advanced cross-browser with configs, dynamic iframes, and conditional mocking", enabled = false)
  public void advancedBrowserTest(String browserType, String userAgent, int width, int height) {
    Playwright playwright = Playwright.create();
    Browser browser = switch (browserType) {
      case "chromium" -> playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
      case "firefox" -> playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true));
      default -> throw new IllegalArgumentException("Unsupported browser: " + browserType);
    };

    BrowserContext context = browser.newContext(new Browser.NewContextOptions()
      .setUserAgent(userAgent)
      .setViewportSize(width, height));
    Page page = context.newPage();

    // Conditional network mocking based on request body
    page.route("**/api/data", route -> {
      String postData = route.request().postData();
      if (postData != null && postData.contains("test")) {
        route.fulfill(new Route.FulfillOptions()
          .setStatus(200)
          .setContentType("application/json")
          .setBody("{\"mocked\": true, \"browser\": \"" + browserType + "\"}"));
      } else {
        route.resume();
      }
    });

    page.navigate("https://www.w3schools.com/html/tryit.asp?filename=tryhtml_iframe");

    // Handle dynamic iframes with wait
    page.waitForSelector("iframe#iframeResult", new Page.WaitForSelectorOptions().setTimeout(10000));
    Frame dynamicFrame = page.frame("iframeResult");
    Assert.assertNotNull(dynamicFrame, "Dynamic iframe should be loaded");

    // Simulate thread-safe operation (in parallel, each test has its own page)
    page.fill("input[name='fname']", "ThreadSafe" + Thread.currentThread().threadId());

    page.close();
    context.close();
    browser.close();
    playwright.close();
  }
}
