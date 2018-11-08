package servlet;

import constant.UrlConstant;
import crawler.CrawlingThreadManager;
import crawler.EdumallMainCrawler;
import crawler.UnicaMainCrawler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

@WebServlet(name = "HomeCrawlerServlet"
		,urlPatterns = {"/","/home"}
		,loadOnStartup = 1)
public class HomeCrawlerServlet extends HttpServlet {
	public static Logger logger = Logger.getLogger(HomeCrawlerServlet.class.toString());
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);

	}

	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;char-set=UTF8");

		logger.info("Thử viết Unicode");
		String btAction = request.getParameter("btAction");
		logger.info("btAction=" + btAction);

		String message = "";

		if (btAction == null) {
			//first load

		} else {
			switch (btAction) {
				case "start":
					String[] domains = request.getParameterValues("domain");
					if (domains != null) {
						startCrawlers(domains);
						message = "Started";
					}else{
						message = "NO DOMAIN SELECTED";
					}
					break;
				case "stop":
					break;
				case "pause":
					CrawlingThreadManager.getInstance().suspendThread();
					break;
				case "resume":
					CrawlingThreadManager.getInstance().resumeThread();
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
		ArrayList<Runnable> domainTaskList = new ArrayList<>();
		for (String domain : domains) {
			switch (domain) {
				case "edumall":
					domainTaskList.add(new EdumallMainCrawler());
					break;
				case "unica":
					domainTaskList.add(new UnicaMainCrawler());
					break;

			}
		}
		for (Runnable task : domainTaskList) {

			CrawlingThreadManager.getInstance().getExecutor().execute(task);
		}
	}
}
