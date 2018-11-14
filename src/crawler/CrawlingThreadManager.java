package crawler;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CrawlingThreadManager {
	private static final Logger logger = Logger.getLogger(CrawlingThreadManager.class.toString());
	public static int THREAD_LIMIT = 10;


	private static final Object LOCK = new Object();
	private static CrawlingThreadManager instance;

	private ThreadPoolExecutor executor;
	private CrawlingThreadManager() {
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_LIMIT);
		executor.allowCoreThreadTimeOut(false);


		//or else the thread would survise all along
//		executor.setKeepAliveTime(5, TimeUnit.SECONDS);


	}


	public ThreadPoolExecutor getExecutor() {
		return executor;
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
		executor.shutdownNow();
	}

}
