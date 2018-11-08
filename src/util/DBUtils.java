package util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DBUtils {
	private static EntityManagerFactory emfactory;

	private static final Object LOCK = new Object();

	public static EntityManager getEntityManager() {
		synchronized (LOCK) {
			if (emfactory == null) {
				try {
					emfactory = Persistence.createEntityManagerFactory("CourseSourcePersistenceUnit");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return emfactory.createEntityManager();
	}


	//todo close when ApplicationClose
	public static void closeEntityFactory() {
		synchronized (LOCK) {
			if (emfactory != null && emfactory.isOpen()) {
				emfactory.close();
			}
		}
	}
}
