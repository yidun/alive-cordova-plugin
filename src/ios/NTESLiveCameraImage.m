//
//  NTESLiveCameraImage.m
//  GameSDK
//
//  Created by 罗礼豪 on 2020/6/11.
//

#import "NTESLiveCameraImage.h"
#import <NTESLiveDetect/NTESLiveDetect.h>

@interface NTESLiveCameraImage()

@property (nonatomic, strong) CDVPluginResult *pluginResult;

@property (nonatomic, strong) NTESLiveDetectManager *detector;

@property (nonatomic, strong) CDVInvokedUrlCommand *command;

@property (nonatomic, strong) NSString *bussnessId;

@end

@implementation NTESLiveCameraImage

- (void)init:(CDVInvokedUrlCommand*)command {
    int x;
    int y;
    int width;
    int height;
    int cornerRadius;
    int timeout;
    NSString *bussnessId;
    
    if (command.arguments.count == 7) {
        NSNumber *numberX  = (NSNumber *)(command.arguments[0]);
        x = [numberX intValue];
           
        NSNumber *numberY  = (NSNumber *)(command.arguments[1]);
        y = [numberY intValue];
           
        NSNumber *numberW  = (NSNumber *)(command.arguments[2]);
        width = [numberW intValue];
           
        NSNumber *numberH  = (NSNumber *)(command.arguments[3]);
        height = [numberH intValue];
           
        NSNumber *numberRadius  = (NSNumber *)(command.arguments[4]);
        cornerRadius = [numberRadius intValue];
           
        NSNumber *numberTime  = (NSNumber *)(command.arguments[5]);
        timeout = [numberTime intValue];
        if (timeout <= 0) {
            timeout = 30000;
        }
           
        NSNumber *numberBussnessId  = (NSNumber *)(command.arguments[6]);
        bussnessId = [numberBussnessId stringValue];
        self.bussnessId = bussnessId;
    } else {
        
    }
    
    UIImageView *cameraImage = [[UIImageView alloc] initWithFrame:CGRectMake(x, y, width, height)];
    cameraImage.layer.cornerRadius = cornerRadius;
    cameraImage.layer.masksToBounds = YES;
    self.detector = [[NTESLiveDetectManager alloc] initWithImageView:cameraImage withDetectSensit:NTESSensitNormal];
    [self.detector setTimeoutInterval:timeout];
    [self.webView.superview insertSubview:cameraImage aboveSubview:self.webView];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(liveDetectStatusChange:) name:@"NTESLDNotificationStatusChange" object:nil];
}

- (void)startDetect:(CDVInvokedUrlCommand*)command {
    self.command = command;
       
    __weak __typeof(self)weakSelf = self;
    [self.detector startLiveDetectWithBusinessID:@"6a1a399443a54d31b91896a4208bf6e0" actionsHandler:^(NSDictionary * _Nonnull params) {
            dispatch_async(dispatch_get_main_queue(), ^{
            NSString *actions = [params objectForKey:@"actions"];
            NSMutableDictionary *dict = [NSMutableDictionary dictionary];
            [dict setValue:actions forKey:@"action_commands"];
                weakSelf.pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dict];
                [weakSelf.pluginResult setKeepCallbackAsBool:YES];
                [weakSelf.commandDelegate sendPluginResult:weakSelf.pluginResult callbackId:command.callbackId];
        });
    } completionHandler:^(NTESLDStatus status, NSDictionary * _Nullable params) {
        dispatch_async(dispatch_get_main_queue(), ^{
            NSMutableDictionary *dict = [weakSelf showToastWithLiveDetectStatus:status];
            NSString *token = [params objectForKey:@"token"];
            [dict setValue:token forKey:@"token"];
            weakSelf.pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dict];
            [weakSelf.pluginResult setKeepCallbackAsBool:YES];
            [weakSelf.commandDelegate sendPluginResult:weakSelf.pluginResult callbackId:command.callbackId];
        });
    }];
}

- (void)liveDetectStatusChange:(NSNotification *)infoNotification {
    NSDictionary *infoDict = [infoNotification.userInfo objectForKey:@"info"];
       
    NSNumber *key = [[infoDict allKeys] firstObject];
    int keyValue = [key intValue];
       
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
       
    NSString *stateTip;
    if (keyValue == 0) {
        stateTip = @"正视前方";
    } else if (keyValue == 1) {
        stateTip = @"向右转头";
    } else if (keyValue == 2) {
        stateTip = @"向左转头";
    } else if (keyValue == 3) {
        stateTip = @"张嘴动作";
    } else if (keyValue == 4) {
        stateTip = @"眨眼动作";
    } else {
        stateTip = @"正视前方";
    }
       
    [dict setValue:@(keyValue) forKey:@"action_type"];
    [dict setValue:stateTip forKey:@"state_tip"];
    self.pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dict];
    [self.pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:self.pluginResult callbackId:self.command.callbackId];
}

- (void)stopDetect:(CDVInvokedUrlCommand*)command {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.detector stopLiveDetect];
    });
}

- (NSMutableDictionary *)showToastWithLiveDetectStatus:(NTESLDStatus)status {
    NSString *msg = @"";
    NSUInteger value = 0;
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    switch (status) {
        case NTESLDCheckPass: {
            value = 1;
            msg = @"活体检测通过";
            [dict setValue:@"true" forKey:@"is_passed"];
        }
            break;
        case NTESLDCheckNotPass:
            value = 2;
            msg = @"活体检测不通过";
            [dict setValue:@"false" forKey:@"is_passed"];
            break;
        case NTESLDOperationTimeout:
        {
            value = 3;
            msg = @"动作检测超时\n请在规定时间内完成动作";
            [dict setValue:@(value) forKey:@"error_code"];
        }
            break;
        case NTESLDGetConfTimeout:
            value = 4;
            msg = @"活体检测获取配置信息超时";
            [dict setValue:@"true" forKey:@"over_time"];
            break;
        case NTESLDOnlineCheckTimeout:
            value = 5;
            msg = @"云端检测结果请求超时";
            [dict setValue:@(value) forKey:@"error_code"];
            break;
        case NTESLDOnlineUploadFailure:
            value = 6;
            msg = @"云端检测上传图片失败";
            [dict setValue:@(value) forKey:@"error_code"];
            break;
        case NTESLDNonGateway:
            value = 7;
            msg = @"网络未连接";
            [dict setValue:@(value) forKey:@"error_code"];
            break;
        case NTESLDSDKError:
            value = 8;
            msg = @"SDK内部错误";
            [dict setValue:@(value) forKey:@"error_code"];
            break;
        case NTESLDCameraNotAvailable:
            value = 9;
            msg = @"App未获取相机权限";
            [dict setValue:@(value) forKey:@"error_code"];
            break;
        default:
            value = 10;
            msg = @"未知错误";
            [dict setValue:@(value) forKey:@"error_code"];
            break;
    }
    
    [dict setValue:msg forKey:@"msg"];
    return dict;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end



