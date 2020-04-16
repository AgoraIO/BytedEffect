// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import <UIKit/UIKit.h>
#import "BECloseableProtocol.h"
#import "BEVideoRecorderViewController.h"


NS_ASSUME_NONNULL_BEGIN


@interface BEModernEffectPickerView : UIView <BECloseableProtocol>

@property (nonatomic, weak) id<BETapDelegate> onTapDelegate;
@property (nonatomic, weak) id<BEDefaultTapDelegate> onDefaultTapDelegate;
//@property (nonatomic, strong) NSString *filterPath;

//@property (nonatomic, strong) UISlider *intensitySlider;
//- (void)setSliderProgress:(CGFloat)progress;

- (void)reloadCollectionViews;
- (void)setDefaultEffect;
- (void)recoverEffect;

@end

NS_ASSUME_NONNULL_END
