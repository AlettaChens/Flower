//package com.example.wordchen.uitls;
//import com.example.wordchen.api.Api;
//
//import org.xutils.common.Callback;
//import org.xutils.common.Callback.Cancelable;
//import org.xutils.http.RequestParams;
//import org.xutils.x;
//import java.util.Map;
//
//
//public class xUtils {
//
//    /**
//     * 发送get请求
//     *
//     * @param <T>
//     */
//    public static <T> Cancelable get(String url, Map<String, String> map, Callback.CommonCallback<T> callback) {
//        RequestParams params = new RequestParams(Api.BASE_URL+url);
//        if (null != map) {
//            for (Map.Entry<String, String> entry : map.entrySet()) {
//                params.addQueryStringParameter(entry.getKey(), entry.getValue());
//            }
//        }
//        return x.http().get(params, callback);
//    }
//
//    /**
//     * 发送post请求
//     *
//     * @param <T>
//     */
//    public static <T> Cancelable post(String url, Map<String, Object> map, Callback.CommonCallback<T> callback) {
//        RequestParams params = new RequestParams(Api.BASE_URL + url);
//        if (null != map) {
//            for (Map.Entry<String, Object> entry : map.entrySet()) {
//                params.addParameter(entry.getKey(), entry.getValue());
//            }
//        }
//        return x.http().post(params, callback);
//    }
//
//    /**
//     * 上传文件
//     *
//     * @param <T>
//     */
//    public static <T> Cancelable upLoadFile(String url, Map<String, Object> map, Callback.CommonCallback<T> callback) {
//        RequestParams params = new RequestParams(Api.BASE_URL + url);
//        if (null != map) {
//            for (Map.Entry<String, Object> entry : map.entrySet()) {
//                params.addParameter(entry.getKey(), entry.getValue());
//            }
//        }
//        params.setMultipart(true);
//        Cancelable cancelable;
//        cancelable = x.http().get(params, callback);
//        return cancelable;
//    }
//
//    /**
//     * 下载文件
//     *
//     * @param <T>
//     */
//    public static <T> Cancelable ownLoadFile(String url, String filepath, Callback.CommonCallback<T> callback) {
//        RequestParams params = new RequestParams(url);
//        //设置断点续传
//        params.setAutoResume(true);
//        params.setSaveFilePath(filepath);
//        return x.http().get(params, callback);
//    }
//}
