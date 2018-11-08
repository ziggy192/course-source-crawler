package crawler;

import util.ParserUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;
import java.util.logging.Logger;

public class EdumallEachCategoryCrawler implements Runnable{

	public static Logger logger = Logger.getLogger(EdumallEachCategoryCrawler.class.toString());

	private int categoryId;
	private String categoryUrl;

	public EdumallEachCategoryCrawler(int categoryId, String categoryUrl) {
		this.categoryId = categoryId;
		this.categoryUrl = categoryUrl;
	}

	private int getTotalPageForEachCategory(String categoryUrl) {

//		String categoryUrl = urlHolder.getCategoryURL();

		String beginSign = "class='list-paginate'";
		String endSign = "form class='form-paginate form-inline'";

		String htmlContent = ParserUtils.parseHTML(categoryUrl, beginSign, endSign);

		htmlContent = ParserUtils.addMissingTag(htmlContent);
		System.out.println(htmlContent);
		int pageCount = 1;
		try {
			XMLEventReader staxReader = ParserUtils.getStaxReader(htmlContent);
			boolean insidePaginationDiv = false;
			while (staxReader.hasNext()) {
				XMLEvent event = staxReader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					Iterator<Attribute> attributes = startElement.getAttributes();

					if (!insidePaginationDiv) {
						if (startElement.getName().getLocalPart().equals("div")) {

							Attribute attributeClass = startElement.getAttributeByName(new QName("class"));
							if (attributeClass != null && attributeClass.getValue().equals("pagination")) {
								insidePaginationDiv = true;
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
//					}
					if (insidePaginationDiv) {

						//todo get all <em class="current"> or <a href="...&page=[number]> content


						if (ParserUtils.checkAttributeContainsKey(startElement, "class", "current")
								|| ParserUtils.getAttributeByName(startElement, "href").contains("page=")
						) {

							String pageContent = ParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
							try {
								int pageNumber = Integer.parseInt(pageContent);

								pageCount = Math.max(pageCount, pageNumber);
							} catch (NumberFormatException e) {
//								e.printStackTrace();
								//not a page number
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
			logger.info("totalPageNumber="+pageNumber);

			//for each page
			if (categoryUrl.contains("page=")) {
				categoryUrl = categoryUrl.substring(0,categoryUrl.indexOf("page=")-1); //?page= or &page=

			}

			synchronized (CrawlingThreadManager.getInstance()) {
				while (CrawlingThreadManager.getInstance().isSuspended()) {
					CrawlingThreadManager.getInstance().wait();
				}
			}


			for (int i = 1; i <= pageNumber; i++) {
				String eachPageUri = categoryUrl + "&page=" + i;

				EdumallCourseInEachCategoryPageCrawler edumallCourseInEachCategoryPageCrawler = new EdumallCourseInEachCategoryPageCrawler(eachPageUri, categoryId);
				//todo thread execute
				CrawlingThreadManager.getInstance().getExecutor().execute(edumallCourseInEachCategoryPageCrawler);

//				courseInEachCategoryPageCrawler.start();


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
