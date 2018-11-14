package crawler;

import config.ConfigManager;
import config.model.SignType;
import dao.CourseDAO;
import entity.CourseEntity;
import sun.java2d.xr.MutableInteger;
import url_holder.CourseUrlHolder;
import util.Formater;
import util.StaxParserUtils;
import util.StringUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class EdumallCourseDetailCrawler implements Runnable {
	private static Logger logger = Logger.getLogger(EdumallCourseDetailCrawler.class.toString());


	private CourseUrlHolder courseDetailUrlHolder;
	private int categoryId;

	public EdumallCourseDetailCrawler(CourseUrlHolder courseDetailUrlHolder, int categoryId) {
		this.courseDetailUrlHolder = courseDetailUrlHolder;
		this.categoryId = categoryId;
	}


	private int toDuration(String durationStr) {
		// hh:mm:ss
		int duration = -1;
		try {
			String[] splitResult = durationStr.split(":");
			int hours = Integer.parseInt(splitResult[0]);
			int minutes = Integer.parseInt(splitResult[1]);
			int seconds = Integer.parseInt(splitResult[2]);

			duration = hours * 3600 + minutes * 60 + seconds;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			duration = -1;
		}


		return duration;
	}

	@Override
	public void run() {


		try {
			logger.info("start thread");

			synchronized (CrawlingThreadManager.getInstance()) {
				while (CrawlingThreadManager.getInstance().isSuspended()) {
					CrawlingThreadManager.getInstance().wait();
				}
			}

//		String uri = "https://edumall.vn/course/guitar-dem-hat-trong-30-ngay";
			String uri = courseDetailUrlHolder.getCourseUrl();

//			String beginSign = "<div class='wrapper'";
//			String endSign = "<section class='section_rating";
			SignType courseDetailSign = ConfigManager.getInstance().getConfigModel().getEdumall().getCourseDetailSign();
			String beginSign =courseDetailSign.getBeginSign();
			String endSign = courseDetailSign.getEndSign();


			String htmlContent = StaxParserUtils.parseHTML(uri, beginSign, endSign);
			htmlContent = StaxParserUtils.addMissingTag(htmlContent);


			String overviewDescription = "";
			String videoUrl;
			double costValue = 0;

			CourseEntity courseEntity = new CourseEntity();

			courseEntity.setCategoryId(categoryId);


			courseEntity.setSourceUrl(courseDetailUrlHolder.getCourseUrl());
			courseEntity.setName(courseDetailUrlHolder.getCourseName());
			courseEntity.setImageUrl(courseDetailUrlHolder.getCourseThumbnailUrl());

			//todo get providerid from EdumallMainCrawler static or singleton
			//get providerid = 'edumall'
			courseEntity.setDomainId(EdumallMainCrawler.domainId);

			try {
				XMLEventReader staxReader = StaxParserUtils.getStaxReader(htmlContent);
				while (staxReader.hasNext()) {
					XMLEvent event = staxReader.nextEvent();
					if (event.isStartElement()) {
						StartElement startElement = event.asStartElement();
						Iterator<Attribute> attributes = startElement.getAttributes();


						//todo get VideoURL

						//<iframe allowfullscreen="" frameborder="0" height="100%" src="https://www.youtube.com/embed/cyGq22d1sbk?modestbranding=0&amp;amp;rel=0&amp;amp;showinfo=0" width="100%"></iframe>
						if (startElement.getName().getLocalPart().equals("iframe")
							//								&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "ytp-title-link")
							//								&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "yt-uix-sessionlink")
						) {

							Attribute srcAtt = startElement.getAttributeByName(new QName("src"));

							if (srcAtt != null) {
								videoUrl = srcAtt.getValue();
//								logger.info("VideURL=" + videoUrl);
								courseEntity.setPreviewVideoUrl(videoUrl);

							}

						}


						//todo getcost
						//<p class="col-md-12 col-xs-12 price" style="" xpath="1">699,000đ</p>

						if (startElement.getName().getLocalPart().equals("p")
								&& (StaxParserUtils.checkAttributeContainsKey(startElement, "class", new String[]{
								"col-md-12", "col-xs-12", "price"})
								|| StaxParserUtils.checkAttributeContainsKey(startElement, "class", new String[]{
								"price_sale"
						}))
						) {
							String cost = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
							if (!cost.isEmpty()) {

								//get int value from cost
								costValue = StringUtils.getNumberValueFromString(cost);

//								logger.info("Cost=" + costValue);
								courseEntity.setCost(costValue);
							}
						}

						//todo get duration
						//<div class="form-layout col-xs-12" xpath="1">
						//<i class="fas fa-clock pull-left padding_icon"></i>
						//<span class="pull-left">Thời lượng video</span>
						//<span class="pull-right">05:58:48</span>
						//</div>
						if (startElement.getName().getLocalPart().equals("div")
								&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "prop")) {
							StaxParserUtils.nextStartEvent(staxReader, "i", new String[]{
									"fas", "fa-clock"
							});
							startElement = StaxParserUtils.nextStartEvent(staxReader, "span", new String[]{"pull-right"}).asStartElement();
							String durationStr = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
							if (!durationStr.isEmpty()) {
								int duration = toDuration(durationStr);
//								logger.info("Duration In seconds=" + duration);
								courseEntity.setDuration(duration);
							}
						}

						//todo get author name
						/*
						<section class="section_author" id="general-author-tab">
						<div class="info_author" style="" xpath="1">
						<div class="pull-left image_author">
						<img src="//d303ny97lru840.cloudfront.net/kelley-57bfb0d3ce4b1438274ba1fd/20160829-hiennt02-edumall/533079_556033297754623_542734647_n.jpg">
						</div>
						<div class="pull-left name_author" style="">
						<p class="author" style="font-size: 16px;">Giảng viên</p>
						<div class="name" style="">Nguyễn Thượng Hiển</div>

						</div>
						</div>*/
						if (startElement.getName().getLocalPart().equals("section")
								&& StaxParserUtils.checkAttributeContainsKey(startElement, "id", "general-author-tab")) {
							//traverse to end section
							String authorInfo = Formater.toHeading2("Tiểu sử");

							int stackCount = 1;

							boolean insideAuthorInfo = false;

							//						List<String> contentList = new ArrayList<>();
							while (stackCount > 0) {
								event = staxReader.nextEvent();
								if (event.isStartElement()) {
									stackCount++;
									startElement = event.asStartElement();

									if (startElement.getName().getLocalPart().equals("div")
											&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "name")) {
										String authorName = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
										stackCount--;
										if (!authorName.isEmpty()) {
//											logger.info("Author=" + authorName);
											courseEntity.setAuthor(authorName);
										}
									}


									//<img src="//d303ny97lru840.cloudfront.net/kelley-57bfb0d3ce4b1438274ba1fd/20160829-hiennt02-edumall/533079_556033297754623_542734647_n.jpg" style="">
									if (startElement.getName().getLocalPart().equals("img")) {
										String imageUrl = startElement.getAttributeByName(new QName("src")).getValue();


//										logger.info("AuthorImage=" + imageUrl);

										imageUrl = StringUtils.beautifyUrl(imageUrl, ConfigManager.getInstance().getConfigModel().getEdumall().getDomainUrl());
										courseEntity.setAuthorImageUrl(imageUrl);
									}
									if (StaxParserUtils.checkAttributeContainsKey(startElement, "id", "author_info")) {
										insideAuthorInfo = true;

									}
									if (insideAuthorInfo) {
										if (startElement.getName().getLocalPart().equals("li")) {
											String content = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
											stackCount--;
											content = Formater.toParagraph(content);
											authorInfo += content;
										}
									}
								}
								if (event.isEndElement()) {
									stackCount--;

								}

							}

							authorInfo = Formater.toRoot(authorInfo);
//							logger.info("AuthorInfo=" + authorInfo);

							courseEntity.setAuthorDescription(authorInfo);
						}

						// TODO: get overview description

						//loi ich tu khoa hoc
						/*<section class="section_benefit section-setting" id="general-info-tab" data-gtm-vis-first-on-screen-8263996_483="1091" data-gtm-vis-total-visible-time-8263996_483="5000" data-gtm-vis-has-fired-8263996_483="1" style="" xpath="1">
						<p class="title" style="">Lợi ích từ khóa học</p>
						<div class="content" style="">
						<div class="content_benefit">
						<i class="fas fa-check-circle"></i>
						<span> Nắm vững nhạc lý: Cách đọc tọa độ, bấm hợp âm, tiết tấu; Cách rải âm và quạt chả.</span>
						</div>
						<div class="content_benefit">
						<i class="fas fa-check-circle"></i>
						<span> Thành thạo các điệu cơ bản: Surf nhanh - chậm, Disco, Blue, Ballad, Báo, Fox, Valse, Bolero, Slow Rock,...</span>
						</div>
						<div class="content_benefit">
						<i class="fas fa-check-circle"></i>
						<span> Thành thạo cách dò các nhịp, điệu của một bài hát, bắt nhịp và chọn điệu, bắt tông cho ca sĩ, đánh intro và outro, search hợp âm chuẩn,...</span>
						</div>
						<div class="content_benefit">
						<i class="fas fa-check-circle"></i>
						<span> Biết cách chọn đàn sao cho phù hợp với mục đích, túi tiền và phong cách nhưng vẫn phải đảm bảo những yêu tố thiết yếu.</span>
						</div>
						</div>
						<div class="clear"></div>
						</section>*/
						if (startElement.getName().getLocalPart().equals("section")
								&& StaxParserUtils.checkAttributeContainsKey(startElement, "id", "general-info-tab")) {

							//traverse to end section
							int stackCount = 1;
							List<String> contentList = new ArrayList<>();
							while (stackCount > 0) {
								event = staxReader.nextEvent();
								if (event.isStartElement()) {
									stackCount++;
									startElement = event.asStartElement();

									if (StaxParserUtils.checkAttributeContainsKey(startElement, "class", "title")) {
										//title
										String title = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
										stackCount--; // getContentAndJumpToEndElement() traverse to EndElement

										title = Formater.toHeading1(title);

										overviewDescription = overviewDescription + title;


									}

									if (StaxParserUtils.checkAttributeContainsKey(startElement, "class", "content_benefit")) {
										//content benefit
										startElement = StaxParserUtils.nextStartEvent(staxReader, "span").asStartElement();
										MutableInteger mutableStack = new MutableInteger(stackCount);

										String contentBenefit = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
										stackCount--; // getContentAndJumpToEndElement() traverse to EndElement
										contentList.add(contentBenefit);
									}

								}
								if (event.isEndElement()) {
									stackCount--;

								}

							}
							overviewDescription += Formater.toList((contentList.toArray(new String[contentList.size()])));
//							logger.info("OverviewPar1=" + overviewDescription);

						}


						//Doi tuong muc tieu && Yeu cau khoa hoc
						/*
						*<section class="section_requirement section-setting" data-gtm-vis-first-on-screen-8263996_503="1091" data-gtm-vis-total-visible-time-8263996_503="5000" data-gtm-vis-has-fired-8263996_503="1" style="" xpath="1">
							<p class="title">Đối tượng mục tiêu</p>
							<ul class="required">
							<div class="cover-require col-md-12">
							<li class="require"> Yêu thích âm nhạc và có cảm hứng đặc biệt với những cây đàn Guitar.</li>
							</div>
							<div class="clear"></div>
							<div class="cover-require col-md-12">
							<li class="require"> Muốn học Guitar đệm hát nhưng chưa biết bắt đầu từ đâu</li>
							</div>
							<div class="clear"></div>
							<div class="cover-require col-md-12">
							<li class="require"> Muốn học Guitar nhưng bị giới hạn về thời gian và tài chính</li>
							</div>
							<div class="clear"></div>
							<div class="cover-require col-md-12">
							<li class="require"> Học đệm hát để chơi các bài hát mà mình yêu thích</li>
							</div>
							<div class="clear"></div>
							</ul>
							</section>
						*/
						if (startElement.getName().getLocalPart().equals("section")
								&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "section_requirement")
						) {
							//traverse to end section
							int stackCount = 1;
							List<String> contentList = new ArrayList<>();

							while (stackCount > 0) {
								event = staxReader.nextEvent();
								if (event.isStartElement()) {
									stackCount++;
									startElement = event.asStartElement();

									//title
									if (StaxParserUtils.checkAttributeContainsKey(startElement, "class", "title")) {
										String title = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
										stackCount--; // getContentAndJumpToEndElement() traverse to EndElement

										overviewDescription += Formater.toHeading1(title);

									}

									//list
									if (startElement.getName().getLocalPart().equals("li")) {
										String listItem = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
										stackCount--; // getContentAndJumpToEndElement() traverse to EndElement

										contentList.add(listItem);
									}


								}
								if (event.isEndElement()) {
									stackCount--;

								}

							}

							overviewDescription += Formater.toList(contentList.toArray(new String[contentList.size()]));
//							logger.info("OverviewPart2" + overviewDescription);


						}

						//Tong quat
						/*<section class="section_description section-setting" data-gtm-vis-first-on-screen-8263996_504="1619" data-gtm-vis-total-visible-time-8263996_504="5000" data-gtm-vis-has-fired-8263996_504="1" xpath="1">
						<div class="title">Tổng quát</div>
						<div class="description-content show-more-content" id="show_more_des">
						<div class="text_description"><p>Khóa học gồm :<br>- 6 học phần<br>- 77 bài giảng được hướng dẫn cụ thể từ giảng viên<br>- 5 cấp độ từ cơ bản đến nâng cao<br>- Hệ thống tài liệu chi tiết cho từng học phần.</p></div>
						</div>
						<div class="show_more" id="show_more_des_button">XEM THÊM</div>
						</section>
						*/

						if (startElement.getName().getLocalPart().equals("section")
								&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "section_description")) {
							//traverse to end section
							int stackCount = 1;

							while (stackCount > 0) {
								event = staxReader.nextEvent();
								if (event.isStartElement()) {
									stackCount++;
									startElement = event.asStartElement();

									//title
									if (StaxParserUtils.checkAttributeContainsKey(startElement, "class", "title")) {
										String title = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
										stackCount--; // getContentAndJumpToEndElement() traverse to EndElement

										overviewDescription += Formater.toHeading1(title);

									}

									//content
									if (startElement.getName().getLocalPart().equals("div")
											&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "text_description")) {
										String htmlDescriptionContent = StaxParserUtils.getAllHtmlContentAndJumpToEndElement(staxReader, startElement);
										stackCount--;
										overviewDescription += htmlDescriptionContent;
									}
								}
								if (event.isEndElement()) {
									stackCount--;

								}
							}

//							logger.info("OverviewPart3=" + overviewDescription);

						}


						//todo get syllabus
						/*
						 * <div id='section_lecture'>
						 * 		<li class='menu'>
						 * 			<div class='menu-title>
						 *
						 *
						 *			<div class='lecture-title>
						 *
						 * -->    <h3>title</h3>
						 * 		<ul>
						 * 			<li>bai 1</li>
						 * */


						if (StaxParserUtils.checkAttributeContainsKey(startElement, "id", "section_lecture")) {
							//traverse
							String syllabus = "";
							int stackCount = 1;
							List<String> lectureNameList = new ArrayList<>();
							while (stackCount > 0) {
								event = staxReader.nextEvent();

								if (event.isStartElement()) {
									stackCount++;
									startElement = event.asStartElement();
									if (StaxParserUtils.checkAttributeContainsKey(startElement, "class", "menu-title")) {

										String content = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
										stackCount--;
										content = Formater.toHeading3(content);

										if (lectureNameList.size() > 0) {
											syllabus += Formater.toList(lectureNameList.toArray(new String[lectureNameList.size()]));
											lectureNameList = new ArrayList<>();
										}


										syllabus += content;

									}
									if (StaxParserUtils.checkAttributeContainsKey(startElement, "class", "lecture_title")) {
										String lectureName = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
										stackCount--;
										lectureNameList.add(lectureName);
									}

								}
								if (event.isEndElement()) {
									stackCount--;

								}

							}

							//dequeue the last lectureNamelist

							if (lectureNameList.size() > 0) {
								syllabus += Formater.toList(lectureNameList.toArray(new String[lectureNameList.size()]));
							}

							syllabus = Formater.toRoot(syllabus);
							courseEntity.setSyllabus(syllabus);
//							logger.info("Syllabus=" + syllabus);

						}
						//lecture-name

						//todo get rating && rating number
						//div class="intro_course'
						//<div class="star-rating" xpath="1">
						//<div class="back-stars">

						//<div class="front-stars" style="width: 100.0%">

						//</div>
						//</div>
						//<b style="padding-left:10px; padding-right:10px;">5.0</b>
						//(153)
						//</div>
						if (startElement.getName().getLocalPart().equals("div")
								&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "intro_course")) {
							//						startElement = nextStartEvent(staxReader, "div", new String[]{"star-rating"}).asStartElement();
							//					}
							//					if (startElement.getName().getLocalPart().equals("div")
							//							&& StaxParserUtils.checkAttributeContainsKey(startElement, "class", "star-rating")
							//					) {
							startElement = StaxParserUtils.nextStartEvent(staxReader, "b").asStartElement();

							String rating = StaxParserUtils.getContentAndJumpToEndElement(staxReader, startElement);
							try {
								double ratingDouble = Double.parseDouble(rating);
								courseEntity.setRating(ratingDouble);
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
//							logger.info("Rating=" + rating);

							event = staxReader.nextEvent();
							if (event.isCharacters()) {
								String ratingNumberStr = event.asCharacters().getData();
								ratingNumberStr = ratingNumberStr.substring(1, ratingNumberStr.length() - 1);
								int ratingNumber = 0;
								try {
									ratingNumber = Integer.parseInt(ratingNumberStr);
								} catch (NumberFormatException e) {
									e.printStackTrace();
								}
								courseEntity.setRatingNumber(ratingNumber);
//								logger.info("RatingNumber=" + ratingNumber);
							}
						}


					}
				}


				overviewDescription = Formater.toRoot(overviewDescription);
//				logger.info("FullOverviewDes=" + overviewDescription);
				courseEntity.setOverviewDescription(overviewDescription);



				//set hash
				courseEntity.setHash(courseEntity.hashCourse());

				//if no exception then save to data base

				//todo save to db here
				//get name, image, url from holder
				//get the rest except tag from above
				//get categoryid



				CourseDAO.getInstance().validateCourseAndSaveToDB(courseEntity);
//				other.DummyDatabase.validateCourseAndSaveToDB(courseEntity);

			} catch (XMLStreamException e) {
				e.printStackTrace();
			}

			synchronized (CrawlingThreadManager.getInstance()) {
				while (CrawlingThreadManager.getInstance().isSuspended()) {
					CrawlingThreadManager.getInstance().wait();
				}
			}

			logger.info("END THREAD");
		} catch (Exception e) {
			e.printStackTrace();

		}


	}


}
