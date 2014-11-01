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
 * User: qii Date: 13-3-2
 */
public class EditMyProfileDao {

	public UserBean update() throws WeiboException {

		String apiUrl = WeiBoURLs.MYPROFILE_EDIT;

		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", access_token);
		map.put("screen_name", screen_name);
		map.put("url", url);
		map.put("description", description);

		String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, apiUrl, map);

		Gson gson = new Gson();

		UserBean value = null;
		try {
			value = gson.fromJson(jsonData, UserBean.class);
		} catch (JsonSyntaxException e) {
			AppLoggerUtils.e(e.getMessage());
		}

		if (this.avatar != null) {
			UploadAvatarDao uploadAvatarDao = new UploadAvatarDao(access_token, avatar);
			uploadAvatarDao.upload();
		}

		return value;

	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public EditMyProfileDao(String token, String screen_name) {
		this.access_token = token;
		this.screen_name = screen_name;
	}

	private String access_token;
	private String screen_name;
	private String url;
	private String description;
	private String avatar;
}
