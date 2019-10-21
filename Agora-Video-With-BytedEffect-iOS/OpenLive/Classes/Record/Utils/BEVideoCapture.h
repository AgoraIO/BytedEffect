// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
#import <AVFoundation/AVFoundation.h>
#import <Foundation/Foundation.h>
#import "BEGLView.h"

#define AVCaptureSessionPreset NSString*

@class BEVideoCapture;
typedef NS_ENUM(NSInteger, VideoCaptureError) {
    VideoCaptureErrorAuthNotGranted = 0,
    VideoCaptureErrorFailedCreateInput = 1,
    VideoCaptureErrorFailedAddDataOutput = 2,
    VideoCaptureErrorFailedAddDeviceInput = 3,
};

@protocol BEVideoCaptureDelegate <NSObject>
- (void)videoCapture:(BEVideoCapture *)camera didOutputSampleBuffer:(CMSampleBufferRef)sampleBuffer;
- (void)videoCapture:(BEVideoCapture *)camera didFailedToStartWithError:(VideoCaptureError)error;
@end

@interface BEVideoCapture : NSObject
@property (nonatomic, assign) id <BEVideoCaptureDelegate> delegate;
@property (nonatomic, readonly) AVCaptureDevicePosition devicePosition; // default AVCaptureDevicePositionFront
@property (nonatomic, copy) AVCaptureSessionPreset sessionPreset;  // default 1280x720
@property (nonatomic, assign) BOOL isOutputWithYUV; // default NO
- (CGSize)videoSize;
- (void)startRunning;
- (void)stopRunning;
- (void)pause;
- (void)resume;
- (void)switchCamera;
- (void)switchCamera:(AVCaptureDevicePosition)position;
- (void) switchToQRCodeScanWithTopView:(BEGLView*) topView;
- (void) switchToVideoCaptureWithTopView:(BEGLView*) topView;
- (CGRect)getZoomedRectWithRect:(CGRect)rect scaleToFit:(BOOL)scaleToFit;
- (void)setFlip:(BOOL)isFlip;
- (void)setOrientation:(AVCaptureVideoOrientation)orientation;
@end
