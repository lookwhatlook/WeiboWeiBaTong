package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.net.HttpUtility;
import org.zarroboogs.weibo.net.WeiboException;
import org.zarroboogs.weibo.net.HttpUtility.HttpMethod;
import org.zarroboogs.weibo.utils.AppLoggerUtils;
import org.zarroboogs.weibo.utils.WeiBoURLs;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Jiang Qi Date: 12-8-7
 */
public class ShowStatusDao {

	private String access_token;
	private String id;

	public ShowStatusDao(String access_token, String id) {

		this.access_token = access_token;
		this.id = id;
	}

	public MessageBean getMsg() throws WeiboException {

		String url = WeiBoURLs.STATUSES_SHOW;

		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", access_token);
		map.put("id", id);

		String json = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

		Gson gson = new Gson();

		MessageBean value = null;
		try {
			value = gson.fromJson(json, MessageBean.class);
		} catch (JsonSyntaxException e) {

			AppLoggerUtils.e(e.getMessage());
		}

		return value;

	}
}
