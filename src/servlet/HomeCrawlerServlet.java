package servlet;

import config.ConfigManager;
import constant.AppConstants;
import constant.UrlConstant;
import crawler.*;
import dao.DomainDAO;
import entity.DomainEntity;
import test.EdumallTest;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@WebServlet(name = "HomeCrawlerServlet"
		, urlPatterns = {"/admin"}
		, loadOnStartup = 1)
public class HomeCrawlerServlet extends HttpServlet {
	public static Logger logger = Logger.getLogger(HomeCrawlerServlet.class.toString());

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);

	}

	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;char-set=UTF8");

		logger.info("Thử viết Unicode");
		String btAction = request.getParameter("btAction");
		logger.info("btAction=" + btAction);


		List<DomainEntity> domainEntityList = DomainDAO.getInstance().getAllDomain();
		request.setAttribute("domains", domainEntityList);


		String message = "";

		if (btAction == null) {
			//first load

		} else {
			switch (btAction) {
				case "start":
					CrawlingThreadManager.getInstance().resumeThread();


					//domains
					String[] domains = request.getParameterValues("domain");
					if (domains != null) {
						//Reread config file before start
						if (ConfigManager.getInstance().readConfigFile()) {
							startCrawlers(domains);
							message = "Started";
						} else {
							//error
							message = "Error when reading Config file - Cannot start";
						}


					} else {
						message = "NO DOMAIN SELECTED";
					}
					break;
				case "stop":
//					CrawlingThreadManager.getInstance().stopAllThread();
					break;
				case "pause":
					CrawlingThreadManager.getInstance().suspendThread();
					message = "Paused";
					break;
				case "resume":
					CrawlingThreadManager.getInstance().resumeThread();
					message = "Resumed";
					break;
				default:
					// do nothing
					break;
			}
		}


		request.setAttribute("message", message);

		RequestDispatcher requestDispatcher = request.getRequestDispatcher(UrlConstant.HOME_CRAWLER_JSP);
		requestDispatcher.forward(request, response);


	}


	private void startCrawlers(String[] domains) {

		for (String domain : domains) {
			switch (domain) {
				case AppConstants.TUYENSINH_DOMAIN_NAME:
					CrawlingThreadManager.getInstance().getTuyenSinhExecutor().execute(new TuyenSinhMainCrawler());

					break;
				case AppConstants.EMOON_DOMAIN_NAME:
					CrawlingThreadManager.getInstance().getEmoonExecutor().execute(new EmoonMainCrawler());

					break;
				case AppConstants.KHOL_DOMAIN_NAME:
					CrawlingThreadManager.getInstance().getkHOLExcecutor().execute(new KHOLMainCrawler());

					break;

			}
		}
	}
}
