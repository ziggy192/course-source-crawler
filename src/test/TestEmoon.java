package test;

import constant.AppConstants;
import crawler.*;
import dao.DomainDAO;
import url_holder.BaseCourseUrlHolder;

import java.util.logging.Logger;

public class TestEmoon {
	private static Logger logger = Logger.getLogger(TestEmoon.class.toString());

	public static void main(String[] args) {
		EmoonMainCrawler.domainId = DomainDAO.getInstance().getDomainByName(AppConstants.EMOON_DOMAIN_NAME).getId();

		testEmoonMain();
//		testCourseCategoryCrawler();
//		testCourseDetail();
	}

	public static void testEmoonMain() {
		EmoonMainCrawler crawler = new EmoonMainCrawler();
		CrawlingThreadManager.getInstance().getEmoonExecutor().execute(crawler);

	}

	public static void testCourseCategoryCrawler() {
		EmoonCourseInEachCategoryPageCrawler crawler = new EmoonCourseInEachCategoryPageCrawler(5, "https://emoon.vn/courses/luyen-thi-TOEIC");
		CrawlingThreadManager.getInstance().getEmoonExecutor().execute(crawler);

	}

	public static void testCourseDetail() {
//		https://emoon.vn/khoa-hoc/10-de-thi-toeic-1506
		Runnable crawler = new EmoonCourseDetailCrawler(
				new BaseCourseUrlHolder("https://emoon.vn/BaiGiangVideo/Image/1464.png", "https://emoon.vn/khoa-hoc/luyen-nghe-toeic-tu-con-so-0-1464")
				, 5);
		CrawlingThreadManager.getInstance().getEmoonExecutor().execute(crawler);


	}

}
