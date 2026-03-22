package pageobjects.pages;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;

import java.nio.file.Path;
import java.util.Map;

public class PlaywrightPage {

  private final Page page;

  public PlaywrightPage(Page page) {
    this.page = page;
  }

  public void navigate(String url) {
    page.navigate(url);
  }

  public String title() {
    return page.title();
  }

  public boolean isVisible(String selector) {
    return page.isVisible(selector);
  }

  public String text(String selector) {
    return page.textContent(selector).trim();
  }

  public void fillField(String selector, String value) {
    page.fill(selector, value);
  }

  public void click(String selector) {
    page.click(selector);
  }

  public void setInputFiles(String selector, Path path) {
    page.setInputFiles(selector, path);
  }

  public Page getPage() {
    return page;
  }

  public Frame frameByUrl(String urlPattern) {
    return page.frameByUrl(urlPattern);
  }

  public Download waitForDownloadAndClick(String selector) {
    return page.waitForDownload(() -> page.click(selector));
  }

  public void routeJson(String urlPattern, String bodyJson) {
    page.route(urlPattern, route -> route.fulfill(new Route.FulfillOptions()
      .setStatus(200)
      .setContentType("application/json")
      .setBody(bodyJson)));
  }

  public void openNewTabAndReturn(String url, String linkSelector) {
    String parentUrl = page.url();
    Page popup = page.waitForPopup(() -> page.click(linkSelector, new Page.ClickOptions().setForce(true)));
    popup.waitForLoadState();
    popup.close();
    page.bringToFront();
    assert page.url().equals(parentUrl);
  }

  public void assertRecaptchaPresent() {
    if (!page.isVisible("iframe[src*='recaptcha']")) {
      throw new IllegalStateException("captcha iframe not detected");
    }
  }

  public void visitLink(String url) {
    navigate(url);
  }

  public String getUrl() {
    return page.url();
  }

  public java.util.List<java.util.Map<String, String>> getExcelDataFromCollection(String excelPath) throws Exception {
    java.io.File dataFile = new java.io.File(excelPath);
    if (!dataFile.exists()) {
      throw new IllegalStateException("Excel data file not found at " + excelPath);
    }

    try (java.io.FileInputStream fis = new java.io.FileInputStream(dataFile);
         org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(fis)) {
      org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
      java.util.Iterator<org.apache.poi.ss.usermodel.Row> rowIterator = sheet.rowIterator();
      if (rowIterator.hasNext()) {
        rowIterator.next(); // skip header
      }
      java.util.List<java.util.Map<String, String>> testData = new java.util.ArrayList<>();
      while (rowIterator.hasNext()) {
        org.apache.poi.ss.usermodel.Row row = rowIterator.next();
        java.util.Map<String, String> rowData = new java.util.HashMap<>();
        rowData.put("url", row.getCell(0).getStringCellValue());
        rowData.put("title", row.getCell(1).getStringCellValue());
        rowData.put("selector", row.getCell(2).getStringCellValue());
        rowData.put("expected", row.getCell(3).getStringCellValue());
        testData.add(rowData);
      }
      return testData;
    }
  }

  public void uploadFile(String selector, String filePath) {
    page.setInputFiles(selector, Path.of(filePath));
  }

  public void verifyNestedFrame(String outerFrameSelector) {
    Frame outerFrame = frameByUrl(outerFrameSelector);
    if (outerFrame == null) {
      throw new IllegalStateException("Outer frame not found:", new Throwable(outerFrameSelector));
    }
    Frame innerFrame = outerFrame.childFrames().stream()
      .findFirst().orElse(null);
    if (innerFrame == null) {
      throw new IllegalStateException("Nested frame not found inside outer frame");
    }
  }

  public void dataDrivenFromExcel(Map<String, String> data) {
    navigate(data.get("url"));
  }
}
