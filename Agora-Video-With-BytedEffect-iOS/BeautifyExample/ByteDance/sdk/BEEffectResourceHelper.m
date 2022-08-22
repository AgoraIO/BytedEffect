//
//  BEEffectResourceHelper.m
//  Effect
//
//  Created by qun on 2021/5/18.
//

#import "BEEffectResourceHelper.h"
#import "BELicenseHelper.h"

static NSString *LICENSE_PATH = @"LicenseBag";
static NSString *COMPOSER_PATH = @"ComposeMakeup";
static NSString *FILTER_PATH = @"FilterResource";
static NSString *STICKER_PATH = @"StickerResource";
static NSString *MODEL_PATH = @"ModelResource";
static NSString *VIDEOSR_PATH = @"videovrsr";

static NSString *BUNDLE = @"bundle";

@interface BEEffectResourceHelper () {
    NSString            *_licensePrefix;
    NSString            *_composerPrefix;
    NSString            *_filterPrefix;
    NSString            *_stickerPrefix;
}

@end

@implementation BEEffectResourceHelper

- (NSString *)composerNodePath:(NSString *)nodeName {
    if (!_composerPrefix) {
        _composerPrefix = [[[NSBundle mainBundle] pathForResource:COMPOSER_PATH ofType:BUNDLE] stringByAppendingString:@"/ComposeMakeup/"];
    }
    if ([nodeName containsString:_composerPrefix]) {
        return nodeName;
    }
    return [_composerPrefix stringByAppendingString:nodeName];
}

- (NSString *)filterPath:(NSString *)filterName {
    if (!_filterPrefix) {
        _filterPrefix = [[[NSBundle mainBundle] pathForResource:FILTER_PATH ofType:BUNDLE] stringByAppendingFormat:@"/Filter/"];
    }
    return [_filterPrefix stringByAppendingString:filterName];
}

- (NSString *)stickerPath:(NSString *)stickerName {
    if (!_stickerPrefix) {
        _stickerPrefix = [[[NSBundle mainBundle] pathForResource:STICKER_PATH ofType:BUNDLE] stringByAppendingString:@"/stickers/"];
    }
    return [_stickerPrefix stringByAppendingString:stickerName];
}

- (NSString *)composerDirPath {
    if (!_composerPrefix) {
        _composerPrefix = [[[NSBundle mainBundle] pathForResource:COMPOSER_PATH ofType:BUNDLE] stringByAppendingString:@"/ComposeMakeup/"];
    }
    return [_composerPrefix stringByAppendingString:@"/composer"];
}

- (const char *)modelDirPath {
    return [[[NSBundle mainBundle] pathForResource:MODEL_PATH ofType:BUNDLE] UTF8String];
}

- (NSString *)videoSRModelPath
{
    return [[NSBundle mainBundle] pathForResource:VIDEOSR_PATH ofType:BUNDLE];
}

@end
