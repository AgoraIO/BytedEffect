// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
#import "BECameraContainerView.h"
#import <Masonry/Masonry.h>
#import "UIViewController+BEAdd.h"
#import "UIResponder+BEAdd.h"
#import "BEStudioConstants.h"
#import "BEModernEffectPickerControlFactory.h"
#import "BEMacro.h"
#import "BETextSliderView.h"
#import "BEDeviceInfoHelper.h"
#import "BEGlobalData.h"

@interface BECameraContainerView() <UIGestureRecognizerDelegate, TextSliderViewDelegate>

@property (nonatomic, strong) UIButton *settingsButton;
@property (nonatomic, strong) UIButton *switchCameraButton;

@property (nonatomic, strong) UIButton *effectButton;
@property (nonatomic, strong) UIButton *stickerButton;
@property (nonatomic, strong) UIButton *animojiButton;

@property (nonatomic, strong) UILabel *effectLabel;
@property (nonatomic, strong) UILabel *stickerLabel;
@property (nonatomic, strong) UILabel *animojiLabel;


@property (nonatomic, strong) UIImageView *watermarkView;
@property (nonatomic, strong) UIButton* saveButton;

@property (nonatomic, strong) UIControl *tapView;
@property (nonatomic, strong) UIView *currentShowView;

@property (nonatomic, strong) UISwitch *switchExclusive;

//@property (nonatomic, strong) BETextSliderView *exposureSlider;
@end

@implementation BECameraContainerView

- (void) dealloc{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
- (instancetype)initWithFrame:(CGRect)frame {
    
    self = [super initWithFrame:frame];
    
    if (self) {
        // [self addSubview:self.settingsButton];
        [self addSubview:self.switchCameraButton];
        [self addSubview:self.watermarkView];
        
#if APP_IS_DEBUG
        [self addSubview:self.saveButton];
#endif
        [self addSubview:self.effectButton];
        [self addSubview:self.stickerButton];
        if (BEGlobalData.animojiEnable) {
            [self addSubview:self.animojiButton];
        };
        
        
        [self addSubview:self.effectLabel];
        [self addSubview:self.stickerLabel];
        if (BEGlobalData.animojiEnable) {
            [self addSubview:self.animojiLabel];
        }
        //        [self addSubview:self.exposureSlider];
        [self addSubview:self.switchExclusive];
        
        NSInteger effectOffset;
        NSInteger stickerOffset;
        if (BEGlobalData.animojiEnable) {
            effectOffset = -80;
            stickerOffset = 0;
        } else {
            effectOffset = -50;
            stickerOffset = 50;
        }
        
        [self.watermarkView mas_makeConstraints:^(MASConstraintMaker *make) {
            if (BEDeviceInfoHelper.isIPhoneXSeries) {
                make.top.equalTo(self).offset(40);
            } else {
                make.top.equalTo(self).offset(30);
            }
            make.leading.equalTo(self).offset(26);
            make.size.mas_equalTo(CGSizeMake(128, 30));
        }];
        
        [self.switchCameraButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.trailing.equalTo(self).offset(-30);
            make.centerY.equalTo(self.watermarkView);
            make.size.mas_equalTo(CGSizeMake(30, 30));
        }];
        
        [self.switchExclusive mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerY.equalTo(self.watermarkView);
            make.right.equalTo(self.switchCameraButton.mas_left).with.offset(-10);
        }];
        
        [self.effectButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.mas_equalTo(self).with.offset(effectOffset);
            make.bottom.equalTo(self.mas_bottom).with.offset(-80);
            make.size.mas_equalTo(CGSizeMake(32, 32));
        }];
        
        [self.effectLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.effectButton.mas_bottom);
            make.centerX.mas_equalTo(self.effectButton);
            make.size.mas_equalTo(CGSizeMake(50, 32));
        }];
        
        [self.stickerButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.mas_equalTo(self).with.offset(stickerOffset);
            make.bottom.equalTo(self.mas_bottom).with.offset(-80);
            make.size.mas_equalTo(CGSizeMake(32, 32));
        }];
        
        [self.stickerLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.stickerButton.mas_bottom);
            make.centerX.mas_equalTo(self.stickerButton);
            make.size.mas_equalTo(CGSizeMake(50, 32));
        }];
        
        if (BEGlobalData.animojiEnable) {
            [self.animojiButton mas_makeConstraints:^(MASConstraintMaker *make) {
                make.centerX.mas_equalTo(self).with.offset(80);
                make.bottom.equalTo(self.mas_bottom).with.offset(-80);
                make.size.mas_equalTo(CGSizeMake(32, 32));
            }];
            
            [self.animojiLabel mas_makeConstraints:^(MASConstraintMaker *make) {
                make.top.equalTo(self.animojiButton.mas_bottom);
                make.centerX.mas_equalTo(self.animojiButton);
                make.size.mas_equalTo(CGSizeMake(50, 32));
            }];
        }
        
        //        [self.exposureSlider  mas_makeConstraints:^(MASConstraintMaker *make) {
        //            make.centerX.mas_equalTo(self);
        //            make.bottom.mas_equalTo(self).offset(-200);
        //            make.size.mas_equalTo(CGSizeMake(200, 60));
        //        }];
        
#if APP_IS_DEBUG
        [self.saveButton mas_makeConstraints:^(MASConstraintMaker *make) {
            make.centerX.mas_equalTo(self);
            make.bottom.mas_equalTo(self).offset(-140);
            make.size.mas_equalTo(CGSizeMake(50, 50));
        }];
        
#endif
        UIGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onTap)];
        gesture.delegate = self;
        [self addGestureRecognizer:gesture];
    }
    return self;
}

#pragma mark - event
- (void) onSwitchCameraClicked {
    if ([self.delegate respondsToSelector:@selector(onSwitchCameraClicked:)]) {
        [self.delegate onSwitchCameraClicked:self.switchCameraButton];
    }
}

- (void) onRecognizeClicked {
    if ([self.delegate respondsToSelector:@selector(onRecognizeClicked:)]) {
        [self.delegate onRecognizeClicked:self.switchCameraButton];
    }
}

- (void)onEffectButtonClicked {
    if ([self.delegate respondsToSelector:@selector(onEffectButtonClicked:)]) {
        [self.delegate onEffectButtonClicked:self.switchCameraButton];
    }
}

- (void)onStickerButtonClicked {
    if ([self.delegate respondsToSelector:@selector(onStickerButtonClicked:)]) {
        [self.delegate onStickerButtonClicked:self.switchCameraButton];
    }
}

- (void)onAnimojiButtonClicked {
    if ([self.delegate respondsToSelector:@selector(onAnimojiButtonClicked:)]) {
        [self.delegate onAnimojiButtonClicked:self.switchCameraButton];
    }
}

- (void)onSaveButtonClicked:(UIButton*) sender{
    if ([self.delegate respondsToSelector:@selector(onSaveButtonClicked:)]){
        [self.delegate onSaveButtonClicked:sender];
    }
}

- (void)onTap {
    [self be_hideView];
}

- (void)onSwitchDidChanged:(UISwitch *)sender {
    if ([self.delegate respondsToSelector:@selector(onExclusiveSwitchChanged:)]) {
        [self.delegate onExclusiveSwitchChanged:sender];
    }
}

#pragma mark - public

- (void)showBottomView:(UIView *)view show:(BOOL)show {
    if (show) {
        [self be_showView:view];
    } else {
        [self be_hideView];
    }
}

- (void)showBottomButton{
    self.saveButton.hidden = NO;
    
    self.effectButton.hidden = NO;
    self.stickerButton.hidden = NO;
    
    self.effectLabel.hidden = NO;
    self.stickerLabel.hidden = NO;
    
    self.animojiButton.hidden = NO;
    self.animojiLabel.hidden = NO;
}

- (void)hiddenBottomButton{
    self.saveButton.hidden = YES;
    
    self.effectButton.hidden = YES;
    self.stickerButton.hidden = YES;
    
    self.effectLabel.hidden = YES;
    self.stickerLabel.hidden = YES;
    
    self.animojiButton.hidden = YES;
    self.animojiLabel.hidden = YES;
}

- (void)setExclusive:(BOOL)exclusive {
    self.switchExclusive.on = exclusive;
}

#pragma mark - private
- (void)be_showView:(UIView *)view {
    [self hiddenBottomButton];
    [self addSubview:view];
    _currentShowView = view;
    [view mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.left.right.equalTo(self);
        make.top.equalTo(self.mas_bottom);
        make.height.mas_equalTo(view.frame.size.height);
    }];
    [self layoutIfNeeded];
    [UIView animateWithDuration:0.3 animations:^{
        [view mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.left.right.bottom.equalTo(self);
            make.height.mas_equalTo(view.frame.size.height);
        }];
        [self layoutIfNeeded];
    }];
}

- (void)be_hideView {
    if (_currentShowView == nil) {
        return;
    }
    [UIView animateWithDuration:0.3 animations:^{
        [_currentShowView mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.left.right.equalTo(self);
            make.top.equalTo(self.mas_bottom);
            make.height.mas_equalTo(_currentShowView.frame.size.height);
        }];
        [self layoutIfNeeded];
    } completion:^(BOOL finished) {
        [_currentShowView removeFromSuperview];
        [self showBottomButton];
        _currentShowView = nil;
    }];
}

#pragma mark - BECloseableProtocol
- (void)onClose {
    
}

#pragma mark - UIGestureRecognizerDelegate
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch {
    return touch.view == self;
}

#pragma mark - TextSliderViewDelegate
- (void)progressDidChange:(CGFloat)progress {
    [[NSNotificationCenter defaultCenter] postNotificationName:BEEffectExporsureValueChangedNotification object:nil userInfo:@{BEEffectNotificationUserInfoKey: @(progress)}];
}

#pragma mark - getter && setter

/*
 * 切换摄像头按键
 */
- (UIButton *)switchCameraButton {
    if (!_switchCameraButton) {
        UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
        UIImage *image = [UIImage imageNamed:@"iconCameraSwitch"];
        [button setImage:image forState:UIControlStateNormal];
        [button addTarget:self action:@selector(onSwitchCameraClicked) forControlEvents:UIControlEventTouchUpInside];
        _switchCameraButton = button;
    }
    return _switchCameraButton;
}

/*
 * 水印
 */
- (UIImageView*) watermarkView{
    if (!_watermarkView){
        UIImage *logoImage = [UIImage imageNamed:@"iconLogo"];
        _watermarkView = [[UIImageView alloc] initWithImage:logoImage];
    }
    return _watermarkView;
}

/*
 * 特效按键
 */
- (UIButton *)effectButton {
    if (!_effectButton) {
        UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
        UIImage *image = [UIImage imageNamed:@"iconEffect"];
        [button setImage:image forState:UIControlStateNormal];
        [button addTarget:self action:@selector(onEffectButtonClicked) forControlEvents:UIControlEventTouchUpInside];
        _effectButton = button;
    }
    return _effectButton;
}

/*
 *特效的label
 */
- (UILabel *)effectLabel {
    if (!_effectLabel){
        _effectLabel = [[UILabel alloc] init];
        _effectLabel.text = NSLocalizedString(@"effect", nil);
        _effectLabel.textColor = [UIColor whiteColor];
        _effectLabel.textAlignment = NSTextAlignmentCenter;
        _effectLabel.numberOfLines = 1;
        _effectLabel.font = [UIFont boldSystemFontOfSize:15];
    }
    return _effectLabel;
}

/*
 * 贴纸按键
 */
- (UIButton *)stickerButton {
    if (!_stickerButton) {
        UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
        UIImage *image = [UIImage imageNamed:@"iconSticker"];
        [button setImage:image forState:UIControlStateNormal];
        [button addTarget:self action:@selector(onStickerButtonClicked) forControlEvents:UIControlEventTouchUpInside];
        _stickerButton = button;
    }
    return _stickerButton;
}

/*
 *贴纸的label
 */
- (UILabel *)stickerLabel{
    if (!_stickerLabel){
        _stickerLabel = [[UILabel alloc] init];
        _stickerLabel.text = NSLocalizedString(@"sticker", nil);
        _stickerLabel.textColor = [UIColor whiteColor];
        _stickerLabel.textAlignment = NSTextAlignmentCenter;
        _stickerLabel.numberOfLines = 1;
        _stickerLabel.font = [UIFont boldSystemFontOfSize:15];
        
    }
    return _stickerLabel;
}

/*
 * Animoji 按钮
 */
- (UIButton *)animojiButton {
    if (!_animojiButton) {
        UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
        UIImage *image = [UIImage imageNamed:@"iconSticker"];
        [button setImage:image forState:UIControlStateNormal];
        [button addTarget:self action:@selector(onAnimojiButtonClicked) forControlEvents:UIControlEventTouchUpInside];
        _animojiButton = button;
    }
    return _animojiButton;
}

/*
 * Animoji label
 */
- (UILabel *)animojiLabel{
    if (!_animojiLabel){
        _animojiLabel = [[UILabel alloc] init];
        _animojiLabel.text = NSLocalizedString(@"moji", nil);
        _animojiLabel.textColor = [UIColor whiteColor];
        _animojiLabel.textAlignment = NSTextAlignmentCenter;
        _animojiLabel.numberOfLines = 1;
        _animojiLabel.font = [UIFont boldSystemFontOfSize:15];
        
    }
    return _animojiLabel;
}

- (NSArray<NSString *> *)segmentItems {
    return @[BEVideoRecorderSegmentContent640x480, BEVideoRecorderSegmentContent1280x720];
}

- (UIButton *) saveButton{
    if (!_saveButton){
        _saveButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [_saveButton addTarget:self action:@selector(onSaveButtonClicked:) forControlEvents:UIControlEventTouchUpInside];
        UIImage* image = [UIImage imageNamed:@"iconButtonSavePhoto.png"];
        UIImage* selectedImage = [UIImage imageNamed:@"iconButtonSavePhotoSelected.png"];
        [_saveButton setImage:image forState:UIControlStateNormal];
        [_saveButton setImage:selectedImage forState:UIControlStateSelected];
    }
    return _saveButton;
}

- (UIControl *)tapView {
    if (!_tapView) {
        _tapView = [[UIControl alloc] initWithFrame:self.bounds];
        _tapView.backgroundColor = [UIColor clearColor];
        [_tapView addTarget:self action:@selector(onTap) forControlEvents:UIControlEventTouchUpInside];
    }
    return _tapView;
}

//- (BETextSliderView *) exposureSlider{
//    if (!_exposureSlider){
//        _exposureSlider = [[BETextSliderView alloc] init];
//        _exposureSlider.backgroundColor = [UIColor clearColor];
//        _exposureSlider.delegate = self;
//    }
//    return _exposureSlider;
//}

- (UISwitch *)switchExclusive {
    if (!_switchExclusive) {
        _switchExclusive = [UISwitch new];
        _switchExclusive.on = YES;
        [_switchExclusive addTarget:self action:@selector(onSwitchDidChanged:) forControlEvents:UIControlEventValueChanged];
    }
    return _switchExclusive;
}

@end
