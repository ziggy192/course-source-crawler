package util;

import com.sun.xml.internal.stream.events.EndElementEvent;
import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaxParserUtils {

	//	public static Logger logger = Logger.getLogger(StaxParserUtils.class.toString());
	public static Logger logger = Logger.getLogger(StaxParserUtils.class.toString());


	public static boolean isAlphaCharOrNumberChar(char x) {
		return (x >= 'a' && x <= 'z') || (x >= '0' && x <= '9');

	}

	private static String getTagName(String content) {
		//ex: <asdfasf>
		/* <asdfafd/> -> null
		 * <dasfasf> -> dasfasf
		 * </dasfasf> -> /dasfasf
		 *<!-- afasfasf -->
		 *
		 * */
		if (content.charAt(content.length() - 2) == '/') {
			return null;
		}

		String result = "";
		int i = 1;
		if (content.charAt(1) == '/') {
			result = result + '/';
			i++;
		}

		// old code from LeHungSon, wrong case for <h3>
		//		while (isAlphaChar(content.charAt(i))) {
		while (isAlphaCharOrNumberChar(content.charAt(i))) {
			result = result + content.charAt(i);
			i++;
		}

		if (result.length() == 0 || (result.length() == 1 && result.charAt(0) == '/')) {
			// "" || "/"
			return null;
		}
		return result;
	}

	private static String removeOnClickAttributes(String tag) {
		String onlickRegex = "onclick=\\'.+?\\'";
		String onclickRegex2 = "onclick=\\\".+?\\\"";
		return tag.replaceAll(onlickRegex, "")
				.replaceAll(onclickRegex2, "");
	}

	private static String getRealTagnameWithRegex(String tag) {
		//WARNING: not return '/' in tagname

		String tagNameRegex = "(</?\\s?)([^\\s>/]+)";
		Pattern pattern = Pattern.compile(tagNameRegex);
		Matcher matcher = pattern.matcher(tag);
		if (matcher.find()) {
			//count from 1
			String tagName = matcher.group(2);
			return tagName;
		}

		return null;

	}
	private static String removeEmptyAttFromHtmlTag(String tag) {


		if (tag.contains("input placeholder=\"Name *\"")) {
			logger.info(tag);
		}
		//remove onclick=''
		tag = removeOnClickAttributes(tag);

		String tagName = getRealTagnameWithRegex(tag);

		if (tagName == null) {
			return tag;
		}

		StringBuilder result = new StringBuilder();
		result.append("<");
		if (tag.charAt(1) == '/') {
			result.append("/");
		}
		result.append(tagName);

		String regExForAtt = "(\\S+)=[\"'].*?[\"']";
		Pattern pattern = Pattern.compile(regExForAtt);
		Matcher matcher = pattern.matcher(tag);
		while (matcher.find()) {

			String att = tag.substring(matcher.start(), matcher.end());
			result.append(" ");
			result.append(att);
			result.append(" ");

		}

		if (tag.trim().endsWith("/>")) {
			result.append("/>");
		} else {
			result.append(">");
		}

		return result.toString();
	}

	public static String removeEmptyAttributes(String content) {
		int i = 0;

		StringBuilder result = new StringBuilder();
		while (i < content.length()) {
			if (content.charAt(i) == '<') {

				int j = content.indexOf('>', i);
				if (j != -1) {
					String tag = content.substring(i, j+1);
					// process tag
					String processedTag = removeEmptyAttFromHtmlTag(tag);
					i = j+1;
					result.append(processedTag);

				} else {
					result.append(content.substring(i));
					break;
				}
			} else {
				result.append(content.charAt(i));
				i++;

			}
		}

		return result.toString();
	}

	public static String addMissingTag(String content) {
		List<String> stack = new ArrayList<>();
		List<Integer> positionStack = new ArrayList<>();
		List<String> tagsNeedToAdd = new ArrayList<>();

		int mark[] = new int[content.length()];
		// mark[j]: the position of the tagsNeedToAdd list in the char at j
		// tagsNeedToAdd.get(mark[j])

		Arrays.fill(mark, -1);


		int i = 0;

		while (i < content.length()) {
			if (content.charAt(i) == '<') {
				int j = i + 1;
				String tagTmp = "" + content.charAt(i);

				while (j < content.length() && content.charAt(j) != '>') {
					tagTmp = tagTmp + content.charAt(j);
					j++;
				}


				tagTmp = tagTmp + '>';
				//remove attributes without value
				//  adfasf="safsf" item asfsf="asfsdf


				int curEnd = j;
				i = j + 1;
				String tag = getTagName(tagTmp);
				if (tag != null) {
					if (tag.charAt(0) != '/') {
						//is open tag
						//add to stack
						stack.add(tag);
						positionStack.add(curEnd);
					} else {

						//is closing tag
						while (stack.size() > 0) {
							if (stack.get(stack.size() - 1).equals(tag.substring(1))) {
								//equals top of the stack
								stack.remove(stack.size() - 1);
								positionStack.remove(positionStack.size() - 1);
								break;

							} else {
								//not equals top of the stack
								tagsNeedToAdd.add(stack.get(stack.size() - 1));
								mark[positionStack.get(positionStack.size() - 1)] = tagsNeedToAdd.size() - 1;

								//remove from stack
								stack.remove(stack.size() - 1);
								positionStack.remove(positionStack.size() - 1);

							}
						}
					}
				}
			} else {
				//not '<'
				i++;
			}
		}
		while (stack.size() > 0) {
			tagsNeedToAdd.add(stack.get(stack.size() - 1));
			mark[positionStack.get(positionStack.size() - 1)] = tagsNeedToAdd.size() - 1;

			stack.remove(stack.size() - 1);
			positionStack.remove(positionStack.size() - 1);
		}


		// fix the string

		String newContent = "";

		for (int j = 0; j < content.length(); j++) {
			newContent = newContent + content.charAt(j);
			if (mark[j] != -1) {
				newContent = newContent + "</" + tagsNeedToAdd.get(mark[j]) + ">";

			}
		}
		return "<root>" + newContent + "</root>";
	}


	public static String getContentAndJumpToEndElement(XMLEventReader eventReader, XMLEvent currentEvent) throws XMLStreamException {


		String content = "";

//		if (currentEvent.isStartElement()) {
//
//		} else {
//
//		}


		int stackCount = 0;
		if (currentEvent.isStartElement()) {
			stackCount = 1;
		}
		while (eventReader.hasNext()) {

			XMLEvent event = eventReader.nextEvent();
			if (event.isStartElement()) {
				stackCount++;
			}
			if (event.isCharacters()) {
				content += event.asCharacters().getData();
			}
			if (event.isEndElement()) {
				stackCount--;
			}

			if (stackCount <= 0) {
				break;
			}
		}

		return content;

	}

	public static XMLEvent nextStartEvent(XMLEventReader reader, String startEventName) throws XMLStreamException {
		XMLEvent event;
		do {
			event = reader.nextEvent();
		} while (!(event.isStartElement()
				&& event.asStartElement().getName().getLocalPart().equals(startEventName)));
		return event;
	}

	public static XMLEvent nextStartEvent(XMLEventReader reader, String[] classAttributeValues) throws XMLStreamException {
		return nextStartEvent(reader, "", classAttributeValues);
	}

	public static XMLEvent nextStartEvent(XMLEventReader reader, String startEventName, String[] classAttributeValues) throws XMLStreamException {
		XMLEvent event;
		boolean found = false;


		do {
			event = reader.nextEvent();
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				if (
						startEventName.isEmpty()
								|| startElement.getName().getLocalPart().equals(startEventName)) {
					Attribute classAttribute = startElement.getAttributeByName(new QName("class"));
					if (classAttribute != null) {
//						found = true;
						found = StaxParserUtils.checkAttributeContainsKey(startElement, "class", classAttributeValues);
//						for (String classAttributeValue : classAttributeValues) {
//							if (!classAttribute.getValue().contains(classAttributeValue)) {
//								found = false;
//							}
//						}
					}
				}

			}
		} while (!found);
		return event;
	}

	public static XMLEvent nextStartEvent(XMLEventReader reader) throws XMLStreamException {

		XMLEvent event;
		do {
			event = reader.nextEvent();
		} while (!event.isStartElement());
		return event;

	}

	public static String getHTMLPage(String uri) {
		String htmlContent = "";


		return htmlContent;
	}

	public static Iterator<XMLEvent> traverseToEndTagAndAddMissingTags(XMLEventReader reader) {
		ArrayList<XMLEvent> eventArrayList = new ArrayList<>();
		int endTagMarker = 0;
		while (endTagMarker >= 0) {

			XMLEvent event = null;
			try {
				event = reader.nextEvent();
			} catch (XMLStreamException e) {
				String msg = e.getMessage();
				String msgErrorString = "The element type \"";
				if (msg.contains(msgErrorString)) {
					String missingTagName =
							msg.substring(msg.indexOf(msgErrorString) + msgErrorString.length(), msg.indexOf("\" must be terminated"));
					EndElement missingTag = new EndElementEvent(new QName(missingTagName));
					event = missingTag;
				}

			} catch (NullPointerException ex) {
				// next until end of document
				break;

			}

			if (event != null) {
				if (event.isStartElement()) {
					endTagMarker++;
				} else if (event.isEndElement()) {
					endTagMarker--;
				}
				if (endTagMarker >= 0) {
					eventArrayList.add(event);
				}
			}
		}

		return eventArrayList.iterator();
	}

	public static void printData(Iterator<XMLEvent> iterator) {
		String result = "";
		while (iterator.hasNext()) {
			XMLEvent event = iterator.next();
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();

				result += "<" + startElement.getName().toString();

				//get attributes
				Iterator attributeIterator = startElement.getAttributes();
				while (attributeIterator.hasNext()) {
					Attribute attr = (Attribute) attributeIterator.next();
					String value = attr.getValue().replace("&", "&#38");
					result += " " + attr.getName().toString() + "\"" + value + "\"";


				}
				result += ">";
			}

			if (event.isCharacters()) {
				Characters characters = event.asCharacters();
				if (!characters.isWhiteSpace()) {
					result += characters.getData().replace("&", "&#38").trim();

				}
			}

			if (event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				result += endElement.toString();
			}

		}
		System.out.println(result);

	}

	public static XMLEventReader transformToEventReader(XMLStreamReader streamReader) throws XMLStreamException {
		XMLInputFactory factory = getXMLInputFactory();
		return factory.createXMLEventReader(streamReader);
	}


	private static XMLInputFactory getXMLInputFactory() {
		XMLInputFactory factory = XMLInputFactory.newInstance();

		factory.setProperty(XMLInputFactory.IS_VALIDATING, false);
		factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		return factory;
	}

	public static XMLEventReader getStaxReader(String content) throws XMLStreamException {
		XMLInputFactory xmlInputFactory = getXMLInputFactory();
		XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(
				new StringReader(content));
//		this.wait();
//		this.notifyAll();


		return xmlEventReader;

	}

	public static Document getDOM(String content) throws ParserConfigurationException, IOException, SAXException {


		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document doc = documentBuilder.parse(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));

		return doc;


	}

	public static XPath createXpath() {

		XPathFactory xPathFactory = XPathFactory.newInstance();
		XPath xPath = xPathFactory.newXPath();
		return xPath;
	}

	public void validateXMLBeforeSaveToDB(InputStream xmlInputStream, Object objectToSave) {

		//todo xsd file path
		final String xsdFilePath = "";
		try {
			JAXBContext context = JAXBContext.newInstance(objectToSave.getClass());
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(new File(xsdFilePath));
			Validator validator = schema.newValidator();
			// todo setErrorHandler here
			validator.validate(new StreamSource(xmlInputStream));

			//todo save to db


		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	public static void saveHtmlToFile(String uri, File file) {
		try {
			if (!uri.startsWith("https://") && !uri.startsWith("http://") && !uri.startsWith("//")) {
				uri = "https://" + uri;
			}
			URL url = new URL(uri);
			URLConnection connection = url.openConnection();
			connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
			InputStream inputStream = connection.getInputStream();
			Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void saveStringToFile(String content, File file) {

		try {
			PrintWriter writer = new PrintWriter(file);
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static String parseDebuggingHtml(File htmlFile, String beginSign, String endSign) {
		StringBuilder htmlBuilder = new StringBuilder();

		boolean isInside = false;


		try {
			InputStream inputStream = new FileInputStream(htmlFile);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			String inputLine;

			while ((inputLine = bufferedReader.readLine()) != null) {
//				System.out.println(inputLine);
				if (inputLine.contains(beginSign)) {
					isInside = true;
				}

				if (isInside) {

					int beginIndex = 0;
					if (inputLine.contains(beginSign) && beginSign.startsWith("<")) {
						beginIndex = inputLine.indexOf(beginSign);
					}

					int endIndex = inputLine.length();
					if (inputLine.contains(endSign) && endSign.startsWith("<")) {
						endIndex = inputLine.indexOf(endSign);
					}

					//for small performance optimize
					if (beginIndex != 0 || endIndex != inputLine.length()) {

						htmlBuilder.append(inputLine.substring(beginIndex, endIndex));
					} else {
						htmlBuilder.append(inputLine);
					}

				}

				if (isInside && inputLine.contains(endSign)) {
					isInside = false;
					break;
				}

			}

			inputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}


		String content = htmlBuilder.toString();
		content = StringEscapeUtils.unescapeHtml4(content);
		content = replaceEntities(content);
		content = removeComment(content);
		return content;
	}

	public static String parseHtml(String uri, String beginSign, String endSign) {
		if (!uri.startsWith("https://") && !uri.startsWith("http://") && !uri.startsWith("//")) {
			uri = "https://" + uri;
		}
		StringBuilder htmlBuilder = new StringBuilder();

		boolean isInside = false;


		try {
			URL url = new URL(uri);
			URLConnection connection = url.openConnection();
			connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");

			InputStream inputStream = connection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			String inputLine;

			while ((inputLine = bufferedReader.readLine()) != null) {
//				System.out.println(inputLine);
				if (inputLine.contains(beginSign)) {
					isInside = true;
				}

				if (isInside) {

					int beginIndex = 0;
					if (inputLine.contains(beginSign) && beginSign.startsWith("<")) {
						beginIndex = inputLine.indexOf(beginSign);
					}

					int endIndex = inputLine.length();
					if (inputLine.contains(endSign) && endSign.startsWith("<")) {
						endIndex = inputLine.indexOf(endSign);
					}

					//for small performance optimize
					if (beginIndex != 0 || endIndex != inputLine.length()) {

						htmlBuilder.append(inputLine.substring(beginIndex, endIndex));
					} else {
						htmlBuilder.append(inputLine);
					}

				}

				if (isInside && inputLine.contains(endSign)) {
					isInside = false;
					break;
				}

			}

			inputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}


		String content = htmlBuilder.toString();
		//remove before unescaping because of &quotes;
		content = StringEscapeUtils.unescapeHtml4(content);
		content = replaceEntities(content);
		content = removeComment(content);
		return content;
	}


	public static String replaceEntities(String content) {
		content = content
				.replace("&", "&amp;")
				.replace("\t", "")
				.replace("\n", "")
				.replace("<br>", "")
				.replace("</br>", "");

		return content;
	}

	public static boolean checkAttributeContainsKey(XMLEvent eventElement, String attributeName, String key) {
//
		return checkAttributeContainsKey(eventElement, attributeName, new String[]{key});
	}


	public static boolean checkAttributeContainsKey(XMLEvent eventElement, String attributeName, String[] keys) {
		// check if having any attribute EQUALS 1 of those keys
		if (eventElement.isStartElement()) {
			StartElement startElement = eventElement.asStartElement();

			Attribute attribute = startElement.getAttributeByName(new QName(attributeName));
			if (attribute != null) {
				//delimiter ' '
				String attValue = attribute.getValue();
				List<String> attValueItems = Arrays.asList(attValue.split(" "));
				for (String key : keys) {
					if (!attValueItems.contains(key)) {
						//list.contains() already use "equals()" to compare -> good
						return false;
					}
				}

				return true;
			}

		}
		return false;
	}
//
//	public static boolean checkAttributeEqualsKey(XMLEvent eventElement, String attributeName, String key) {
//
//		if (eventElement.isStartElement()) {
//			StartElement startElement = eventElement.asStartElement();
//			Attribute attribute = startElement.getAttributeByName(new QName(attributeName));
//			if (attribute != null) {
//				return attribute.getValue().trim().equals(key.trim());
//
//			}
//		}
//		return false;
//	}

	public static String getAllHtmlContentAndJumpToEndElement(XMLEventReader reader, XMLEvent currentEvent) throws XMLStreamException {
		String result = "";

//		if (currentEvent.isStartElement()) {
//
//		} else {
//
//		}


		int stackCount = 0;
		if (currentEvent.isStartElement()) {
			stackCount = 1;
		}
		while (reader.hasNext()) {

			XMLEvent event = reader.nextEvent();
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				stackCount++;
				result += "<";

				result += startElement.getName().getLocalPart();


				Iterator attIterator = startElement.getAttributes();
				// <div class='asfsaf asfasf'>
				//html dont have prefix !
				while (attIterator.hasNext()) {
					Attribute att = (Attribute) attIterator.next();
					result += String.format(" %s='%s'",
							att.getName().getLocalPart()
							, att.getValue());
				}
				result += ">";


			}


			if (event.isCharacters()) {
				result += event.asCharacters().getData();
			}
			if (event.isEndElement()) {
				stackCount--;
				EndElement endElement = event.asEndElement();
				if (currentEvent.isStartElement() && stackCount > 0) {
					result += String.format("</%s>", endElement.getName().getLocalPart());
				}
			}

			if (stackCount <= 0) {
				break;
			}
		}

		return result;
	}

	private static String formatQNameWithPrefix(QName qName) {
		String result = "";
		if (qName.getPrefix().isEmpty()) {
			result = qName.getLocalPart();

		} else {

			result = String.format("%s:%s",
					qName.getPrefix(),
					qName.getLocalPart());
		}
		return result;
	}


	public static String getAttributeByName(StartElement startElement, String attributeName) {
		Attribute attribute = startElement.getAttributeByName(new QName(attributeName));
		if (attribute != null) {
			return attribute.getValue();
		}
		return "";
	}

	public static String removeComment(String content) {
		//remove all line breaks
		content = content.replace("\n", "");
		String commentPattern = "<!--.*?-->";
		return content.replaceAll(commentPattern, "");
	}


}
