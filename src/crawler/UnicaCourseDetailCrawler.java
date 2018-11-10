package crawler;

import dao.CourseDAO;
import entity.CourseEntity;
import url_holder.UnicaCourseUrlHolder;
import util.Formater;
import util.ParserUtils;

import java.util.logging.Logger;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;

public class UnicaCourseDetailCrawler implements Runnable {
	private static Logger logger = Logger.getLogger(UnicaCourseDetailCrawler.class.toString());


	private UnicaCourseUrlHolder unicaCourseUrlHolder;
	private int categoryId;

	public UnicaCourseDetailCrawler(UnicaCourseUrlHolder unicaCourseUrlHolder, int categoryId) {
		this.unicaCourseUrlHolder = unicaCourseUrlHolder;
		this.categoryId = categoryId;
	}


	public void setUnicaCourseUrlHolder(UnicaCourseUrlHolder unicaCourseUrlHolder) {
		this.unicaCourseUrlHolder = unicaCourseUrlHolder;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}


	public static int toDuration(String durationContent) {
		//hh giờ mm phút
		//-->

		int hour = 0;
		int minute = 0;
		try {
			String hourStr = durationContent.substring(0, durationContent.indexOf("giờ")).trim();
			String minuteStr = durationContent.substring(durationContent.indexOf("giờ") + 3
					, durationContent.indexOf("phút")).trim();

			hour = Integer.parseInt(hourStr);
			minute = Integer.parseInt(minuteStr);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;

		}

		return hour * 3600 + minute * 60;

	}

	@Override
	public void run() {

		try {
			logger.info("start thread");
			String uri = unicaCourseUrlHolder.getCourseUrl();

			String beginSign = "<main";
//			String endSign = "</main>";
			String endSign = "<input type=\"hidden\" id=\"user_id\"";


			String htmlContent = ParserUtils.parseHTML(uri, beginSign, endSign);

			htmlContent = ParserUtils.addMissingTag(htmlContent);

//			ParserUtils.saveToHtmlFileForDebugging("testHTML.html", htmlContent);

			String overviewDescription = "";
			String authorDes = "";
			String videoUrl;
			double costValue = 0;

			CourseEntity courseEntity = new CourseEntity();
			courseEntity.setCategoryId(categoryId);
			courseEntity.setSourceUrl(unicaCourseUrlHolder.getCourseUrl());
			courseEntity.setImageUrl(unicaCourseUrlHolder.getCourseThumbnailUrl());
			courseEntity.setCost(unicaCourseUrlHolder.getCost());


			//todo get providerid from EdumallMainCrawler static or singleton
			//get providerid = 'edumall'
			courseEntity.setDomainId(UnicaMainCrawler.domainId);

			//generate utils flag for parsing
			boolean flagForDurationInsideBlockUtils = false;

			try {
				XMLEventReader staxReader = ParserUtils.getStaxReader(htmlContent);
				while (staxReader.hasNext()) {
					XMLEvent event = staxReader.nextEvent();
					if (event.isStartElement()) {
						StartElement startElement = event.asStartElement();

						//todo get course name
						if (startElement.getName().getLocalPart().equals("div")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "u-detail-block-title")) {
							startElement = ParserUtils.nextStartEvent(staxReader, "h3").asStartElement();
							String courseName = ParserUtils.getContentAndJumpToEndElement(staxReader, startElement);

							logger.info("CourseName=" + courseName);
							courseEntity.setName(courseName);

						}


						//todo get VideoURL

						if (startElement.getName().getLocalPart().equals("div")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "u-video")) {
							startElement = ParserUtils.nextStartEvent(staxReader, "iframe").asStartElement();
							String videoURL = ParserUtils.getAttributeByName(startElement, "src");
							logger.info("VideoURL=" + videoURL);
							courseEntity.setPreviewVideoUrl(videoURL);


						}


						//todo get duration

						if (startElement.getName().getLocalPart().equals("div")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "block-ulti")) {


							//traverse to end div
							int stackCount = 1;


							//						List<String> contentList = new ArrayList<>();
							while (stackCount > 0) {
								event = staxReader.nextEvent();
								if (event.isStartElement()) {
									stackCount++;
									startElement = event.asStartElement();
									if (startElement.getName().getLocalPart().equals("i")
											&& ParserUtils.checkAttributeContainsKey(startElement, "class", "fa-clock-o")) {
										startElement = ParserUtils.nextStartEvent(staxReader, "p").asStartElement();
										stackCount++;
										String durationContent = ParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
										stackCount--;
										int duration = toDuration(durationContent);
										if (duration >= 0) {
											logger.info("Duration=" + duration);
											courseEntity.setDuration(duration);
										}


									}
								}
								if (event.isEndElement()) {
									stackCount--;

								}

							}

						}


						//todo get author image
						if (startElement.getName().getLocalPart().equals("div")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "uct-ava-gv")) {
							startElement = ParserUtils.nextStartEvent(staxReader, "img").asStartElement();
							String src = ParserUtils.getAttributeByName(startElement, "src");

							logger.info("AuthorImageUrl=" + src);
							courseEntity.setAuthorImageUrl(src);

						}

						//todo get author name

						if (startElement.getName().getLocalPart().equals("div")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "uct-name-gv")) {
							String authorName = ParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
							logger.info("AuthorName=" + authorName);
							courseEntity.setAuthor(authorName);

						}


						//todo get author des

						//uct-des-gv
						if (startElement.getName().getLocalPart().equals("div")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "uct-des-gv")) {
							String authorTitle = ParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
							authorDes += Formater.toEmphasis(authorTitle);
							logger.info("authorTitle=" + authorTitle);

							courseEntity.setAuthorDescription(authorDes);

						}


						if (startElement.getName().getLocalPart().equals("div")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "uct-more-gv")) {
							String authorDesHtml = ParserUtils.getAllHtmlContentAndJumpToEndElement(staxReader, startElement).trim();

							authorDes += authorDesHtml;
							logger.info("AuthorDesHtml=" + authorDesHtml);

						}


						// TODO: get overview description

						//ban se hoc duoc gi
						/*<div class="u-learn-what" xpath="1">
                            <h3>Bạn sẽ học được gì</h3>
                            <div class="content">
                                <div class="row">
                                    <div class="col-sm-6"><div class="title">

								Phân tích bố cục cần thiết khi chụp một tấm hình đẹp</div></div><div class="col-sm-6"><div class="title">

								Tự tin xử lí các tình huống bất ngờ xảy ra khi chụp hình</div></div></div><div class="row"><div class="col-sm-6"><div class="title">

								Tự mình chỉnh sửa hậu kì cho những tấm hình&nbsp;</div></div><div class="col-sm-6"><div class="title">

								Tự tạo thiết kế baner, poster đẹp mắt</div></div></div><div class="row"><div class="col-sm-6"><div class="title">

								Trở nên đa tài hơn, lãng tử hơn trong mắt phái đẹp</div></div><div class="col-sm-6"><div class="title">

								Thoải sức sáng tạo, trao dồi cho công việc của mình thật tốt.&nbsp;</div></div></div><div class="row"><div class="col-sm-6"><div class="title">

						</div></div>                                </div>
                            </div>
                        </div>*/
						if (startElement.getName().getLocalPart().equals("div")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "u-learn-what")) {


							//traverse
							int stackCount = 1;
							List<String> items = new ArrayList<>();

							while (stackCount > 0) {
								event = staxReader.nextEvent();
								if (event.isStartElement()) {
									stackCount++;
									startElement = event.asStartElement();

									if (startElement.getName().getLocalPart().equals("h3")) {
										//tilte
										String title = ParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
										overviewDescription += Formater.toHeading2(title);
										stackCount--;

									}
									if (startElement.getName().getLocalPart().equals("div")
											&& ParserUtils.checkAttributeContainsKey(startElement, "class", "title")) {
										String learnWhatListItem = ParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
										items.add(Formater.toListItem(learnWhatListItem));
										stackCount--;

									}
								}
								if (event.isEndElement()) {
									stackCount--;

								}

							}


							overviewDescription += Formater.toList(items.toArray(new String[items.size()]));

						}

						//gioi thieu khoa hoc
						/*<div class="u-des-course" xpath="1">
                            <h3>Giới thiệu khóa học</h3>
							<p>
								Bạn là người săn lùng chụp những tấm ảnh đẹp nhưng vẫn còn chưa biết sử dụng máy cơ hay canh góc chụp hoàn hảo.&nbsp;</p>
							<p>
								Dù bạn là phóng viên, marketer, designer, freelancer hay chỉ đơn giản là người đam mê chụp ảnh sáng tạo thì việc sở hữu cho mình một kinh nghiệm và bí quyết để đưa máy lên chụp những tấm ảnh thật ấn tượng dù trong không gian hay thời gian khắc nghiệt đến đâu cũng rất quan trọng.</p>
							<p>
								Để chụp được tấm ảnh đẹp đã khó và design những tấm ảnh đó cho thật lung linh, ấn tượng, sắc nét lại còn khó hơn.</p>
							<p>
								Khóa học này không những giúp cho bạn nắm chắc cách thức chụp được những tấm ảnh đẹp mà còn có thể giúp bạn xử lý thật tinh tế những tấm ảnh đó qua từng góc chụp độc đáo và sắc nét nhất.&nbsp;</p>
                        </div>*/
						if (startElement.getName().getLocalPart().equals("div")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "u-des-course")) {

//							overviewDescription += Formater.toHeading2("Giới thiệu khoá học");
							//<h3>Giới thiệu khóa học</h3>
							startElement = ParserUtils.nextStartEvent(staxReader).asStartElement();

							overviewDescription += Formater.toHeading2(ParserUtils.getContentAndJumpToEndElement(staxReader, startElement).trim());
							String allHtmlContentInside = ParserUtils.getAllHtmlContentAndJumpToEndElement(staxReader, startElement);

							overviewDescription += allHtmlContentInside;

						}


						//todo get syllabus

						if (startElement.getName().getLocalPart().equals("div")
								&& ParserUtils.checkAttributeContainsKey(startElement, "class", "u-list-course")) {

							//jump to div class='content'
							startElement = ParserUtils.nextStartEvent(staxReader, "div", new String[]{"content"}).asStartElement();

							//traverse to end content div


							int stackCount = 1;
							String syllabus = "";
							List<String> lectureNameList = new ArrayList<>();

							while (stackCount > 0) {
								event = staxReader.nextEvent();
								if (event.isStartElement()) {
									stackCount++;
									startElement = event.asStartElement();

									//get section heading

									if (ParserUtils.checkAttributeContainsKey(startElement, "class", "panel-title")) {

										String sectionTitle = ParserUtils.getContentAndJumpToEndElement(staxReader, startElement).trim();
										stackCount--;


										if (!lectureNameList.isEmpty()) {
											syllabus += Formater.toList(lectureNameList.toArray(new String[lectureNameList.size()]));
											lectureNameList.clear();


										}
										syllabus += Formater.toHeading3(sectionTitle);
									}

									//get section content
									if (startElement.getName().getLocalPart().equals("div")
											&& ParserUtils.checkAttributeContainsKey(startElement, "class", "title")) {
										String content = ParserUtils.getContentAndJumpToEndElement(staxReader, startElement).trim();
										stackCount--;
										lectureNameList.add(content);
									}


								}
								if (event.isEndElement()) {
									stackCount--;

								}

							}


							if (!lectureNameList.isEmpty()) {
								syllabus += Formater.toList(lectureNameList.toArray(new String[lectureNameList.size()]));

							}

							syllabus = Formater.toRoot(syllabus);
							courseEntity.setSyllabus(syllabus);
							logger.info("Syllabus=" + syllabus);
						}


						//todo get rating && rating number

						if (ParserUtils.checkAttributeContainsKey(startElement, "class", "number-big-rate")) {
							/*<div class="number-big-rate" xpath="1">5</div>*/
							String ratingStr = ParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
							try {
								double rating = Double.parseDouble(ratingStr);
								courseEntity.setRating(rating);
								logger.info("rating=" + rating);
							} catch (NumberFormatException e) {
//								e.printStackTrace();
							}
						}

						if (ParserUtils.checkAttributeContainsKey(startElement, "class", "count-rate")) {
							/*<div class="count-rate" xpath="1">27 đánh giá</div>*/
							String ratingNumberStr = ParserUtils.getContentAndJumpToEndElement(staxReader, startElement);

							int ratingNumber = 0;
							//get rating number for ratingNumberStr
							for (int i = 0; i < ratingNumberStr.length(); i++) {
								if (ratingNumberStr.charAt(i) >= '0'
										&& ratingNumberStr.charAt(i) <= '9') {
									int charNum = ratingNumberStr.charAt(i) - '0';
									ratingNumber = ratingNumber * 10 + charNum;
								}
							}

							logger.info("ratingNumber=" + ratingNumber);
							courseEntity.setRatingNumber(ratingNumber);

						}


					}
				}


				overviewDescription = Formater.toRoot(overviewDescription);
				logger.info("FullOverviewDes=" + overviewDescription);
				courseEntity.setOverviewDescription(overviewDescription);


				authorDes = Formater.toRoot(authorDes);
				courseEntity.setAuthorDescription(authorDes);
				logger.info("AuthorDes=" + authorDes);


				//if no exception then save to data base

				logger.info("FinishAllAttributes|" + courseEntity.toString());
				// set hashing
				courseEntity.setHash(courseEntity.hashCourse());



				// validate course and save to db
				CourseDAO.getInstance().validateCourseAndSaveToDB(courseEntity);
//				other.DummyDatabase.validateCourseAndSaveToDB(courseEntity);

			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
			logger.info("END THREAD");
		} catch (Exception e) {
			e.printStackTrace();

		}


	}
}
