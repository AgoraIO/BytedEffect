//
//  BEGlobalData.h
//  BytedEffects
//
//  Created by QunZhang on 2019/11/17.
//  Copyright © 2019 ailab. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface BEGlobalData : NSObject

+ (void)setBeautyEnable:(BOOL)enable;
+ (BOOL)beautyEnable;
+ (void)setStickerEnable:(BOOL)enable;
+ (BOOL)stickerEnable;
+ (BOOL)animojiEnable;
+ (void)setAnimojiEnable:(BOOL)enable;

@end
