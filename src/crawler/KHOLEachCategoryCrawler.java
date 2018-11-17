package crawler;

import config.ConfigManager;
import config.model.SignType;
import util.StaxParserUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.logging.Logger;

public class KHOLEachCategoryCrawler implements Runnable {
	private static final Logger logger = Logger.getLogger(KHOLEachCategoryCrawler.class.toString());


	private int categoryId;
	private String categoryUrl;

	public KHOLEachCategoryCrawler(int categoryId, String categoryUrl) {
		this.categoryId = categoryId;
		this.categoryUrl = categoryUrl;
	}

	private int getTotalPages() {
		int totalPages = 1;


		try {
			SignType signType = ConfigManager.getInstance().getConfigModel().getKhoaHocOnline().getPaginationSign();

			String beginSign = signType.getBeginSign();
			String endSign = signType.getEndSign();

			String htmlContent = StaxParserUtils.parseHtml(categoryUrl, beginSign, endSign);

			htmlContent = StaxParserUtils.addMissingTag(htmlContent);
			logger.info(htmlContent);

			XMLEventReader staxReader = StaxParserUtils.getStaxReader(htmlContent);
			while (staxReader.hasNext()) {
				XMLEvent event = staxReader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();

					if (!startElement.getName().getLocalPart().equals("ul") &&
							StaxParserUtils.checkAttributeContainsKey(startElement, "class", "page-numbers")) {
						String pageContent = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);

						try {
							int pageNumber = Integer.parseInt(pageContent);
							totalPages = Math.max(totalPages, pageNumber);
						} catch (NumberFormatException e) {
							//do nothing
						}
					}

				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return totalPages;

	}

	@Override
	public void run() {
		logger.info("Start Thread");
		try {
			int totalPage = getTotalPages();
//			logger.info("totalPage=" + totalPage);
			CrawlingThreadManager.getInstance().checkSuspendStatus();

			for (int page = 1; page <= totalPage; page++) {
				String courseInEachPageUrl = categoryUrl + "page/" + page + "/";
				KHOLCourseInEachCategoryPageCrawler inEachCategoryPageCrawler = new KHOLCourseInEachCategoryPageCrawler(categoryId, courseInEachPageUrl);
				CrawlingThreadManager.getInstance().getkHOLExcecutor().execute(inEachCategoryPageCrawler);


				CrawlingThreadManager.getInstance().checkSuspendStatus();

			}
			CrawlingThreadManager.getInstance().checkSuspendStatus();
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("End Thread");

	}
}
