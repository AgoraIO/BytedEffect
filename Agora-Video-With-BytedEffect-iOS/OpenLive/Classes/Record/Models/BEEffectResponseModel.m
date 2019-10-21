// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import "BEEffectResponseModel.h"
#import "NSString+BEAdd.h"

@implementation BEEffect

- (void)setTitle:(NSString *)title {
    _title = title;
//    _imageName = [NSString stringWithFormat:@"iconFilter%@", [title be_transformToPinyin]];
}

- (void)setImageName:(NSString *)imageName{
    _imageName = [NSString stringWithFormat:@"iconFilter%@", [imageName be_transformToPinyin]];
}

@end

@implementation BEEffectGroup

@end

@implementation  BEEffectCategoryModel

+ (instancetype)categoryWithType:(BEEffectPanelTabType)type title:(NSString *)title {
    return [[self alloc] initWithType:type title:title];
}

- (instancetype)initWithType:(BEEffectPanelTabType)type title:(NSString *)title {
    if (self = [super init]) {
        _type = type;
        _title = title;
    }
    return self;
}

@end

@implementation BEEffectSticker

@end

@implementation BEEffectStickerGroup
@end

@implementation BEEffectResponseModel

@end

@implementation BEEffectFaceMakeUp

@end

@implementation BEEffectFaceMakeUpGroup

@end
