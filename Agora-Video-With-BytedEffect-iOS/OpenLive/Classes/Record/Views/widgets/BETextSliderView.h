//
//  TextSliderView.h
//  oc_demo
//
//  Created by QunZhang on 2019/8/3.
//  Copyright © 2019 wuruoye. All rights reserved.
//

#import <UIKit/UIKit.h>

static const CGFloat DEFAULT_LINE_HEIGHT = 3;
static const CGFloat DEFAULT_CIRCLE_RADIUS = 15;
static const CGFloat DEFAULT_TEXT_SIZE = 12;
static const CGFloat DEFAULT_PADDING_LEFT = 5;
static const CGFloat DEFAULT_PADDING_RIGHT = 5;
static const CGFloat DEFAULT_PADDING_BOTTOM = 10;
static const CGFloat DEFAULT_TEXT_OFFSET = 30;
static const NSInteger DEFAULT_ANIMATION_TIME = 200;

/**
 * TextSliderView 进度改变的回调
 */
@protocol TextSliderViewDelegate <NSObject>

/**
 进度回调方法

 @param progress 进度值，0～1
 */
- (void) progressDidChange:(CGFloat)progress;

@end

/**
 * 能够动态显示当前进度值的 Slider
 */
IB_DESIGNABLE
@interface BETextSliderView : UIView

/// 进度回调
@property(nonatomic, weak) IBInspectable id<TextSliderViewDelegate> delegate;

/// 激活状态的颜色
@property(nonatomic, strong) IBInspectable UIColor *activeLineColor;
/// 非激活状态的颜色
@property(nonatomic, strong) IBInspectable UIColor *inactiveLineColor;
/// 圆形颜色
@property(nonatomic, strong) IBInspectable UIColor *circleColor;
/// 文字颜色
@property(nonatomic, strong) IBInspectable UIColor *textColor;

/// 线的高度
@property(nonatomic) IBInspectable CGFloat lineHeight;
/// 圆形的默认半径
@property(nonatomic) IBInspectable CGFloat circleRadius;
/// 文字的大小
@property(nonatomic) IBInspectable CGFloat textSize;
/// 文字的偏移量，即相对于线的高度
@property(nonatomic) IBInspectable CGFloat textOffset;

/// 左边距
@property(nonatomic) IBInspectable CGFloat paddingLeft;
/// 右边距
@property(nonatomic) IBInspectable CGFloat paddingRight;
/// 底边距
@property(nonatomic) IBInspectable CGFloat paddingBottom;

/// 动画速度，值越大越慢
@property(nonatomic) IBInspectable NSInteger animationTime;

/// 进度，0～1
@property(nonatomic) CGFloat progress;

@end
