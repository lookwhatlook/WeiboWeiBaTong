package lib.org.zarroboogs.weibo.login.httpclient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lib.org.zarroboogs.weibo.login.javabean.UploadPicResult;
import lib.org.zarroboogs.weibo.login.utils.Constaces;
import lib.org.zarroboogs.weibo.login.utils.LogTool;
import lib.org.zarroboogs.weibo.login.utils.PatternUtils;

import org.apache.http.Header;
import org.apache.http.entity.FileEntity;
import org.apache.http.message.BasicHeader;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class UploadHelper {
    private Context mContext;
    private AsyncHttpClient mAsyncHttpClient;
    public UploadHelper(Context context, AsyncHttpClient asyncHttpClient){
        this.mContext = context;
        this.mAsyncHttpClient = asyncHttpClient;
    }
    
    public static final int MSG_UPLOAD = 0x1000;
    public static final int MSG_UPLOAD_DONE = 0x1001;
    public int mHasUploadFlag = -1;

    private String mPids = "";
    private List<String> mNeedToUpload = new ArrayList<String>();
    
    private OnUpFilesListener mOnUpFilesListener;
    
    
    public static interface OnUpFilesListener{
        public void onUpSuccess(String pids);
    }
    
    
    public void uploadFiles(List<String> files, OnUpFilesListener listener){
        this.mNeedToUpload = files;
        this.mOnUpFilesListener = listener;
        mHasUploadFlag = 0;
        mHandler.sendEmptyMessage(MSG_UPLOAD);
    }
    Handler mHandler = new Handler(){
      public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPLOAD: {
                    uploadFile(mNeedToUpload.get(mHasUploadFlag));
                    break;
                }
                case MSG_UPLOAD_DONE: {
                    if (mOnUpFilesListener != null) {
                        mOnUpFilesListener.onUpSuccess(mPids);
                    }
                    break;
                }

                default:
                    break;
            }
      };  
    };
    
    
    private void uploadFile(String file) {
        // "/sdcard/tencent/zebrasdk/Photoplus.jpg"

        Header[] getHeader = {
                new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"),
                new BasicHeader("Accept-Encoding", "gzip,deflate"),
                new BasicHeader("Accept-Language", "en-US,en;q=0.8"),
                new BasicHeader("Cache-Control", "max-age=0"),
                new BasicHeader("Connection", "keep-alive"),
                new BasicHeader("Content-Type", "application/octet-stream"),
                new BasicHeader("Host", "picupload.service.weibo.com"),
                new BasicHeader("Origin", "http://weibo.com"),
                new BasicHeader("User-Agent", Constaces.User_Agent),
                new BasicHeader("Referer", "http://tjs.sjs.sinajs.cn/open/widget/static/swf/MultiFilesUpload.swf?"
                        + "version=1411256448572"),
                // new BasicHeader("Content-Type",
                // "multipart/form-data; boundary=----WebKitFormBoundary7oINSxHhxVRAcxGL"),
        };

        final String contentType = RequestParams.APPLICATION_OCTET_STREAM;
        File uploadFile = new File(file);

        FileEntity reqEntity = new FileEntity(uploadFile, "binary/octet-stream");

        String unMark = "http://picupload.service.weibo.com/interface/pic_upload.php?app="
                + "miniblog&data=1&mime=image/png&ct=0.2805887470021844";
        String url = "http://picupload.service.weibo.com/interface/pic_upload.php?cb=http://weibo.com/aj/static/upimgback.html?_wv=5&callback=STK_ijax_141952136307389&url=weibo.com/u/2294141594&markpos=1&logo=1&nick=@iBeebo&marks=0&app=miniblog&s=rdxt";
        // http://picupload.service.weibo.com/interface/pic_upload.php?cb=http://weibo.com/aj/static/upimgback.html?_wv=5&callback=STK_ijax_141952136307389&url=weibo.com/u/2294141594&markpos=1&logo=1&nick=@iBeebo&marks=0&app=miniblog&s=rdxt

        mAsyncHttpClient.post(mContext.getApplicationContext(), unMark, getHeader, reqEntity, contentType,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String result = new String(responseBody);

                        Gson mGson = new Gson();
                        UploadPicResult ur = mGson.fromJson(PatternUtils.preasePid(result), UploadPicResult.class);
                        Log.d("uploadFile   pid: ", ur.getPid());
                        LogTool.D("uploadFile onSuccess" + " " + result);
                        mHasUploadFlag++;
                        mPids += ur.getPid() + ",";
                        if (mHasUploadFlag < mNeedToUpload.size()) {
                            mHandler.sendEmptyMessage(MSG_UPLOAD);
                        }else {
                            mHandler.sendEmptyMessage(MSG_UPLOAD_DONE);
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        LogTool.D("uploadFile onFailure" + " statusCode:" + statusCode + "   " + error.getLocalizedMessage());
                    }
                });
    }
}
