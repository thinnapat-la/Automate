package selenium.utility;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import lombok.extern.slf4j.Slf4j;
import tech.grasshopper.reporter.ExtentPDFReporter;

@Slf4j
public class ReportUtility {

    private WebDriver driver;
    private ExtentReports extentHtml; // Report HTML
    private ExtentReports extentPdf;  // Report PDF
    private ExtentTest logHtml;       // Log for HTML
    private ExtentTest logPdf;        // Log for PDF

    private static final String CONFIG_PATH = "config/";
    private static final String PDF_XML_PATH = CONFIG_PATH + "pdfReport-config.xml";
    private static final String HTML_XML_PATH = CONFIG_PATH + "htmlReport-config.xml";
    private final SimpleDateFormat sdfDateTimeReport = new SimpleDateFormat("dd-MMM-yyyy_HH-mm", Locale.ENGLISH);
    private final SimpleDateFormat sdfReportFile = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
	private final SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd-MMM-yyyy_HH-mm-ss", Locale.ENGLISH);
    private final Date resultDate = new Date();
	private String folderReport = "output/report/" + sdfDateTimeReport.format(resultDate) + "/";

	public ReportUtility(WebDriver driver) {
		this.driver = driver;
	}

    public void setupReport() throws IOException {
        String reportFile = folderReport + "TestReport-" + sdfReportFile.format(resultDate);

        File reportDir = new File(folderReport);
        if (!reportDir.exists() && !reportDir.mkdirs()) {
            throw new IOException("Failed to create report directory: " + folderReport);
        }

        ExtentSparkReporter htmlReporter = new ExtentSparkReporter(reportFile + ".html");
        ExtentPDFReporter pdfReporter = new ExtentPDFReporter(reportFile + ".pdf");
        extentHtml = new ExtentReports();
        extentPdf = new ExtentReports();

        extentHtml.attachReporter(htmlReporter);
        htmlReporter.loadXMLConfig(HTML_XML_PATH);

        extentPdf.setMediaResolverPath(new String[]{folderReport + "Image/"}); 
        extentPdf.attachReporter(pdfReporter);
        pdfReporter.loadXMLConfig(PDF_XML_PATH);

        systemInfo();
        log.info("================== Report initial successful. ==================");
    }

    private void systemInfo() {
        extentHtml.setSystemInfo("JAVA Version", System.getProperty("java.version"));
        extentHtml.setSystemInfo("OS", System.getProperty("os.name"));
        extentPdf.setSystemInfo("JAVA Version", System.getProperty("java.version"));
        extentPdf.setSystemInfo("OS", System.getProperty("os.name"));
    }

	public void createScenario(String scenarioName) {
		logHtml = extentHtml.createTest(scenarioName);
		logPdf = extentPdf.createTest(scenarioName);
	}
	
	public void reportLogInfo(String logDetail) {
		logHtml.info(logDetail);
		logPdf.info(logDetail);
	}

	public void reportLogInfoPic(String logDetail) throws IOException {
		logHtml.info(logDetail, MediaEntityBuilder.createScreenCaptureFromPath(screenshotFail()).build());
		logPdf.info(logDetail, MediaEntityBuilder.createScreenCaptureFromPath(screenshotFail()).build());
	}

	public void reportLogPass(String logDetail) {
		logHtml.pass(logDetail);
		logPdf.pass(logDetail);
	}

	public void reportLogPassPic(String logDetail) throws InterruptedException, IOException {
		Thread.sleep(1000);
		logHtml.pass(logDetail, MediaEntityBuilder.createScreenCaptureFromPath(screenshotPass()).build());
		logPdf.pass(logDetail, MediaEntityBuilder.createScreenCaptureFromPath(screenshotPass()).build());
	}

	public void reportLogFail(String logDetail) {
		logHtml.fail(logDetail);
		logPdf.fail(logDetail);
	}
	
	public void reportLogFailPic(String logDetail) throws IOException {
		logHtml.fail(logDetail, MediaEntityBuilder.createScreenCaptureFromPath(screenshotFail()).build());
		logPdf.fail(logDetail, MediaEntityBuilder.createScreenCaptureFromPath(screenshotFail()).build());
	}

	public void reportLogWarnPic(String logDetail) throws IOException {
		logPdf.warning(logDetail, MediaEntityBuilder.createScreenCaptureFromPath(screenshotFail()).build());
		logHtml.warning(logDetail, MediaEntityBuilder.createScreenCaptureFromPath(screenshotFail()).build());
	}
	
	// Screenshot Image for Fail Test
	private String screenshotFail() throws IOException {
		return this.captureScreenshot("Image/fail_");
	}

	// Screenshot Image for Pass Test
	private String screenshotPass() throws IOException {
		return this.captureScreenshot("Image/pass_");
	}

	private String captureScreenshot(String typeImage) throws IOException {

		// image name
		Date resultdate = new Date(System.currentTimeMillis());
		String fileName = typeImage + sdfDateTime.format(resultdate) + ".png";

		// take screenshot and save it in a file
		File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

		String fullPath = folderReport + fileName;

		// copy the file to the required path
		File destinationFile = new File(fullPath);
		FileUtils.copyFile(sourceFile, destinationFile);

		// add path image to HTML
		return fullPath.substring(32);
	}

    public void flushReport() {
        if (extentHtml != null) extentHtml.flush();
        if (extentPdf != null) extentPdf.flush();
        log.info("================== Reports flushed successfully. ==================");
    }
}