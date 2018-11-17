package util;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.fop.apps.*;
import org.xml.sax.SAXException;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class PDFUtils {
	public static void transformToPDF(OutputStream out) {

		try {

			FopFactoryBuilder builder = new FopFactoryBuilder(new File(".").toURI());
			DefaultConfigurationBuilder defaultConfigurationBuilder = new DefaultConfigurationBuilder();
			Configuration configuration = defaultConfigurationBuilder.buildFromFile(AppUtils.getFileWithRealPath("WEB-INF/foconfig.xml"));
			builder.setConfiguration(configuration);
			FopFactory fopFactory = builder.build();

			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

			//xml file
			File xmlFile = AppUtils.getFileWithRealPath("test/testXML.xml");

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
}
