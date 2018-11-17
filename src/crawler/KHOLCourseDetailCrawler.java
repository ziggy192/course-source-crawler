package crawler;

import config.ConfigManager;
import config.model.SignType;
import constant.AppConstants;
import dao.CourseDAO;
import entity.CourseEntity;
import sun.net.www.ParseUtil;
import sun.rmi.runtime.Log;
import url_holder.BaseCourseUrlHolder;
import util.AppUtils;
import util.Formater;
import util.StaxParserUtils;
import util.StringUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KHOLCourseDetailCrawler implements Runnable {
	private static Logger logger = Logger.getLogger(KHOLCourseDetailCrawler.class.toString());

	private BaseCourseUrlHolder urlHolder;
	private int categoryId;

	public KHOLCourseDetailCrawler(BaseCourseUrlHolder urlHolder, int categoryId) {
		this.urlHolder = urlHolder;
		this.categoryId = categoryId;
	}


	public static int toDuration(String durationContent) {
		//hh giờ mm phút
		//-->


		int hour = 0;
		int minute = 0;
		try {
			String hourStr = "0";
			String minuteStr = "0";

			if (durationContent.contains("giờ")) {
				hourStr = durationContent.substring(0, durationContent.indexOf("giờ")).trim();
				if (durationContent.contains("phút")) {
					minuteStr = durationContent.substring(durationContent.indexOf("giờ") + 3
							, durationContent.indexOf("phút")).trim();
				}

			} else if (durationContent.contains("phút")) {

				minuteStr = durationContent.substring(0, durationContent.indexOf("phút")).trim();
			}

			hour = Integer.parseInt(hourStr);
			minute = Integer.parseInt(minuteStr);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;

		}

		return hour * 3600 + minute * 60;

	}


	private double toRating(String ratingContent) {
//		'5 sao'
		double rating = 0;

		String ratingStr = ratingContent.substring(0, ratingContent.indexOf("Sao")).trim();
		try {
			rating = Double.parseDouble(ratingStr);
		} catch (NumberFormatException e) {
			//not a double
		}
		return rating;

	}


	@Override
	public void run() {
		logger.info("Start thread");

		try {
			CrawlingThreadManager.getInstance().checkSuspendStatus();

			String uri = urlHolder.getCourseUrl();

			SignType signType = ConfigManager.getInstance().getConfigModel().getKhoaHocOnline().getCourseDetailSign();

			String beginSign = signType.getBeginSign();
			String endSign = signType.getEndSign();

			String htmlContent = StaxParserUtils.parseHtml(uri, beginSign, endSign);
			htmlContent = StaxParserUtils.addMissingTag(htmlContent);
			htmlContent = StaxParserUtils.removeEmptyAttributes(htmlContent);


//todo debugging  future comment this
//			StaxParserUtils.saveStringToFile(htmlContent, AppUtils.getFileWithRealPath(
//					"html-crawled/KhoahoconlineCourseDetail.html"
//			));

			CourseEntity courseEntity = new CourseEntity();
			courseEntity.setCategoryId(categoryId);
			courseEntity.setDomainId(KHOLMainCrawler.domainId);

			courseEntity.setImageUrl(urlHolder.getCourseThumbnailUrl());
			courseEntity.setSourceUrl(urlHolder.getCourseUrl());

			XMLEventReader staxReader = StaxParserUtils.getStaxReader(htmlContent);
			while (staxReader.hasNext()) {
				XMLEvent event = staxReader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();

					//todo get course name
					if (StaxParserUtils.checkAttributeContainsKey(startElement, "class", "entry-title")) {
						String courseName = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
						courseEntity.setName(courseName);

					}


					//todo get video url
					if (startElement.getName().getLocalPart().equals("div")
							&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "media-intro")) {
						StartElement iframeStartElement = StaxParserUtils.nextStartEvent(staxReader, "iframe").asStartElement();
						String src = StaxParserUtils.getAttributeByName(iframeStartElement, "src");
						if (!src.isEmpty()) {
							courseEntity.setPreviewVideoUrl(src);
						}
					}
					//todo get cost

					if (StaxParserUtils.checkAttributeContainsKey(startElement, "class", "course-origin-price")) {
						String costStr = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
						double cost = StringUtils.getNumberValueFromString(costStr);

						courseEntity.setCost(cost);
					}


					//todo get duration

					if (startElement.getName().getLocalPart().equals("li")
							&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "duration-feature")) {
						StartElement spanStartElement = StaxParserUtils.nextStartEvent(staxReader, "span", new String[]{"value"}).asStartElement();
						String durationStr = StaxParserUtils.getContentAndJumpToEndElement(staxReader, spanStartElement);
						int duraion = toDuration(durationStr);
						courseEntity.setDuration(duraion);

					}

					//todo get author name & descriotnio & image


					//image
					if (StaxParserUtils.checkAttributeContainsKey(startElement, "class", "author-avatar")) {
						StartElement imgStartElement = StaxParserUtils.nextStartEvent(staxReader, "img").asStartElement();
						String src = StaxParserUtils.getAttributeByName(imgStartElement, "src");
						src = StringUtils.beautifyUrl(src,ConfigManager.getInstance().getConfigModel().getKhoaHocOnline().getDomainUrl());

						courseEntity.setAuthorImageUrl(src);
					}

					//name

					if (StaxParserUtils.checkAttributeContainsKey(startElement, "class", "author-bio")) {
						StartElement nameStartElement = StaxParserUtils.nextStartEvent(staxReader, new String[]{"name"}).asStartElement();
						String authorName = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);

						if (courseEntity.getAuthor() == null || courseEntity.getAuthor().isEmpty()) {
							courseEntity.setAuthor(authorName);

						}

					}


					//todo get overview description

					if (StaxParserUtils.checkAttributeContainsKey(startElement, "class", "course-description")) {
						StartElement overviewStartElement = StaxParserUtils.nextStartEvent(staxReader, new String[]{"thim-course-content"}).asStartElement();


						String overviewContent = StaxParserUtils.getAllHtmlContentAndJumpToEndElement(staxReader, overviewStartElement);

						overviewContent = Formater.toRoot(overviewContent);

						courseEntity.setOverviewDescription(overviewContent);

					}


					//todo get syllabus


					//todo get rating
					if (StaxParserUtils.checkAttributeContainsKey(startElement, "class", "assessments-feature")) {
						StartElement ratingStartElement = StaxParserUtils.nextStartEvent(staxReader, new String[]{"value"}).asStartElement();
						String ratingContent = StaxParserUtils.getContentAndJumpToEndElement(staxReader, ratingStartElement);
						double rating = this.toRating(ratingContent);

						courseEntity.setRating(rating);

					}

					//todo get rating number

					if (StaxParserUtils.checkAttributeContainsKey(startElement, "class", "students-feature")) {
						StartElement ratingNumberStartElement = StaxParserUtils.nextStartEvent(staxReader, new String[]{"value"}).asStartElement();
						String ratingNumberContent = StaxParserUtils.getContentAndJumpToEndElement(staxReader, ratingNumberStartElement);

						int ratingNumber = Integer.parseInt(ratingNumberContent);
						courseEntity.setRatingNumber(ratingNumber);

					}


				}
			}

			//save to database
			courseEntity.setHash(courseEntity.hashCourse());

			CourseDAO.getInstance().validateCourseAndSaveToDB(courseEntity);

			CrawlingThreadManager.getInstance().checkSuspendStatus();

		} catch (Exception e) {
			e.printStackTrace();

			logger.severe(String.format("url=%s | erorr= %s", urlHolder.getCourseUrl(),e.getMessage()));

		}
		logger.info("End thread");
	}
}
