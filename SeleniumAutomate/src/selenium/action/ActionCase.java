package selenium.action;

import lombok.extern.slf4j.Slf4j;
import selenium.test.RunTestCase;
import selenium.utility.ActionUtility;
import selenium.utility.ReportUtility;

@Slf4j
public class ActionCase {

	private static String ID = "id";
	private static String NAME = "name";
	private static String CSS = "css";
	private static String XPATH = "xpath";
	
    public void selectMainMenu(ActionUtility actionUtility, ReportUtility reportUtility, String mainmenu) throws Exception {
    	actionUtility.actionOpenURL("https://www.tisco.co.th/th/");
        reportUtility.reportLogPassPic("Open URL Successful");
        actionUtility.actionClick(CSS, mainmenu);
        reportUtility.reportLogPassPic("Select Menu Successful");
        log.info("selectMainMenu");
    }
    
    public void searchBankBranch(ActionUtility actionUtility, ReportUtility reportUtility, String city, String citybranch) throws Exception {
        actionUtility.actionSelectDropDown(ID, "province_branch", "value", city);     
        reportUtility.reportLogPassPic("Select Bank Branch Successful");
        actionUtility.actionClick(ID, "btnSearchBranch");
        reportUtility.reportLogPassPic("Click Search Bank Branch Successful");
        actionUtility.actionSelectLabel(CSS, "div[id='tiscobank_branch'] div[class='list-detail-branch-header '] span", citybranch);
        reportUtility.reportLogPassPic("Select Bank Branch Successful");
        log.info("searchBankBranch");
    }
}