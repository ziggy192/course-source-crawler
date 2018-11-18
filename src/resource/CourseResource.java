package resource;

import com.sun.istack.internal.logging.Logger;
import dao.CourseDAO;
import entity.CourseEntity;
import util.PDFUtils;
import util.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Path("/course")
public class CourseResource {

	public static Logger logger = Logger.getLogger(CourseResource.class);

	@GET
	@Produces(MediaType.APPLICATION_XML)
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

	@GET()
	@Path("/pdf/{id}")
	public Response getPDF(@PathParam("id") int courseId) {
		StreamingOutput streamingOutput = new StreamingOutput() {
			@Override
			public void write(OutputStream outputStream) throws IOException, WebApplicationException {
				CourseEntity courseEntity = CourseDAO.getInstance().findByID(courseId);
				PDFUtils.transformCourseToPDF(outputStream, courseEntity);
			}
		};
		return Response.ok(streamingOutput,"application/pdf").build();
	}


}
