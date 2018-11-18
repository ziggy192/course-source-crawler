package test;

import constant.AppConstants;
import crawler.*;
import dao.DomainDAO;
import url_holder.TuyenSinhCourseUrlHolder;

import java.util.logging.Logger;

public class TestTuyenSinh {
	private static Logger logger = Logger.getLogger(TestTuyenSinh.class.toString());

	public static void main(String[] args) {
		TuyenSinhMainCrawler.domainId = DomainDAO.getInstance().getDomainByName(AppConstants.TUYENSINH_DOMAIN_NAME).getId();

//		testMain();
//		testGetCourseList();

		testCourseDetail();
	}

	public static void testMain() {
		Runnable crawler = new TuyenSinhMainCrawler();
		CrawlingThreadManager.getInstance().getTuyenSinhExecutor().execute(crawler);

	}

	public static void testGetCourseList() {
		Runnable crawler = new TuyenSinhCourseInEachCategoryPageCrawler(
				5,
				"https://tuyensinh247.com//eHome/loadCourse?list_cat_ids=218"
		);
		CrawlingThreadManager.getInstance().getTuyenSinhExecutor().execute(crawler);

	}

	public static void testCourseDetail() {
		//https://tuyensinh247.com//ngu-van-6-k667.html
		Runnable crawler = new TuyenSinhCourseDetailCrawler(
				new TuyenSinhCourseUrlHolder(
						"https://images.tuyensinh247.com/picture/2018/0825/600-imggv9_4.png"
						, "https://tuyensinh247.com//ngu-van-6-k667.html"
						, "Cô Tạ Minh Thủy"
						, "https://images.tuyensinh247.com/picture/2018/0825/600-imggv9_4.png"
				)
				, 5
		);
		CrawlingThreadManager.getInstance().getTuyenSinhExecutor().execute(crawler);

	}

}
