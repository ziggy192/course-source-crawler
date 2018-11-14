package test;

import crawler.*;
import url_holder.UnicaCourseUrlHolder;
import util.StaxParserUtils;

public class UnicaTest {
	public static void main(String[] args) {
//	testGetAllCategories();
//		testGetCourseForEachCategoryPage();
//		testGetCourseDetail();
//testRemoveComments();
//		testUnicaGetCategory();
		testGetCourseDetail();
		testGetCourseForEachCategoryPage();
		testUnicaGetCategory();
		testUnicaMain();

	}


	public static void testUnicaGetCategory() {
		UnicaMainCrawler crawler = new UnicaMainCrawler();
		crawler.getCategories();
	}


	public static void testUnicaMain() {
		Thread unicaMainCrawler = new Thread(new UnicaMainCrawler());
		CrawlingThreadManager.getInstance().getExecutor().execute(unicaMainCrawler);

	}

	public static void testRemoveComments() {

		String content = "<div class=\"panel panel-default\">                                                    <!-- phần -->\n" +
				"                    <div class=\"panel-heading\">\n" +
				"                        <div class=\"row\">\n" +
				"                            <div class=\"col-md-12\"><h4 class=\"panel-title\"><a data-toggle=\"collapse\"\n" +
				"                                                                              data-parent=\"#accordion\" href=\"#collapse1\"\n" +
				"                                                                              class=\"\" aria-expanded=\"true\"><i\n" +
				"                                    class=\"fa fa-minus-square\" aria-hidden=\"true\"></i> Phần 1: Làm quen với máy ảnh</a>\n" +
				"                            </h4></div>\n" +
				"                        </div>\n" +
				"                    </div>                                                    <!-- bài -->\n" +
				"                    <div id=\"collapse1\" class=\"panel-collapse collapse in \" aria-expanded=\"true\">\n" +
				"                        <div class=\"panel-body\">\n" +
				"                            <div class=\"col\">\n" +
				"                                <div class=\"container-fluid\">";
//String content = "<html>\n" +
//				"<head>\n" +
//				"\n" +
//				"</head>\n" +
//				"<body>\n" +
//				"<!--asdfasdf asf asdf sadf saf dasfd asdf asd fdas dfas dfas df asf\n" +
//				"asfsadfasfasfdasd\n" +
//				"fdsa\n" +
//				"fasd\n" +
//				"fasd\n" +
//				"\n" +
//				"<h1>asdfasdf<2asdfas>\n" +
//				"f\n" +
//				"asdf\n" +
//				"asdf\n" +
//				"asdf\n" +
//				"asd\n" +
//				"f-->\n" +
//				"\n" +
//				"<!--                                        <li><i class=\"fa fa-star\" aria-hidden=\"true\"></i> <span>4.6</span> đánh giá</li>                                        -->\n" +
//				"\n" +
//				"<h1>asdfasfdasdfasdfsadf</h1>\n" +
//				"</body>\n" +
//				"</html>";

		System.out.println(StaxParserUtils.removeComment(content));
	}

	public static void testGetCourseDetail() {
		UnicaCourseUrlHolder holder = new UnicaCourseUrlHolder();
		holder.setCourseUrl("https://unica.vn/hoc-nhiep-anh-tu-co-ban-den-nang-cao");
		holder.setCourseThumbnailUrl("https://static.unica.vn/uploads/images/linhntd@unica.vn/athang_m.png");
		holder.setCost(599000.0);

		Thread courseDetailCrawler = new Thread(new UnicaCourseDetailCrawler(holder, 6));
		UnicaMainCrawler.domainId = 4;
		CrawlingThreadManager.getInstance().getExecutor().execute(courseDetailCrawler);
	}

	public static void testGetCourseForEachCategoryPage() {


		Thread courseInEachCategoryPageCrawler = new Thread(
				new UnicaCourseInEachCategoryPageCrawler("https://unica.vn/course/nhiep-anh-dung-phim"
						, 5));
		//todo thread execute
//		courseInEachCategoryPageCrawler.start();
		CrawlingThreadManager.getInstance().getExecutor().execute(courseInEachCategoryPageCrawler);

	}
}
