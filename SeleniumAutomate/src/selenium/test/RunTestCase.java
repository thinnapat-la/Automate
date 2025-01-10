package selenium.test;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import lombok.extern.slf4j.Slf4j;
import selenium.action.ActionCase;
import selenium.utility.ActionUtility;
import selenium.utility.DriverUtility;
import selenium.utility.LogUtility;
import selenium.utility.ReportUtility;
import selenium.utility.WaitUtility;

@Slf4j
public class RunTestCase {

    private WebDriver driver;
    private ReportUtility reportUtility;

	private static String ID = "id";
	private static String NAME = "name";
	private static String CSS = "css";
	private static String XPATH = "xpath";
	
    @BeforeClass(alwaysRun = true)
    public void setUp() {
        try {
            // Initialize Log
        	LogUtility logUtility = new LogUtility();
        	logUtility.initialLog();
            // Initialize WebDriverUtility
        	DriverUtility driverUtility = new DriverUtility();
            driver = driverUtility.initializeDriver("local", "chrome", "TRUE", "FALSE");
            // Initialize Report
            reportUtility = new ReportUtility(driver); // Pass WebDriver to ReportUtility
            reportUtility.setupReport();
            reportUtility.createScenario("Search Branch Tisco");
            log.info("================== Setup completed. ==================");
        } catch (Exception e) {
            log.error("Error during setup: ", e);
            throw new RuntimeException("Setup failed", e);
        }
    }

    @Test
    public void performTest() throws Exception {
            // Initialize WaitUtility and ActionUtility
        	WaitUtility waitUtility = new WaitUtility(driver);
        	ActionUtility actionUtility = new ActionUtility(driver, waitUtility, reportUtility);
        	ActionCase actionCase = new ActionCase();
        	
            log.info("================== Executing test case ==================");
            
            actionCase.selectMainMenu(actionUtility, reportUtility, "ul[id='menu-1-2760b80'] li[class='menu-item menu-item-type-custom menu-item-object-custom menu-item-32']");
            actionCase.searchBankBranch(actionUtility, reportUtility, "กรุงเทพมหานคร", "สาขาเซ็นทรัลปิ่นเกล้า");
            
            log.info("================== Test executed successfully. ==================");

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