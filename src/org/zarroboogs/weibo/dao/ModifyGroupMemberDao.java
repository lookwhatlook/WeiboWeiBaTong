package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.weibo.bean.UserBean;
import org.zarroboogs.weibo.net.HttpUtility;
import org.zarroboogs.weibo.net.WeiboException;
import org.zarroboogs.weibo.net.HttpUtility.HttpMethod;
import org.zarroboogs.weibo.utils.AppLoggerUtils;
import org.zarroboogs.weibo.utils.WeiBoURLs;

import java.util.HashMap;
import java.util.Map;

/**
 * User: qii Date: 12-11-6
 */
public class ModifyGroupMemberDao {

	public void add(String list_id) throws WeiboException {
		this.list_id = list_id;
		executeTask(WeiBoURLs.GROUP_MEMBER_ADD);
	}

	public void delete(String list_id) throws WeiboException {
		this.list_id = list_id;
		executeTask(WeiBoURLs.GROUP_MEMBER_DESTROY);
	}

	private UserBean executeTask(String url) throws WeiboException {

		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", access_token);
		map.put("uid", uid);
		map.put("list_id", list_id);

		String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, url, map);

		Gson gson = new Gson();

		UserBean value = null;
		try {
			value = gson.fromJson(jsonData, UserBean.class);
		} catch (JsonSyntaxException e) {
			AppLoggerUtils.e(e.getMessage());
		}
		return value;
	}

	public ModifyGroupMemberDao(String token, String uid) {
		this.access_token = token;
		this.uid = uid;
	}

	private String access_token;
	private String uid;
	private String list_id;
}
