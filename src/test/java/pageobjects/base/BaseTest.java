package pageobjects.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import pageobjects.pages.PlaywrightPage;

public class BaseTest {

  protected Playwright playwright;
  protected Browser browser;
  protected BrowserContext context;
  protected Page page;
  protected PlaywrightPage app;

  @BeforeClass
  public void beforeClass() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new com.microsoft.playwright.BrowserType.LaunchOptions().setHeadless(true));
  }

  @BeforeMethod
  public void beforeMethod() {
    context = browser.newContext();
    page = context.newPage();
    app = new PlaywrightPage(page);
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
