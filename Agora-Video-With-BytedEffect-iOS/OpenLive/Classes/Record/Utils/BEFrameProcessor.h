// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
#import <GLKit/GLKit.h>
#import <AVFoundation/AVFoundation.h>
#import <Foundation/Foundation.h>
#import "BEButtonItemModel.h"

@class BEFrameProcessor;

@interface BEProcessResult : NSObject
@property (nonatomic, assign) GLuint texture;
@property (nonatomic, assign) unsigned char *rawData;
@property (nonatomic, assign) CVPixelBufferRef pixelBuffer;
@property (nonatomic, assign) CGSize size;
@end

@interface BEFrameProcessor : NSObject
@property (nonatomic, assign) AVCaptureDevicePosition cameraPosition;
@property (nonatomic, assign) CGSize videoDimensions;
@property (nonatomic, readonly) NSString *triggerAction;

- (instancetype)initWithContext:(EAGLContext *)context videoSize:(CGSize)size;
- (BEProcessResult *)process:(CVPixelBufferRef)pixelBuffer timeStamp:(double)timeStamp;
- (void)dealloc;

//- (void)setIndensity:(float)intensity type:(BEEffectFaceBeautyType)type;
- (void)setFilterPath:(NSString *)path;
- (void)setFilterIntensity:(float)intensity;
- (void)setRenderLicense:(NSString *)license;
- (void)setStickerPath:(NSString *)path;

- (void)updateComposerNodes:(NSArray<NSNumber *> *)nodes;
- (void)updateComposerNodeIntensity:(BEEffectNode)node intensity:(CGFloat)intensity;
- (void)setEffectOn:(BOOL)on;

- (void)effectManagerSetInitalStatus;

@end
