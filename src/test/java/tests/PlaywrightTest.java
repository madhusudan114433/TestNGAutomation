package tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PlaywrightTest {

  private Playwright playwright;
  private Browser browser;
  private BrowserContext context;
  private Page page;

  @BeforeClass
  public void beforeClass() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
  }

  @BeforeMethod
  public void beforeMethod() {
    context = browser.newContext();
    page = context.newPage();
  }

  @Test(description = "Verify example.com title and heading")
  public void verifyExampleDotCom() {
    page.navigate("https://example.com");
    Assert.assertEquals(page.title(), "Example Domain", "Page title should be Example Domain");
    String actualHeading = page.textContent("h1").trim();
    Assert.assertEquals(actualHeading, "Example Domain", "Page heading should be Example Domain");
  }

  @AfterMethod
  public void afterMethod() {
    if (page != null) {
      page.close();
    }
    if (context != null) {
      context.close();
    }
  }

  @AfterClass
  public void afterClass() {
    if (browser != null) {
      browser.close();
    }
    if (playwright != null) {
      playwright.close();
    }
  }
}
