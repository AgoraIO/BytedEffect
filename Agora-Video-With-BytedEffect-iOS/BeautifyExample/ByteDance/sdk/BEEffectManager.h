//  Copyright © 2019 ailab. All rights reserved.

#ifndef BEEffectManager_h
#define BEEffectManager_h

#import <Foundation/Foundation.h>
#import <OpenGLES/ES2/glext.h>
#import "bef_effect_ai_api.h"

@interface BEEffectManager : NSObject

/// set filter path
/// @param path absolute path
- (void)setFilterPath:(NSString*) path;

/// set filter intensity
/// @param intensity 0-1
- (void)setFilterIntensity:(float)intensity;

/// set sticker path
/// @param path absolute path
- (void)setStickerPath:(NSString*) path;

/// set composer mode
/// @param mode 0: exclusive between composer and sticker, 1: not exclusive between composer and sticker
- (void)setComposerMode:(int)mode;

/// update composer nodes
/// @param nodes absolute path of nodes
- (void)updateComposerNodes:(NSArray<NSString *> *)nodes;

/// update composer node intensity
/// @param node absolute path of node
/// @param key key of feature, such as smooth,white...
/// @param intensity 0-1
- (void)updateComposerNodeIntensity:(NSString *)node key:(NSString *)key intensity:(float)intensity;

/// init effect manager
/// @param license absolute path of license
/// @param model absolute path of mode dir
- (void)setupEffectManagerWithLicense:(NSString *)license model:(NSString *)model;

/// init effect composer
/// @param composer absolute path of composer
- (void)initEffectCompose:(NSString *)composer;

/// set texture/buffer with/height/orientation
/// @param iWidth int
/// @param iHeight int
/// @param orientation look up bef_ai_rotate_type
- (void)setWidth:(int) iWidth height:(int)iHeight orientation:(int)orientation;

/// algorithm Buffer
/// @param buffer buffer
/// @param format format
/// @param width width
/// @param height height
/// @param bytesPerRow bytesPerRow
/// @param timeStamp timeStamp
- (void)algorithmBuffer:(unsigned char *)buffer format:(bef_ai_pixel_format)format width:(int)width height:(int)height bytesPerRow:(int)bytesPerRow timeStamp:(double)timeStamp;

/// algorithm texture with buffer
/// @param texture texture id
/// @param buffer buffer
/// @param format format
/// @param width with
/// @param height height
/// @param bytesPerRow bytesPerRow
/// @param timeStamp timeStamp
- (void)algorithmTexture:(GLuint)texture withBuffer:(unsigned char *)buffer format:(bef_ai_pixel_format)format width:(int)width height:(int)height bytesPerRow:(int)bytesPerRow timeStamp:(double)timeStamp;

/// algirhtm texture
/// @param texture texture id
/// @param timeStamp timeStamp
- (void)algorithmTexture:(GLuint)texture timeStamp:(double)timeStamp;

/// process texture
/// @param inputTexture input
/// @param outputTexture output
/// @param timeStamp current time
- (GLuint)processTexture:(GLuint)inputTexture outputTexture:(GLuint)outputTexture timeStamp:(double)timeStamp;

/// release effect manager
- (void)releaseEffectManager;

/// get available features in sdk
- (NSArray<NSString *> *)availableFeatures;

/// get version of sdk
- (NSString *)sdkVersion;

/// set camera position
/// @param isFront YES: texture/buffer/CVPxielBuffer is from front camera
- (BOOL)setCameraPosition:(BOOL)isFront;

/// set image mode
/// @param imageMode YES for image process when reuse texture
- (BOOL)setImageMode:(BOOL)imageMode;

/// set use pipline
/// @param usePipeline use pipeline
- (BOOL)setPipelineProcess:(BOOL)usePipeline;

/// set use 3buffer
/// @param use3buffer prevent copying of buffer when algorithm buffer if set YES
- (BOOL)setPipline3buffer:(BOOL)use3buffer;

/// clean pipeline task
/// invoke when switching camera, pause or something else,
/// to avoid to use old algorithm result which is not of last frame
- (BOOL)cleanPipeline;

/// process touch event
/// @param x x of point
/// @param y y of point
- (BOOL)processTouchEvent:(float)x y:(float)y;

/// get face info
/// @param faceInfo bef_ai_face_info
- (BOOL)getFaceInfo:(bef_ai_face_info *)faceInfo;

/// get hand info
/// @param handInfo bef_ai_hand_info
- (BOOL)getHandInfo:(bef_ai_hand_info *)handInfo;

/// get skeleton info
/// @param skeletonInfo bef_ai_skeleton_result
- (BOOL)getSkeletonInfo:(bef_ai_skeleton_result *)skeletonInfo;
@end


#endif /* BEEffectManager_h */
