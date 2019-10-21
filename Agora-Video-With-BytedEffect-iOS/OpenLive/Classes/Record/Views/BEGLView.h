// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
#import <GLKit/GLKit.h>

@interface BEGLView : GLKView

- (void)resetWidthAndHeight;
- (void)renderWithTexture:(unsigned int)name
                     size:(CGSize)size
                  flipped:(BOOL)flipped
      applyingOrientation:(int)orientation
     savingCurrentTexture:(bool)enableSaving;
- (void)releaseContext;
@end
