package test;

import dao.CourseDAO;
import entity.CourseEntity;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;
import util.AppUtils;
import util.PDFUtils;
import util.StaxParserUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class UtilsTest {

	public static Logger logger = LoggerFactory.logger(UtilsTest.class);

	public static void main(String[] args) throws IOException {
//		testRemoveEmptyAttributes();
//		testPDF();
//		testXSL();
		testPDFWithXSL();
	}

	public static void testXSL() {
		try {
			PDFUtils.transformToFo();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public static void testPDFWithXSL() {
		try {
			CourseEntity courseEntity = CourseDAO.getInstance().findByID(3409);
			PDFUtils.transformCourseToPDF(new FileOutputStream(AppUtils.getFileWithRealPath("test/pdfResult.pdf"))
					, courseEntity);
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		}

	}
	public static void testPDF() {
		try {
			PDFUtils.transformToPDF(new FileOutputStream(AppUtils.getFileWithRealPath("test/pdfResult.pdf")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		}

	}

	public static void testRemoveEmptyAttributes() {

		String beginSign = "<article id=\"post-0\"";
		String endSign = "</article";
		String htmlContent = StaxParserUtils.parseDebuggingHtml(
				AppUtils.getFileWithRealPath("html-crawled/khoahoconlineCategory.html")
				, beginSign
				, endSign
		);
		htmlContent = StaxParserUtils.addMissingTag(htmlContent);
		htmlContent = StaxParserUtils.removeEmptyAttributes(htmlContent);
		StaxParserUtils.saveStringToFile(htmlContent,
				AppUtils.getFileWithRealPath("html-crawled/removedAtrributeKhoahoconlineCategory.html"));

	}

}
