package servlet;

import constant.UrlConstant;
import dao.CategoryDAO;
import dao.CourseDAO;
import dao.DomainDAO;
import entity.CategoryEntity;
import entity.CourseEntity;
import entity.DomainEntity;
import util.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CourseDetailServlet", urlPatterns = "/course")
public class CourseDetailServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");


		List<CategoryEntity> categoryEntityList = CategoryDAO.getInstance().getCategoryList();
		request.setAttribute("categories", categoryEntityList);

		String courseId = request.getParameter("id");

		CourseEntity course;
		if (courseId == null) {
			course = CourseDAO.getInstance().getFirstCourse();
		} else {

			int id = Integer.parseInt(courseId);
			course = CourseDAO.getInstance().findByID(id);

		}

		request.setAttribute("course", course);

		DomainEntity domain = DomainDAO.getInstance().findByID(course.getDomainId());
		CategoryEntity category = CategoryDAO.getInstance().findByID(course.getCategoryId());

		String durationString = StringUtils.toDurationString(course.getDuration());


		request.setAttribute("domain", domain);
		request.setAttribute("category", category);

		request.setAttribute("durationString", durationString);


		RequestDispatcher requestDispatcher = request.getRequestDispatcher(UrlConstant.COURSE_DETAIL_JSP);
		requestDispatcher.forward(request, response);

	}
}

