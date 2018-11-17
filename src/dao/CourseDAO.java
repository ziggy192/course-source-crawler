package dao;

import constant.AppConstants;
import entity.CourseEntity;
import util.AppUtils;
import util.DBUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
			logger.severe(String.format("NOT VALID | Message= %s | \n Course=%s", e.getMessage(),courseEntity));
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	public static CourseDAO getInstance() {
		synchronized (LOCK) {
			if (instance == null) {
				instance = new CourseDAO();
			}
		}
		return instance;
	}

	public CourseEntity getFirstCourse() {
		EntityManager entityManager = DBUtils.getEntityManager();
		CourseEntity singleResult = (CourseEntity) entityManager.createNamedQuery("CourseEntity.findAllCourse")
				.setMaxResults(1)
				.getSingleResult();

		return singleResult;

	}

	public List<CourseEntity> searchCourse(String query) {

		logger.info(String.format("searchQueryOnly,query=", query));

		return DBUtils.getEntityManager().createNamedQuery("CourseEntity.findCourseByNameQuery", CourseEntity.class)
				.setParameter("query", query)
				.getResultList();

	}

	public List<CourseEntity> searchCourse(String query
			, List<Integer> domainIdList
			, List<Integer> categoryIdList
			, String sort
			, int page) {

		logger.info(String.format("query=%s|domainId=%s|categoryId=%s|sort=%s|page=%s", query, domainIdList, categoryIdList, sort, page));

		String jpqlQuery = "SELECT c FROM CourseEntity c where c.name like concat('%',:query,'%')";

		if (!domainIdList.isEmpty()) {

			jpqlQuery += "  and c.domainId in ( ";
			for (int i = 0; i < domainIdList.size(); i++) {
				int domainId = domainIdList.get(i);
				jpqlQuery += domainId;
				if (i < domainIdList.size() - 1) {
					jpqlQuery += ",";
				}
			}
			jpqlQuery += ") ";
		}

		if (!categoryIdList.isEmpty()) {

			jpqlQuery += "  and c.categoryId in ( ";
			for (int i = 0; i < categoryIdList.size(); i++) {
				int categoryId = categoryIdList.get(i);
				jpqlQuery += categoryId;
				if (i < categoryIdList.size() - 1) {
					jpqlQuery += ", ";
				}
			}
			jpqlQuery += ") ";
		}


		//sort

		//name-up
		//name-down
		//rating-up
		//rating-down

		//cost-up
		//cost-down

		switch (sort.toLowerCase()) {
			case AppConstants.NAME_UP:
				jpqlQuery += " order by c.name asc ";
				break;
			case AppConstants.NAME_DOWN:
				jpqlQuery += " order by c.name desc";
				break;
			case AppConstants.COST_UP:
				jpqlQuery += " order by c.cost asc ";
				break;
			case AppConstants.COST_DOWN:
				jpqlQuery += " order by c.cost desc";

				break;
			case AppConstants.RATING_UP:
				jpqlQuery += " order by c.rating asc ";
				break;
			case AppConstants.RATING_DOWN:
				jpqlQuery += " order by c.rating desc";

				break;
			default:

				//do nothing
				break;

		}

		//get count result

		int firstResult = AppConstants.PAGE_SIZE * (page - 1);

		logger.info(String.format("jpqlQuery=%s", jpqlQuery));
		EntityManager entityManager = DBUtils.getEntityManager();
		TypedQuery<CourseEntity> typedQuery = entityManager.createQuery(jpqlQuery, CourseEntity.class);
		entityManager.createQuery(jpqlQuery, CourseEntity.class);

		typedQuery = typedQuery.setParameter("query", query);

		typedQuery = typedQuery.setFirstResult(firstResult);
		typedQuery = typedQuery.setMaxResults(AppConstants.PAGE_SIZE);

		return typedQuery.getResultList();
	}
}
