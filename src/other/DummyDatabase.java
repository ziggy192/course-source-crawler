package other;

import dao.CategoryDAO;
import entity.CategoryMapping;
import util.DBUtils;

import javax.servlet.ServletContext;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DummyDatabase {

	private static Logger logger = Logger.getLogger(DummyDatabase.class.toString());

	private static ServletContext context;

	public static ServletContext getContext() {
		return context;
	}

	public static void setContext(ServletContext context) {
		DummyDatabase.context = context;
	}
}
