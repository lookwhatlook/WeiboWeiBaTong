package org.zarroboogs.weibo.net;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Entity;

public class HttpPostFactory {
	public static HttpPost createHttpPost(String mPostURL, Header[] headers, List<NameValuePair> mFornData) {
		HttpPost mHttpPost = new HttpPost(mPostURL);

		mHttpPost.setHeaders(headers);

		// mHttpPost.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		// mHttpPost.addHeader("Accept-Encoding","gzip,deflate,sdch");
		// mHttpPost.addHeader("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6,ja;q=0.4,zh-TW;q=0.2");
		// mHttpPost.addHeader("Connection", "keep-alive");
		// mHttpPost.addHeader("Content-Type",
		// "text/plain"/*"application/x-www-form-urlencoded"*/);
		// mHttpPost.addHeader("User-Agent",
		// "Mozilla/5.0 (Linux; Android 4.3; Nexus 10 Build/JSS15Q) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.72 Safari/537.36");
		// mHttpPost.addHeader("Host","reg.163.com");
		// mHttpPost.addHeader("Origin", "http://study.163.com");
		// mHttpPost.addHeader("Referer", mReferer);
		// mHttpPost.addHeader("Proxy-Connection","keep-alive");
		UrlEncodedFormEntity mEncodedFormEntity = null;
		try {
			mEncodedFormEntity = new UrlEncodedFormEntity(mFornData, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		mHttpPost.setEntity(mEncodedFormEntity);

		return mHttpPost;
	}
}
