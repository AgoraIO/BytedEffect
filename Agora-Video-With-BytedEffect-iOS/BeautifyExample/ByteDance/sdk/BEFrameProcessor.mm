// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
#import "BEFrameProcessor.h"

#import <OpenGLES/ES2/glext.h>
#import "RenderMsgDelegate.h"

#import "BERender.h"
#import "BEEffectManager.h"
#import "BEResourceHelper.h"
#import "BETimeRecoder.h"
#import "BEEffectResourceHelper.h"


@implementation BEProcessResultBuffer
@end

@implementation BEProcessResult
@end

@interface BEFrameProcessor() <RenderMsgDelegate> {
    
    EAGLContext *_glContext;

    BOOL                    _effectOn;
    BEEffectManager         *_effectManager;
    BERender                *_render;
    BEResourceHelper        *_resourceHelper;
    IRenderMsgDelegateManager *_manager;
    BETimeRecoder           *_timeRecoder;
    BEFormatType            _inputFormat;

    BOOL                    _shouldResetComposer;
    bef_ai_face_info        *_faceInfo;
    bef_ai_hand_info        *_handInfo;
    bef_ai_skeleton_result  *_skeletonInfo;
}

@end

@implementation BEFrameProcessor

/**
 * license有效时间2019-03-01到2019-04-30
 * license只是为了追踪使用情况，可以随时申请无任何限制license
 */

- (instancetype)initWithContext:(EAGLContext *)context resourceDelegate:(id<BEResourceHelperDelegate>)delegate {
    self = [super init];
    if (self) {
        _glContext = context;
        [EAGLContext setCurrentContext:context];
        
        _effectOn = YES;
        _shouldResetComposer = YES;
        _pixelBufferAccelerate = YES;
        _processorResult = BETexture;
        _faceInfo = NULL;
        _handInfo = NULL;
        _skeletonInfo = NULL;
        BEEffectResourceHelper *resourceHelper = [BEEffectResourceHelper new];
        _effectManager = [[BEEffectManager alloc] initWithResourceProvider:resourceHelper licenseProvider:[BELicenseHelper shareInstance]];
        int ret = [_effectManager initTask];
        NSLog(@"ret == %d", ret);
        if (ret == BEF_RESULT_SUC) {
            [self setEffectOn:true];
        }
        _render = [[BERender alloc] init];
        _resourceHelper = [[BEResourceHelper alloc] init];
        _resourceHelper.delegate = delegate;
        self.usePipeline = YES;
        
#ifdef TIME_LOG
        _timeRecoder = [BETimeRecoder new];
#endif
    }
    return self;
}

- (void)dealloc {
    NSLog(@"BEFrameProcessor dealloc %@", NSStringFromSelector(_cmd));
    free(_faceInfo);
    free(_handInfo);
    free(_skeletonInfo);
    [EAGLContext setCurrentContext:_glContext];
    [self be_releaseSDK];
}

/*
 * 帧处理流程
 */
- (BEProcessResult *)process:(CVPixelBufferRef)pixelBuffer timeStamp:(double)timeStamp{
    CVPixelBufferLockBaseAddress(pixelBuffer, 0);
    BEPixelBufferInfo *info = [_render getCVPixelBufferInfo:pixelBuffer];
    if (info.format == BE_UNKNOW) {
        NSLog(@"unknow pixelBuffer format, use format show in BEFormatType...");
        return nil;
    }
    _inputFormat = info.format;
#ifdef TIME_LOG
    [_timeRecoder record:@"totalProcess"];
#endif
    // 设置 OpenGL 环境 , 需要与初始化 SDK 时一致
    if ([EAGLContext currentContext] != _glContext) {
        [EAGLContext setCurrentContext:_glContext];
    }

    BEProcessResult *result;
    if (_pixelBufferAccelerate) {
#ifdef DEBUG_LOG
        NSLog(@"transfor CVPixelBuffer to texture...");
#endif
        GLuint inputTexture = [_render transforCVPixelBufferToTexture:pixelBuffer];
        [_render initOutputTextureAndCVPixelBufferWithWidth:info.width height:info.height format:info.format];
        result = [self process:inputTexture width:info.width height:info.height timeStamp:timeStamp fromPixelBuffer:YES];
    } else {
#ifdef DEBUG_LOG
        NSLog(@"transfor CVPixelBuffer to buffer...");
#endif
        int bytesPerRow = info.width * 4;
        unsigned char *baseAddress = [_render transforCVPixelBufferToBuffer:pixelBuffer outputFormat:info.format];
        if (baseAddress == nil) {
            CVPixelBufferUnlockBaseAddress(pixelBuffer, 0);
            return nil;
        }
        result = [self process:baseAddress width:info.width height:info.height bytesPerRow:bytesPerRow timeStamp:timeStamp format:info.format fromPixelBuffer:YES];
    }

    if ((_processorResult & BECVPixelBuffer)) {
        if (_pixelBufferAccelerate) {
            result.pixelBuffer = [_render getOutputPixelBuffer];
        } else {
#ifdef DEBUG_LOG
            NSLog(@"transfor buffer to CVPixelBuffer...");
#endif
            BEProcessResultBuffer *buffer = result.buffer;
            if (buffer) {
                result.pixelBuffer = [_render transforBufferToCVPixelBuffer:buffer.buffer pixelBuffer:pixelBuffer width:buffer.width height:buffer.height bytesPerRow:buffer.bytesPerRow inputFormat:buffer.format outputFormat:[self be_outputFormat]];
            }
        }
    }
    
    CVPixelBufferUnlockBaseAddress(pixelBuffer, 0);
#ifdef TIME_LOG
    [_timeRecoder stop:@"totalProcess"];
#endif
    return result;
}

- (BEProcessResult *)process:(unsigned char *)buffer width:(int)width height:(int)height bytesPerRow:(int)bytesPerRow timeStamp:(double)timeStamp format:(BEFormatType)format {
    // 设置 OpenGL 环境 , 需要与初始化 SDK 时一致
    if ([EAGLContext currentContext] != _glContext) {
        [EAGLContext setCurrentContext:_glContext];
    }

    _inputFormat = format;
    return [self process:buffer width:width height:height bytesPerRow:bytesPerRow timeStamp:timeStamp format:format fromPixelBuffer:NO];
}

- (BEProcessResult *)process:(unsigned char *)buffer width:(int)width height:(int)height bytesPerRow:(int)bytesPerRow timeStamp:(double)timeStamp format:(BEFormatType)format fromPixelBuffer:(BOOL)fromPixelBuffer {
#ifdef DEBUG_LOG
    NSLog(@"transfor buffer to texture...");
#endif
    // transfor buffer to texture
    GLuint inputTexture = [_render transforBufferToTexture:buffer width:width height:height bytesPerRow:bytesPerRow inputFormat:format];

    return [self process:inputTexture width:width height:height timeStamp:timeStamp fromPixelBuffer:fromPixelBuffer];
}

- (BEProcessResult *)process:(GLuint)texture width:(int)width height:(int)height timeStamp:(double)timeStamp {
    // 设置 OpenGL 环境 , 需要与初始化 SDK 时一致
    if ([EAGLContext currentContext] != _glContext) {
        [EAGLContext setCurrentContext:_glContext];
    }

    _inputFormat = BE_RGBA;
    return [self process:texture width:width height:height timeStamp:timeStamp fromPixelBuffer:NO];
}

- (BEProcessResult *)process:(GLuint)texture width:(int)width height:(int)height timeStamp:(double)timeStamp fromPixelBuffer:(BOOL)fromPixelBuffer {
    //设置后续美颜以及其他识别功能的基本参数
//    [_effectManager setWidth:width height:height orientation:[self getDeviceOrientation]];
    
    GLuint textureResult;
    if (_effectOn) {
#ifdef DEBUG_LOG
        NSLog(@"process texture...");
#endif
        GLuint outputTexutre = [_render getOutputTexture:width height:height];
#ifdef TIME_LOG
        [_timeRecoder record:@"algorithmProcess"];
#endif
//        [_effectManager algorithmTexture:texture timeStamp:timeStamp];
#ifdef TIME_LOG
        [_timeRecoder stop:@"algorithmProcess"];
        [_timeRecoder record:@"effectProcess"];
#endif
        textureResult = [_effectManager processTexture:texture outputTexture:outputTexutre width:width height:height rotate:BEF_AI_CLOCKWISE_ROTATE_0 timeStamp:timeStamp];
#ifdef TIME_LOG
        [_timeRecoder stop:@"effectProcess"];
#endif
    } else {
        textureResult = texture;
    }
    
    // transfor texture to buffer/CVPxielbuffer/UIImage with format be_outputFormat
    BEProcessResult *result = [self be_transforTextureToResult:textureResult width:width height:height fromPixelBuffer:fromPixelBuffer];
    
    // check and capture current frame, for taking photo
    [self be_checkAndCaptureFrame:result];

    return result;
}

/*
 * 设置滤镜强度
 */
-(void)setFilterIntensity:(float)intensity{
    [_effectManager setFilterIntensity:intensity];
}

/*
 * 设置贴纸资源
 */
- (void)setStickerPath:(NSString *)path {
    if (path != nil && ![path isEqualToString:@""]) {
        _shouldResetComposer = true;
        path = [_resourceHelper stickerPath:path];
    }
    [_effectManager setStickerPath:path];
}

- (void)setComposerMode:(int)mode {
    _composerMode = mode;
//    [_effectManager setComposerMode:mode];
}

- (void)updateComposerNodes:(NSArray<NSString *> *)nodes {
    [self be_checkAndSetComposer];
    
    NSMutableArray<NSString *> *paths = [NSMutableArray arrayWithCapacity:nodes.count];
    for (int i = 0; i < nodes.count; i++) {
        NSString *path = [_resourceHelper composerNodePath:nodes[i]];
        if (path) {
            [paths addObject:path];            
        }
    }
    
    [_effectManager updateComposerNodes:paths];
}

- (void)updateComposerNodeIntensity:(NSString *)node key:(NSString *)key intensity:(CGFloat)intensity {
    [_effectManager updateComposerNodeIntensity:[_resourceHelper composerNodePath:node] key:key intensity:intensity];
}

/*
 * 设置滤镜资源路径和系数
 */
- (void)setFilterPath:(NSString *)path {
    if (path != nil && ![path isEqualToString:@""]) {
        path = [_resourceHelper filterPath:path];
    }
    [_effectManager setFilterPath:path];
}

- (void)setEffectOn:(BOOL)on
{
    _effectOn = on;
}

- (NSArray<NSString *> *)availableFeatures {
    return [_effectManager availableFeatures];
}

- (NSString *)sdkVersion {
    return [_effectManager sdkVersion];
}

- (BOOL)setCameraPosition:(BOOL)isFront {
    [_effectManager setFrontCamera:isFront];
    return YES;
}

- (BOOL)setImageMode:(BOOL)imageMode {
//    return [_effectManager setImageMode:imageMode];
    return YES;
}

- (BOOL)processTouchEvent:(float)x y:(float)y {
//    return [_effectManager processTouchEvent:x y:y];
    return YES;
}

- (bef_ai_face_info *)getFaceInfo {
    return [_effectManager getFaceInfo];
}

- (bef_ai_hand_info *)getHandInfo {
    return [_effectManager getHandInfo];
}

- (bef_ai_skeleton_result *)getSkeletonInfo {
    return [_effectManager getSkeletonInfo];
}

#pragma mark - RenderMsgDelegate
- (BOOL)msgProc:(unsigned int)unMsgID arg1:(int)nArg1 arg2:(int)nArg2 arg3:(const char *)cArg3 {
#ifdef DEBUG_LOG
    NSLog(@"msg proc: %d, arg: %d in processor: %lu", unMsgID, nArg1, self.hash);
#endif
    return NO;
}

#pragma mark - setter
- (void)setUsePipeline:(BOOL)usePipeline {
    _usePipeline = usePipeline;
    if (_effectManager != nil) {
        [_effectManager setUsePipeline:usePipeline];
    }
    if (_render != nil) {
        _render.useCacheTexture = usePipeline;
    }
}

#pragma mark - private

- (void)be_releaseSDK {
    // 要在opengl上下文中调用
    [_effectManager destroyTask];
}

- (void)be_checkAndSetComposer {
    if ([self be_shouldResetComposer]) {
//        [_effectManager initEffectCompose:[_resourceHelper composerPath]];
        _shouldResetComposer = false;
    }
}

- (BOOL)be_shouldResetComposer {
    return _shouldResetComposer && _composerMode == 0;
}

- (BEFormatType)be_outputFormat {
    if (_outputFormat) {
        return _outputFormat;
    }
    return _inputFormat;
}

- (BEProcessResult *)be_transforTextureToResult:(GLuint)texture  width:(int)width height:(int)height fromPixelBuffer:(BOOL)fromPixelBuffer {
    BEProcessResult *result = [BEProcessResult new];
    result.texture = texture;
    result.size = CGSizeMake(width, height);

    BEProcessResultBuffer *buffer;
    if (_processorResult & (BERawData | (BECVPixelBuffer & !_pixelBufferAccelerate) | BEImage)) {
#ifdef DEBUG_LOG
        NSLog(@"transfor texture to buffer...");
#endif
        buffer = [BEProcessResultBuffer new];
        buffer.format = [self be_outputFormat];
        buffer.width = width;
        buffer.height = height;
        int bytesPerRow = 0;
        buffer.buffer = [_render transforTextureToBuffer:texture width:width height:height outputFormat:[self be_outputFormat] bytesPerRowPointer:&bytesPerRow];
        buffer.bytesPerRow = bytesPerRow;
        result.buffer = buffer;
    }
    if (!fromPixelBuffer && (_processorResult & BECVPixelBuffer)) {
#ifdef DEBUG_LOG
        NSLog(@"transfor buffer to CVPixelBuffer...");
#endif
        if (buffer) {
            result.pixelBuffer = [_render transforBufferToCVPixelBuffer:buffer.buffer width:buffer.width height:buffer.height bytesPerRow:buffer.bytesPerRow inputFormat:buffer.format outputFormat:[self be_outputFormat]];
        } else {
            NSLog(@"getCVPixelBuffer error: no buffer");
        }
    }
    if ((_processorResult & BEImage)) {
#ifdef DEBUG_LOG
        NSLog(@"transfor buffer to image...");
#endif
        if (buffer) {
            result.image = [_render transforBufferToUIImage:buffer.buffer width:buffer.width height:buffer.height bytesPerRow:buffer.bytesPerRow inputFormat:buffer.format];
        } else {
            NSLog(@"getImage error: no buffer");
        }
    }
    return result;
}

- (void)be_checkAndCaptureFrame:(BEProcessResult *)result {
    if (_captureNextFrame) {
#ifdef DEBUG_LOG
        NSLog(@"capture frame...");
#endif
        int width = result.size.width;
        int height = result.size.height;
        UIImage *image;
        if (result.image) {
            image = result.image;
        } else if (result.buffer) {
#ifdef DEBUG_LOG
            NSLog(@"transfor buffer to image...");
#endif
            BEProcessResultBuffer *buffer = result.buffer;
            image = [_render transforBufferToUIImage:buffer.buffer width:buffer.width height:buffer.height bytesPerRow:buffer.bytesPerRow inputFormat:buffer.format];
        } else {
#ifdef DEBUG_LOG
            NSLog(@"transfor texture to buffer to image...");
#endif
            int bytesPerRow;
            BEFormatType format = BE_RGBA;
            unsigned char *buffer = [_render transforTextureToBuffer:result.texture width:width height:height outputFormat:format bytesPerRowPointer:&bytesPerRow];
            image = [_render transforBufferToUIImage:buffer width:width height:height bytesPerRow:bytesPerRow inputFormat:format];
        }
        if (self.captureDelegate) {
            if (image) {
                [self.captureDelegate onImageCapture:image];
            } else {
                NSLog(@"captureNextFrame error: no image");
            }
        }
        _captureNextFrame = NO;
    }
}

/*
 * 获取设备旋转角度
 */
- (int)getDeviceOrientation {
    UIDeviceOrientation orientation = [[UIDevice currentDevice] orientation];
    switch (orientation) {
        case UIDeviceOrientationPortrait:
            return BEF_AI_CLOCKWISE_ROTATE_0;

        case UIDeviceOrientationPortraitUpsideDown:
            return BEF_AI_CLOCKWISE_ROTATE_180;

        case UIDeviceOrientationLandscapeLeft:
            return BEF_AI_CLOCKWISE_ROTATE_270;

        case UIDeviceOrientationLandscapeRight:
            return BEF_AI_CLOCKWISE_ROTATE_90;

        default:
            return BEF_AI_CLOCKWISE_ROTATE_0;
    }
}

@end

