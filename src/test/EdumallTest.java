package test;

import crawler.CrawlingThreadManager;
import crawler.EdumallCourseDetailCrawler;
import crawler.EdumallCourseInEachCategoryPageCrawler;
import crawler.EdumallMainCrawler;
import dao.DomainDAO;
import entity.CategoryEntity;
import entity.DomainEntity;
import url_holder.CategoryUrlHolder;
import url_holder.CourseUrlHolder;
import util.DBUtils;


import javax.persistence.EntityManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.logging.Logger;

public class EdumallTest {

	private static Logger logger = Logger.getLogger(EdumallTest.class.toString());

	public static void main(String[] args) throws UnsupportedEncodingException {
//		testInsertDomain(constant.AppConstants.EDUMALL_DOMAIN_NAME, constant.AppConstants.EDUMALL_DOMAIN);
//		testValidate();

		testGetCourseDetail();

	}


	public static void testEdumallGetCategory() {
		EdumallMainCrawler crawler = new EdumallMainCrawler();
		List<CategoryUrlHolder> categories = crawler.getCategories();
	}
	public static void testEdumall() {
		EdumallMainCrawler edumallMainCrawler = new EdumallMainCrawler();
		CrawlingThreadManager.getInstance().getExecutor().execute(edumallMainCrawler);
	}


	private static void testGetDomainByName(String domainName) {
		DomainEntity domainByName = DomainDAO.getInstance().getDomainByName(domainName);
		System.out.println(domainByName);


	}
	private static void testJDBCConnection() {
		String jdbcURL = "jdbc:mysql://localhost:3306/course_source";
		String user = "root";
		String password = "12345678";
		try (Connection con = DriverManager.getConnection(jdbcURL, user, password)) {

			System.out.println("Connected");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to connect");

		}


	}


	private static void testGetCourseListFromEachPage() {

		String uri = "https://edumall.vn/courses/filter?categories[]=phong-thuy-nhan-tuong-hoc";


		new Thread(new EdumallCourseInEachCategoryPageCrawler(uri, 1)).start();

	}

	public static void testGetCourseDetail() {

		CourseUrlHolder dummyCourseUrlHolder = new CourseUrlHolder("Hướng dẫn chi tiết giảm cân an toàn tại nhà"
				, "https://d1nzpkv5wwh1xf.cloudfront.net/640/k-57b67d6e60af25054a055b20/20180619-a301906/luyen-tap-giam-can.jpg"
				, "edumall.vn/course/huong-dan-chi-tiet-giam-can-an-toan-tai-nha");
//				, "https://edumall.vn/course/dao-tao-ky-thuat-truong-cửa-hang-gas");
//				, "https://edumall.vn/course/tao-slide-trinh-bay-an-tuong-voi-prezi-google-trinh-chieu-va-power-point");

		EdumallMainCrawler.domainId = 3;

		new Thread(new EdumallCourseDetailCrawler(dummyCourseUrlHolder, 15)).start();

//		getCourseDetail(dummyCourseUrlHolder);

	}


	public static void testValidateCourse() {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(CategoryEntity.class);
			CategoryEntity category = new CategoryEntity();
			category.setId(1);
			category.setName("Luyện thi");


			ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

			jaxbContext.createMarshaller().marshal(category, byteOutputStream);
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());

//			other.DummyDatabase.validateXMLBeforeSaveToDB(byteInputStream);
		} catch (JAXBException e) {
			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
		}

	}


	public static void testInsertDomain(String domainName, String domainURL) {


		EntityManager entitymanager = DBUtils.getEntityManager();
		entitymanager.getTransaction().begin();

		DomainEntity domainEntity = new DomainEntity();
		domainEntity.setDomainUrl(domainURL);
		domainEntity.setName(domainName);

		entitymanager.persist(domainEntity);
		entitymanager.getTransaction().commit();

		entitymanager.close();
	}
}
