package servlet;

import constant.UrlConstant;
import dao.CategoryDAO;
import dao.DomainDAO;
import entity.CategoryEntity;
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

@WebServlet(name = "SearchServlet",urlPatterns = "/search")
public class SearchServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");

		//get categoyr list

		List<CategoryEntity> categoryEntityList = CategoryDAO.getInstance().getCategoryList();
		request.setAttribute("categories", categoryEntityList);

		//get domain list

		List<DomainEntity> domainEntityList = DomainDAO.getInstance().getAllDomain();
		request.setAttribute("domains", domainEntityList);


		//get domain param list
		String domainParam = request.getParameter("domainId");
		List<Integer> domainParams = StringUtils.toIntegerList(domainParam);
		request.setAttribute("domainParams", domainParams);

		//get categroy param list
		String categoryParam = request.getParameter("categoryId");
		List<Integer> categoryParams = StringUtils.toIntegerList(categoryParam);
		request.setAttribute("categoryParams", categoryParams);

		//get param list

		RequestDispatcher requestDispatcher = request.getRequestDispatcher(UrlConstant.SEARCH_RESULT_JSP);
		requestDispatcher.forward(request, response);

	}
}
