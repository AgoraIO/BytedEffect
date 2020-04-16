// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.

#import <UIKit/UIKit.h>
#import "BEFrameProcessor.h"


@protocol BETapDelegate <NSObject>

- (void)onTap;

@end

@protocol BEDefaultTapDelegate <NSObject>

- (void)onDefaultTap;

@end

@interface BEVideoRecorderViewController : UIViewController

- (void)initProcessor:(EAGLContext *)context;
- (BEProcessResult *)process:(CVPixelBufferRef)pixelBuffer timeStamp:(double)timeStamp;

@end

