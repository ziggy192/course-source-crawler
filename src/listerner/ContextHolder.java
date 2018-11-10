package listerner;

import javax.servlet.ServletContext;

public class ContextHolder {
	private static ServletContext context;

	public static ServletContext getApplicationContext() {
		return context;
	}

	public static void setApplicationContext(ServletContext context) {
		ContextHolder.context = context;
	}
}
