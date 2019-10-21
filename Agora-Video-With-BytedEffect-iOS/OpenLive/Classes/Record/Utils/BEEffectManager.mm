//  Copyright © 2019 ailab. All rights reserved.
//
#import <Foundation/Foundation.h>
#import "BEMacro.h"
#import "BEEffectManager.h"
#import "bef_effect_ai_api.h"
#import "BEButtonItemModel.h"

@interface BEEffectManager (){
    GLuint _textureInput;
    GLuint _textureOutput;
}

@property (nonatomic, strong) NSDictionary *composerNodeDict;

@property (nonatomic, copy) NSString *licensePath;
@property (nonatomic, assign) bef_effect_handle_t renderMangerHandle;
@end

static NSString *EFFECT_RESOURCE_DIR_NAME = @"/ComposeMakeup";

@implementation BEEffectManager

/*
 * 初始化时设置effect manager的 license，license 无效时，美颜，美妆，滤镜，贴纸无效果
 */
- (void)setupEffectMangerWithLicenseVersion:(NSString* )path{
    _licensePath = [path mutableCopy];
    
    bef_effect_result_t result = bef_effect_ai_create(&_renderMangerHandle);
    if (result != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_create error: %d", result);
    }
    
    NSString *licBundleName = [[NSBundle mainBundle] pathForResource:@"LicenseBag" ofType:@"bundle"];
    NSString *licbag = [licBundleName stringByAppendingString:_licensePath];
    // 检查license
    result = bef_effect_ai_check_license(_renderMangerHandle, licbag.UTF8String);
    if (result != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_check_license error: %d", result);
    }
    
    NSString *resourceBundleName = [[NSBundle mainBundle] pathForResource:@"ModelResource" ofType:@"bundle"];
    // 此处宽高传入视频捕获宽高。此处传入的路径是算法模型文件所在目录的父目录，设备名称传空即可。
    result = bef_effect_ai_init(_renderMangerHandle, VIDEO_INPUT_WIDTH, VIDEO_INPUT_HEIGHT, resourceBundleName.UTF8String, "");
    if (result != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_init error: %d", result);
    }
    
    [self initEffectCompose];
}


/*
 * 初始化高级美妆和美颜资源路径
 * 初始化步骤：
 * 1.初始化特效部分，传入算法的组合模块，composer文件
 */
- (void)initEffectCompose {
    //特效的bundle
    NSString *composeBundle = [[NSBundle mainBundle] pathForResource:@"ComposeMakeup" ofType:@"bundle"];
    //特效的父目录
    NSString *composeDirPath = [composeBundle stringByAppendingPathComponent:EFFECT_RESOURCE_DIR_NAME];
    //特效中的组合组合特效路径
    NSString *composerPath = [composeDirPath stringByAppendingPathComponent:@"composer"];
    
    bef_effect_result_t result;
    
    // 传入compose 文件的路径
    result = bef_effect_ai_set_effect(_renderMangerHandle, [composerPath UTF8String]);
    if (result != BEF_RESULT_SUC){
        NSLog(@"bef_effect_set_effect error: %d", result);
        return ;
    }
}

/*
 * 根据输入的buffer在内存得到输入和输出的纹理，这一步必须在eagl环境中，不然会有OpenGL erroe
 */
- (void) genInputAndOutputTexture:(unsigned char*) buffer width:(int)iWidth height:(int)iHeigth
{
    GLuint textureInput;
    
    glGenTextures(1, &textureInput);
    glBindTexture(GL_TEXTURE_2D, textureInput);
    
    // 加载相机数据到纹理
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, iWidth, iHeigth, 0, GL_BGRA, GL_UNSIGNED_BYTE, buffer);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glBindTexture(GL_TEXTURE_2D, 0);
    
    GLuint textureOutput;
    
    glGenTextures(1, &textureOutput);
    glBindTexture(GL_TEXTURE_2D, textureOutput);
    
    // 为输出纹理开辟空间
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, iWidth, iHeigth, 0, GL_BGRA, GL_UNSIGNED_BYTE, NULL);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glBindTexture(GL_TEXTURE_2D, 0);
    
    _textureInput = textureInput;
    _textureOutput = textureOutput;
}


/**
 当不需要展示特效时，只生成一个输出纹理

 @param buffer buffer
 @param width width
 @param height height
 */
- (GLuint)genOutputTexture:(unsigned char *)buffer width:(int)width height:(int)height
{
    GLuint textureOut;
    
    glGenTextures(1, &textureOut);
    glBindTexture(GL_TEXTURE_2D, textureOut);
    
    // 加载相机数据到纹理
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_BGRA, GL_UNSIGNED_BYTE, buffer);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glBindTexture(GL_TEXTURE_2D, 0);
    
    return textureOut;
}

/*
 * SDK内部处理纹理，返回经过特效处理后的纹理，必须先使用genInputAndOutputTexture 函数产生输入输出纹理
 */
- (GLuint) processInputTexture:(double) timeStamp{
    bef_effect_result_t ret;
    // 用于大眼瘦脸，根据输入纹理执行算法，检测人脸位置，要在opengl上下文中调用
    bef_effect_ai_algorithm_texture(_renderMangerHandle, _textureInput, timeStamp);
    GLuint textureResult = _textureInput;
    
    // 帧处理，渲染到输出纹理，要在opengl上下文中调用
    ret = bef_effect_ai_process_texture(_renderMangerHandle, _textureInput, _textureOutput, timeStamp);
    
    if (ret == BEF_RESULT_SUC) {
        textureResult = _textureOutput;
    } else {
        NSLog(@"bef_effect_ai_process_texture error: %d", ret);
    }
    
    if (textureResult != _textureInput) {
        glDeleteTextures(1, &_textureInput);
    }
    
    if (textureResult != _textureOutput) {
        glDeleteTextures(1, &_textureOutput);
    }
    
    return textureResult;
}

/*
 * 将美颜美妆的效果传入SDK中, SDK内部需要保存当前使用特效的资源，资源文件组成二维数组
 */
//- (void)_setMakeUpComposeNodes{
//    NSString *effectBundle = [[NSBundle mainBundle] pathForResource:@"ComposeMakeup" ofType:@"bundle"];
//    NSString *effectPath = [effectBundle stringByAppendingPathComponent:EFFECT_RESOURCE_DIR_NAME];
//
//    //保存最终资源的路径数组，需要外部分配内存
//    char** nodesPath;
//
//    unsigned long effectCount = _effectWithIntensityDict.count + _effectWithOutIntensityDict.count;
//    nodesPath = (char**)malloc(effectCount * sizeof(char*));
//    bef_effect_result_t result;
//
//    int validCount = 0;
//    //设置带有强度资源
//    for (NSString *key in [_effectWithIntensityDict allKeys]){
//        NSString *path = [_effectWithIntensityDict valueForKey:key];
//
//        //如果的资源的路径长度为0
//        if (path.length == 0)
//            continue;
//
//        NSString *absolutePath = [effectPath stringByAppendingPathComponent:path];
//
//        //资源路径应该确保最终的字符串以'\0'结尾，不然会有内存错误
//        nodesPath[validCount] = (char*)malloc((absolutePath.length + 1) * sizeof(char));
//        strncpy(nodesPath[validCount], [absolutePath UTF8String], absolutePath.length);
//        nodesPath[validCount++][absolutePath.length] = '\0';
//    }
//
//    //设置没有强度的资源的路径
//    for (NSString *key in [_effectWithOutIntensityDict allKeys]){
//        NSString *path = [_effectWithOutIntensityDict valueForKey:key];
//
//        //如果的资源的路径长度为0
//        if (path.length == 0)
//            continue;
//
//        //资源路径应该确保最终的字符串以'\0'结尾，不然会有内存错误
//        NSString *absolutePath = [effectPath stringByAppendingPathComponent:path];
//        nodesPath[validCount] = (char*)malloc((absolutePath.length + 1) * sizeof(char));
//        strncpy(nodesPath[validCount], [absolutePath UTF8String], absolutePath.length);
//        nodesPath[validCount++][absolutePath.length] = '\0';
//    }
//
//    //传入sdk组合特效
//    result = bef_effect_ai_composer_set_nodes(_renderMangerHandle, (const char **)nodesPath, validCount);
//    if (result != BEF_RESULT_SUC){
//        NSLog(@"bef_effect_ai_composer_set_nodes error: %d", result);
//    }
//
//    //释放内存
//    for (int i = 0; i < validCount; i ++){
//        free(nodesPath[i]);
//    }
//    free(nodesPath);
//}

/*
 * 设置当前不支持调节强度的特效种类和路径
 */
//- (void)setEffectMakeUpType:(NSString *)type resourcePath:(NSString*) path{
//    [_effectWithOutIntensityDict setObject:path forKey:type];
//    [self _setMakeUpComposeNodes];
//}


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
- (void) setFilterPath:(NSString *)path{
    bef_effect_result_t status = BEF_RESULT_SUC;
    status = bef_effect_ai_set_color_filter_v2(_renderMangerHandle, [path UTF8String]);
    
    if (status != BEF_RESULT_SUC){
        NSLog(@"bef_effect_ai_set_color_filter_v2 error: %d", status);
    }
}

/*
 * 设置滤镜的强度
 */
-(void) setFilterIntensity:(float)intensity{
    bef_effect_result_t status = BEF_RESULT_SUC;
    status = bef_effect_ai_set_intensity(_renderMangerHandle, BEF_INTENSITY_TYPE_GLOBAL_FILTER_V2, intensity);
    
    if (status != BEF_RESULT_SUC){
        NSLog(@"bef_effect_ai_set_intensity error: %d", status);
    }
}

/*
 * 设置贴纸资源的路径
 */
- (void) setStickerPath:(NSString *)path{
    bef_effect_result_t status = BEF_RESULT_SUC;
    status = bef_effect_ai_set_effect(_renderMangerHandle, [path UTF8String]);
    
    if (status != BEF_RESULT_SUC){
        NSLog(@"bef_effect_ai_set_effect error: %d", status);
    }
}

/*
 *设置贴纸资源的license
 */
- (void)setEffectMangerLicense:(NSString *)license{
    bef_effect_result_t result = bef_effect_ai_check_license(_renderMangerHandle, license.UTF8String);
    if (result != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_ai_set_license error: %d", result);
    }
}

/*
 * 释放SDK内部的handle
 */
- (void)releaseEffectManager{
    bef_effect_ai_destroy(_renderMangerHandle);
}


/*
 *兼容2.7以前的接口
 */

// 加载美颜资源路径
//- (void)initFaceReshapedResource{
//    NSString *resourcePath = [[NSBundle mainBundle] pathForResource:@"BeautyResource" ofType:@"bundle"];
//    NSError *error = nil;
//    NSArray *faceBeautyPaths = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:resourcePath error:&error];
//    NSString *tmpPath;
//
//    for (NSString *path in faceBeautyPaths) {
//        tmpPath = [resourcePath stringByAppendingPathComponent:path];
//        break;
//    }
////    [self setEffectPath:tmpPath type:BEEffectBeautify];
//}
//
//// 加载瘦脸资源路径
//- (void)initFaceBeautyResource{
//    //
//    NSString *reshapePath = [[NSBundle mainBundle] pathForResource:@"ReshapeResource" ofType:@"bundle"];
//    NSError *error;
//    NSString *tmpPath;
//    NSArray *reshapePaths = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:reshapePath error:&error];
//
//    for (NSString *path in reshapePaths) {
//        tmpPath = [reshapePath stringByAppendingPathComponent:path];
//        break;
//    }
////    [self setEffectPath:tmpPath type:BEEffectReshape];
//}
//
//// 加载美妆资源路径
//- (void)initFaceMakeUpResource{
//    NSString *makeUpPath = [[NSBundle mainBundle] pathForResource:@"BuildinMakeup" ofType:@"bundle"];
//    NSError *error;
//    NSString *tmpPath;
//    NSArray *makeUpPaths = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:makeUpPath error:&error];
//
//    for (NSString *path in makeUpPaths) {
//        tmpPath = [makeUpPath stringByAppendingPathComponent:path];
//        break;
//    }
////    [self setEffectPath:tmpPath type:BEEffectMakeup];
//}

//设置美颜，美妆，路径特效资源的路径
//- (bef_effect_result_t)setEffectPath:(NSString *)path type:(BEEffectType)type {
//
//    if (!path) {
//        path = @"";
//    }
//    bef_effect_result_t status = BEF_RESULT_SUC;
//
//    switch (type) {
//        case BEEffectReshape:
//            status = bef_effect_ai_set_reshape_face(_renderMangerHandle, [path UTF8String]);
//            break;
//
//        case BEEffectFilter:
//            status = bef_effect_ai_set_color_filter_v2(_renderMangerHandle, [path UTF8String]);
//            break;
//
//        case BEEffectBeautify:
//            status = bef_effect_ai_set_beauty(_renderMangerHandle, [path UTF8String]);
//            break;
//
//        case BEEffectMakeup:
//            status = bef_effect_ai_set_buildin_makeup(_renderMangerHandle, [path UTF8String]);
//            break;
//        default:
//            break;
//    }
//    if (status != BEF_RESULT_SUC) {
//        NSLog(@"bef_effect_ai_set_effect error: %d", status);
//    }
//
//    return status;
//}

//设置美颜和tob2.6及以前的美妆的强度
//- (void)setIndensity:(BEIndensityParam)indensity type:(BEEffectType)type {
//    switch (type) {
//        case BEEffectReshape:
//            bef_effect_ai_set_intensity(_renderMangerHandle, BEF_INTENSITY_TYPE_FACE_SHAPE, indensity.indensity);
//            break;
//        case BEEffectReshapeTwoParam:
//            bef_effect_ai_update_reshape_face_intensity(_renderMangerHandle, indensity.eyeIndensity, indensity.cheekIndensity);
//            break;
//        case BEEffectFilter:
//            bef_effect_ai_set_intensity(_renderMangerHandle, BEF_INTENSITY_TYPE_GLOBAL_FILTER_V2, indensity.indensity);
//            break;
//        case BEEffectBeautify:
//            bef_effect_ai_set_intensity(_renderMangerHandle, BEF_INTENSITY_TYPE_BEAUTY_SMOOTH, indensity.smoothIndensity);
//            bef_effect_ai_set_intensity(_renderMangerHandle, BEF_INTENSITY_TYPE_BEAUTY_WHITEN, indensity.whiteIndensity);
//            bef_effect_ai_set_intensity(_renderMangerHandle, BEF_INTENSITY_TYPE_BEAUTY_SHARP, indensity.sharpIndensity);
//            break;
//        case BEEffectMakeup:
//            bef_effect_ai_set_intensity(_renderMangerHandle, BEF_INTENSITY_TYPE_BUILDIN_LIP, indensity.lipIndensity);
//            bef_effect_ai_set_intensity(_renderMangerHandle, BEF_INTENSITY_TYPE_BUILDIN_BLUSHER, indensity.blusherIndensity);
//            break;
//        default:
//            break;
//    }
//}

- (void)updateComposerNodes:(NSArray<NSNumber *> *)nodes {
    NSString *pathPrefix = [[[NSBundle mainBundle] pathForResource:@"ComposeMakeup" ofType:@"bundle"] stringByAppendingString:EFFECT_RESOURCE_DIR_NAME];
    
    NSMutableSet<NSString *> *set = [NSMutableSet set];
    unsigned int count = 0;
    char **nodesPath = (char **)malloc(nodes.count * sizeof(char *));
    for (NSNumber *number in nodes) {
        NSNumber *superNum = @([number longValue] & ~SUB_MASK);
        BEComposerNodeModel *model = [self.composerNodeDict objectForKey:superNum];
        if (model == nil) {
            NSLog(@"model not found, node: %ld", [number longValue]);
            continue;
        }
        NSString *path;
        if (([number longValue] & (SUB_MASK)) != 0) {
            // 有三级菜单
            path = [pathPrefix stringByAppendingString:model.pathArray[(([number longValue] & SUB_MASK) - 1)]];
        } else {
            // 无三级菜单
            path = [pathPrefix stringByAppendingString:model.path];
        }
        if ([set containsObject:path]) {
            continue;
        }
        [set addObject:path];
        
        nodesPath[count] = (char *)malloc((path.length + 1) * sizeof(char));
        strncpy(nodesPath[count], [path UTF8String], path.length);
        nodesPath[count++][path.length] = '\0';
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

- (void)updateComposerNodeIntensity:(BEEffectNode)node intensity:(CGFloat)intensity {
    BEComposerNodeModel *model = [self.composerNodeDict objectForKey:@(node)];
    if (model == nil) {
        NSLog(@"model not found, node: %ld", (long)node);
        return;
    }
    NSString *pathPrefix = [[[NSBundle mainBundle] pathForResource:@"ComposeMakeup" ofType:@"bundle"] stringByAppendingString:EFFECT_RESOURCE_DIR_NAME];
    NSString *path = [pathPrefix stringByAppendingString:model.path];
    
    bef_effect_result_t result = bef_effect_ai_composer_update_node(_renderMangerHandle, [path UTF8String], [model.key UTF8String], intensity);
    
    if (result != BEF_RESULT_SUC) {
        NSLog(@"bef_effect_composer_update_node error: %d", result);
    }
}

#pragma mark - getter
- (NSDictionary *)composerNodeDict {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _composerNodeDict = @{
                              // 美颜
                              @(BETypeBeautyFaceSharpe):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/beauty"
                                   key:@"epm/frag/sharpen"],
                              
                              @(BETypeBeautyFaceSmooth):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/beauty"
                                   key:@"epm/frag/blurAlpha"],
                              
                              @(BETypeBeautyFaceWhiten):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/beauty"
                                   key:@"epm/frag/whiten"],
                              
                              // 美形
                              @(BETypeBeautyReshapeFaceOverall):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/reshape"
                                   key:@"Internal_Deform_Overall"],
                              
                              @(BETypeBeautyReshapeFaceCut):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/reshape"
                                   key:@"Internal_Deform_CutFace"],
                              
                              @(BETypeBeautyReshapeFaceSmall):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/reshape"
                                   key:@"Internal_Deform_Face"],
                              
                              @(BETypeBeautyReshapeEye):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/reshape"
                                   key:@"Internal_Deform_Eye"],
                              
                              @(BETypeBeautyReshapeEyeRotate):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/reshape"
                                   key:@"Internal_Deform_RotateEye"],
                              
                              @(BETypeBeautyReshapeCheek):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/reshape"
                                   key:@"Internal_Deform_Zoom_Cheekbone"],
                              
                              @(BETypeBeautyReshapeJaw):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/reshape"
                                   key:@"Internal_Deform_Zoom_Jawbone"],
                              
                              @(BETypeBeautyReshapeNoseLean):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/reshape"
                                   key:@"Internal_Deform_Nose"],
                              
                              @(BETypeBeautyReshapeNoseLong):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/reshape"
                                   key:@"Internal_Deform_MovNose"],
                              
                              @(BETypeBeautyReshapeChin):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/reshape"
                                   key:@"Internal_Deform_Chin"],
                              
                              @(BETypeBeautyReshapeForehead):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/reshape"
                                   key:@"Internal_Deform_Forehead"],
                              
                              @(BETypeBeautyReshapeMouthZoom):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/reshape"
                                   key:@"Internal_Deform_ZoomMouth"],
                              
                              @(BETypeBeautyReshapeMouthSmile):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/reshape"
                                   key:@"Internal_Deform_MouthCorner"],
                              
                              // 美体
                              @(BETypeBeautyBodyThin):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/body/thin"
                                   key:@""],
                              
                              @(BETypeBeautyBodyLegLong):
                                  [[BEComposerNodeModel alloc]
                                   initWithPath:@"/body/longleg"
                                   key:@""],
                              
                              // 美妆
                              @(BETypeMakeupLip):
                                  [BEComposerNodeModel
                                   initWithPathArray:@[@"/lip/huluobohong", @"/lip/huoliju", @"/lip/yingsuhong"]
                                   keyArray:@[@"", @"", @""]],
                              
                              @(BETypeMakeupBlusher):
                                  [BEComposerNodeModel
                                   initWithPathArray:@[@"/blush/shaishanghong", @"/blush/weixunfen", @"/blush/yuanqiju"]
                                   keyArray:@[@"", @"", @""]],
                              
                              @(BETypeMakeupEyelash):
                                  [BEComposerNodeModel
                                   initWithPathArray:@[@"/eyelash/nongmi", @"/eyelash/shanxing", @"/eyelash/wumei"]
                                   keyArray:@[@"", @"", @""]],
                              
                              @(BETypeMakeupPupil):
                                  [BEComposerNodeModel
                                   initWithPathArray:@[@"/pupil/babizi", @"/pupil/hunxuelan", @"/pupil/hunxuelv"]
                                   keyArray:@[@"", @"", @""]],
                              
                              @(BETypeMakeupHair):
                                  [BEComposerNodeModel
                                   initWithPathArray:@[@"/hair/anlan", @"/hair/molv", @"/hair/shenzong"]
                                   keyArray:@[@"", @"", @""]],
                              
                              @(BETypeMakeupEyeshadow):
                                  [BEComposerNodeModel
                                   initWithPathArray:@[@"/eyeshadow/shaonvfen", @"/eyeshadow/yanxunzong", @"/eyeshadow/ziranlan"]
                                   keyArray:@[@"", @"", @""]],
                              
                              @(BETypeMakeupEyebrow):
                                  [BEComposerNodeModel
                                   initWithPathArray:@[@"/eyebrow/chunhei", @"/eyebrow/danhui", @"/eyebrow/ziranhei"]
                                   keyArray:@[@"", @"", @""]],
                              
                              };
    });
    return _composerNodeDict;
}
@end
