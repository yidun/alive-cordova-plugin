//
//  NTESLiveCameraImage.h
//  GameSDK
//
//  Created by 罗礼豪 on 2020/6/11.
//

#import <Foundation/Foundation.h>
#import <Cordova/CDV.h>

NS_ASSUME_NONNULL_BEGIN

@interface NTESLiveCameraImage : CDVPlugin

/**
初始化活体检测
*/
- (void)init:(CDVInvokedUrlCommand*)command;

/**
 结束活体检测
 */
- (void)startDetect:(CDVInvokedUrlCommand*)command;

/**
 开始活体检测
 */
- (void)stopDetect:(CDVInvokedUrlCommand*)command;

@end

NS_ASSUME_NONNULL_END

