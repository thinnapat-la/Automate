package selenium.utility;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WaitUtility {

	private final WebDriver driver;

	public WaitUtility(WebDriver driver) {
		this.driver = driver;
	}
	
	private int timeFluentWait=30;
	// Helper method to create FluentWait
	private FluentWait<WebDriver> createFluentWait(int time) {
		return new FluentWait<>(driver).withTimeout(Duration.ofSeconds(time)).pollingEvery(Duration.ofMillis(500))
				.ignoring(NoSuchElementException.class).ignoring(ElementNotInteractableException.class)
				.ignoring(StaleElementReferenceException.class)
				.withMessage("Element not found within the specified timeout");
	}

	// Wait for the page to finish loading
	private void waitReadyState() {
		createFluentWait(timeFluentWait).until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));
	}

	private void waitJquery() {
		createFluentWait(timeFluentWait).until(drivers -> {
			try {
				return createFluentWait(timeFluentWait).until(
						ExpectedConditions.jsReturnsValue("return window.jQuery != undefined && jQuery.active === 0;"));
			} catch (NoSuchElementException e) {
				log.error("waitJquery");
				// Element is not present at all
				return true;
			}
		});
	}

	// Wait for the page to finish loading
	public void waitJSReady() {
		waitReadyState();
		waitJquery();
	}

	public void waitNotDisplay(By by) {
	    // Temporarily disable implicit wait
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
		createFluentWait(timeFluentWait).until(drivers -> {
			try {
				// Find elements matching the locator
				List<WebElement> elements = driver.findElements(by);

				if (elements.isEmpty()) {
					log.trace("Element isEmpty");
					return true; // pass
				}

				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
				return wait.until(ExpectedConditions.invisibilityOfElementLocated(by));

			} catch (NoSuchElementException e) {
				log.trace("Element not present, assuming it's not displayed.");
				return true; // Treat as not displayed if the element is missing
			} catch (StaleElementReferenceException e) {
				log.trace("Element became stale, assuming it's not displayed.");
				return true; // Treat as not displayed if the element is stale
			} catch (Exception e) {
				log.trace(e.getMessage(), e);
				return true; // Keep waiting if the element is still visible
			} finally {
		        // Restore the implicit wait
		        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		    }
		});
	}

	// Wait for the alert to be present
	public void waitForAlert() {
		createFluentWait(timeFluentWait).until(ExpectedConditions.alertIsPresent());
	}

	// Wait until the element is present in the DOM and visible on the page
	public WebElement waitForElement(By by) {
		waitJSReady();
		// Wait for the element to be visible
		return createFluentWait(timeFluentWait).until(ExpectedConditions.visibilityOfElementLocated(by));
	}

	// Wait for the element to be clickable
	public WebElement waitForClickableElement(By by) {
		waitJSReady();
		waitForElement(by);
		// Wait until the element is present in the DOM and visible on the page and can
		// click it
		return createFluentWait(timeFluentWait).until(ExpectedConditions.elementToBeClickable(by));
	}

	// Wait until the page finishes loading and the element becomes invisible
	public void waitForElementInvisible(By by) {
		waitJSReady();
		// Wait until the element is not present in the DOM or not visible on the page
		createFluentWait(timeFluentWait).until(ExpectedConditions.invisibilityOfElementLocated(by));
	}

	// text to be present in the element
	public void waitForText(By by, String expectedText) {
		waitJSReady();

		// Wait until the element is present in the DOM and visible on the page
		waitForElement(by);
		// Wait for the expected text to be present in the element
		createFluentWait(timeFluentWait).until(ExpectedConditions.textToBePresentInElementLocated(by, expectedText));
	}

	// Wait for the page to finish loading and for the presence of all elements
	public List<WebElement> waitForPresenceOfAllElements(By by) {
		waitJSReady();

		// Wait for the list of elements matching the provided locator
		return createFluentWait(timeFluentWait).until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
	}

	// Ensure the element is visible
	public void elementVisible(By by) {

		waitJSReady();

		// Ensure the element is present in the DOM
		WebElement element = createFluentWait(timeFluentWait).until(ExpectedConditions.presenceOfElementLocated(by));
		// Ensure the element is visible
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		boolean isElementVisible = (Boolean) jse.executeScript("return arguments[0].offsetParent !== null;", element);

		// If the element is not visible, make it visible
		if (!isElementVisible) {
			try {
				jse.executeScript("arguments[0].style.visibility='visible';", element);
				log.info("Element [{}] made visible.", by.toString());
			} catch (Exception e) {
				log.error("Failed to make element [{}] visible", by.toString(), e);
			}
		}
	}

	public void checkDisplayEnable(WebElement element, String locator) throws InterruptedException {
		// check element display and enable
	   if (!element.isDisplayed() || !element.isEnabled()) {
	      if (!element.isDisplayed()) {
	          log.trace("Element [{}] is not displayed.", locator);
	      }
	      if (!element.isEnabled()) {
	          log.trace("Element [{}] is not enabled.", locator);
	      }
	      TimeUnit.MILLISECONDS.sleep(1000);
	   }
	}
	
	// Method to handle waiting for network idle
	public void waitForNetworkIdle() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		int maxRetries = 2;

		for (int attempt = 0; attempt < maxRetries; attempt++) {
			try {
				// Handle alert if present before checking for network idle
				if (isAlertPresent()) {
					log.info("Have Alert");
				} else {
					// Check if jQuery is available
					Boolean isJQueryAvailable = (Boolean) js.executeScript("return (typeof jQuery !== 'undefined');");
					log.trace("Check JQuery");
					if (Boolean.TRUE.equals(isJQueryAvailable)) {
						try {
							log.trace("Wait for network idle...");
							wait.until(drivers -> {
								boolean isNetworkIdle = (Boolean) js.executeScript("return (jQuery.active === 0);");
								log.trace("jQuery.active: {}", js.executeScript("return jQuery.active;"));
								log.trace("Status jQuery.active");
								return isNetworkIdle;
							});

							break; // Exit the loop if successful
						} catch (TimeoutException e) {
							log.info("TimeoutException network does not become idle in 30 sec occurred on attempt {}: ",
									attempt + 1, e);
							if (attempt == maxRetries - 1) {
								log.error("Timeout: Network did not become idle within the specified time: ", e);
								throw e; // Re-throw the exception if max retries exceeded
							}
						}

					} else {
						log.info("jQuery is not available on the page. Skipping waiting for network idle.");
						break;
					}
				}

			} catch (NoSuchWindowException e) {
				log.info("NoSuchWindowException: {}", e.getMessage());
				driver.switchTo().window(getMainWindowHandle());
			} catch (WebDriverException e) {
				handleWebDriverException(e, attempt, maxRetries);
			}
		}
	}

	// Method to check if an alert is present
	private boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException ex) {
			return false;
		}
	}

	// Method to handle WebDriver exceptions
	private void handleWebDriverException(WebDriverException e, int attempt, int maxRetries) {
		if (e.getMessage().contains("no such execution context")) {
			log.info("Execution context changed. Retrying...");
			if (attempt == maxRetries - 1) {
				log.info("Exceeded maximum retries for waitForNetworkIdle due to execution context change");
				throw e;
			}
		} else {
			if (attempt == maxRetries - 1) {
				log.info("Exceeded maximum retries for waitForNetworkIdle");
				throw e;
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			log.error("Thread was interrupted during retry wait: ", ie);
		}
	}

	private String getMainWindowHandle() {
		// Assuming the main window is the first one opened by the WebDriver
		return driver.getWindowHandles().iterator().next();
	}

}