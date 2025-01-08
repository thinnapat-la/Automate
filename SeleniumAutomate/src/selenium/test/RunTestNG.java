package selenium.test;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import lombok.extern.slf4j.Slf4j;
import selenium.utility.DriverUtility;
import selenium.utility.LogUtility;
import selenium.utility.ReportUtility;

@Slf4j
public class RunTestNG {

    private WebDriver driver;
    private ReportUtility reportUtility;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        try {
            // Initialize Log
        	LogUtility logUtility = new LogUtility();
        	logUtility.initialLog();
            // Initialize WebDriverUtility
        	DriverUtility driverUtility = new DriverUtility();
            driver = driverUtility.initializeDriver("local", "chrome", "FALSE", "FALSE");
            // Initialize Report
            reportUtility = new ReportUtility(driver); // Pass WebDriver to ReportUtility
            reportUtility.setupReport();
            reportUtility.createScenario("Test");
            log.info("================== Setup completed. ==================");
        } catch (Exception e) {
            log.error("Error during setup: ", e);
            throw new RuntimeException("Setup failed", e);
        }
    }

    @Test
    public void runTest() {
        try {
            log.info("================== Executing test case ==================");
            driver.get("https://www.google.com");
            reportUtility.reportLogPassPic("Open URL Pass");

            log.info("================== Test executed successfully. ==================");
        } catch (Exception e) {
            log.error("Error during test execution: ", e);
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
            }
            if (reportUtility != null) {
                reportUtility.flushReport();
            }
        } catch (Exception e) {
            log.error("Error during teardown: ", e);
        }
    }
}