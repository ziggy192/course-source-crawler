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

import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EdumallMainCrawler implements Runnable {

	public static Logger logger = Logger.getLogger(EdumallMainCrawler.class.toString());

	public static int domainId;








	public static void testGetAllCourse() {
		String uri = "https://edumall.vn/courses/filter?categories[]=luyen-thi&page=2";

		CategoryUrlHolder categoryUrlHolder = new CategoryUrlHolder("Luyện thi", uri);

//		getAllCoursesFromCategory(categoryUrlHolder);

//		for (EdumallCourseUrlHolder courseUrlHolder : allCoursesFromCategory) {
//			logger.info("Detail=======================");
//			getCourseDetail(courseUrlHolder);
//
//		}
	}

	public static void testGetPageNumber() {
		String uri = "https://edumall.vn/courses/filter?categories[]=luyen-thi&page=2";

		CategoryUrlHolder categoryUrlHolder = new CategoryUrlHolder("Luyện thi", uri);
		//test get total pages
//		int page = getTotalPageForEachCategory(categoryUrlHolder);
//		System.out.println("Total page =" + page);

	}





	public List<CategoryUrlHolder> getCategories() {
		List<CategoryUrlHolder> categories = new ArrayList<>();

		String uri = ConfigManager.getInstance().getConfigModel().getEdumall().getDomainUrl();

		SignType categoryListSign = ConfigManager.getInstance().getConfigModel().getEdumall().getCategoryListSign();

		String beginSign = categoryListSign.getBeginSign();
		String endSign = categoryListSign.getEndSign();

//		String beginSign = "col-xs col-sm col-md col-lg main-header-v4--content-c-header-left";
//		String endSign = "col-xs col-sm col-md col-lg main-header-v4--content-c-header-search";

		String htmlContent = StaxParserUtils.parseHtml(uri, beginSign, endSign);
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
					Iterator<Attribute> attributes = startElement.getAttributes();

					if (!insideMainMenu) {
						Attribute attributeClass = startElement.getAttributeByName(new QName("class"));
						if (attributeClass != null && attributeClass.getValue().equals("main-menu")) {
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
						if (startElement.getName().getLocalPart().equals("li")) {
							startElement = StaxParserUtils.nextStartEvent(staxReader).asStartElement();

							if (startElement.getName().getLocalPart().equals("a")) {
								String href = startElement.getAttributeByName(new QName("href")).getValue();
								//exclude the All Category tag
								if (href.contains("categories") && !href.contains("sub_categories")) {

									String categoryURL = href;
									categoryURL = ConfigManager.getInstance().getConfigModel().getEdumall().getDomainUrl() + categoryURL;

									startElement = StaxParserUtils.nextStartEvent(staxReader, "span").asStartElement();
									String categoryName = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);


//									logger.info(String.format("categoryURL=%s | categoryName=%s", categoryURL,categoryName));
									CategoryUrlHolder categoryUrlHolder = new CategoryUrlHolder(categoryName, categoryURL);
									categories.add(categoryUrlHolder);
									logger.info(categoryUrlHolder.toString());

								}


							}


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
			if (DomainDAO.getInstance().getDomainByName(AppConstants.EDUMALL_DOMAIN_NAME) == null) {
				//insert to database
//				other.DummyDatabase.insertDomain(constant.AppConstants.EDUMALL_DOMAIN_NAME, constant.AppConstants.EDUMALL_DOMAIN);
				DomainEntity domainEntity = new DomainEntity();
				domainEntity.setName(AppConstants.EDUMALL_DOMAIN_NAME);
				domainEntity.setDomainUrl(ConfigManager.getInstance().getConfigModel().getEdumall().getDomainUrl());
				DomainDAO.getInstance().persist(domainEntity);
			}
			domainId = DomainDAO.getInstance().getDomainByName(AppConstants.EDUMALL_DOMAIN_NAME).getId();

			//get all category from domain url
			List<CategoryUrlHolder> categories = getCategories();

			//check issuspend
			synchronized (CrawlingThreadManager.getInstance()) {
				while (CrawlingThreadManager.getInstance().isSuspended()) {
					CrawlingThreadManager.getInstance().wait();
				}
			}


			//domain name and url co truoc trong database
			//
			for (CategoryUrlHolder categoryUrlHolder : categories) {

				//map edumall category name -> my general category name -> categoryId
				try {
					String edumallCategoryName = categoryUrlHolder.getCategoryName();

					CategoryNameType categoryNameType = CategoryMapper.getInstance().mapEdumall(edumallCategoryName);

					//get categoryId from database
					int categoryId = CategoryDAO.getInstance().getCategoryId(categoryNameType);


					EdumallEachCategoryCrawler edumallEachCategoryCrawler = new EdumallEachCategoryCrawler(categoryId, categoryUrlHolder.getCategoryURL());


					//todo thread executor
					CrawlingThreadManager.getInstance().getEdumallExecutor().execute(edumallEachCategoryCrawler);
//				edumallEachCategoryCrawler.start();
				} catch (Exception e) {
				}


				//check is suspend
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
