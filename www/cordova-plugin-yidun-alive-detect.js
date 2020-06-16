var exec = require('cordova/exec');

var PLUGIN_NAME = "NTESLiveCameraImage";

function isFunction(obj) {
  return !!(obj && obj.constructor && obj.call && obj.apply);
};

var YidunAliveDetect = function(options, onCallback) {
  if (!options) {
    if (isFunction(onCallback)) {
      onCallback(new Error ('businessId is required'))
    } else {
      throw new Error('businessId is required')
    }
    return
  }

  options.x = options.x || 0;
  options.y = options.y || 0;
  options.width = options.width || window.screen.width;
  options.height = options.height || window.screen.height;
  options.radius = options.radius || 0;
  options.time = options.time || 30000;
  options.businessId = options.businessId;
  
  this.onSuccess = function (ev) {
    onCallback(ev)
  }
  
  this.onError = function (err) {
    onCallback(err)
  }

  exec(this.onSuccess, this.onError, PLUGIN_NAME, "init", [
    options.x, 
    options.y, 
    options.width, 
    options.height,
    options.radius,
    options.time,
    options.businessId
  ]);

  // exec(this.onSuccess, this.onError, PLUGIN_NAME, "startDetect",[]);
};

YidunAliveDetect.prototype.startDetect = function() {
  Cordova.exec(this.onSuccess, this.onError, PLUGIN_NAME, "startDetect",[]);
};

YidunAliveDetect.prototype.stopDetect = function() {
  exec(this.onSuccess, this.onError, PLUGIN_NAME, "stopDetect",[]);
};

YidunAliveDetect.DETECT_ACTION_TYPES = {
  '0': '正视前方',
	'1': '向右转头',
	'2': '向左转头',
	'3': '张嘴动作',
	'4': '眨眼动作',
	'5': '动作错误',
	'6': '动作通过'
};

module.exports = YidunAliveDetect;

