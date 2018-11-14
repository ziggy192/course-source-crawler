package crawler;

import config.CategoryMapper;
import config.ConfigManager;
import config.model.SignType;
import constant.AppConstants;
import dao.CategoryDAO;
import dao.DomainDAO;
import config.model.CategoryNameType;
import entity.DomainEntity;
import url_holder.CategoryUrlHolder;
import util.StaxParserUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UnicaMainCrawler implements Runnable {
	public static Logger logger = Logger.getLogger(UnicaMainCrawler.class.toString());

	public static int domainId;



	public List<CategoryUrlHolder> getCategories() {
		List<CategoryUrlHolder> categories = new ArrayList<>();

		String uri = ConfigManager.getInstance().getConfigModel().getUnica().getDomainUrl();

		SignType categoryListSign = ConfigManager.getInstance().getConfigModel().getUnica().getCategoryListSign();

		String beginSign = categoryListSign.getBeginSign();
		String endSign = categoryListSign.getEndSign();

//		String beginSign = "col-lg-3 col-md-3 col-sm-4 cate-md";
//		String endSign = "col-lg-5 col-md-5 col-sm-4 cate-sm";

		String htmlContent = StaxParserUtils.parseHTML(uri, beginSign, endSign);
		String newContent = StaxParserUtils.addMissingTag(htmlContent);

//		System.out.println(newContent);;
		logger.info(newContent);


		try {
			XMLEventReader staxReader = StaxParserUtils.getStaxReader(newContent);
			boolean insideMainMenu = false;
			while (staxReader.hasNext()) {
				XMLEvent event = staxReader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					if (!insideMainMenu) {
						Attribute idAttribute = startElement.getAttributeByName(new QName("id"));
						if (idAttribute != null && idAttribute.getValue().equals("mysidebarmenu")) {
							insideMainMenu = true;
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
					if (insideMainMenu) {

						if (startElement.getName().getLocalPart().equals("a")
								&& !StaxParserUtils.getAttributeByName(startElement,"title").trim().isEmpty()) {
							String href = StaxParserUtils.getAttributeByName(startElement, "href");
							String titleAtt = StaxParserUtils.getAttributeByName(startElement, "title");
							//exclude the All Category tag

							String categoryURL = href;
							categoryURL = ConfigManager.getInstance().getConfigModel().getUnica().getDomainUrl() + categoryURL;

							String categoryName = titleAtt;

							CategoryUrlHolder categoryUrlHolder = new CategoryUrlHolder(categoryName, categoryURL);
							categories.add(categoryUrlHolder);
							logger.info(categoryUrlHolder.toString());


						}


					}
				}

			}

		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return categories;

	}

	@Override
	public void run() {
		logger.info("start thread");

		try {
			// todo insert domain to db if not yet availabe
			if (DomainDAO.getInstance().getDomainByName(AppConstants.UNICA_DOMAIN_NAME) == null) {
				//insert to database
//				other.DummyDatabase.insertDomain(constant.AppConstants.EDUMALL_DOMAIN_NAME, constant.AppConstants.EDUMALL_DOMAIN);
				DomainEntity domainEntity = new DomainEntity();
				domainEntity.setName(AppConstants.UNICA_DOMAIN_NAME);
				domainEntity.setDomainUrl(ConfigManager.getInstance().getConfigModel().getUnica().getDomainUrl());
				DomainDAO.getInstance().persist(domainEntity);
			}
			domainId = DomainDAO.getInstance().getDomainByName(AppConstants.UNICA_DOMAIN_NAME).getId();

			//get all category from domain url
			List<CategoryUrlHolder> categories = getCategories();

			//check issuspend
			CrawlingThreadManager.getInstance().checkSuspendStatus();



			//domain name and url co truoc trong database
			//
			for (CategoryUrlHolder categoryUrlHolder : categories) {

				//map edumall category name -> my general category name -> categoryId
				String edumallCategoryName = categoryUrlHolder.getCategoryName();

				CategoryNameType categoryNameType = CategoryMapper.getInstance().mapUnica(edumallCategoryName);

				//get categoryId from database
				int categoryId = CategoryDAO.getInstance().getCategoryId(categoryNameType);


				Thread unicalEachCategoryCrawler = new Thread(new UnicaEachCategoryCrawler(categoryId, categoryUrlHolder.getCategoryURL()));


				//todo thread executor
				CrawlingThreadManager.getInstance().getExecutor().execute(unicalEachCategoryCrawler);
//				edumallEachCategoryCrawler.start();


				//check is suspend
				CrawlingThreadManager.getInstance().checkSuspendStatus();

			}
		} catch (Exception e) {
			e.printStackTrace();

		}

		logger.info("END THREAD");
	}
}
