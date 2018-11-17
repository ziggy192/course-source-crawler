package crawler;

import com.sun.xml.internal.stream.events.StartElementEvent;
import config.ConfigManager;
import config.model.SignType;
import url_holder.BaseCourseUrlHolder;
import url_holder.EdumallCourseUrlHolder;
import util.StaxParserUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class KHOLCourseInEachCategoryPageCrawler implements Runnable {

	private static Logger logger = Logger.getLogger(KHOLCourseInEachCategoryPageCrawler.class.toString());

	private int categoryId;
	private String categoryPageUrl;

	public KHOLCourseInEachCategoryPageCrawler(int categoryId, String categoryPageUrl) {
		this.categoryId = categoryId;
		this.categoryPageUrl = categoryPageUrl;
	}

	@Override
	public void run() {
		logger.info("start thread");
		try {
			List<BaseCourseUrlHolder> courseListFromEachPage = getCourseListFromEachPage(categoryPageUrl);
			for (BaseCourseUrlHolder courseUrlHolder : courseListFromEachPage) {

				KHOLCourseDetailCrawler courseDetailCrawler = new KHOLCourseDetailCrawler(courseUrlHolder, categoryId);

				CrawlingThreadManager.getInstance().checkSuspendStatus();

				CrawlingThreadManager.getInstance().getkHOLExcecutor().execute(courseDetailCrawler);


			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("END THREAD");
	}

	public List<BaseCourseUrlHolder> getCourseListFromEachPage(String uri) {
		ArrayList<BaseCourseUrlHolder> courseUrlHolders = new ArrayList<>();

		try {
			SignType signType = ConfigManager.getInstance().getConfigModel().getKhoaHocOnline().getCourseListSign();

			String beginSign = signType.getBeginSign();
			String endSign = signType.getEndSign();
			String htmlContent = StaxParserUtils.parseHtml(categoryPageUrl, beginSign, endSign);

			htmlContent = StaxParserUtils.addMissingTag(htmlContent);
			htmlContent = StaxParserUtils.removeEmptyAttributes(htmlContent);

			XMLEventReader staxReader = StaxParserUtils.getStaxReader(htmlContent);
			BaseCourseUrlHolder urlHolder = new BaseCourseUrlHolder();
			while (staxReader.hasNext()) {
				XMLEvent event = staxReader.nextEvent();
				if (event.isStartElement()) {

					StartElement startElement = event.asStartElement();

					if (startElement.getName().getLocalPart().equals("div")
							&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "course-item")) {
						urlHolder = new BaseCourseUrlHolder();
						courseUrlHolders.add(urlHolder);
					}
					if (startElement.getName().getLocalPart().equals("a")
							&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "thumb")) {
						String href = StaxParserUtils.getAttributeByName(startElement, "href");
						urlHolder.setCourseUrl(href);

						StartElement imgStartEvent = StaxParserUtils.nextStartEvent(staxReader, "img").asStartElement();
						String src = StaxParserUtils.getAttributeByName(imgStartEvent, "src");
						urlHolder.setCourseThumbnailUrl(src);


					}


				}
			}


		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();

		}

		return courseUrlHolders;
	}
}
