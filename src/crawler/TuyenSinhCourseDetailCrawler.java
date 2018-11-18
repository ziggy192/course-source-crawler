package crawler;

import config.ConfigManager;
import config.model.SignType;
import dao.CourseDAO;
import entity.CourseEntity;
import url_holder.BaseCourseUrlHolder;
import url_holder.TuyenSinhCourseUrlHolder;
import util.AppUtils;
import util.Formater;
import util.StaxParserUtils;
import util.StringUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.logging.Logger;

public class TuyenSinhCourseDetailCrawler implements Runnable {

	private static Logger logger = Logger.getLogger(TuyenSinhCourseDetailCrawler.class.toString());


	private TuyenSinhCourseUrlHolder urlHolder;
	private int categoryId;

	public TuyenSinhCourseDetailCrawler(TuyenSinhCourseUrlHolder urlHolder, int categoryId) {
		this.urlHolder = urlHolder;
		this.categoryId = categoryId;
	}

	@Override
	public void run() {
		logger.info("start thread");
		try {
			CrawlingThreadManager.getInstance().checkSuspendStatus();


			String uri = urlHolder.getCourseUrl();

			SignType courseDetailSign = ConfigManager.getInstance().getConfigModel().getTuyenSinh247().getCourseDetailSign();


			String beginSign = courseDetailSign.getBeginSign();
			String endSign = courseDetailSign.getEndSign();
//			String beginSign = courseDetailSign.getBeginSign();
//			String endSign = courseDetailSign.getEndSign();

//

			String htmlContent = StaxParserUtils.parseHtml(uri, beginSign, endSign);
			htmlContent = StaxParserUtils.addMissingTag(htmlContent);
			htmlContent = StaxParserUtils.removeEmptyAttributes(htmlContent);


			//todo debugging  future comment this
//			StaxParserUtils.saveStringToFile(htmlContent, AppUtils.getFileWithRealPath(
//					"html-crawled/tuyensinhCourseDetail.xml"
//			));


			CourseEntity courseEntity = new CourseEntity();
			courseEntity.setCategoryId(categoryId);
			courseEntity.setSourceUrl(urlHolder.getCourseUrl());
			courseEntity.setImageUrl(urlHolder.getCourseThumbnailUrl());
			courseEntity.setAuthorImageUrl(urlHolder.getAuthorImageUrl());
			courseEntity.setAuthor(urlHolder.getAuthorName());

			courseEntity.setDomainId(TuyenSinhMainCrawler.domainId);

			String syllabus = "";
			syllabus += Formater.addBeginList();

			boolean passTable1StartElement = false;
			boolean passTable2StartElement = false;
			//generate utils flag for parsing
			try {

				XMLEventReader staxReader = StaxParserUtils.getStaxReader(htmlContent);
				while (staxReader.hasNext()) {
					XMLEvent event = staxReader.nextEvent();
					if (event.isStartElement()) {
						StartElement startElement = event.asStartElement();


						//todo get course name
						if (StaxParserUtils.checkAttributeContainsKey(startElement, "class", "table1")) {
							passTable1StartElement = true;


						}
						//todo get course name

						if (passTable1StartElement
								&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", new String[]{"clblue", "s20"})) {
							String courseName = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
							courseEntity.setName(courseName);

						}


						//todo get cost

						if (passTable1StartElement
								&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "fl")
								&& !StaxParserUtils.checkAttributeContainsKey(startElement, "class", "magr10")) {
							if (courseEntity.getCost()==null) {

								String costStr = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);

								double cost = StringUtils.toCost(costStr);

								courseEntity.setCost(cost);
							}
						}

						//todo get video url ( deo co)


						//todo get duration ( deo co)


						//todo get overview description
						if (startElement.getName().getLocalPart().equals("div")
								&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "decrip")) {
							String overviewDes = StaxParserUtils.getAllHtmlContentAndJumpToEndElement(staxReader, startElement);
							overviewDes = Formater.toRoot(overviewDes);
							courseEntity.setOverviewDescription(overviewDes);

						}

						if (startElement.getName().getLocalPart().equals("div")
								&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "table2")) {
							passTable2StartElement = true;
						}
						//todo get syllabus

						if (passTable2StartElement
								&& startElement.getName().getLocalPart().equals("td")
								&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "txt_left")) {
							startElement = StaxParserUtils.nextStartEvent(staxReader, "a").asStartElement();
							String syllabusItem = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
							syllabusItem = Formater.toListItem(syllabusItem);
							syllabus += syllabusItem;
						}


						//todo get rating
						//khong co
						courseEntity.setRating(0d);

						//todo get rating number
						//khong co
						courseEntity.setRatingNumber(0);


					}


				}
				syllabus += Formater.addEndList();
				syllabus = Formater.toRoot(syllabus);

				courseEntity.setSyllabus(syllabus);
			} catch (Exception e) {
				e.printStackTrace();
				logger.severe(String.format("url=%s | erorr= %s", urlHolder.getCourseUrl(), e.toString()));
			}


			//save to database
			courseEntity.setHash(courseEntity.hashCourse());

			CourseDAO.getInstance().validateCourseAndSaveToDB(courseEntity);

			CrawlingThreadManager.getInstance().checkSuspendStatus();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			logger.severe(String.format("url=%s | erorr= %s", urlHolder.getCourseUrl(), e.toString()));
		}
		logger.info("End Thread");

	}
}
