package org.zarroboogs.weibo.dao;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.zarroboogs.util.net.HttpUtility;
import org.zarroboogs.util.net.WeiboException;
import org.zarroboogs.util.net.HttpUtility.HttpMethod;
import org.zarroboogs.utils.AppLoggerUtils;
import org.zarroboogs.utils.WeiBoURLs;
import org.zarroboogs.weibo.bean.MessageBean;
import org.zarroboogs.weibo.bean.ShareListBean;
import org.zarroboogs.weibo.setting.SettingUtils;
import org.zarroboogs.weibo.support.utils.TimeUtility;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User: qii Date: 13-2-27
 */
public class ShareShortUrlTimeLineDao {

	private String getMsgListJson() throws WeiboException {
		String url = WeiBoURLs.SHORT_URL_SHARE_TIMELINE;

		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", access_token);
		map.put("count", count);
		map.put("max_id", max_id);
		map.put("url_short", url_short);

		String jsonData = null;

		jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

		return jsonData;
	}

	public ShareListBean getGSONMsgList() throws WeiboException {

		String json = getMsgListJson();
		Gson gson = new Gson();

		ShareListBean value = null;
		try {
			value = gson.fromJson(json, ShareListBean.class);
		} catch (JsonSyntaxException e) {

			AppLoggerUtils.e(e.getMessage());
		}

		if (value != null) {

			Iterator<MessageBean> iterator = value.getItemList().iterator();

			while (iterator.hasNext()) {

				MessageBean msg = iterator.next();
				if (msg.getUser() == null) {
					iterator.remove();
				} else {
					msg.getListViewSpannableString();
					TimeUtility.dealMills(msg);
				}
			}

		}

		return value;
	}

	private String access_token;
	private String url_short;
	private String count;
	private String max_id;

	public ShareShortUrlTimeLineDao(String access_token, String url_short) {

		this.access_token = access_token;
		this.url_short = url_short;
		this.count = SettingUtils.getMsgCount();
	}

	public ShareShortUrlTimeLineDao setCount(String count) {
		this.count = count;
		return this;
	}

	public ShareShortUrlTimeLineDao setMaxId(String max_id) {
		this.max_id = max_id;
		return this;
	}
}
