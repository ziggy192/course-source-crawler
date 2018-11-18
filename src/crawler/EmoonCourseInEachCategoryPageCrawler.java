package crawler;

import config.ConfigManager;
import config.model.SignType;
import constant.AppConstants;
import url_holder.BaseCourseUrlHolder;
import util.AppUtils;
import util.StaxParserUtils;
import util.StringUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EmoonCourseInEachCategoryPageCrawler implements Runnable {

	private static Logger logger = Logger.getLogger(EmoonCourseInEachCategoryPageCrawler.class.toString());

	private int categoryId;
	private String categoryPageUrl;

	public EmoonCourseInEachCategoryPageCrawler(int categoryId, String categoryPageUrl) {
		this.categoryId = categoryId;
		this.categoryPageUrl = categoryPageUrl;
	}

	@Override
	public void run() {
		logger.info("start thread");
		try {
			List<BaseCourseUrlHolder> courseListFromEachPage = getCourseListFromEachPage(categoryPageUrl);
			for (BaseCourseUrlHolder courseUrlHolder : courseListFromEachPage) {
				logger.info(courseUrlHolder.toString());
				EmoonCourseDetailCrawler courseDetailCrawler = new EmoonCourseDetailCrawler(courseUrlHolder, categoryId);

				CrawlingThreadManager.getInstance().checkSuspendStatus();

				CrawlingThreadManager.getInstance().getEmoonExecutor().execute(courseDetailCrawler);


			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("END THREAD");
	}


	public List<BaseCourseUrlHolder> getCourseListFromEachPage(String uri) {
		ArrayList<BaseCourseUrlHolder> courseUrlHolders = new ArrayList<>();

		try {
			SignType signType = ConfigManager.getInstance().getConfigModel().getEmoon().getCourseListSign();

			String beginSign = signType.getBeginSign();
			String endSign = signType.getEndSign();
			String htmlContent = StaxParserUtils.parseHtml(uri, beginSign, endSign);

			htmlContent = StaxParserUtils.addMissingTag(htmlContent);
			htmlContent = StaxParserUtils.removeEmptyAttributes(htmlContent);



			//todo dugging uncomment this
			StaxParserUtils.saveStringToFile(htmlContent,
					AppUtils.getFileWithRealPath("html-crawled/emoonCourseList.xml"));

			XMLEventReader staxReader = StaxParserUtils.getStaxReader(htmlContent);
			BaseCourseUrlHolder urlHolder = new BaseCourseUrlHolder();
			while (staxReader.hasNext()) {
				XMLEvent event = staxReader.nextEvent();
				if (event.isStartElement()) {

					StartElement startElement = event.asStartElement();

					if (startElement.getName().getLocalPart().equals("div")
							&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "portfolio-item")) {
						urlHolder = new BaseCourseUrlHolder();
						courseUrlHolders.add(urlHolder);
					}
					if (startElement.getName().getLocalPart().equals("a")
							&& (urlHolder.getCourseUrl() == null || urlHolder.getCourseUrl().isEmpty())) {
						String href = StaxParserUtils.getAttributeByName(startElement, "href");
						href = StringUtils.beautifyUrl(href,
								ConfigManager.getInstance().getConfigModel().getEmoon().getDomainUrl());
						urlHolder.setCourseUrl(href);

					}
					if (startElement.getName().getLocalPart().equals("img")
					&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "card-img-top")) {
						String src = StaxParserUtils.getAttributeByName(startElement, "src");
						src = StringUtils.beautifyUrl(src,
								ConfigManager.getInstance().getConfigModel().getEmoon().getDomainUrl());
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
