package util;

import constant.AppConstants;
import listerner.ContextHolder;

import java.io.File;

public class AppUtils {


	public static File getFileWithRealPath(String filePath) {


		if (ContextHolder.getApplicationContext() != null) {
			String realPath = ContextHolder.getApplicationContext().getRealPath("/" +filePath);

			return new File(realPath);

		} else {

			return new File("web/" + filePath);
		}

	}
}
