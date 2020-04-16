//
//  BEGlobalData.m
//  BytedEffects
//
//  Created by QunZhang on 2019/11/17.
//  Copyright © 2019 ailab. All rights reserved.
//

#import "BEGlobalData.h"

static BOOL beautyEnable = YES;
static BOOL stickerEnable = YES;
static BOOL animojiEnable = NO;

@implementation BEGlobalData

+ (void)setBeautyEnable:(BOOL)enable {
    beautyEnable = enable;
}

+ (BOOL)beautyEnable {
    return beautyEnable;
}

+ (void)setStickerEnable:(BOOL)enable {
    stickerEnable = enable;
}

+ (BOOL)stickerEnable {
    return stickerEnable;
}

+ (void)setAnimojiEnable:(BOOL)enable {
    animojiEnable = enable;
}

+ (BOOL)animojiEnable {
    return animojiEnable;
}

@end
