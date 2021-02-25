//
//  ByteDanceFilter.m
//  FULiveDemo
//
//  Created by 刘洋 on 2017/8/18.
//  Copyright © 2017年 刘洋. All rights reserved.
//

#import "ByteDanceFilter.h"
#import "BEFrameProcessor.h"

@interface ByteDanceFilter(){
    BEFrameProcessor *_processor;
}

@end

static ByteDanceFilter *shareManager = NULL;

@implementation ByteDanceFilter

+ (ByteDanceFilter *)shareManager
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        shareManager = [[ByteDanceFilter alloc] init];
    });

    return shareManager;
}

- (instancetype)init
{
    if (self = [super init]) {
        EAGLContext *context = [[EAGLContext alloc] initWithAPI:kEAGLRenderingAPIOpenGLES2];
        [EAGLContext setCurrentContext:context];
        _processor = [[BEFrameProcessor alloc] initWithContext:context resourceDelegate:nil];
        _processor.processorResult = BECVPixelBuffer;
        
        [_processor setEffectOn:YES];
        [_processor updateComposerNodes:@[@"/beauty_IOS_live"]];
        [_processor updateComposerNodeIntensity:@"/beauty_IOS_live" key:@"smooth" intensity:0.8];
        [_processor updateComposerNodeIntensity:@"/beauty_IOS_live" key:@"whiten" intensity:0.3];
        [_processor updateComposerNodeIntensity:@"/beauty_IOS_live" key:@"sharp" intensity:0.56];
        [_processor setFilterPath:@"Filter_01_38"];
        [_processor setFilterIntensity:1];
    }
    
    return self;
}


#pragma mark - VideoFilterDelegate
/// process your video frame here
- (CVPixelBufferRef)processFrame:(CVPixelBufferRef)frame frameTime:(CMTime)time{
    if(self.enabled) {
        double timeStamp = (double)time.value / time.timescale;
        BEProcessResult *result = [_processor process:frame timeStamp:timeStamp];
        return result.pixelBuffer;
    }
    return frame;
}



@end
