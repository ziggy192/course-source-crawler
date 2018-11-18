package crawler;

import config.ConfigManager;
import org.json.JSONObject;
import org.json.XML;
import url_holder.TuyenSinhCourseUrlHolder;
import util.AppUtils;
import util.StaxParserUtils;
import util.StringUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TuyenSinhCourseInEachCategoryPageCrawler implements Runnable {

	private static Logger logger = Logger.getLogger(TuyenSinhCourseInEachCategoryPageCrawler.class.toString());


	private int categoryId;
	private String categoryPageUrl;

	public TuyenSinhCourseInEachCategoryPageCrawler(int categoryId, String categoryPageUrl) {
		this.categoryId = categoryId;
		this.categoryPageUrl = categoryPageUrl;
	}

	@Override
	public void run() {
		logger.info("start thread");
		try {
			List<TuyenSinhCourseUrlHolder> courseListFromEachPage = getCourseList(categoryPageUrl);
			for (TuyenSinhCourseUrlHolder courseUrlHolder : courseListFromEachPage) {
				logger.info(courseUrlHolder.toString());
				TuyenSinhCourseDetailCrawler courseDetailCrawler = new TuyenSinhCourseDetailCrawler(courseUrlHolder, categoryId);

				CrawlingThreadManager.getInstance().checkSuspendStatus();

				CrawlingThreadManager.getInstance().getTuyenSinhExecutor().execute(courseDetailCrawler);


			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("END THREAD");
	}

	public List<TuyenSinhCourseUrlHolder> getCourseList(String apiUrl) {
		ArrayList<TuyenSinhCourseUrlHolder> courseUrlHolders = new ArrayList<>();

		TuyenSinhCourseUrlHolder courseUrlHolder = new TuyenSinhCourseUrlHolder();
		try {
			String parsed = StaxParserUtils.parseXml(apiUrl);
			JSONObject object = new JSONObject(parsed);
			String xmlContent = XML.toString(object);
			xmlContent = "<root>" + xmlContent + "</root>";
			//todo debugging
//			StaxParserUtils.saveStringToFile(xmlContent,
//					AppUtils.getFileWithRealPath("html-crawled/testTuyensinh.xml"));

			XMLEventReader staxReader = StaxParserUtils.getStaxReader(xmlContent);
			boolean inTeachers = false;

			while (staxReader.hasNext()) {
				XMLEvent event = staxReader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();

					if (startElement.getName().getLocalPart().equals("data")) {
						courseUrlHolder = new TuyenSinhCourseUrlHolder();
						courseUrlHolders.add(courseUrlHolder);
					}

					if (startElement.getName().getLocalPart().equals("link_course")) {
						String linkCourse = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
						linkCourse = StringUtils.beautifyUrl(linkCourse,
								ConfigManager.getInstance().getConfigModel().getTuyenSinh247().getDomainUrl());
						courseUrlHolder.setCourseUrl(linkCourse);

					}
					if (startElement.getName().getLocalPart().equals("origin_avatar")) {
						String thumbnailURL = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
						thumbnailURL = StringUtils.beautifyUrl(thumbnailURL,
								ConfigManager.getInstance().getConfigModel().getTuyenSinh247().getDomainUrl());
						courseUrlHolder.setCourseThumbnailUrl(thumbnailURL);
						courseUrlHolder.setAuthorImageUrl(thumbnailURL);
					}

					if (startElement.getName().getLocalPart().equals("teachers")) {
						inTeachers = true;
						startElement = StaxParserUtils.nextStartEvent(staxReader, "title").asStartElement();
						String authorName = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
						courseUrlHolder.setAuthorName(authorName);
					}
				}

				if (event.isEndElement()) {
					EndElement endElement = event.asEndElement();
					if (endElement.getName().getLocalPart().equals("teachers")) {
						inTeachers = false;
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
