package listerner;

import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import crawler.CrawlingThreadManager;
import dao.CategoryDAO;
import config.model.CategoryNameType;
import util.DBUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener()
public class MainListener implements ServletContextListener,
		HttpSessionListener, HttpSessionAttributeListener {
	public static Logger logger = Logger.getLogger(MainListener.class.toString());


	// Public constructor is required by servlet spec
	public MainListener() {
	}

	// -------------------------------------------------------
	// ServletContextListener implementation
	// -------------------------------------------------------
	public void contextInitialized(ServletContextEvent sce) {

		// TODO: 11/10/18 future: get context directly from this class
		// separate just because cannot load WebListenner when run JavaApplication

		ContextHolder.setApplicationContext(sce.getServletContext());

		//turn off log for hibernate
		logger.info("CrawlerServletListener: Context init");

		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);

		// todo (do this in servletInit(): insert category names to db if not yet available
		CategoryNameType[] categoryNameTypes = CategoryNameType.values();
		for (CategoryNameType value : categoryNameTypes) {
			CategoryDAO.getInstance().insertCategoryByNameIfNotExist(value.getValue());
		}

	}

	public void contextDestroyed(ServletContextEvent sce) {

		logger.info("CrawlerServletListener: Context destroy");
		DBUtils.closeEntityFactory();

		CrawlingThreadManager.getInstance().stopAllThread();



		// TODO: 11/10/18  	set context = null here
	}

	// -------------------------------------------------------
	// HttpSessionListener implementation
	// -------------------------------------------------------
	public void sessionCreated(HttpSessionEvent se) {
		/* Session is created. */
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		/* Session is destroyed. */
	}

	// -------------------------------------------------------
	// HttpSessionAttributeListener implementation
	// -------------------------------------------------------

	public void attributeAdded(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute 
         is added to a session.
      */
	}

	public void attributeRemoved(HttpSessionBindingEvent sbe) {
      /* This method is called when an attribute
         is removed from a session.
      */
	}

	public void attributeReplaced(HttpSessionBindingEvent sbe) {
      /* This method is invoked when an attibute
         is replaced in a session.
      */
	}
}
