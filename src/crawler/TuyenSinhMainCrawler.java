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

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class TuyenSinhMainCrawler implements Runnable {
	private static Logger logger = Logger.getLogger(TuyenSinhMainCrawler.class.toString());
	public static int domainId = 0;


	

	@Override
	public void run() {
		logger.info("start thread");

		try {
			DomainDAO.getInstance().insertDomainIfNotExist(
					new DomainEntity(AppConstants.TUYENSINH_DOMAIN_NAME,
							ConfigManager.getInstance().getConfigModel().getTuyenSinh247().getDomainUrl())
			);

			domainId = DomainDAO.getInstance().getDomainByName(AppConstants.TUYENSINH_DOMAIN_NAME).getId();


			List<CategoryUrlHolder> categories = getCategories();

			for (CategoryUrlHolder categoryUrlHolder : categories) {
				logger.info(categoryUrlHolder.toString());
				//map TUYENSINH category name -> my general category name -> categoryId
				try {
					String categoryName = categoryUrlHolder.getCategoryName();
					CategoryNameType categoryNameType = CategoryMapper.getInstance().mapTuyenSinh(categoryName);

					//get categoryId from database
					int categoryId = CategoryDAO.getInstance().getCategoryId(categoryNameType);


					Runnable crawler = new TuyenSinhCourseInEachCategoryPageCrawler(categoryId, categoryUrlHolder.getCategoryURL());
					CrawlingThreadManager.getInstance().getTuyenSinhExecutor().execute(crawler);
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

	public List<CategoryUrlHolder> getCategories() {


		ArrayList<CategoryUrlHolder> categoryUrlHolders = new ArrayList<>();

		SignType categoryListSign = ConfigManager.getInstance().getConfigModel().getTuyenSinh247().getCategoryListSign();

		String beginSign = categoryListSign.getBeginSign();
		String endSign = categoryListSign.getEndSign();


		String htmlContent = StaxParserUtils.parseHtml(ConfigManager.getInstance().getConfigModel().getTuyenSinh247().getDomainUrl()
				, beginSign, endSign);

		htmlContent = StaxParserUtils.addMissingTag(htmlContent);


//		StaxParserUtils.saveStringToFile(htmlContent,
//				AppUtils.getFileWithRealPath("html-crawled/testTuyensinh.xml"));
		ArrayList<String> itemIdList = new ArrayList<>();
		try {

			XMLEventReader staxReader = StaxParserUtils.getStaxReader(htmlContent);
			while (staxReader.hasNext()) {
				XMLEvent event = staxReader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					//contains td-next-sub
					if (startElement.getName().getLocalPart().equals("a")
							&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "td-next-sub")) {
						String[] itemIds = StaxParserUtils.getAttributeByName(startElement, "item-id")
								.split(",");
						for (String itemId : itemIds) {
							if (!itemIdList.contains(itemId)) {
								itemIdList.add(itemId);
								String courseListUrl = ConfigManager.getInstance().getConfigModel().getTuyenSinh247().getDomainUrl()
										+ "/eHome/loadCourse?list_cat_ids="
										+ itemId;

								CategoryUrlHolder urlHolder = new CategoryUrlHolder("Lớp 11", courseListUrl);
								categoryUrlHolders.add(urlHolder);
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



}
