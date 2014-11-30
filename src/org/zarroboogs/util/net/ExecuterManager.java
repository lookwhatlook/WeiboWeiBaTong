package org.zarroboogs.util.net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecuterManager {
	private int jobs = 0;
	private ExecutorService mService = Executors.newFixedThreadPool(9);

	public ExecuterManager() {
		// TODO Auto-generated constructor stub

	}

	public void shutDown() {
		mService.shutdown();
	}

	public void addJobs(UploadThread ut) {
		jobs++;
		ut.addToThread(mService);
	}

	public void reAddJobs(UploadThread ut) {
		ut.addToThread(mService);
	}

	public void reduceJobs() {
		jobs--;
	}

	public boolean isAllFinished() {
		return jobs == 0;
	}
}
