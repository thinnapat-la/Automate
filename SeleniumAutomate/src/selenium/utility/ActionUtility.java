package selenium.utility;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionUtility {

	private final WebDriver driver;
	private final WaitUtility waitUtility;
	private final ReportUtility reportUtility;

	public ActionUtility(WebDriver driver, WaitUtility waitUtility, ReportUtility reportUtility) {
		this.driver = driver;
		this.waitUtility = waitUtility;
		this.reportUtility = reportUtility;
	}
	
	private LocalDate currentDate = LocalDate.now();
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("th", "TH"));
	private String formattedDate = currentDate.format(formatter);

	// Time stop
	public void timeWait(String secondsInput) throws InterruptedException {
		double seconds = Double.parseDouble(secondsInput); // seconds 0.5 or 1
		long milliseconds = (long) (seconds * 1000); // Convert seconds to milliseconds
//	    Thread.sleep(milliseconds);
		TimeUnit.MILLISECONDS.sleep(milliseconds);
	}

	// new window
	public void actionNewWindow() {
        // Open a new window
        driver.switchTo().newWindow(WindowType.WINDOW);
	}
	
	// Open URL
	public void actionOpenURL(String url) {
		driver.get(url);
		waitUtility.waitJSReady();
		log.trace("actionOpenURL");
	}

	// Click Element
	public void actionClick(String elementType, String locator) throws InterruptedException {
		By by = getBy(elementType, locator);
		WebElement element = waitUtility.waitForClickableElement(by);
		waitUtility.checkDisplayEnable(element, locator);
		element.click();
		log.trace("actionClick");
	}

	// Input Text in Element
	public void actionInputText(String elementType, String locator, String keys) throws InterruptedException {
		By by = getBy(elementType, locator);
		WebElement element = waitUtility.waitForClickableElement(by);
		waitUtility.checkDisplayEnable(element, locator);
		// delete text
		element.sendKeys(Keys.CONTROL, "a", Keys.DELETE);
		if (keys.equalsIgnoreCase("$date/")) {
			// input current date
			element.sendKeys(formattedDate);
		} else {
			// input text
			element.sendKeys(keys);
		}
		log.trace("actionInputText");
	}

	// Input Text and Tab in Element
	public void actionInputTextTab(String elementType, String locator, String keys) throws InterruptedException {
		By by = getBy(elementType, locator);
		WebElement element = waitUtility.waitForClickableElement(by);
		waitUtility.checkDisplayEnable(element, locator);
		// delete text
		element.sendKeys(Keys.CONTROL, "a", Keys.DELETE);
		if (keys.equalsIgnoreCase("$date/")) {
			// input current date
			element.sendKeys(formattedDate + Keys.TAB);
		} else {
			// input text
			element.sendKeys(keys + Keys.TAB);
		}
		log.trace("sccSendTextTab");
	}

	// Move Mouse to Element
	public void actionMoveMouse(String elementType, String locator) {
		By by = getBy(elementType, locator);
		WebElement element = waitUtility.waitForClickableElement(by);
		// Create an instance of Actions class
		Actions actions = new Actions(driver);
		// Move the mouse pointer to the element
		actions.moveToElement(element).perform();
		log.trace("actionMoveMouse");
	}

	// Get Text in Element
	public void actionGetText(String elementType, String locator) throws InterruptedException {
		By by = getBy(elementType, locator);
		WebElement element = waitUtility.waitForElement(by);
		waitUtility.checkDisplayEnable(element, locator);
		// get text
		String txtGetText = element.getText();
		// log info in report
		reportUtility.reportLogInfo(txtGetText);
		log.trace("actionGetText");
	}

	// Get Attribute in Element
	public void actionGetAttributeText(String elementType, String locator, String attributeType)
			throws InterruptedException {
		By by = getBy(elementType, locator);
		WebElement element = waitUtility.waitForElement(by);
		waitUtility.checkDisplayEnable(element, locator);
		// placeholder //value
		String txtGetAttribute = element.getAttribute(attributeType.toLowerCase());
		// log info in report
		reportUtility.reportLogInfo(txtGetAttribute);
		log.trace("actionGetAttributeText");
	}

	// Get Text and Keep for input in Element
	public String actionKeepText(String elementType, String locator) throws InterruptedException {
		By by = getBy(elementType, locator);
		WebElement element = waitUtility.waitForElement(by);
		waitUtility.checkDisplayEnable(element, locator);
		log.trace("actionKeepText");
		return element.getText();
	}

	// Get Attribute and Keep for input in Element
	public String actionKeepAttribute(String elementType, String locator, String attributeType)
			throws InterruptedException {
		By by = getBy(elementType, locator);
		WebElement element = waitUtility.waitForElement(by);
		waitUtility.checkDisplayEnable(element, locator);
		log.trace("actionKeepAttribute");
		return element.getAttribute(attributeType.toLowerCase());
	}

	// Select Label in Element
	public void actionSelectLabel(String elementType, String locator, String valueCheck) throws Exception {
		By by = getBy(elementType, locator);
		waitUtility.waitJSReady();
		List<WebElement> type = waitUtility.waitForPresenceOfAllElements(by);
		log.trace("list label:{}", type);
		// Iterate through the list of elements
		for (WebElement element : type) {
			waitUtility.checkDisplayEnable(element, locator);
			// Get the text of the current element
			String valuesGet = element.getText();
			// Check if the text matches
			log.trace("element click:{}", element);
			if (valuesGet.equals(valueCheck)) {
				// If matched, click the element
				JavascriptExecutor click = (JavascriptExecutor) driver;
				click.executeScript("arguments[0].click()", element);
//				element.click();
				// Exit the loop once clicked
				break;
			}
		}
		log.trace("actionSelectLabel");
	}

	// Select DropDown Box in Element
	public void actionSelectDropDown(String elementType, String locator, String valueMode, String valueCheck)
			throws Exception {
		By by = getBy(elementType, locator);
		WebElement element = waitUtility.waitForClickableElement(by);
		waitUtility.checkDisplayEnable(element, locator);
		// Create an instance of Select class
		Select sc = new Select(element);
		switch (valueMode.toUpperCase()) {
		case "INDEX":
			sc.selectByIndex(Integer.parseInt(valueCheck));
			break;
		case "VALUE":
			sc.selectByValue(valueCheck);
			break;
		case "TEXT":
			sc.selectByVisibleText(valueCheck);
			break;
		default:
			log.error("error select mode");
			break;
		}
		log.trace("actionSelectDropDown");
	}

	// Upload File
	public void actionUploadFile(String elementType, String locator, String filePath) throws InterruptedException {
		By by = getBy(elementType, locator);
		waitUtility.waitForClickableElement(by);
		WebElement element = driver.findElement(by);
		waitUtility.checkDisplayEnable(element, locator);
		element.sendKeys(System.getProperty("user.dir") + File.separator + filePath);
		log.trace("actionUploadFile");
	}

	// Upload File with SciTE
	public void actionUploadFileSciTE(String elementType, String locator, String filePath)
			throws IOException, InterruptedException {
		By by = getBy(elementType, locator);
		waitUtility.waitForClickableElement(by);
		WebElement element = driver.findElement(by);
		waitUtility.checkDisplayEnable(element, locator);
		element.click();
		Runtime.getRuntime().exec(filePath);
		log.trace("actionUploadFileSciTE");
	}

	// Accept Alert
	public void actionAcceptAlert() throws Exception {
		waitUtility.waitForAlert();
		timeWait("0.5");
		driver.switchTo().alert().accept();
		log.trace("actionAcceptAlert");
	}

	// Dismiss Alert
	public void actionDismissAlert() throws Exception {
		waitUtility.waitForAlert();
		timeWait("0.5");
		driver.switchTo().alert().dismiss();
		log.trace("actionDismissAlert");
	}

	// Text Alert
	public void actionTextAlert(String keys) throws Exception {
		waitUtility.waitForAlert();
		timeWait("0.5");
		driver.switchTo().alert().sendKeys(keys);
		log.trace("actionTextAlert");
	}

	// In IFrame
	public void actionInIframe() {
	    // Temporarily disable implicit wait
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		try {
			// List all the frames on the page
			List<WebElement> frames = driver.findElements(By.tagName("iframe"));
			frames.addAll(driver.findElements(By.tagName("frame")));
			timeWait("0.5");
			log.trace("actionInIframe:{}", frames);
			// Check if frames are present
			if (frames.isEmpty()) {
				log.info("No frames found on the page.");
			} else {
				if (frames.size() == 1) {
					try {
						log.info("Number of frame found: {}", frames.size());
						driver.switchTo().frame((frames.size()));
					} catch (Exception e) {
						log.info("Number of frame -1 found: {}", frames.size());
						actionIframe();
					}
				} else {
					log.info("Number of frames found: {}", frames.size());
					try {
						log.trace("frame");
						driver.switchTo().frame((frames.size()));
					} catch (Exception e) {
						log.trace("frame-1");
						driver.switchTo().frame((frames.size() - 1));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
	        // Restore the implicit wait
	        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
	    }
		log.trace("actionInIframe");
	}

	private void actionIframe() throws Exception {
		// List all the frames on the page
		List<WebElement> frames = driver.findElements(By.tagName("iframe"));
		log.trace("actionIframe:{}", frames);
		frames.addAll(driver.findElements(By.tagName("frame")));
		timeWait("0.5");
		driver.switchTo().frame((frames.size() - 1));
		log.trace("actionIframe");
	}

	// Out IFrame
	public void actionOutIframe() throws Exception {
		timeWait("0.5");
		driver.switchTo().defaultContent();
		log.trace("actionOutIframe");
	}

	// Switch with Index
	public void actionSwitchToWindowIndex() throws Exception {
		waitUtility.waitJSReady();
		// Hold all window handles in an ArrayList
		ArrayList<String> handles = new ArrayList<>(driver.getWindowHandles());
		// Validate the index
		int windowIndex = handles.size() - 1;
		if (windowIndex < 0) {
			throw new IndexOutOfBoundsException("Invalid window index: " + windowIndex);
		}
		log.trace("windowIndex:{}", windowIndex);
		int attempts = 3; // Number of attempts to switch to the window
		boolean switched = false;
		while (attempts > 0) {
			try {
				timeWait("1");
				// Switch to the window/tab based on the provided index
				driver.switchTo().window(handles.get(windowIndex));
				switched = true;
				break; // Exit the loop if switch is successful
			} catch (NoSuchWindowException e) {
				// If switching fails, attempts will decrement and loop will continue
			}
			attempts--; // Decrement the attempts counter
		}
		if (!switched) {
			throw new RuntimeException("Failed to switch to window after multiple attempts");
		}
	}

	// Switch with Title
	public void actionSwitchToWindowTitle(String title) {
		waitUtility.waitJSReady();
		int attempts = 3; // Number of attempts to switch to the window
		boolean switched = false;
		while (attempts > 0) {
			try {
				// Sleep for a second to ensure the window is available
				timeWait("1");
				ArrayList<String> handles = new ArrayList<>(driver.getWindowHandles());
				for (String handle : handles) {
					driver.switchTo().window(handle);
					if (driver.getTitle().equals(title)) {
						switched = true;
					}
				}
			} catch (NoSuchWindowException e) {
				// Window not found, attempt again
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // Restore interrupted status
			}
			attempts--; // Decrement the attempts counter
		}
		if (!switched) {
			throw new RuntimeException("Failed to switch to window with title '" + title + "' after multiple attempts");
		}
	}

	// Check Equals Text in Element
	public void actionAssertEqualsText(String elementType, String locator, String expectedText) {
		By by = getBy(elementType, locator);
		try {
			waitUtility.elementVisible(by);
			waitUtility.waitForText(by, expectedText);
			log.trace("actionAssertEqualsText");
		} catch (StaleElementReferenceException e) {
			throw e;
		} catch (Exception e) {
			WebElement element = waitUtility.waitForElement(by);
			String actualText = element.getText();
			log.error("Error Element is : {} && Error Text is : {} && Actual is : {}", locator, expectedText,
					actualText);
			throw e;
		}
	}

	// Check Not Equals Text in Element
	public void actionAssertNotEqualsText(String elementType, String locator, String expectedText) throws Exception {
		By by = getBy(elementType, locator);
		waitUtility.elementVisible(by);
		WebElement element = waitUtility.waitForElement(by);
		waitUtility.checkDisplayEnable(element, locator);
		if (element.getText().equals(expectedText)) {
			log.error("Error Element is : {} && Error Text is : {}", locator, expectedText);
			throw new Exception(
					"Error is " + locator + ", expected = " + expectedText + ", actual = " + element.getText());
		} else {
			log.trace("actionAssertNotEqualsText");
		}
	}

	// Check Equals Attribute in Element
	public void actionAssertEqualsTextAttribute(String elementType, String locator, String expectedText,
			String attributeType) throws InterruptedException {
		By by = getBy(elementType, locator);
		waitUtility.elementVisible(by);
		WebElement element = waitUtility.waitForElement(by);
		waitUtility.checkDisplayEnable(element, locator);
		// placeholder //value
		String txtGetAttribute = element.getAttribute(attributeType.toLowerCase());
		if (txtGetAttribute.equals(expectedText)) {
			log.trace("actionAssertEqualsTextAttribute");
		} else {
			log.error("Error Element is : {} && Error Text is : {} && Actual is : {}", locator, expectedText,
					txtGetAttribute);
			throw new RuntimeException("Expected text '" + expectedText + "' but found '" + txtGetAttribute + "'");
		}
	}

	// Check Element Displayed
	public void actionAssertDisplayed(String elementType, String locator) throws Exception {
		By by = getBy(elementType, locator);
		WebElement element = waitUtility.waitForElement(by);
		waitUtility.checkDisplayEnable(element, locator);
		element.isDisplayed();
		log.trace("actionAssertDisplayed");
	}

	// Check Element Not Displayed
	public void actionAssertNotDisplayed(String elementType, String locator) {
		By by = getBy(elementType, locator);
		waitUtility.waitNotDisplay(by);
		log.trace("actionAssertNotDisplayed");
	}

	// Check Title
	public void actionAssertPageTitle(String expectedText) throws Exception {
//		if(driver.getPageSource().contains("status code 403")) {
//			log.error("status code 403");
//		}
		if (!driver.getTitle().equalsIgnoreCase(expectedText)) {
			log.error("Error Title is : {}", driver.getTitle());
			throw new Exception("Error Title is : " + driver.getTitle());
		} else {
			log.trace("actionAssertPageTitle");
		}
	}

	// Check Status Code
	public void actionCheckStatusCode() throws Exception {
		String currentUrl = driver.getCurrentUrl();
		// Create a connection to the URL
		HttpURLConnection connection = (HttpURLConnection) new URL(currentUrl).openConnection();
		try {
			connection.setRequestMethod("GET");
			connection.connect();
			// Get the status code
			int statusCode = connection.getResponseCode();
			log.trace("url-{}", currentUrl);
			log.trace("status-{}", statusCode);
			// 200
			if (statusCode == HttpURLConnection.HTTP_OK) {
				// Success
				reportUtility.reportLogInfoPic("Status-" + statusCode + " " + "URL-" + currentUrl);
				log.trace("Page loaded successfully with status: {}", statusCode);
			} else if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP) {
				// Handle 302 redirect
				String redirectUrl = connection.getHeaderField("Location");
				log.trace("Redirecting from: {}", redirectUrl);
				reportUtility.reportLogWarnPic("Status-" + statusCode + " " + "URL-" + currentUrl);
			} else {
				// Handle other statuses
				throw new Exception("Error Status: " + statusCode);
			}
		} finally {
			connection.disconnect();
		}
		log.trace("actionCheckStatusCode");
	}

	// Scroll Mouse
	public void actionScrollMouse(String elementType, String locator) throws Exception {
		By by = getBy(elementType, locator);
		WebElement element = waitUtility.waitForClickableElement(by);
		waitUtility.checkDisplayEnable(element, locator);
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].scrollIntoView()", element);
		log.trace("sccScrollMouse");
	}
	
	// Click in Element with SciTE
	public void actionSciTEClick(String filePath) throws Exception {
		waitUtility.waitJSReady();
		Runtime.getRuntime().exec(filePath);
		log.trace("actionSciTEClick");
	}

	// Click in Element with JavascriptExecutor
	public void actionJSClick(String elementType, String locator) {
		By by = getBy(elementType, locator);
		WebElement element = waitUtility.waitForClickableElement(by);
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('style','visibility:visible;');",
				element);
		jse.executeScript("arguments[0].click()", element);
		log.trace("actionJSClick");
	}

	// Input Text in Element with JavascriptExecutor
	public void actionJSInputText(String elementType, String locator, String keys) {
		By by = getBy(elementType, locator);
		WebElement element = waitUtility.waitForClickableElement(by);
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].setAttribute('style','visibility:visible;');", element);
		jse.executeScript("arguments[0].value='" + keys + "'", element);
		log.trace("actionJSSendKeys");
	}

	// Input Java Command
	public void actionCommand(String txtScript) throws Exception {
		Runtime.getRuntime().exec(txtScript);
		log.trace("actioCommand");
	}

	// Refresh Page
	public void actionRefresh() throws InterruptedException {
		waitUtility.waitJSReady();
		driver.navigate().refresh();
		log.trace("actionRefresh");
	}

	// Input Random in Element
	public void actionRandom(String elementType, String locator, int numCount, String keys)
			throws InterruptedException {
		By by = getBy(elementType, locator);
		WebElement element = waitUtility.waitForClickableElement(by);
		waitUtility.checkDisplayEnable(element, locator);
		element.sendKeys(Keys.CONTROL, "a", Keys.DELETE); // delete text
		element.sendKeys(RandomUtility.randomInput(keys.toUpperCase(), numCount)); // input random
		log.trace("actionRandom");
	}

	// Input Random and Tab in Element
	public void actionRandomTab(String elementType, String locator, int numCount, String keys)
			throws InterruptedException {
		By by = getBy(elementType, locator);
		WebElement element = waitUtility.waitForClickableElement(by);
		waitUtility.checkDisplayEnable(element, locator);
		// delete text and input random
		element.sendKeys(Keys.CONTROL, "a", Keys.DELETE); // delete text
		element.sendKeys(RandomUtility.randomInput(keys.toUpperCase(), numCount) + Keys.TAB); // input random and tab
		log.trace("actionRandomTab");
	}

	// CloseTab
	public void actionCloseTab() {
//		waitUtility.waitJSReady();
		// Hold all window handles in an ArrayList
		ArrayList<String> handles = new ArrayList<>(driver.getWindowHandles());
		log.trace("index-{}", handles.size());
		driver.close();
		int windowIndex = handles.size() - 2;
		log.trace("index-{}", windowIndex);
		driver.switchTo().window(handles.get(windowIndex));
		log.trace("actionCloseTab");
	}

	// Utility method to get By object based on elementType and locator
	private By getBy(String elementType, String locator) {
		// Get the By object using a custom method getBy, passing the element type and
		// locator
		switch (elementType.toLowerCase()) {
		case "id":
			return By.id(locator);
		case "name":
			return By.name(locator);
		case "class":
		case "classname":
			return By.className(locator);
		case "tag":
		case "tagname":
			return By.tagName(locator);
		case "link":
		case "linktext":
			return By.linkText(locator);
		case "partiallink":
		case "partiallinktext":
			return By.partialLinkText(locator);
		case "css":
		case "cssselector":
			return By.cssSelector(locator);
		case "xpath":
			return By.xpath(locator);
		default:
			throw new IllegalArgumentException("Unsupported element type: " + elementType);
		}
	}

}