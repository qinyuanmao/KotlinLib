package com.wisesoft.android.kotlinlib.http;

import android.text.TextUtils;
import com.blankj.utilcode.util.LogUtils;
import okhttp3.*;
import okio.Buffer;

import java.io.IOException;

public class LoggerInterceptor implements Interceptor {

    public static final String TAG = "HttpClient";
    private String tag;
    private boolean showResponse;

    public LoggerInterceptor(String tag, boolean showResponse) {
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        this.showResponse = showResponse;
        this.tag = tag;
    }

    public LoggerInterceptor(String tag) {
        this(tag, false);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        logForRequest(request);
        Response response = chain.proceed(request);
        return logForResponse(response);
    }

    private void logForRequest(Request request) {
        try {
            String url = request.url().toString();
            Headers headers = request.headers();

            LogUtils.d("method : " + request.method() + "  ║  url : " + url);
            if (headers != null && headers.size() > 0) {
                //LogUtils.d("headers : " + headers.toString());
            }

            RequestBody requestBody = request.body();
            if (requestBody != null) {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null) {
                    //LogUtils.d("requestBody's contentType : " + mediaType.toString());
                    if (isText(mediaType)) {
                        LogUtils.d("requestBody's content : " + bodyToString(request));
                    } else {
                        //LogUtils.e("requestBody's content : " + " maybe [file part] , too large too print , ignored!");
                    }
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private Response logForResponse(Response response) {
        try {
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            LogUtils.d("url : " + clone.request().url() + "  ║  code : " + clone.code() + "  ║  protocol : " + clone.protocol());
            if (!TextUtils.isEmpty(clone.message()))
                //LogUtils.d("message : " + clone.message());

                if (showResponse) {
                    ResponseBody body = clone.body();
                    if (body != null) {
                        MediaType mediaType = body.contentType();
                        if (mediaType != null) {
                            //LogUtils.d("responseBody's contentType : " + mediaType.toString());
                            if (isText(mediaType)) {
                                String resp = body.string();
                                //打印json格式或者xml格式日志
                                switch (mediaType.subtype()) {
                                    case "xml":
                                        LogUtils.xml(resp);
                                        break;
                                    case "json":
                                        LogUtils.json(resp);
                                        break;
                                    default:
                                        LogUtils.d(resp);
                                        break;
                                }
                                body = ResponseBody.create(mediaType, resp);
                                return response.newBuilder().body(body).build();
                            } else {
                                LogUtils.e("responseBody's content : " + " maybe [file part] , too large too print , ignored!");
                            }
                        }
                    }
                }
        } catch (Exception e) {
//            e.printStackTrace();
        }

        return response;
    }


    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
                    )
                return true;
        }
        return false;
    }

    private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }
}
