package com.netease.alivedetector.cordovaplugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * Created by hzhuqi on 2020/6/15
 */
public class KeepAliveCallbackContext extends CallbackContext {

    public static KeepAliveCallbackContext newInstance(CallbackContext callbackContext) {
        CordovaWebView webView = null;
        Class cls = callbackContext.getClass();
        try {
            Field field = cls.getDeclaredField("webView");
            field.setAccessible(true);
            webView = (CordovaWebView) field.get(callbackContext);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return new KeepAliveCallbackContext(callbackContext.getCallbackId(), webView);
    }

    public KeepAliveCallbackContext(String callbackId, CordovaWebView webView) {
        super(callbackId, webView);
    }

    public void sendPluginResult(PluginResult pluginResult, boolean isKeep) {
        pluginResult.setKeepCallback(isKeep);
        super.sendPluginResult(pluginResult);
    }


    /**
     * success回调,isKeep 为true的时候,可以持续把数据返回到js层中
     *
     * @param message
     * @param isKeep
     */
    public void success(JSONObject message, boolean isKeep) {
        sendPluginResult(new PluginResult(PluginResult.Status.OK, message), isKeep);
    }

    /**
     * success回调,isKeep 为true的时候,可以持续把数据返回到js层中
     *
     * @param message
     * @param isKeep
     */
    public void success(String message, boolean isKeep) {
        sendPluginResult(new PluginResult(PluginResult.Status.OK, message), isKeep);
    }

    /**
     * success回调,isKeep 为true的时候,可以持续把数据返回到js层中
     *
     * @param message
     * @param isKeep
     */
    public void success(JSONArray message, boolean isKeep) {
        sendPluginResult(new PluginResult(PluginResult.Status.OK, message), isKeep);
    }

    /**
     * success回调,isKeep 为true的时候,可以持续把数据返回到js层中
     *
     * @param message
     * @param isKeep
     */
    public void success(byte[] message, boolean isKeep) {
        sendPluginResult(new PluginResult(PluginResult.Status.OK, message), isKeep);
    }

    /**
     * success回调,isKeep 为true的时候,可以持续把数据返回到js层中
     *
     * @param message
     * @param isKeep
     */
    public void success(int message, boolean isKeep) {
        sendPluginResult(new PluginResult(PluginResult.Status.OK, message), isKeep);
    }

    /**
     * success回调,isKeep 为true的时候,可以持续把数据返回到js层中
     *
     * @param isKeep
     */
    public void success(boolean isKeep) {
        sendPluginResult(new PluginResult(PluginResult.Status.OK), isKeep);
    }

    /**
     * error回调,isKeep 为true的时候,可以持续把数据返回到js层中
     *
     * @param message
     * @param isKeep
     */
    public void error(JSONObject message, boolean isKeep) {
        sendPluginResult(new PluginResult(PluginResult.Status.ERROR, message), isKeep);
    }

    /**
     * error回调,isKeep 为true的时候,可以持续把数据返回到js层中
     *
     * @param message
     * @param isKeep
     */
    public void error(String message, boolean isKeep) {
        sendPluginResult(new PluginResult(PluginResult.Status.ERROR, message), isKeep);
    }

    /**
     * error回调,isKeep 为true的时候,可以持续把数据返回到js层中
     *
     * @param message
     * @param isKeep
     */
    public void error(int message, boolean isKeep) {
        sendPluginResult(new PluginResult(PluginResult.Status.ERROR, message), isKeep);
    }
}

