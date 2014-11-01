package org.zarroboogs.weibo.net;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;

import android.util.Log;

public class BroserContent {

	private CookieStore mCookieStore = new BasicCookieStore();

	private CloseableHttpClient mHttpClient;

	private static BroserContent mBroserContent = null;

	public static synchronized BroserContent getInstance() {
		if (mBroserContent == null) {
			mBroserContent = new BroserContent();
		}
		return mBroserContent;
	}

	public CookieStore getCookieStore() {
		return mCookieStore;
	}

	public CloseableHttpClient getHttpClient() {
		mHttpClient = (CloseableHttpClient) HttpClientFactory.creteHttpClient(mCookieStore);
		return mHttpClient;
	}

	public void release() {/*
							 * 
							 * mCookieStore.clear(); try { if (mHttpClient !=
							 * null) { mHttpClient.close(); }
							 * HttpClientFactory.release(); mHttpClient =
							 * (CloseableHttpClient)
							 * HttpClientFactory.creteHttpClient(mCookieStore);
							 * } catch (IOException e) { e.printStackTrace(); }
							 */
	}
}
