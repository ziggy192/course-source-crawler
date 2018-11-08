package crawler;

import constant.AppConstants;
import url_holder.CourseUrlHolder;
import util.ParserUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class EdumallCourseInEachCategoryPageCrawler implements Runnable {


	public static Logger logger = Logger.getLogger(EdumallCourseInEachCategoryPageCrawler.class.toString());

	private String categoryPageUrl;
	private int categoryId;

	public EdumallCourseInEachCategoryPageCrawler(String categoryPageUrl, int categoryId) {
		this.categoryPageUrl = categoryPageUrl;
		this.categoryId = categoryId;
	}

	@Override
	public void run() {
		logger.info("start thread");
		try {
			List<CourseUrlHolder> courseListFromEachPage = getCourseListFromEachPage(categoryPageUrl);
			for (CourseUrlHolder courseUrlHolder : courseListFromEachPage) {
				EdumallCourseDetailCrawler edumallCourseDetailCrawler = new EdumallCourseDetailCrawler(courseUrlHolder, categoryId);

				//check suspend
				synchronized (CrawlingThreadManager.getInstance()) {
					while (CrawlingThreadManager.getInstance().isSuspended()) {
						CrawlingThreadManager.getInstance().wait();
					}
				}

				//todo thread execute
				CrawlingThreadManager.getInstance().getExecutor().execute(edumallCourseDetailCrawler);
//				courseDetailCrawler.start();


			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("END THREAD");


	}

	public static List<CourseUrlHolder> getCourseListFromEachPage(String uri) {

//		String uri = "https://edumall.vn/courses/filter&page=2";

//		String uri = categoryUrlHolder.getCategoryURL();
		String beginSign = "section class='area-display-courses'";
		String endSign = "</section>";
//		String endSign = "form class='form-paginate form-inline'";

		String htmlContent = ParserUtils.parseHTML(uri, beginSign, endSign);
		htmlContent = ParserUtils.addMissingTag(htmlContent);


		List<CourseUrlHolder> courseList = new ArrayList<>();

		boolean insideCourseListDiv = false;
		try {
			XMLEventReader staxReader = ParserUtils.getStaxReader(htmlContent);
			while (staxReader.hasNext()) {
				XMLEvent event = staxReader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					Iterator<Attribute> attributes = startElement.getAttributes();


					//todo check inside courseListDiv
					//<div class='list-courses-filter'
					if (!insideCourseListDiv) {
						{
							if (ParserUtils.checkAttributeContainsKey(startElement, "class", "list-courses-filter")) {
								insideCourseListDiv = true;
							}
						}
					}
//					while (attributes.hasNext()) {
//						Attribute attribute = attributes.next();
//						if (!insideMainMenu) {
//							if (attribute.getName().toString().equals("class") && attribute.getValue().equals("main-menu")) {
//								//inside the main menu div
//
//								insideMainMenu = true;
//							}
//						}
//
//
//
//					}					int d
					if (insideCourseListDiv) {


						//todo traverse each div with  <div class='col-4'>
						if (startElement.getName().getLocalPart().equals("div")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "col-4")) {
//							courseCount++;
//							XMLEvent articleElement = nextStartEvent(staxReader, "article");


							CourseUrlHolder courseUrlHolder = new CourseUrlHolder();
							courseList.add(courseUrlHolder);

						}
						//todo get thumnail image in
						// <div class="course-header img-thumb gtm_section_recommendation"
						// data-src="//d1nzpkv5wwh1xf.cloudfront.net/320/k-57b67d6e60af25054a055b20/20170817-tungnt9image1708/thanhnd04.png"

						if (startElement.getName().getLocalPart().equals("div")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "course-header")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "img-thumb")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "gtm_section_recommendation")) {
							Attribute thumnailAttribute = startElement.getAttributeByName(new QName("data-src"));
							if (thumnailAttribute != null) {

								String thumnaillUrl = thumnailAttribute.getValue();
								if (thumnaillUrl.startsWith("//")) {
									thumnaillUrl = thumnaillUrl.substring(2);
								}
								CourseUrlHolder courseUrlHolder = courseList.get(courseList.size() - 1);
								courseUrlHolder.setCourseThumbnailUrl(thumnaillUrl);
								logger.info(String.format("course number=%s || thumnail=%s", courseList.size() - 1, thumnaillUrl));
							}
						}


						//todo get CourseName
						//	<h5 class="gtm_section_recommendation course-title" xpath="1">Microsoft Word cơ bản và hiệu quả</h5>
						if (startElement.getName().getLocalPart().contains("h")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "gtm_section_recommendation")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "course-title")) {
							String title = ParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
							CourseUrlHolder lastCourse = courseList.get(courseList.size() - 1);
							lastCourse.setCourseName(title);
							logger.info(String.format("course number=%s || courseName =%s", courseList.size() - 1, title));

						}
						//todo get courseUrl
						//<a class='gtm_section_recommendation's

						if (startElement.getName().getLocalPart().equals("a")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "gtm_section_recommendation")) {
							CourseUrlHolder lastCourse = courseList.get(courseList.size() - 1);
							if (lastCourse.getCourseUrl() == null || lastCourse.getCourseUrl().isEmpty()) {
								//check for dupplicate a tags
								Attribute hrefAttribute = startElement.getAttributeByName(new QName("href"));

								if (hrefAttribute != null) {
									String hrefValue = hrefAttribute.getValue();
//									try {
//										hrefValue = URLEncoder.encode(hrefValue, "ISO-8859-1");
//									} catch (UnsupportedEncodingException e) {
//										e.printStackTrace();
//									}

//									try {
//
//										//UTF-8 encode URL
//										URI urimodel = new URI("https", constant.AppConstants.EDUMALL_DOMAIN, hrefValue, null);
//										hrefValue  = urimodel.toASCIIString();
//									} catch (URISyntaxException e) {
//										e.printStackTrace();
//
//									}
									hrefValue = AppConstants.EDUMALL_DOMAIN + hrefValue;
									lastCourse.setCourseUrl(hrefValue);
									logger.info(String.format("course number=%s || coureUrl=%s", courseList.size() - 1, hrefValue));

								}
							}
						}


					}
				}

			}

		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return courseList;
	}

}
