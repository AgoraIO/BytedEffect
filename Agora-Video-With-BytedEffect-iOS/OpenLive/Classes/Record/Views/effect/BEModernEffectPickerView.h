// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import <UIKit/UIKit.h>
#import "BECloseableProtocol.h"


NS_ASSUME_NONNULL_BEGIN

@interface BEModernEffectPickerView : UIView <BECloseableProtocol>

//@property (nonatomic, strong) UISlider *intensitySlider;
- (void)setSliderProgress:(CGFloat)progress;

- (void)reloadCollectionViews;

@end

NS_ASSUME_NONNULL_END
