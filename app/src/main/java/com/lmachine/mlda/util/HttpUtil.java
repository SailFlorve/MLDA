package com.lmachine.mlda.util;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by SailFlorve on 2017/7/21 0021.<br>
 * OkHttp请求封装类 <br>
 * <br>
 * 使用方法： <br>
 * HttpUtil.load(url) <br>
 * .addHeader("Content-TimeType", "Content-TimeType: application/") <br>
 * .addParam("Username", "user") <br>
 * .addParam("Password", "123") <br>
 * .addFile("Img", "user.jpg", file) <br>
 * .setTimeOut(10) <br>
 * .post(callback); <br>
 */

public class HttpUtil {

    public static HttpBuilder load(String url) {
        return new HttpBuilder().load(url);
    }

    @SuppressWarnings("WeakerAccess")
    public static class HttpBuilder {
        //连接超时
        private int timeOut = 3;
        //是否重定向
        private boolean isFollowRedirects = false;

        private Request.Builder requestBuilder;
        private MultipartBody.Builder multipartBuilder;
        private OkHttpClient client;
        private Call call;

        private HttpBuilder() {
            multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            requestBuilder = new Request.Builder();
            client = new OkHttpClient.Builder()
                    .connectTimeout(timeOut, TimeUnit.SECONDS)
                    .followRedirects(isFollowRedirects)
                    .build();
        }

        /**
         * 加载Url。
         *
         * @param url 要加载的url
         */
        private HttpBuilder load(String url) {
            requestBuilder = requestBuilder.url(url);
            return this;
        }

        /**
         * 添加请求头
         */
        public HttpBuilder addHeader(String key, String value) {
            requestBuilder.addHeader(key, value);
            return this;
        }

        /**
         * 添加请求参数
         *
         * @param key   参数名称
         * @param value 参数值
         */
        public HttpBuilder addParam(String key, String value) {
            multipartBuilder = multipartBuilder.addFormDataPart(key, value);
            return this;
        }

        /**
         * 添加文件
         *
         * @param key      key值
         * @param fileName 文件名
         * @param file     文件
         */
        public HttpBuilder addFile(String key, String fileName, File file) {
            if (file != null) {
                multipartBuilder = multipartBuilder.addFormDataPart(key, fileName,
                        RequestBody.create(MediaType.parse("application/formdata"), file));
            }
            return this;
        }

        /**
         * 以post方式发送Http请求。
         *
         * @param callback 请求回调
         */
        public Call post(Callback callback) {
            Request request = requestBuilder.post(multipartBuilder.build()).build();
            call = client.newCall(request);
            call.enqueue(callback);
            return call;
        }

        /**
         * 以get方式发送Http请求。
         *
         * @param callback 请求回调
         */
        public Call get(Callback callback) {
            Request request = requestBuilder.build();
            call = client.newCall(request);
            call.enqueue(callback);
            return call;
        }

        /**
         * 设置连接超时。
         *
         * @param timeOut 超时时间（秒）
         */
        public HttpBuilder setTimeOut(int timeOut) {
            this.timeOut = timeOut;
            return this;
        }

        /**
         * 设置是否重定向
         */
        public HttpBuilder setFollowRedirects(boolean followRedirects) {
            isFollowRedirects = followRedirects;
            return this;
        }
    }
}
