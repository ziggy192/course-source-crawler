package crawler;

import util.ParserUtils;
import java.util.logging.Logger;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class UnicaEachCategoryCrawler implements Runnable{
	public static Logger logger = Logger.getLogger(UnicaEachCategoryCrawler.class.toString());

	private int categoryId;
	private String categoryUrl;

	public UnicaEachCategoryCrawler(int categoryId, String categoryUrl) {
		this.categoryId = categoryId;
		this.categoryUrl = categoryUrl;
	}

	private int getTotalPageForEachCategory(String categoryUrl) {

//		String categoryUrl = urlHolder.getCategoryURL();

		String beginSign = "<ul class=\"pagination\"";
		String endSign = "<li class=\"next\"";

		String htmlContent = ParserUtils.parseHTML(categoryUrl, beginSign, endSign);

		htmlContent = ParserUtils.addMissingTag(htmlContent);
		int pageCount = 1;
		try {
			XMLEventReader staxReader = ParserUtils.getStaxReader(htmlContent);
			while (staxReader.hasNext()) {
				XMLEvent event = staxReader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					if (startElement.getName().getLocalPart().equals("a")
					) {
						String href = ParserUtils.getAttributeByName(startElement, "href");
						if (href.contains("page=")) {
							String pageContent = ParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
							try {
								int pageNumber = Integer.parseInt(pageContent);

								pageCount = Math.max(pageCount, pageNumber);
							} catch (NumberFormatException e) {
							}
						}
					}
				}

			}

		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return pageCount;
	}

	@Override
	public void run() {
		logger.info("start thread");
		logger.info(categoryUrl);

		try {
			int pageNumber = getTotalPageForEachCategory(categoryUrl);
			logger.info(String.format("totalPageNumber=%d | categoryUrl=%s", pageNumber,categoryUrl));

			//for each page
			if (categoryUrl.contains("page=")) {
				categoryUrl = categoryUrl.substring(0,categoryUrl.indexOf("page=")-1); //?page= or &page=
			}

			synchronized (CrawlingThreadManager.getInstance()) {
				while (CrawlingThreadManager.getInstance().isSuspended()) {
					CrawlingThreadManager.getInstance().wait();
				}
			}

//
			for (int i = 1; i <= pageNumber; i++) {
				String eachPageUri = categoryUrl + "?page=" + i;
				logger.info(String.format("pageNumber=%s | pageURL=%s", pageNumber,eachPageUri));
				UnicaCourseInEachCategoryPageCrawler unicaCourseInEachCategoryPageCrawler = new UnicaCourseInEachCategoryPageCrawler(eachPageUri, categoryId);
				//todo thread execute
				CrawlingThreadManager.getInstance().getExecutor().execute(unicaCourseInEachCategoryPageCrawler);

				//check issuspend
				synchronized (CrawlingThreadManager.getInstance()) {
					while (CrawlingThreadManager.getInstance().isSuspended()) {
						CrawlingThreadManager.getInstance().wait();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		logger.info("END THREAD");

	}
}
