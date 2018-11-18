package crawler;

import config.CategoryMapper;
import config.ConfigManager;
import config.model.CategoryNameType;
import config.model.SignType;
import constant.AppConstants;
import dao.CategoryDAO;
import dao.DomainDAO;
import entity.DomainEntity;
import sun.rmi.runtime.Log;
import url_holder.CategoryUrlHolder;
import util.StaxParserUtils;
import util.StringUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EmoonMainCrawler implements Runnable {
	private static Logger logger = Logger.getLogger(EmoonMainCrawler.class.toString());
	public static int domainId = 0;


	public List<CategoryUrlHolder> getCategories() {


		ArrayList<CategoryUrlHolder> categoryUrlHolders = new ArrayList<>();

		SignType categoryListSign = ConfigManager.getInstance().getConfigModel().getEmoon().getCategoryListSign();

		String beginSign = categoryListSign.getBeginSign();
		String endSign = categoryListSign.getEndSign();


		String htmlContent = StaxParserUtils.parseHtml(ConfigManager.getInstance().getConfigModel().getEmoon().getDomainUrl()
				, beginSign, endSign);

		htmlContent = StaxParserUtils.addMissingTag(htmlContent);


		try {

			XMLEventReader staxReader = StaxParserUtils.getStaxReader(htmlContent);
			while (staxReader.hasNext()) {
				XMLEvent event = staxReader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					if (startElement.getName().getLocalPart().equals("li") &&
							StaxParserUtils.checkAttributeContainsKey(startElement, "class", "nav-item")) {
						if (StaxParserUtils.checkAttributeContainsKey(startElement, "class", "dropdown")) {
							//in dropdown
							StartElement nameStartElement = StaxParserUtils.nextStartEvent(staxReader, "a", new String[]{"dropdown-toggle"}).asStartElement();
							String categoryName = StaxParserUtils.getContentAndJumpToEndElement(staxReader, nameStartElement).trim();
							StaxParserUtils.nextStartEvent(staxReader, "div", new String[]{"dropdown-menu"});
							StartElement aStartElement = StaxParserUtils.nextStartEvent(staxReader,new String[]{"dropdown-item"}).asStartElement();
							if (aStartElement.getName().getLocalPart().equals("a")) {

								String href = StaxParserUtils.getAttributeByName(aStartElement, "href");
								String categoryUrl = StringUtils.beautifyUrl(
										href,
										ConfigManager.getInstance().getConfigModel().getEmoon().getDomainUrl());

								CategoryUrlHolder categoryUrlHolder = new CategoryUrlHolder(categoryName, categoryUrl);
								categoryUrlHolders.add(categoryUrlHolder);

							}



						} else {
							//not have dropdown
							startElement = StaxParserUtils.nextStartEvent(staxReader, new String[]{"nav-link"}).asStartElement();
							if (startElement.getName().getLocalPart().equals("a")) {
								String href = StaxParserUtils.getAttributeByName(startElement, "href");
								String categoryUrl = StringUtils.beautifyUrl(
										href,
										ConfigManager.getInstance().getConfigModel().getEmoon().getDomainUrl());


								String categoryName = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
								CategoryUrlHolder categoryUrlHolder = new CategoryUrlHolder(categoryName, categoryUrl);
								categoryUrlHolders.add(categoryUrlHolder);

							}

						}
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
					new DomainEntity(AppConstants.EMOON_DOMAIN_NAME,
							ConfigManager.getInstance().getConfigModel().getEmoon().getDomainUrl())
			);

			domainId = DomainDAO.getInstance().getDomainByName(AppConstants.EMOON_DOMAIN_NAME).getId();


			List<CategoryUrlHolder> categories = getCategories();

			for (CategoryUrlHolder categoryUrlHolder : categories) {
				logger.info(categoryUrlHolder.toString());
				//map Emoon category name -> my general category name -> categoryId
				try {
					String categoryName = categoryUrlHolder.getCategoryName();

					CategoryNameType categoryNameType = CategoryMapper.getInstance().mapEmoon(categoryName);

					//get categoryId from database
					int categoryId = CategoryDAO.getInstance().getCategoryId(categoryNameType);


					 EmoonCourseInEachCategoryPageCrawler crawler = new EmoonCourseInEachCategoryPageCrawler(categoryId, categoryUrlHolder.getCategoryURL());
					CrawlingThreadManager.getInstance().getEmoonExecutor().execute(crawler);
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
