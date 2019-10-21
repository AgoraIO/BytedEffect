// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.

#import <UIKit/UIKit.h>
#import "BEBeautyPickerCommonDefines.h"
#import "BEVideoRecorderViewController.h"
#import "BECloseableProtocol.h"

@class BEGesturePropertyListViewController, BEFacePropertyListViewController, BEFaceVerifyListViewController;

@protocol BECameraContainerViewDelegate <NSObject>
@optional
- (void)onSwitchCameraClicked:(id)sender;
- (void)onSegmentControlChanged:(UISegmentedControl *)sender;
- (void)onRecognizeClicked:(id)sender;
- (void)onEffectButtonClicked:(id)sender;
- (void)onStickerButtonClicked:(id)sender;
- (void)onSaveButtonClicked:(UIButton*)sender;
@end

@interface BECameraContainerView : UIView <BECloseableProtocol>

@property(nonatomic, weak) id<BECameraContainerViewDelegate> delegate;

- (void)showBottomView:(UIView *)view show:(BOOL)show;

- (NSArray <NSString *>*)segmentItems;

- (void)showBottomButton;
- (void)hiddenBottomButton;
@end
