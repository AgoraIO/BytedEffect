//
//  ByteDanceFilter.h
//  FULiveDemo
//
//  Created by 刘洋 on 2017/8/18.
//  Copyright © 2017年 刘洋. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>
#import "VideoFilterDelegate.h"



@interface ByteDanceFilter : NSObject <VideoFilterDelegate>

@property (nonatomic, assign) BOOL enabled;


+ (ByteDanceFilter *)shareManager;
@end
