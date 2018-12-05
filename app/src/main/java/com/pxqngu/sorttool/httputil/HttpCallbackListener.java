package com.pxqngu.sorttool.httputil;

/**
 * 创建日期：18-3-9 on 下午11:06
 * 描述:http回调接口
 * 作者:彭辛乾 pxqngu
 */
public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);
}
