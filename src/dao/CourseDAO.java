package dao;

import constant.AppConstants;
import listerner.ContextHolder;
import listerner.MainListener;
import other.DummyDatabase;
import entity.CourseEntity;
import sun.applet.Main;
import util.AppUtils;
import util.DBUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.util.List;
import java.util.logging.Logger;

public class CourseDAO extends BaseDAO<CourseEntity, Integer> {
	private static Logger logger = Logger.getLogger(CourseDAO.class.toString());



	private static CourseDAO instance;
	private static Object LOCK = new Object();

	public CourseDAO() {

	}


	public static CourseDAO getInstance() {
		synchronized (LOCK) {
			if (instance == null) {
				instance = new CourseDAO();
			}
		}
		return instance;
	}


	public void checkIfExistOrInsertToDB(CourseEntity courseEntity) {
//		DBUtils.getEntityManager()
		List<CourseEntity> resultList = DBUtils.getEntityManager().createNamedQuery("CourseEntity.findCourseByHashing")
				.setParameter("hash", courseEntity.getHash())
				.getResultList();

		if (!resultList.isEmpty()) {
			//set correct id to courseEntity
			CourseEntity foundCourseEntity = resultList.get(0);

			if (!foundCourseEntity.isDataFixed()) {
				courseEntity.setId(foundCourseEntity.getId());

				//update course
				logger.info(String.format("course exist, update course|%s", courseEntity));
				this.merge(courseEntity);
			} else {
				logger.info(String.format("course exist, BUT DATAFIXED -> NOT UPDATE |%s", courseEntity));

			}

		} else {
			//insert new coure entity
			logger.info(String.format("course not exist, insert new course|%s", courseEntity));
			this.persist(courseEntity);
		}


	}

	public void validateCourseAndSaveToDB(CourseEntity courseEntity) {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(courseEntity.getClass());
			ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

			jaxbContext.createMarshaller().marshal(courseEntity, byteOutputStream);
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteOutputStream.toByteArray());

			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			File courseXSDFile  = AppUtils.getFileWithRealPath(AppConstants.COURSE_SCHEMA_PATH);


			Schema schema = schemaFactory.newSchema(courseXSDFile);
			logger.info(courseEntity.toString());
			Validator validator = schema.newValidator();
			validator.validate(new SAXSource(new InputSource(byteInputStream)));

			logger.info(String.format("Validated, saving to db | Course=%s", courseEntity));


			checkIfExistOrInsertToDB(courseEntity);



		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
			logger.severe(String.format("NOT VALID | Message= %s | \n Course=%s", e.getMessage(),courseEntity));
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}
