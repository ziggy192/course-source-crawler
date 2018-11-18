package crawler;

import config.ConfigManager;
import config.model.SignType;
import dao.CourseDAO;
import entity.CourseEntity;
import sun.swing.StringUIClientPropertyKey;
import url_holder.BaseCourseUrlHolder;
import util.AppUtils;
import util.Formater;
import util.StaxParserUtils;
import util.StringUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.logging.Logger;

public class EmoonCourseDetailCrawler implements Runnable {
	private static Logger logger = Logger.getLogger(EmoonCourseDetailCrawler.class.toString());


	private BaseCourseUrlHolder urlHolder;
	private int categoryId;

	public EmoonCourseDetailCrawler(BaseCourseUrlHolder urlHolder, int categoryId) {
		this.urlHolder = urlHolder;
		this.categoryId = categoryId;
	}

	@Override
	public void run() {
		logger.info("start thread");
		try {
			CrawlingThreadManager.getInstance().checkSuspendStatus();


			String uri = urlHolder.getCourseUrl();

			SignType courseDetailSign = ConfigManager.getInstance().getConfigModel().getEmoon().getCourseDetailSign();


			String beginSign = courseDetailSign.getBeginSign();
			String endSign = courseDetailSign.getEndSign();
//			String beginSign = courseDetailSign.getBeginSign();
//			String endSign = courseDetailSign.getEndSign();

//

			String htmlContent = StaxParserUtils.parseHtml(uri, beginSign, endSign);
			htmlContent = StaxParserUtils.addMissingTag(htmlContent);
			htmlContent = StaxParserUtils.removeEmptyAttributes(htmlContent);


//todo debugging  future comment this
			StaxParserUtils.saveStringToFile(htmlContent, AppUtils.getFileWithRealPath(
					"html-crawled/EmoonCourseDetail.xml"
			));


			CourseEntity courseEntity = new CourseEntity();
			courseEntity.setCategoryId(categoryId);
			courseEntity.setSourceUrl(urlHolder.getCourseUrl());
			courseEntity.setImageUrl(urlHolder.getCourseThumbnailUrl());

			courseEntity.setDomainId(EmoonMainCrawler.domainId);

			//generate utils flag for parsing
			try {

				XMLEventReader staxReader = StaxParserUtils.getStaxReader(htmlContent);
				while (staxReader.hasNext()) {
					XMLEvent event = staxReader.nextEvent();
					if (event.isStartElement()) {
						StartElement startElement = event.asStartElement();

//todo get author name & descriotnio & image

						//author name
						if (StaxParserUtils.checkAttributeContainsKey(startElement, "class", "col-sm-9")) {
							StaxParserUtils.nextStartEvent(staxReader, new String[]{"glyphicon-user"});
							startElement = StaxParserUtils.nextStartEvent(staxReader, "b").asStartElement();

							String authorName = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement).trim();
							courseEntity.setAuthor(authorName);


						}

						//todo get course name

						if (startElement.getName().getLocalPart().equals("div")
								&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "bg_Course")) {

							//jump to the div containing name because this shit have no class !
							startElement = StaxParserUtils.nextStartEvent(staxReader, "div").asStartElement();
							startElement = StaxParserUtils.nextStartEvent(staxReader, "div").asStartElement();
							String name = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement).trim();
							courseEntity.setName(name);

						}

						//todo get video url ( deo co)


						//todo get cost
						if (startElement.getName().getLocalPart().equals("div")
								&& StaxParserUtils.checkAttributeContainsKey(startElement, "id", "RightCourseID")) {
							startElement = StaxParserUtils.nextStartEvent(staxReader, "b").asStartElement();
							String costStr = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
							double cost = StringUtils.toCost(costStr);
							courseEntity.setCost(cost);
						}

						//todo get duration ( deo co)


						//todo get overview description

						//todo get syllabus

						if (startElement.getName().getLocalPart().equals("div")
								&& StaxParserUtils.checkAttributeContainsKey(startElement, "id", "ContentPlaceHolder1_Panel1")) {
							startElement = StaxParserUtils.nextStartEvent(staxReader, "table").asStartElement();

							//traverse to end
							int stackCount = 1;
							String syllabusHtmlContent = "";
							syllabusHtmlContent += Formater.addBeginList();

							//						List<String> contentList = new ArrayList<>();
							while (stackCount > 0) {
								event = staxReader.nextEvent();
								if (event.isStartElement()) {
									stackCount++;
									startElement = event.asStartElement();
									if (startElement.getName().getLocalPart().equals("tr")) {
										String content = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);

										stackCount--;
										content = Formater.toListItem(content);
										syllabusHtmlContent += content;
									}


								}
								if (event.isEndElement()) {
									stackCount--;

								}
							}

							syllabusHtmlContent += Formater.addEndList();

							syllabusHtmlContent = Formater.toRoot(syllabusHtmlContent);

							courseEntity.setSyllabus(syllabusHtmlContent);
						}

						//todo get rating
						//khong co
						courseEntity.setRating(0d);

						//todo get rating number
						//khong co
						courseEntity.setRatingNumber(0);


					}


				}
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
		logger.info("End thread");

	}
}
