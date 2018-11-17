package crawler;

import config.CategoryMapper;
import config.ConfigManager;
import config.model.CategoryNameType;
import config.model.SignType;
import constant.AppConstants;
import dao.CategoryDAO;
import dao.DomainDAO;
import entity.DomainEntity;
import url_holder.CategoryUrlHolder;
import util.AppUtils;
import util.StaxParserUtils;
import util.StringUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class KHOLMainCrawler implements Runnable {
	public static Logger logger = Logger.getLogger(KHOLMainCrawler.class.toString());

	public static int domainId = 0;


	public static void getDebuggingHtmlFile() {
		File kholFile = AppUtils.getFileWithRealPath("html-crawled/khoahoconline.vn.html");
		StaxParserUtils.saveHtmlToFile("https://khoahoc.online/", kholFile);

	}

	public List<CategoryUrlHolder> getCategories() {

		ArrayList<CategoryUrlHolder> categoryUrlHolders = new ArrayList<>();

		SignType categoryListSign = ConfigManager.getInstance().getConfigModel().getKhoaHocOnline().getCategoryListSign();

		String beginSign = categoryListSign.getBeginSign();
		String endSign = categoryListSign.getEndSign();

//		String htmlContent = StaxParserUtils.parseDebuggingHtml(AppUtils.getFileWithRealPath("html-crawled/khoahoconline.vn.html")
//				, beginSign, endSign);


		String htmlContent = StaxParserUtils.parseHtml(ConfigManager.getInstance().getConfigModel().getKhoaHocOnline().getDomainUrl()
				, beginSign, endSign);

		htmlContent = StaxParserUtils.addMissingTag(htmlContent);

		logger.info(htmlContent);

		try {

			XMLEventReader staxReader = StaxParserUtils.getStaxReader(htmlContent);
			while (staxReader.hasNext()) {
				XMLEvent event = staxReader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					if (startElement.getName().getLocalPart().equals("a")) {
						String href = startElement
								.getAttributeByName(new QName("href")).getValue();

						href = StringUtils.beautifyUrl(href, ConfigManager.getInstance().getConfigModel().getKhoaHocOnline().getDomainUrl());
						String categoryName = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
						CategoryUrlHolder categoryUrlHolder = new CategoryUrlHolder(categoryName, href);
						categoryUrlHolders.add(categoryUrlHolder);
						logger.info(categoryUrlHolder.toString());


					}
				}

			}


		} catch (Exception e) {
			e.printStackTrace();

		}
		return categoryUrlHolders;

	}

	@Override
	public void run() {
		logger.info("start thread");

		try {
			DomainDAO.getInstance().insertDomainIfNotExist(
					new DomainEntity(AppConstants.KHOL_DOMAIN_NAME,
							ConfigManager.getInstance().getConfigModel().getKhoaHocOnline().getDomainUrl())
			);

			domainId = DomainDAO.getInstance().getDomainByName(AppConstants.KHOL_DOMAIN_NAME).getId();


			List<CategoryUrlHolder> categories = getCategories();

			for (CategoryUrlHolder categoryUrlHolder : categories) {

				//map edumall category name -> my general category name -> categoryId
				try {
					String categoryName = categoryUrlHolder.getCategoryName();

					CategoryNameType categoryNameType = CategoryMapper.getInstance().mapKhoaHocOnline(categoryName);

					//get categoryId from database
					int categoryId = CategoryDAO.getInstance().getCategoryId(categoryNameType);


					KHOLEachCategoryCrawler kholEachCategoryCrawler = new KHOLEachCategoryCrawler(categoryId, categoryUrlHolder.getCategoryURL());
					CrawlingThreadManager.getInstance().getkHOLExcecutor().execute(kholEachCategoryCrawler);
				} catch (Exception e) {
				}


				//check is suspend
				synchronized (CrawlingThreadManager.getInstance()) {
					while (CrawlingThreadManager.getInstance().isSuspended()) {
						CrawlingThreadManager.getInstance().wait();
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();

		}


	}
}
