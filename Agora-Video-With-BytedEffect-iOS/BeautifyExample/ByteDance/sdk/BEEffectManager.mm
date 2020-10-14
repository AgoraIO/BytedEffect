//  Copyright © 2019 ailab. All rights reserved.
//
#import "BEEffectManager.h"

#import "bef_effect_ai_api.h"
#import "bef_effect_ai_version.h"

@interface BEEffectManager ()

@property (nonatomic, copy) NSString *licensePath;
@property (nonatomic, assign) bef_effect_handle_t renderMangerHandle;
@end

@implementation BEEffectManager

#pragma mark - public

- (void)setupEffectManagerWithLicense:(NSString *)license model:(NSString *)model {
    _licensePath = [license mutableCopy];
    
    NSLog(@"sdk version is: %@", [self sdkVersion]);
    
    bef_effect_result_t result = bef_effect_ai_create(&_renderMangerHandle);
    if (result != BEF_RESULT_SUC) {
        [self be_sendNotification:@"Effect Initialization failed"];
        NSLog(@"bef_effect_ai_create error: %d", result);
        return;
    }

    result = bef_effect_ai_check_license(_renderMangerHandle, _licensePath.UTF8String);
    if (result != BEF_RESULT_SUC) {
        [self be_sendNotification:@"Effect Initialization failed"];
        NSLog(@"bef_effect_ai_check_license error: %d", result);
        return;
    }

    // 此处宽高传入视频捕获宽高。此处传入的路径是算法模型文件所在目录的父目录，设备名称传空即可。
    result = bef_effect_ai_init(_renderMangerHandle, 10, 10, model.UTF8String, "");
    if (result != BEF_RESULT_SUC) {
        [self be_sendNotification:@"Effect Initialization failed"];
        NSLog(@"bef_effect_ai_init error: %d", result);
        return;
    }
}


/*
 * 初始化高级美妆和美颜资源路径
 * 初始化步骤：
 * 1.初始化特效部分，传入算法的组合模块，composer文件
 */
- (void)initEffectCompose:(NSString *)composer {
    bef_effect_result_t result;
    
    // 传入compose 文件的路径
    result = bef_effect_ai_set_effect(_renderMangerHandle, [composer UTF8String]);
    if (result != BEF_RESULT_SUC) {
        [self be_sendNotification:@"Effect Initialization failed"];
        NSLog(@"bef_effect_set_effect error: %d", result);
    }
}

- (void)algorithmBuffer:(unsigned char *)buffer format:(bef_ai_pixel_format)format width:(int)width height:(int)height bytesPerRow:(int)bytesPerRow timeStamp:(double)timeStamp {
    bef_effect_result_t ret = bef_effect_ai_algorithm_buffer(_renderMangerHandle, buffer, format, width, height, bytesPerRow, timeStamp);
    if (ret != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_algorithm_buffer error: %d", ret);
    }
}

- (void)algorithmTexture:(GLuint)texture withBuffer:(unsigned char *)buffer format:(bef_ai_pixel_format)format width:(int)width height:(int)height bytesPerRow:(int)bytesPerRow timeStamp:(double)timeStamp {
    bef_effect_result_t ret = bef_effect_ai_algorithm_texture_with_buffer(_renderMangerHandle, texture, buffer, format, width, height, bytesPerRow, timeStamp);
    if (ret != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_algorithm_buffer error: %d", ret);
    }
}

- (void)algorithmTexture:(GLuint)texture timeStamp:(double)timeStamp {
    [self algorithmTexture:texture withBuffer:nil format:BEF_AI_PIX_FMT_BGRA8888 width:0 height:0 bytesPerRow:0 timeStamp:timeStamp];
}

- (GLuint)processTexture:(GLuint)inputTexture outputTexture:(GLuint)outputTexture timeStamp:(double)timeStamp {
    bef_effect_result_t ret = bef_effect_ai_process_texture(_renderMangerHandle, inputTexture, outputTexture, timeStamp);
    
    if (ret != BEF_RESULT_SUC && ret != 1) {
        NSLog(@"bef_effect_ai_process_texture error: %d", ret);
        return inputTexture;
    }
    
    return outputTexture;
}

/*
 * 特效sdk设置输入图片的宽，高和方向，每一次处理前调用
 */
- (void)setWidth:(int) iWidth height:(int)iHeight orientation:(int)orientation{
    bef_effect_result_t ret = bef_effect_ai_set_width_height(_renderMangerHandle, iWidth, iHeight);
    if (ret != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_set_width_height error: %d", ret);
    }
    
    ret = bef_effect_ai_set_orientation(_renderMangerHandle, (bef_ai_rotate_type)orientation);
    if (ret != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_set_orientation: error %d", ret);
    }
}

/*
 * 设置滤镜资源的路径
 */
- (void) setFilterPath:(NSString *)path {
    bef_effect_result_t status = BEF_RESULT_SUC;
    status = bef_effect_ai_set_color_filter_v2(_renderMangerHandle, [path UTF8String]);
    
    if (status != BEF_RESULT_SUC){
        NSLog(@"bef_effect_ai_set_color_filter_v2 error: %d", status);
    }
}

/*
 * 设置滤镜的强度
 */
-(void) setFilterIntensity:(float)intensity {
    bef_effect_result_t status = BEF_RESULT_SUC;
    status = bef_effect_ai_set_intensity(_renderMangerHandle, BEF_INTENSITY_TYPE_GLOBAL_FILTER_V2, intensity);
    
    if (status != BEF_RESULT_SUC){
        NSLog(@"bef_effect_ai_set_intensity error: %d", status);
    }
}

/*
 * 设置贴纸资源的路径
 */
- (void) setStickerPath:(NSString *)path {
    bef_effect_result_t status = BEF_RESULT_SUC;
    status = bef_effect_ai_set_effect(_renderMangerHandle, [path UTF8String]);
    
    if (status != BEF_RESULT_SUC){
        NSLog(@"bef_effect_ai_set_effect error: %d", status);
    }
}

/*
 * 释放SDK内部的handle
 */
- (void)releaseEffectManager {
    bef_effect_ai_destroy(_renderMangerHandle);
        
}

- (void)setComposerMode:(int)mode {
    bef_effect_result_t result = bef_effect_ai_composer_set_mode(_renderMangerHandle, mode, 0);
    if (result != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_composer_set_mode error: %d", result);
    }
}

- (void)updateComposerNodes:(NSArray<NSString *> *)nodes {
    char **nodesPath = (char **)malloc(nodes.count * sizeof(char *));
    int count = 0;
    
    NSMutableSet *set = [NSMutableSet set];
    for (NSString *node in nodes) {
        if ([set containsObject:node]) {
            continue;
        }
        [set addObject:node];
        
        if ([node canBeConvertedToEncoding:NSUTF8StringEncoding]) {
            NSUInteger strLength  = [node lengthOfBytesUsingEncoding:NSUTF8StringEncoding];
            nodesPath[count] = (char *)malloc((strLength + 1) * sizeof(char *));
            strncpy(nodesPath[count], [node cStringUsingEncoding:NSUTF8StringEncoding], strLength);
            nodesPath[count++][strLength] = '\0';
        }
    }
    
    bef_effect_result_t result = bef_effect_ai_composer_set_nodes(_renderMangerHandle, (const char **)nodesPath, count);
    if (result != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_composer_set_nodes error: %d", result);
    }
    
    for (int i = 0; i < count; i++) {
        free(nodesPath[i]);
    }
    free(nodesPath);
}

- (void)updateComposerNodeIntensity:(NSString *)node key:(NSString *)key intensity:(float)intensity {
    bef_effect_result_t result = bef_effect_ai_composer_update_node(_renderMangerHandle, (const char *)[node UTF8String], (const char *)[key UTF8String], intensity);
    if (result != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_composer_update_node error: %d", result);
    }
}

- (NSArray<NSString *> *)availableFeatures {
    //Dynamic lookup feature availability
    char features[30][BEF_EFFECT_FEATURE_LEN];
    int feature_len = 30;
    int *pf = &feature_len;
    int code = bef_effect_available_features(features, pf);
    if (code == BEF_RESULT_SUC) {
        NSMutableArray *array = [NSMutableArray arrayWithCapacity:feature_len];
        for (int i = 0; i < feature_len; i++) {
            [array addObject:[NSString stringWithUTF8String:features[i]]];
        }
        return [array copy];
    } else {
        NSLog(@"dynamic lookup feature availability failed");
        if (code == BEF_RESULT_FAIL) {
             NSLog(@"feature size is more than you expected, there is %d features", feature_len);
        }
        else if (code == BEF_RESULT_INVALID_EFFECT_HANDLE) {
            NSLog(@"bef_effect_available_features must be called after bef_effect_ai_init");
        }
        return @[];
    }
}

- (NSString *)sdkVersion {
    char version[10];
    bef_effect_ai_get_version(version, 10);
    return [NSString stringWithUTF8String:version];
}

- (BOOL)setCameraPosition:(BOOL)isFront {
    bef_effect_result_t ret = bef_effect_ai_set_camera_device_position(_renderMangerHandle, isFront ? bef_ai_camera_position_front : bef_ai_camera_position_back);
    if (ret != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_set_camera_device_position error: %d", ret);
    }
    return ret == BEF_RESULT_SUC;
}

- (BOOL)setImageMode:(BOOL)imageMode {
    bef_effect_result_t ret = bef_effect_ai_set_image_mode(_renderMangerHandle, imageMode);
    if (ret != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_set_image_mode error: %d", ret);
    }
    return ret == BEF_RESULT_SUC;
}

- (BOOL)setPipelineProcess:(BOOL)usePipeline {
    bef_effect_result_t ret = bef_effect_ai_use_pipeline_processor(_renderMangerHandle, usePipeline);
    if (ret != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_use_pipeline_processor error: %d", ret);
    }
    return ret == BEF_RESULT_SUC;
}

- (BOOL)setPipline3buffer:(BOOL)use3buffer {
    bef_effect_result_t ret = bef_effect_ai_use_3buffer(_renderMangerHandle, use3buffer);
    if (ret != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_use_3buffer error: %d", ret);
    }
    return ret == BEF_RESULT_SUC;
}

- (BOOL)cleanPipeline {
    bef_effect_result_t ret = bef_effect_ai_clean_pipeline_processor_task(_renderMangerHandle);
    if (ret != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_clean_pipeline_processor_task error: %d", ret);
    }
    return ret == BEF_RESULT_SUC;
}

- (BOOL)processTouchEvent:(float)x y:(float)y {
    bef_effect_result_t ret = bef_effect_ai_process_touch_event(_renderMangerHandle, x, y);
    if (ret != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_process_touch_event error: %d", ret);
    }
    return ret == BEF_RESULT_SUC;
}

- (BOOL)getFaceInfo:(bef_ai_face_info *)faceInfo {
    int ret =  bef_effect_ai_get_face_detect_result(_renderMangerHandle, faceInfo);
    if (ret != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_get_face_detect_result error: %d", ret);
    }
    return ret == BEF_RESULT_SUC;
}

- (BOOL)getHandInfo:(bef_ai_hand_info *)handInfo {
    int ret = bef_effect_ai_get_hand_detect_result(_renderMangerHandle, handInfo);
    if (ret != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_get_hand_detect_result error: %d", ret);
    }
    return ret == BEF_RESULT_SUC;
}

- (BOOL)getSkeletonInfo:(bef_ai_skeleton_result *)skeletonInfo {
    int ret = bef_effect_ai_get_skeleton_detect_result(_renderMangerHandle, skeletonInfo);
    if (ret != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_get_skeleton_detect_result error: %d", ret);
    }
    return ret == BEF_RESULT_SUC;
}

#pragma mark - private

- (void)be_sendNotification:(NSString *)msg {
    [[NSNotificationCenter defaultCenter] postNotificationName:@"kBESdkErrorNotification"
                                                        object:nil
                                                      userInfo:@{
                                                          @"data": msg
                                                      }];
}

@end
