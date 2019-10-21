// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import <Foundation/Foundation.h>

@class BEEffect, BEEffectGroup,BEEffectStickerGroup, BEEffectFaceMakeUpGroup, BEEffectFaceMakeUp;

typedef NS_ENUM(NSUInteger, BEEffectPanelTabType) {
    BEEffectPanelTabBeautyFace,
    BEEffectPanelTabBeautyReshape,
    BEEffectPanelTabBeautyBody,
    BEEffectPanelTabFilter,
    BEEffectPanelTabMakeup
};

NS_ASSUME_NONNULL_BEGIN

@interface BEEffectResponseModel : NSObject

@property (nonatomic, copy) NSArray <BEEffectGroup *> *filterGroups;
@property (nonatomic, copy) NSArray <BEEffectStickerGroup *> *stickerGroup;
@property (nonatomic, copy) NSArray <BEEffectFaceMakeUpGroup *> *makeUpGroup;
@end

@interface BEEffectGroup : NSObject

@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSArray <BEEffect *>*filters;

@end

@interface BEEffect : NSObject

@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSString *imageName;
@property (nonatomic, copy) NSString *filePath;

@end

@interface BEEffectCategoryModel : NSObject

@property (nonatomic, readonly) BEEffectPanelTabType type;
@property (nonatomic, readonly, copy) NSString *title;

+ (instancetype)categoryWithType:(BEEffectPanelTabType)type title:(NSString *)title;

@end

@interface BEEffectSticker : NSObject

@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSString *imageName;
@property (nonatomic, copy) NSString *filePath;
@property (nonatomic, copy) NSString *toastString;

@end

@interface BEEffectStickerGroup : NSObject

@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSArray <BEEffectSticker*> *stickers;

@end

@interface BEEffectFaceMakeUp : NSObject

@property (nonatomic, copy) NSString *title;
@property (nonatomic, copy) NSString *filePath;
@property (nonatomic, copy) NSString *selectedImageName;
@property (nonatomic, copy) NSString *normalImageName;
@property (nonatomic, assign) float  intensity;

@end

@interface BEEffectFaceMakeUpGroup : NSObject

@property (nonatomic, copy) NSString* title;
@property (nonatomic, copy) NSArray<BEEffectFaceMakeUp*> *faceMakeUps;
@property (nonatomic, strong) NSIndexPath *selectedIndex;

@end

NS_ASSUME_NONNULL_END
