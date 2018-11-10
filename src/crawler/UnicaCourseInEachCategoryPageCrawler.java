package crawler;

import config.ConfigManager;
import config.model.SignType;
import url_holder.UnicaCourseUrlHolder;
import util.ParserUtils;
import util.StringUtils;
import java.util.logging.Logger;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UnicaCourseInEachCategoryPageCrawler implements Runnable {

	public static Logger logger = Logger.getLogger(UnicaCourseInEachCategoryPageCrawler.class.toString());

	private String categoryPageUrl;
	private int categoryId;

	public UnicaCourseInEachCategoryPageCrawler(String categoryPageUrl, int categoryId) {
		this.categoryPageUrl = categoryPageUrl;
		this.categoryId = categoryId;
	}


	@Override
	public void run() {

		logger.info("start thread");
		try {
			List<UnicaCourseUrlHolder> courseListFromEachPage = getCourseListFromEachPage(categoryPageUrl);
			for (UnicaCourseUrlHolder courseUrlHolder : courseListFromEachPage) {
				logger.info(courseUrlHolder.toString());
				//check suspend

				CrawlingThreadManager.getInstance().checkSuspendStatus();

				UnicaCourseDetailCrawler unicaCourseDetailCrawler = new UnicaCourseDetailCrawler(courseUrlHolder, categoryId);
				CrawlingThreadManager.getInstance().getExecutor().execute(unicaCourseDetailCrawler);


			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("END THREAD");
	}

	public static List<UnicaCourseUrlHolder> getCourseListFromEachPage(String uri) {

//		String uri = "https://edumall.vn/courses/filter&page=2";


		SignType courseListSign = ConfigManager.getInstance().getConfigModel().getUnica().getCourseListSign();

		String beginSign = courseListSign.getBeginSign();
		String endSign = courseListSign.getEndSign();

//		String beginSign = "<div class=\"u-all-course\"";
//		String endSign = "<div class=\"u-number-page\">";


		String htmlContent = ParserUtils.parseHTML(uri, beginSign, endSign);
		htmlContent = ParserUtils.addMissingTag(htmlContent);

		logger.info(htmlContent);

		List<UnicaCourseUrlHolder> courseList = new ArrayList<>();

		boolean insideCourseListSection = false;
		try {
			XMLEventReader staxReader = ParserUtils.getStaxReader(htmlContent);
			UnicaCourseUrlHolder courseUrlHolder = null;
			while (staxReader.hasNext()) {
				XMLEvent event = staxReader.nextEvent();


				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					Iterator<Attribute> attributes = startElement.getAttributes();


					//todo check inside courseListDiv
					//<div class='list-courses-filter'
					if (!insideCourseListSection) {
						{
							if (startElement.getName().getLocalPart().equals("section")) {
								insideCourseListSection = true;
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
					if (insideCourseListSection) {


						//todo traverse each div with  <div class='col-4'>
						if (startElement.getName().getLocalPart().equals("div")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "box-pop")) {
//							courseCount++;
//							XMLEvent articleElement = nextStartEvent(staxReader, "article");
							if (courseUrlHolder != null) {
								logger.info(String.format("course number=%s || CourseHolder=%s", courseList.size() - 1, courseUrlHolder));
							}
							courseUrlHolder = new UnicaCourseUrlHolder();
							courseList.add(courseUrlHolder);

						}

						//todo get thumbnail image

						if (startElement.getName().getLocalPart().equals("div")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "img-course")) {
							startElement = ParserUtils.nextStartEvent(staxReader, "img").asStartElement();
							String src = ParserUtils.getAttributeByName(startElement, "src");

							courseUrlHolder.setCourseThumbnailUrl(src);
							logger.info(String.format("course number=%s || thumnail=%s", courseList.size() - 1, src));
						}

						//todo get course URL
						if (startElement.getName().getLocalPart().equals("a")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "course-box-slider")) {
							String href = ParserUtils.getAttributeByName(startElement, "href");

							courseUrlHolder.setCourseUrl(href);
							logger.info(String.format("course number=%s || courseURL=%s", courseList.size() - 1, href));

						}

						//todo get cost
						if (startElement.getName().getLocalPart().equals("span")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "price-a")) {
							String costStr = ParserUtils.getContentAndJumpToEndElement(staxReader, startElement);

							if (!costStr.isEmpty()) {
								double costValue = StringUtils.getNumberValueFromString(costStr);
								courseUrlHolder.setCost(costValue);
								logger.info(String.format("course number=%s || cost=%s", courseList.size() - 1, costValue));

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
