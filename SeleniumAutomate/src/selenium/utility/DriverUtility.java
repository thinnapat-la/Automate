package selenium.utility;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DriverUtility {

	private WebDriver driver;

	private static final int TIMEWAIT_SECOND = 30;
	private static final String URL_HUB = "http://172.17.3.228:4444/wd/hub";
	private static final String BROWSERLOCAL = "local";
	private static final String BROWSERHUB = "hub";
	private static final String HEADLESS_ARGUMENTS = "--headless=new";
	private static final String HEADLESS_OLD = "--headless";
	private static final String WINDOW_ARGUMENTS = "--window-size=1920,1080";
	private static final String TRUE = "TRUE";
	private static final String INCOGNITO = "--incognito";
	private static final String PATH_CHROME_BROWSER = "config/webdriver/chrome/chromebrowser/" + PropertiesUtility.getValue("chrome.browser");
	private static final String PATH_FIREFOX_BROWSER = "config/webdriver/firefox/firefoxbrowser/" + PropertiesUtility.getValue("firefox.browser");
	private static final String PATH_EDGE_BROWSER = "config/webdriver/edge/edgebrowser/" + PropertiesUtility.getValue("edge.browser");

	public WebDriver initializeDriver(String browserMode, String browserType, String headlessMode, String incognitoMode)
			throws MalformedURLException {
			log.info("Initializing WebDriver - Mode:{}, Browser:{}, Headless:{}, Incognito:{}", browserMode,
					browserType, headlessMode, incognitoMode);
			try {
				driver = setDriver(browserMode, browserType, headlessMode, incognitoMode);
				driver.manage().deleteAllCookies();
				if (headlessMode.equalsIgnoreCase("FALSE")) {
					// not headless
					driver.manage().window().maximize();
				} else if (headlessMode.equalsIgnoreCase("TRUE") && !browserType.equalsIgnoreCase("CHROME")) {
					// headless & not chrome
					driver.manage().window().fullscreen();
				}
				// Set implicit wait globally to wait for findElement() and findElements()
				driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(TIMEWAIT_SECOND));
				// Set page load timeout
				driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TIMEWAIT_SECOND));
				// Set script timeout
				driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(TIMEWAIT_SECOND));
				log.info("================== WebDriver initial successful. ==================");
			} catch (Exception e) {
				log.error("WebDriver ERROR {}", e);
				return null;
			}
			return driver; 
	}

	private WebDriver setDriver(String browserMode, String browserType, String headlessMode, String incognitoMode)
			throws MalformedURLException {
		if (browserMode.equalsIgnoreCase(BROWSERHUB)) {
			log.trace(BROWSERHUB);
			if (browserType.equalsIgnoreCase("CHROME")) {
				ChromeOptions optionsChrome = new ChromeOptions();
				optionsChrome.setCapability("platformName", "Linux");
				optionsChrome.setCapability("browserName", browserType);
				initializeChrome(optionsChrome, headlessMode, incognitoMode, PATH_CHROME_BROWSER);
				optionsChrome.addArguments(WINDOW_ARGUMENTS); // Set window size
				return new RemoteWebDriver(new URL(URL_HUB), optionsChrome);
			} else if (browserType.equalsIgnoreCase("FIREFOX")) {
				FirefoxOptions optionsFirefox = new FirefoxOptions();
				optionsFirefox.setCapability("platformName", "Linux");
				optionsFirefox.setCapability("browserName", "firefox");
				initializeFireFox(optionsFirefox, headlessMode, incognitoMode, PATH_FIREFOX_BROWSER);
				optionsFirefox.addArguments(WINDOW_ARGUMENTS); // Set window size
				return new RemoteWebDriver(new URL(URL_HUB), optionsFirefox);
			} else if (browserType.equalsIgnoreCase("EDGE")) {
				EdgeOptions optionsEdge = new EdgeOptions();
				optionsEdge.setCapability("platformName", "Linux");
				optionsEdge.setCapability("browserName", "MicrosoftEdge");
				initializeEdge(optionsEdge, headlessMode, incognitoMode, PATH_EDGE_BROWSER);
				optionsEdge.addArguments(WINDOW_ARGUMENTS); // Set window size
				return new RemoteWebDriver(new URL(URL_HUB), optionsEdge);
			}
		} else if (browserMode.equalsIgnoreCase(BROWSERLOCAL)) {
			log.trace(BROWSERLOCAL);
			if (browserType.equalsIgnoreCase("CHROME")) {
				ChromeOptions optionsChrome = new ChromeOptions();
				initializeChrome(optionsChrome, headlessMode, incognitoMode, PATH_CHROME_BROWSER);
				return new ChromeDriver(optionsChrome);
			} else if (browserType.equalsIgnoreCase("FIREFOX")) {
				FirefoxOptions optionsFirefox = new FirefoxOptions();
				initializeFireFox(optionsFirefox, headlessMode, incognitoMode, PATH_FIREFOX_BROWSER);
				return new FirefoxDriver(optionsFirefox);
			} else if (browserType.equalsIgnoreCase("EDGE")) {
				EdgeOptions optionsEdge = new EdgeOptions();
				initializeEdge(optionsEdge, headlessMode, incognitoMode, PATH_EDGE_BROWSER);
				return new EdgeDriver(optionsEdge);
			} else if (browserType.equalsIgnoreCase("IE")) {
				InternetExplorerOptions optionsIE = new InternetExplorerOptions();
				// For Edge Chromium compatibility
				optionsIE.attachToEdgeChrome();
				// Enable capabilities for compatibility
				optionsIE.introduceFlakinessByIgnoringSecurityDomains(); // Ignore security domains (can cause flakiness)
				optionsIE.ignoreZoomSettings(); // Ignore zoom level (should match 100% in settings if not ignored)
				// Additional Capabilities
				optionsIE.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true); // Brings the IE window to the foreground
				optionsIE.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true); // Ignores the zoom level
				optionsIE.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true); // Enables hover events
				// Page Load Strategy
				optionsIE.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true); // Ensures a clean session (removes cookies and cache)
				optionsIE.setPageLoadStrategy(PageLoadStrategy.NORMAL);
				return new InternetExplorerDriver(optionsIE);
			}
		}
		log.error("Invalid browser : {} - {}", browserType, browserMode);
		return driver;
	}

	private ChromeOptions initializeChrome(ChromeOptions options, String headlessMode, String incognitoMode, String customBinaryPath) {
	    // Set binary if provided
	    if (customBinaryPath != null && !customBinaryPath.isEmpty()) {
	    	options.setBinary(customBinaryPath);
	    }
		// Important and Likely Needed
		options.addArguments("--ignore-certificate-errors", "--remote-allow-origins=*");
		options.addArguments("--allow-running-insecure-content", "--disable-popup-blocking");
		options.addArguments("--disable-blink-features=AutomationControlled");
		options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
		options.setExperimentalOption("useAutomationExtension", false);
		options.addArguments("--password-store=basic", "--disable-web-security", "--no-proxy-server");

		// Less Important or Redundant
		options.addArguments("--disable-gpu", "--disable-extensions", "--no-sandbox");
		options.addArguments("--disable-infobars", "--disable-dev-shm-usage");
		options.setAcceptInsecureCerts(true);
		options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
		options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

		// Check INCOGNITO_MODE
		if (incognitoMode == null || incognitoMode.isEmpty()) {
			// Property is not set; no action needed for incognito mode
		} else {
			// Property is set; check if it is TRUE
			if (incognitoMode.equalsIgnoreCase(TRUE)) {
				options.addArguments(INCOGNITO);
			}
		}

		if (headlessMode.equalsIgnoreCase(TRUE)) {
			options.addArguments(HEADLESS_ARGUMENTS);
			log.info("headless");
			options.addArguments(WINDOW_ARGUMENTS); // Set window size in headless mode
			options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
		}

		return options;
	}

	private FirefoxOptions initializeFireFox(FirefoxOptions options, String headlessMode, String incognitoMode, String customBinaryPath) {
	    // Set binary if provided
	    if (customBinaryPath != null && !customBinaryPath.isEmpty()) {
	    	options.setBinary(customBinaryPath);
	    }
	    
		// User agent
		options.addArguments("--user-agent=YourUserAgentString");
		// Accept insecure certificates
		options.setAcceptInsecureCerts(true);
		// Ignore certificate errors
		options.addArguments("--ignore-certificate-errors");
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("dom.webnotifications.enabled", false); // Disable pop-ups
		profile.setPreference("browser.privatebrowsing.autostart", true); // Private browsing (similar to no extensions)
		profile.setPreference("browser.safebrowsing.enabled", false); // Disable Safe Browsing
		// Apply Firefox profile settings
		options.setProfile(profile);

		// Check INCOGNITO_MODE
		if (incognitoMode == null || incognitoMode.isEmpty()) {
			// Property is not set; no action needed for incognito mode
		} else {
			// Property is set; check if it is TRUE
			if (incognitoMode.equalsIgnoreCase(TRUE)) {
				options.addArguments(INCOGNITO);
			}
		}

		if (headlessMode.equalsIgnoreCase(TRUE)) {
			options.addArguments(HEADLESS_OLD);
			log.info(headlessMode);
			options.addArguments(WINDOW_ARGUMENTS); // Set window size in headless mode
		}

		return options;
	}

	private EdgeOptions initializeEdge(EdgeOptions options, String headlessMode, String incognitoMode, String customBinaryPath) {
	    // Set binary if provided
	    if (customBinaryPath != null && !customBinaryPath.isEmpty()) {
	    	System.setProperty("webdriver.edge.driver", customBinaryPath);
	    }
	    
	    // Common settings
		options.addArguments("--disable-extensions");
		options.addArguments("--disable-popup-blocking");
		options.setAcceptInsecureCerts(true);
	    
		// Check INCOGNITO_MODE
		if (incognitoMode == null || incognitoMode.isEmpty()) {
			// Property is not set; no action needed for incognito mode
		} else {
			// Property is set; check if it is TRUE
			if (incognitoMode.equalsIgnoreCase(TRUE)) {
				options.addArguments(INCOGNITO);
			}
		}

		if (headlessMode.equalsIgnoreCase(TRUE)) {
			options.addArguments("headless");
			log.info(headlessMode);
			options.addArguments(WINDOW_ARGUMENTS); // Set window size in headless mode
		}
		return options;
	}

	public void quitDriver() {
		if (driver != null) {
			driver.quit();
		}
	}

	public WebDriver getDriver() {
		return driver;
	}
}