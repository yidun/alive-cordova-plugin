package com.netease.alivedetector.cordovaplugin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.yidunalivedetect.R;
import com.netease.nis.alivedetected.ActionType;
import com.netease.nis.alivedetected.AliveDetector;
import com.netease.nis.alivedetected.DetectedListener;
import com.netease.nis.alivedetected.NISCameraPreview;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.CameraBridgeViewBase;

import java.util.logging.Logger;

/**
 * Created by hzhuqi on 2020/6/11
 */
public class YdCameraPreview extends CordovaPlugin {
    private static final String TAG = "Alive";
    private FrameLayout cameraContainer;
    private NISCameraPreview cameraPreview;
    private AliveDetector aliveDetector;
    private DetectedListener detectedListener;
    private KeepAliveCallbackContext callbackContext;
    private ViewGroup contentView;

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        internalInit();
        Log.d(TAG, "-----------------onStart-------------");
    }

    //    @Override
//    public void onResume(boolean multitasking) {
//        super.onResume(multitasking);
//    //    internalInit();
//        Log.d(TAG, "-----------------onResume-------------");
//    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        super.execute(action, args, callbackContext);
        if (this.callbackContext == null) {
            this.callbackContext = KeepAliveCallbackContext.newInstance(callbackContext);
        }
        boolean isSuccess = false;
        Log.d(TAG, "action:" + action + " args:" + args);
        if ("init".equals(action)) {
            //    internalInit();
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    init(args);
                }
            });
            isSuccess = true;
        } else if ("startDetect".equals(action)) {
            startDetect();
            isSuccess = true;
        } else if ("stopDetect".equals(action)) {
            stopDetect();
            isSuccess = true;
        } else {
            callbackContext.error("invalid action");
        }
        return isSuccess;
    }

    protected NISCameraPreview getPreview(Context context, ViewGroup parent) {
        if (cameraContainer == null) {
            cameraContainer = getPreviewContainer(context, parent);
        }
        cameraPreview = cameraContainer.findViewById(R.id.surface_view);
        return cameraPreview;
    }

    protected FrameLayout getPreviewContainer(Context context, ViewGroup parent) {
        cameraContainer = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.preview_layout, parent);
        return cameraContainer;
    }


    public void init(JSONArray options) {
        try {
            int x = options.getInt(0);
            int y = options.getInt(1);
            int width = options.getInt(2);
            int height = options.getInt(3);
            int radius = options.getInt(4);
            int time = options.getInt(5);
            updateLayoutParams(x, y, width, height);
            String businessId = options.getString(6);
            aliveDetector = AliveDetector.getInstance();
            cameraPreview.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
            aliveDetector.init(cordova.getContext(), cameraPreview, businessId);
            if (time != 0) {
                aliveDetector.setTimeOut(time);
            }
            aliveDetector.setDetectedListener(detectedListener);
            Log.d("Alive", "component init");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "init exception:" + e.getMessage());
        }
    }


    public void startDetect() {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cameraPreview.getVisibility() == View.INVISIBLE) {
                    cameraPreview.setVisibility(View.VISIBLE);
                }
                aliveDetector.startDetect();
                Log.d(TAG, "component startDetect");
            }
        });

//        if (cameraPreview.getVisibility() == View.INVISIBLE) {
//            cameraPreview.setVisibility(View.VISIBLE);
//        }
//        aliveDetector.startDetect();
        Log.d(TAG, "component startDetect");
    }


    public void stopDetect() {
        aliveDetector.stopDetect();
        Log.d(TAG, "component stopDetect");
    }

    private void internalInit() {
        Log.d(TAG, "thread name" + Thread.currentThread().getName());
        ViewGroup decorView = (ViewGroup) cordova.getActivity().getWindow().getDecorView();
        contentView = decorView.findViewById(android.R.id.content);
        cameraContainer = getPreviewContainer(cordova.getContext(), contentView);
        cameraPreview = getPreview(cordova.getContext(), contentView);
        cameraPreview.setVisibility(View.INVISIBLE);
//        cordova.getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                //   cameraPreview.setVisibility(View.INVISIBLE);
//            }
//        });
        initListener();
    }

    private void updateLayoutParams(int x, int y, int width, int height) {
        Log.d(TAG, "x:" + x + " y:" + y + " width:" + width + " height:" + height);
        while (cameraPreview == null) {
            // wait for cameraPreview init
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) cameraPreview.getLayoutParams();
        layoutParams.width = dip2px(cordova.getContext(), width);
        layoutParams.height = dip2px(cordova.getContext(), height);
        layoutParams.leftMargin = dip2px(cordova.getContext(), x);
        layoutParams.topMargin = dip2px(cordova.getContext(), y);
        cameraPreview.setLayoutParams(layoutParams);
    }

    private void initListener() {
        detectedListener = new DetectedListener() {
            @Override
            public void onReady(boolean isInitSuccess) {
                Log.d(TAG, "onReady callback");
                final JSONObject callBackJson = new JSONObject();
                try {
                    if (isInitSuccess) {
                        callBackJson.put("init_success", true);
                    } else {
                        callBackJson.put("init_success", false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, callBackJson);
//                pluginResult.setKeepCallback(true);
//                callbackContext.sendPluginResult(pluginResult);
                callbackContext.success(callBackJson, true);
            }

            @Override
            public void onActionCommands(ActionType[] actionTypes) {
                Log.d(TAG, "onActionCommands callback");
                final JSONObject callBackJson = new JSONObject();
                String commands = buildActionCommand(actionTypes);
                try {
                    callBackJson.put("action_commands", commands);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, callBackJson);
//                pluginResult.setKeepCallback(true);
//                callbackContext.sendPluginResult(pluginResult);
                callbackContext.success(callBackJson, true);
            }

            @Override
            public void onStateTipChanged(ActionType actionType, String stateTip) {
                Log.d(TAG, "onStateTipChanged callback");
                final JSONObject callBackJson = new JSONObject();
                try {
                    callBackJson.put("action_type", actionType.getActionID());
                    callBackJson.put("state_tip", stateTip);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, callBackJson);
//                pluginResult.setKeepCallback(true);
//                callbackContext.sendPluginResult(pluginResult);
                callbackContext.success(callBackJson, true);
            }

            @Override
            public void onPassed(boolean isPassed, String token) {
                Log.d(TAG, "onPassed callback");
                final JSONObject callBackJson = new JSONObject();
                try {
                    if (isPassed) {
                        callBackJson.put("is_passed", true);
                    } else {
                        callBackJson.put("is_passed", false);
                    }
                    callBackJson.put("token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, callBackJson);
//                pluginResult.setKeepCallback(true);
//                callbackContext.sendPluginResult(pluginResult);
                callbackContext.success(callBackJson, true);
            }

            @Override
            public void onError(int code, String msg, String token) {
                Log.d(TAG, "onError callback");
                final JSONObject callBackJson = new JSONObject();
                try {
                    callBackJson.put("error_code", code);
                    callBackJson.put("msg", msg);
                    callBackJson.put("token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, callBackJson);
//                pluginResult.setKeepCallback(true);
//                callbackContext.sendPluginResult(pluginResult);
                callbackContext.success(callBackJson, true);
            }

            @Override
            public void onOverTime() {
                Log.d(TAG, "onOverTime callback");
                final JSONObject callBackJson = new JSONObject();
                try {
                    callBackJson.put("over_time", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, callBackJson);
//                pluginResult.setKeepCallback(true);
//                callbackContext.sendPluginResult(pluginResult);
                callbackContext.success(callBackJson, true);
            }
        };
    }

    private String buildActionCommand(ActionType[] actionCommands) {
        StringBuilder commands = new StringBuilder();
        for (ActionType actionType : actionCommands) {
            commands.append(actionType.getActionID());
        }
        return commands == null ? "" : commands.toString();
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
