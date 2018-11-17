package resource;

import com.sun.istack.internal.logging.Logger;
import dao.CourseDAO;
import entity.CourseEntity;
import util.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/course")
@Produces(MediaType.APPLICATION_XML)
public class CourseResource {

	public static Logger logger = Logger.getLogger(CourseResource.class);

	@GET
	public List<CourseEntity> getCourseByQuery(
			@DefaultValue("") @QueryParam("query") String query
			, @DefaultValue("") @QueryParam("domainId") String domainIdListString
			, @DefaultValue("") @QueryParam("categoryId") String categoryIdListString
			, @DefaultValue("") @QueryParam("sort") String sort
			, @DefaultValue("1") @QueryParam("page") int page
	) {
		logger.info("start logging ");
		logger.info(String.format("getCourseByQuery() \n query=%s \n sort=%s \n page=%s \n domainId=%s \n categoryId=%s", query, sort, page, categoryIdListString, categoryIdListString));

		List<Integer> domainIdList = StringUtils.toIntegerList(domainIdListString);
		List<Integer> categoryIdList = StringUtils.toIntegerList(categoryIdListString);
		logger.info(String.format("categoryListsize=%s | domainIdListSize=%s", categoryIdList.size(), domainIdList.size()));


		//throw NumberFormatException with errorcode=500
		return CourseDAO.getInstance().searchCourse(query, domainIdList, categoryIdList, sort, page);
	}


}
