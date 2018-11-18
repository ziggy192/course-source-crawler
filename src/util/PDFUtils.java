package util;

import dao.CategoryDAO;
import dao.DomainDAO;
import entity.CourseEntity;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.fop.apps.*;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PDFUtils {
	private static Object LOCK = new Object();


	public static void transformToPDF(OutputStream out) {

		try {

			FopFactoryBuilder builder = new FopFactoryBuilder(new File(".").toURI());
			DefaultConfigurationBuilder defaultConfigurationBuilder = new DefaultConfigurationBuilder();
			Configuration configuration = defaultConfigurationBuilder.buildFromFile(AppUtils.getFileWithRealPath("WEB-INF/foconfig.xml"));
			builder.setConfiguration(configuration);
			FopFactory fopFactory = builder.build();

			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

			//xml file
			File xmlFile = AppUtils.getFileWithRealPath("testing/Course.xml");

			//xsl file

			File foFile = AppUtils.getFileWithRealPath("test/testDetailPDF.fo");


			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			Source xmlSource = new StreamSource(xmlFile);

			Source foSource = new StreamSource(foFile);

			Result fopResult = new SAXResult(fop.getDefaultHandler());
			transformer.transform(foSource, fopResult);

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FOPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}


	}

	private static DocumentBuilder documentBuilder;

	public static Document getNewDocument() {
		synchronized (LOCK) {
			if (documentBuilder == null) {
				try {
					documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				} catch (ParserConfigurationException e) {

				}
			}
		}

		return documentBuilder.newDocument();
	}


	public static void transformCourseToPDF(OutputStream out, CourseEntity courseEntity) {

		try {

			Node xmlNode = getNewDocument();

			JAXBContext jaxbContext = JAXBContext.newInstance(courseEntity.getClass());
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.marshal(courseEntity, xmlNode);
			Source xmlSource = new DOMSource(xmlNode);

			String categoryName = CategoryDAO.getInstance().findByID(courseEntity.getCategoryId()).getName();
			String domainName = DomainDAO.getInstance().findByID(courseEntity.getDomainId()).getName();

			FopFactoryBuilder builder = new FopFactoryBuilder(new File(".").toURI());
			DefaultConfigurationBuilder defaultConfigurationBuilder = new DefaultConfigurationBuilder();
			Configuration configuration = defaultConfigurationBuilder.buildFromFile(AppUtils.getFileWithRealPath("WEB-INF/foconfig.xml"));
			builder.setConfiguration(configuration);
			FopFactory fopFactory = builder.build();
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

			//xml file
//			File xmlFile = AppUtils.getFileWithRealPath("testing/Course.xml");

			//xsl file
			File xslFile = AppUtils.getFileWithRealPath("xsl/detailPDF.xsl");
			Source xslSource = new StreamSource(xslFile);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer(xslSource);
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

			transformer.setParameter("date", dateFormat.format(new Date()));
			transformer.setParameter("categoryName", categoryName);
			transformer.setParameter("domainName", domainName);


			Result fopResult = new SAXResult(fop.getDefaultHandler());

			transformer.transform(xmlSource, fopResult);


		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FOPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
		} catch (JAXBException e) {

			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}


	}

	public static void transformToFo() {

		try {

			FopFactoryBuilder builder = new FopFactoryBuilder(new File(".").toURI());
			DefaultConfigurationBuilder defaultConfigurationBuilder = new DefaultConfigurationBuilder();
			Configuration configuration = defaultConfigurationBuilder.buildFromFile(AppUtils.getFileWithRealPath("WEB-INF/foconfig.xml"));
			builder.setConfiguration(configuration);
			FopFactory fopFactory = builder.build();

//			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

			//xml file
			File xmlFile = AppUtils.getFileWithRealPath("testing/Course.xml");

			//xsl file
			File xslFile = AppUtils.getFileWithRealPath("xsl/detailPDF.xsl");
			Source xslSource = new StreamSource(xslFile);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer(xslSource);

			Source xmlSource = new StreamSource(xmlFile);


//			Result fopResult = new SAXResult(fop.getDefaultHandler());
			Result fopResult = new StreamResult(AppUtils.getFileWithRealPath("testing/transformResult.fo"));
			transformer.transform(xmlSource, fopResult);

		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FOPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}


	}


}
