
package org.zarroboogs.weibo.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lib.org.zarroboogs.weibo.login.httpclient.RealLibrary;
import lib.org.zarroboogs.weibo.login.httpclient.SinaPreLogin;
import lib.org.zarroboogs.weibo.login.httpclient.UploadHelper;
import lib.org.zarroboogs.weibo.login.httpclient.UploadHelper.OnUpFilesListener;
import lib.org.zarroboogs.weibo.login.httpclient.WaterMark;
import lib.org.zarroboogs.weibo.login.javabean.PreLoginResult;
import lib.org.zarroboogs.weibo.login.javabean.RequestResultParser;
import lib.org.zarroboogs.weibo.login.javabean.SendResultBean;
import lib.org.zarroboogs.weibo.login.utils.Constaces;
import lib.org.zarroboogs.weibo.login.utils.LogTool;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.zarroboogs.weibo.setting.SettingUtils;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.evgenii.jsevaluator.JsEvaluator;
import com.evgenii.jsevaluator.interfaces.JsCallback;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.ResponseHandlerInterface;

public class BaseLoginActivity extends SharedPreferenceActivity {
    private SinaPreLogin mSinaPreLogin;
    private PreLoginResult mPreLoginResult;

    private JsEvaluator mJsEvaluator;
    private String rsaPwd;

    private RequestResultParser mRequestResultParser;

    private String mUserName;
    private String mPassword;
    private WaterMark mWaterMark;
    private String mWeibaCode;
    private String mWeiboText;
    private List<String> mPics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJsEvaluator = new JsEvaluator(getApplicationContext());

        mSinaPreLogin = new SinaPreLogin();
        
        mRequestResultParser = new RequestResultParser();
    }

    public RequestResultParser getRequestResultParser(){
        return mRequestResultParser;
    }
    public void sendWeibo(String uname, String upwd, WaterMark mark, final String weiboCode, final String text,
            List<String> pics) {
        this.mUserName = uname;
        this.mPassword = upwd;

        this.mWaterMark = mark;
        this.mWeibaCode = weiboCode;
        this.mWeiboText = text;
        this.mPics = pics;
        LogTool.D("sendWeibo   start" + " name:" + uname + "   password:" + upwd + "  weiba:" + weiboCode);

        doPreLogin(this.mUserName, this.mPassword);

    }

    private void dosend(WaterMark mark, final String weiboCode, final String text, List<String> pics) {
        if (pics == null || pics.isEmpty()) {
            sendWeiboWidthPids(weiboCode, text, null);
            // sendWeiboWidthPids("ZwpYj", "Test: " + SystemClock.uptimeMillis() + "", null);
            LogTool.D("uploadFile     Not Upload");
        } else {
            LogTool.D("uploadFile    upload");
            UploadHelper mUploadHelper = new UploadHelper(getApplicationContext(), getAsyncHttpClient());
            mUploadHelper.uploadFiles(buildMark(mark), pics, new OnUpFilesListener() {

                @Override
                public void onUpSuccess(String pids) {
                    LogTool.D("uploadFile pids: " + pids);
                    sendWeiboWidthPids(weiboCode, text, pids);
                }
            });
        }
    }

    public String buildMark(WaterMark mark) {
        if (SettingUtils.getEnableWaterMark()) {
            String markpos = SettingUtils.getWaterMarkPos();
            String logo = SettingUtils.isWaterMarkWeiboICONShow() ? "1" : "0";
            String nick = SettingUtils.isWaterMarkScreenNameShow() ? "%40" + mark.getNick() : "";
            String url = SettingUtils.isWaterMarkWeiboURlShow() ? mark.getUrl() : "";
            return "&marks=1&markpos=" + markpos + "&logo=" + logo + "&nick=" + nick + "&url=" + url;
        } else {
            return "&marks=0";
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constaces.MSG_ENCODE_PWD: {
                    encodePassword(mPassword, mPreLoginResult);
                    break;
                }

                case Constaces.MSG_ENCODE_PWD_DONW: {
                    doAfterPreLogin();
                    break;
                }
                case Constaces.MSG_AFTER_LOGIN_DONE: {
                    doLogin();
                    break;
                }
                case Constaces.MSG_LONGIN_SUCCESS: {
                    // sendWeibo("");
                    dosend(mWaterMark, mWeibaCode, mWeiboText, mPics);
                    // sendWeiboWidthPids("ZwpYj", "Test: " + SystemClock.uptimeMillis() + "",
                    // null);
                    break;
                }

                default:
                    break;
            }
        }
    };

    protected void sendWeibo(String pid) {
        HttpEntity sendEntity = mSinaPreLogin.sendWeiboEntity("ZwpYj", SystemClock.uptimeMillis() + "", getCookieStore()
                .toString(), pid);
        getAsyncHttpClient().post(getApplicationContext(), Constaces.ADDBLOGURL, mSinaPreLogin.sendWeiboHeaders("ZwpYj"),
                sendEntity,
                "application/x-www-form-urlencoded", new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        
                        SendResultBean sendResultBean = mRequestResultParser.parse(responseBody, SendResultBean.class);
                        LogTool.D("sendWeibo   onSuccess" + sendResultBean.getMsg());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        LogTool.D("sendWeibo   onFailure" + new String(responseBody));
                    }
                });
    }

    /**
     * @param weiboCode "ZwpYj"
     * @param pid
     */
    protected void sendWeiboWidthPids(String weiboCode, String text, String pids) {
        HttpEntity sendEntity = mSinaPreLogin.sendWeiboEntity(weiboCode, text, getCookieStore().toString(), pids);
        getAsyncHttpClient().post(getApplicationContext(), Constaces.ADDBLOGURL, mSinaPreLogin.sendWeiboHeaders(weiboCode),
                sendEntity,
                "application/x-www-form-urlencoded", this.mSendWeiboHandler);
    }
    private ResponseHandlerInterface mSendWeiboHandler;
    public void setOnSendWeiboListener(ResponseHandlerInterface rhi){
        this.mSendWeiboHandler = rhi;
    }

    public void repostWeibo(String app_src, String content, String cookie, String mid) {
        List<Header> headerList = new ArrayList<Header>();
        headerList.add(new BasicHeader("Accept", "*/*"));
        headerList.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
        headerList.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4"));
        headerList.add(new BasicHeader("Connection", "keep-alive"));
        headerList.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
        headerList.add(new BasicHeader("Host", "widget.weibo.com"));
        headerList.add(new BasicHeader("Origin", "http://widget.weibo.com"));
        headerList.add(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
        headerList.add(new BasicHeader("Referer",
                "http://widget.weibo.com/dialog/publish.php?button=forward&language=zh_cn&mid=" + mid +
                        "&app_src=" + app_src + "&refer=1&rnd=14128245"));
        headerList.add(new BasicHeader("User-Agent", Constaces.User_Agent));
        Header[] repostHeaders = new Header[headerList.size()];
        headerList.toArray(repostHeaders);

        List<NameValuePair> nvs = new ArrayList<NameValuePair>();
        nvs.add(new BasicNameValuePair("content", content));
        nvs.add(new BasicNameValuePair("visible", "0"));
        nvs.add(new BasicNameValuePair("refer", ""));

        nvs.add(new BasicNameValuePair("app_src", app_src));
        nvs.add(new BasicNameValuePair("mid", mid));
        nvs.add(new BasicNameValuePair("return_type", "2"));

        nvs.add(new BasicNameValuePair("vsrc", "publish_web"));
        nvs.add(new BasicNameValuePair("wsrc", "app_publish"));
        nvs.add(new BasicNameValuePair("ext", "login=>1;url=>"));
        nvs.add(new BasicNameValuePair("html_type", "2"));
        nvs.add(new BasicNameValuePair("is_comment", "1"));
        nvs.add(new BasicNameValuePair("_t", "0"));

        UrlEncodedFormEntity repostEntity = null;
        try {
            repostEntity = new UrlEncodedFormEntity(nvs, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        getAsyncHttpClient().post(getApplicationContext(), Constaces.REPOST_WEIBO, repostHeaders, repostEntity,
                "application/x-www-form-urlencoded", mRepostHandler);
    }

    private ResponseHandlerInterface mRepostHandler;
    public void setOnRepostWeiboListener(ResponseHandlerInterface rhi){
        this.mRepostHandler = rhi;
    }
    
    private void doLogin() {
        getAsyncHttpClient().get(mRequestResultParser.getUserPageUrl(), mLoginHandler);
    }
    
    private ResponseHandlerInterface mLoginHandler;;
    public void setOnLoginListener(ResponseHandlerInterface rhi){
        this.mLoginHandler = rhi;
    }

    private void doAfterPreLogin() {
        HttpEntity httpEntity = mSinaPreLogin.afterPreLoginEntity(encodeAccount(mUserName), rsaPwd, null, mPreLoginResult);
        getAsyncHttpClient().post(getApplicationContext(), Constaces.LOGIN_FIRST_URL, mSinaPreLogin.afterPreLoginHeaders(),
                httpEntity, "application/x-www-form-urlencoded", new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        String response = null;
                        try {
                            response = new String(responseBody, "GBK");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        mRequestResultParser = new RequestResultParser(response);
                        if (mRequestResultParser.isLogin()) {
                            LogTool.D("doAfterPrelogin onSuccess" + " AfterLogin Success");
                        } else {
                            LogTool.D("doAfterPrelogin onSuccess" + " AfterLogin Failed : " + mRequestResultParser.getErrorReason());
                        }
                        mHandler.sendEmptyMessage(Constaces.MSG_AFTER_LOGIN_DONE);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        LogTool.D("doAfterPrelogin onFailure" + statusCode + new String(responseBody));
                    }
                });
    };

    private String encodeAccount(String account) {
        String encodedString;
        try {
            encodedString = new String(Base64.encodeBase64(URLEncoder.encode(account, "UTF-8").getBytes()));
            String userName = encodedString.replace('+', '-').replace('/', '_');
            return userName;
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void encodePassword(String password, PreLoginResult preLonginBean) {
        RealLibrary realLibrary = new RealLibrary(getApplicationContext());
        String js = realLibrary.getRsaJs();

        String pwd = "\"" + password + "\"";
        String servertime = "\"" + preLonginBean.getServertime() + "\"";
        String nonce = "\"" + preLonginBean.getNonce() + "\"";
        String pubkey = "\"" + preLonginBean.getPubkey() + "\"";
        String call = " var rsaPassWord = getRsaPassWord(" + pwd + ", " + servertime + ", " + nonce + ", " + pubkey
                + "); rsaPassWord; ";
        String jsMethod = "getRsaPassWord(" + pwd + ", " + servertime + ", " + nonce + ", " + pubkey + ")";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mJsEvaluator.evaluate("file:///android_asset/ssologin.html", jsMethod, new JsCallback() {

                @Override
                public void onResult(String value) {
                    // TODO Auto-generated method stub
                    Log.d("mJsEvaluator", "[" + value + "]");
                    Message msg = new Message();
                    rsaPwd = value.replace("\"", "");
                    msg.what = Constaces.MSG_ENCODE_PWD_DONW;
                    mHandler.sendMessage(msg);
                }
            });
        } else {
            mJsEvaluator.evaluate(js + call, new JsCallback() {

                @Override
                public void onResult(String value) {

                    // TODO Auto-generated method stub
                    Message msg = new Message();
                    rsaPwd = value;
                    msg.what = Constaces.MSG_ENCODE_PWD_DONW;
                    mHandler.sendMessage(msg);

                }
            });
        }
    }

    public void doPreLogin(String uname, String upwd) {
        this.mUserName = uname;
        this.mPassword = upwd;
        
        long time = new Date().getTime();
        String encodeName = mSinaPreLogin.encodeAccount(mUserName);
        String url = mSinaPreLogin.buildPreLoginUrl(encodeName, Constaces.SSOLOGIN_JS, time + "");
        getAsyncHttpClient().get(getApplicationContext(), url, mSinaPreLogin.preloginHeaders(), null,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        LogTool.D("LoginBeebo " + "onSuccess " + statusCode + new String(responseBody));
                        mPreLoginResult = mSinaPreLogin.buildPreLoginResult(new String(responseBody));
                        mHandler.sendEmptyMessage(Constaces.MSG_ENCODE_PWD);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        LogTool.D("LoginBeebo " + "onFailure " + statusCode + new String(responseBody)
                                + error.getLocalizedMessage());
                    }
                });
    }
}
