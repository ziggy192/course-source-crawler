package listerner;

import dao.CategoryDAO;
import entity.CategoryMapping;
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

		//turn off log for hibernate
		logger.info("CrawlerServletListener: Context init");

		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);

		// todo (do this in servletInit(): insert category names to db if not yet available
		CategoryMapping[] categoryMappings = CategoryMapping.values();
		for (CategoryMapping value : categoryMappings) {
			CategoryDAO.getInstance().insertCategoryByNameIfNotExist(value.getValue());
		}

	}

	public void contextDestroyed(ServletContextEvent sce) {

		logger.info("CrawlerServletListener: Context destroy");
		DBUtils.closeEntityFactory();
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
