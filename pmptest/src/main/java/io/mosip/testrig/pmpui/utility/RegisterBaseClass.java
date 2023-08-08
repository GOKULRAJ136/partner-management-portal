package io.mosip.testrig.pmpui.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.Reporter;
// Generated by Selenium IDE
//import org.junit.Test;
//import org.junit.Before;
//import org.junit.After;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.mosip.testrig.pmpui.kernel.util.ConfigManager;
import io.mosip.testrig.pmpui.kernel.util.KeycloakUserManager;
import io.mosip.testrig.pmpui.kernel.util.S3Adapter;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class RegisterBaseClass {
	protected WebDriver driver;
	protected Map<String, Object> vars;
	protected JavascriptExecutor js;
	protected String langcode;
	protected String envPath = ConfigManager.getiam_adminportal_path();
	protected String env=ConfigManager.getiam_apienvuser();
	protected String userid = KeycloakUserManager.moduleSpecificUser;
	protected String[] allpassword = ConfigManager.getIAMUsersPassword().split(",");
	protected String password = allpassword[0];
	protected String data = Commons.appendDate;
	public static ExtentSparkReporter html;
    public static    ExtentReports extent;
    public static    ExtentTest test;

	public void setLangcode(String langcode) throws Exception {
		this.langcode = Commons.getFieldData("langcode");
	}
	
@BeforeMethod
	
    public void set() {
        extent=ExtentReportManager.getReports();

}

	@BeforeMethod
	public void setUp() throws Exception {
		Reporter.log("BaseClass",true);
		   test=extent.createTest(getCommitId(),getCommitId());
		 
		WebDriverManager.chromedriver().setup();
		ChromeOptions options = new ChromeOptions();
		String headless=JsonUtil.JsonObjParsing(Commons.getTestData(),"headless");
		if(headless.equalsIgnoreCase("yes")) {
			options.addArguments("--headless=new");
		}
		
		driver = new ChromeDriver(options);
		js = (JavascriptExecutor) driver;
		vars = new HashMap<String, Object>();
		driver.get(envPath);
		driver.manage().window().maximize();
		Thread.sleep(500);
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		

	}

	@AfterMethod
	public void tearDown() throws InterruptedException {
		Commons.click(test,driver, By.id("menuButton"));
		Commons.click(test,driver, By.id("Logout"));
		driver.quit();
		extent.flush();
	}

	@AfterSuite
	public void pushFileToS3() {
		getCommitId();
		if (ConfigManager.getPushReportsToS3().equalsIgnoreCase("yes")) {
			// EXTENT REPORT
			
			File repotFile = new File(ExtentReportManager.Filepath);
			System.out.println("reportFile is::" + repotFile);
			 String reportname = repotFile.getName();
			
			
			S3Adapter s3Adapter = new S3Adapter();
			boolean isStoreSuccess = false;
			try {
				isStoreSuccess = s3Adapter.putObject(ConfigManager.getS3Account(), BaseTestCaseFunc.testLevel, null,
						"PmpUi",env+BaseTestCaseFunc.currentModule+data+".html", repotFile);
				
				System.out.println("isStoreSuccess:: " + isStoreSuccess);
			} catch (Exception e) {
				System.out.println("error occured while pushing the object" + e.getLocalizedMessage());
				e.printStackTrace();
			}
			if (isStoreSuccess) {
				System.out.println("Pushed file to S3");
			} else {
				System.out.println("Failed while pushing file to S3");
			}
		}
		
		}
//	@DataProvider(name = "data-provider-partner")
//	public Object[] dpMethod() {
//		String listFilename[] = readFolderJsonList();
//		
//		return listFilename;
//	}
	
	@DataProvider(name = "data-provider-FTM")
	public Object[] ftmDataProvider() {
		String listFilename[] = readFolderJsonList("\\ftm_cert\\");

		return listFilename;
	}
	
	@DataProvider(name = "data-provider-DEVICE-SBI")
	public Object[] deviceSbiDataProvider() {
		String listFilename[] = readFolderJsonList("\\device_sbi_cert\\");

		return listFilename;
	}
	
	
	@DataProvider(name = "data-provider-AUTH")
	public Object[] authDataProvider() {
		String listFilename[] = readFolderJsonList("\\auth_cert\\");

		return listFilename;
	}

	
	public static String[] readFolderJsonList(String str) {
		String contents[] = null;
		try {
			File directoryPath = new File(System.getProperty("user.dir") + str);

			if (directoryPath.exists()) {

				contents = directoryPath.list();
				System.out.println("List of files and directories in the specified directory:");
				for (int i = 0; i < contents.length; i++) {
					System.out.println(contents[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contents;
	}
	
	 private String getCommitId(){
		 
	    	Properties properties = new Properties();
			try (InputStream is = ExtentReportManager.class.getClassLoader().getResourceAsStream("git.properties")) {
				properties.load(is);
				
				return "Commit Id is: " + properties.getProperty("git.commit.id.abbrev") + " & Branch Name is:" + properties.getProperty("git.branch");

			} catch (IOException e) {
//				logger.error(e.getStackTrace());
				return "";
			}
			
	    }
	

//	public static String[] readFolderJsonList() {
//		String contents[] = null;
//		try {
//				
//			File directoryPath = new File(System.getProperty("user.dir") + "\\partner_cert\\");
//
//			if (directoryPath.exists()) {
//
//				contents = directoryPath.list();
//				System.out.println("List of files and directories in the specified directory:");
//				for (int i = 0; i < contents.length; i++) {
//					System.out.println(contents[i]);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return contents;
//	}
}