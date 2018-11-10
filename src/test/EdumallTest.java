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

//		testGetCourseDetail();

//		testEdumall();
//		testGetCourseListFromEachPage();
//		testEdumallGetCategory();

		testEdumall();
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

		CourseUrlHolder dummyCourseUrlHolder = new CourseUrlHolder("GIÁO DỤC SỚM CHO TRẺ THEO PHƯƠNG PHÁP  GLENN DOMAN: NHẬN BIẾT THẾ GIỚI XUNG Q..."
				, "d1nzpkv5wwh1xf.cloudfront.net/320/k-577a160c047c994bb7e5b397/20180702-/teach-baby-by-sweet-mother.jpg"
				, "edumall.vn/course/giao-duc-som-cho-tre-theo-phuong-phap--glenn-doman-nhan-biet-the-gioi-xung-quanh");
//				, "https://edumall.vn/course/dao-tao-ky-thuat-truong-cửa-hang-gas");
//				, "https://edumall.vn/course/tao-slide-trinh-bay-an-tuong-voi-prezi-google-trinh-chieu-va-power-point");

		EdumallMainCrawler.domainId = 3;

		new Thread(new EdumallCourseDetailCrawler(dummyCourseUrlHolder, 5)).start();

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
