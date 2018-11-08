package crawler;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CrawlingThreadManager {
	public static int THREAD_LIMIT = 10;

	private static final Object LOCK = new Object();
	private static CrawlingThreadManager instance;

	private ThreadPoolExecutor executor;
	private CrawlingThreadManager() {
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_LIMIT);
		//or else the thread would survise all along
		executor.setKeepAliveTime(5, TimeUnit.SECONDS);
		executor.allowCoreThreadTimeOut(true);

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
		System.out.println("Suspended all threads");
	}


	//it's synchronized because it must be under the same monitor to notifyAll(), by synchronized it make THIS became the monitor
	// same as synchronized(CrawlingThreadManager.getDefault()) { notifyAll();}
	public synchronized void resumeThread() {
		setSuspended(false);
		notifyAll();
		System.out.println("Resume all threads");
	}


}
