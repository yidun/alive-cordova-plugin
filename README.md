# Cordova Plugin Yidun Alive Detect

易盾活体检测cordova原生插件版

## Installation

```
cordova plugin add cordova-plugin-yidun-alive-detect

ionic cordova plugin add cordova-plugin-yidun-alive-detect

meteor add cordova:cordova-plugin-yidun-alive-detect@X.X.X

<gap:plugin name="cordova-plugin-yidun-alive-detect" />
```

#### iOS Quirks
1. 易盾活体检测用到摄像头功能，请确保有摄像和网络权限
2. If you are developing for iOS 10+ you must also add the following to your config.xml

```xml
<config-file platform="ios" target="*-Info.plist" parent="NSCameraUsageDescription" overwrite="true">
  <string>Allow the app to use your camera</string>
</config-file>

<!-- or for Phonegap -->

<gap:config-file platform="ios" target="*-Info.plist" parent="NSCameraUsageDescription" overwrite="true">
  <string>Allow the app to use your camera</string>
</gap:config-file>
```
3.app接入活体设置（如权限设置等）请参考易盾文档

#### Android Quirks

1. 预览高度不要随意设置，请遵守大部分相机支持的预览高宽比，3:4或9:16
2. 如果想在android设置预览的容器形状，请修改preview_layout.xml

## 活体接入

### 初始化
```js
  var $actionEl = document.getElementById('detectAction');  // 显示动作提示的元素
  var yidunAliveDetector = new YidunAliveDetect({
          x: 100,  // Number, 预览窗口x坐标
          y: 100,  // Number, 预览窗口y坐标
          width: 180,  // Number, 预览窗口宽度
          height: 240, // Number, 预览窗口高度
          radius: 90, // Number, 预览窗口圆角，android无效
          businessId: '从易盾申请的id'
        }, function (ev) {
          // 初始化活体检测引擎成功
          if (ev['init_success']) {
            return;
          }
          // 检测提示
          if (ev['state_tip']) {
            // 用户可以根据action_type自定义提示文案
            $actionEl.innerText = `${YidunAliveDetect.DETECT_ACTION_TYPES[ev['action_type']]}--文案: ${ev['state_tip']}`;
          } else {
            $actionEl.innerText = '';
          }
          // 检测结果
          if (ev['is_passed']) {
            // TODO: 处理token, ev['token']
            return;
          }
          
          // 检测失败
          if (ev['error_code']) {
            // TODO: 处理检测失败情况，ev['error_code']
            return;
          }
          
          if (ev['over_time']) {
            // TODO: 处理检测超时
            return;
          }
        });
```

### 开始检测
```js
  yidunAliveDetector.startDetect()
```

### 结束检测
```js
  yidunAliveDetector.stopDetect()
```

### 动作类型映射
```
YidunAliveDetect.DETECT_ACTION_TYPES = {
  '0': '正视前方',
	'1': '向右转头',
	'2': '向左转头',
	'3': '张嘴动作',
	'4': '眨眼动作',
	'5': '动作错误',
	'6': '动作通过'
};
```

## 简单示例app

<a href="https://github.com/yidun/alive-cordova-plugin-demo">cordova-yidun-alive-detect-demo</a> for a complete working Cordova example for Android and iOS platforms.

