package test;

import config.ConfigManager;
import constant.AppConstants;
import crawler.*;
import dao.DomainDAO;
import url_holder.BaseCourseUrlHolder;
import util.AppUtils;
import util.StaxParserUtils;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class KHOLTest {
	private static Logger logger = Logger.getLogger(KHOLTest.class.toString());

	public static void main(String[] args) {
		KHOLMainCrawler.domainId = DomainDAO.getInstance().getDomainByName(AppConstants.KHOL_DOMAIN_NAME).getId();

//		KHOLMainCrawler.getDebuggingHtmlFile();
//		testKHOLMainCrawler();
//		testKHOLEachCategory();
//		testGetCourseList();
//		StaxParserUtils.saveHtmlToFile("https://khoahoc.online/courses/typescript-es6-javascript-shopping-cart-nen-tang-node-js-va-angularjs-2/"
//				,AppUtils.getFileWithRealPath("html-crawled/kholCourseDetailJavascript.html")
//				);
//		testGetCourseDetail();
		testKHOLMainCrawler();
	}

	public static void testKHOLMainCrawler() {
		KHOLMainCrawler kholMainCrawler = new KHOLMainCrawler();
		CrawlingThreadManager.getInstance().getkHOLExcecutor().execute(kholMainCrawler);

	}

	public static void testKHOLEachCategory() {
		KHOLEachCategoryCrawler kholEachCategoryCrawler = new KHOLEachCategoryCrawler(12, "https://khoahoc.online/course-category/cong-nghe-doi-song/");
		CrawlingThreadManager.getInstance().getkHOLExcecutor().execute(kholEachCategoryCrawler);

	}

	public static void testGetCourseList() {
		KHOLCourseInEachCategoryPageCrawler inEachCategoryPageCrawler = new KHOLCourseInEachCategoryPageCrawler(12, "https://khoahoc.online/course-category/cong-nghe-doi-song/");
		List<BaseCourseUrlHolder> courseListFromEachPage = inEachCategoryPageCrawler.getCourseListFromEachPage("https://khoahoc.online/course-category/cong-nghe-doi-song/page/1/");
		for (BaseCourseUrlHolder baseCourseUrlHolder : courseListFromEachPage) {
			logger.info(baseCourseUrlHolder.toString());

		}

	}

	public static void testGetCourseDetail() {
		KHOLCourseDetailCrawler kholCourseDetailCrawler = new KHOLCourseDetailCrawler(new BaseCourseUrlHolder("https://khoahoc.online/wp-content/uploads/2018/05/lap-trinh-typescript-toan-tap-400x320.jpg",
				"https://khoahoc.online/courses/typescript-es6-javascript-shopping-cart-nen-tang-node-js-va-angularjs-2/"), 12);

		CrawlingThreadManager.getInstance().getkHOLExcecutor().execute(kholCourseDetailCrawler);

	}



}
