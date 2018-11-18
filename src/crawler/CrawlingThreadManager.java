package crawler;

import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import config.ConfigManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CrawlingThreadManager {
	private static final Logger logger = Logger.getLogger(CrawlingThreadManager.class.toString());


	private static final Object LOCK = new Object();
	private static CrawlingThreadManager instance;

	private ThreadPoolExecutor edumallExecutor;
	private ThreadPoolExecutor unicaExecutor;
	private ThreadPoolExecutor kHOLExcecutor;
	private ThreadPoolExecutor emoonExecutor;
	private ThreadPoolExecutor tuyenSinhExecutor;

	private CrawlingThreadManager() {

		//todo debugging remove edumall and unica
		edumallExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
		edumallExecutor.allowCoreThreadTimeOut(false);
		unicaExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
		unicaExecutor.allowCoreThreadTimeOut(false);
		kHOLExcecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(
				ConfigManager.getInstance().getConfigModel().getKhoaHocOnline().getThreadLimit()
		);
		kHOLExcecutor.allowCoreThreadTimeOut(false);

		emoonExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(
				ConfigManager.getInstance().getConfigModel().getEmoon().getThreadLimit()

		);
		emoonExecutor.allowCoreThreadTimeOut(false);
		tuyenSinhExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(
				ConfigManager.getInstance().getConfigModel().getEmoon().getThreadLimit()

		);
		tuyenSinhExecutor.allowCoreThreadTimeOut(false);



		//or else the thread would survise all along
//		executor.setKeepAliveTime(5, TimeUnit.SECONDS);


	}


	public ThreadPoolExecutor getEdumallExecutor() {
		return edumallExecutor;
	}

	public ThreadPoolExecutor getUnicaExecutor() {
		return unicaExecutor;
	}

	public ThreadPoolExecutor getEmoonExecutor() {
		return emoonExecutor;
	}

	public ThreadPoolExecutor getkHOLExcecutor() {

		return kHOLExcecutor;
	}

	public ThreadPoolExecutor getTuyenSinhExecutor() {
		return tuyenSinhExecutor;
	}

	public static CrawlingThreadManager getInstance() {
		synchronized (LOCK) {
			if (instance == null) {
				instance = new CrawlingThreadManager();

			}
		}
		return instance;
	}

	private boolean suspended = false;

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean isSuspended) {
		this.suspended = isSuspended;
	}


	public void suspendThread() {
		setSuspended(true);
		logger.info("Suspended all threads");
	}


	//it's synchronized because it must be under the same monitor to notifyAll(), by synchronized it make THIS became the monitor
	// same as synchronized(CrawlingThreadManager.getDefault()) { notifyAll();}
	public synchronized void resumeThread() {
		setSuspended(false);
		notifyAll();
		logger.info("Resume all threads");
	}


	public synchronized void checkSuspendStatus() throws InterruptedException {
		while (isSuspended()) {
			logger.info("Suspend this thread");
			CrawlingThreadManager.getInstance().wait();
			logger.info("Resume this thread");

		}
	}

	public void stopAllThread() {
		edumallExecutor.shutdownNow();
		unicaExecutor.shutdownNow();
		kHOLExcecutor.shutdownNow();
		emoonExecutor.shutdownNow();
		tuyenSinhExecutor.shutdownNow();
	}

}
