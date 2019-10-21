//  Copyright © 2019 ailab. All rights reserved.

#ifndef BEEffectManager_h
#define BEEffectManager_h

#import <Foundation/Foundation.h>
#import "BEButtonItemModel.h"
#import <OpenGLES/EAGL.h>
#import <OpenGLES/ES2/glext.h>

@interface BEEffectManager : NSObject

- (void)initEffectCompose;
- (void)setFilterPath:(NSString*) path;
- (void)setFilterIntensity:(float)intensity;
- (void)setStickerPath:(NSString*) path;
- (void)setupEffectMangerWithLicenseVersion:(NSString* )path;

- (void)updateComposerNodes:(NSArray<NSNumber *> *)nodes;
- (void)updateComposerNodeIntensity:(BEEffectNode)node intensity:(CGFloat)intensity;

- (void)setWidth:(int) iWidth height:(int)iHeight orientation:(int)orientation;
- (void)setEffectMangerLicense:(NSString *)license;
- (void)releaseEffectManager;
- (GLuint) processInputTexture:(double) timeStamp;
- (void) genInputAndOutputTexture:(unsigned char*) buffer width:(int)iWidth height:(int)iHeigth;
- (GLuint) genOutputTexture:(unsigned char*)buffer width:(int)width height:(int)height;
@end


#endif /* BEEffectManager_h */
