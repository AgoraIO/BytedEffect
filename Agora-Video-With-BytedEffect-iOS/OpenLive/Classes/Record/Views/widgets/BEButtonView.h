//
//  BEButtonView.h
//  BytedEffects
//
//  Created by QunZhang on 2019/8/13.
//  Copyright © 2019 ailab. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "BEButtonItemModel.h"

@class BEButtonView;
@protocol BEButtonViewDelegate <NSObject>

- (void)onButtonClicked:(BEButtonView *)view;

@end

@interface BEButtonView : UIView

@property (nonatomic, weak) id<BEButtonViewDelegate> delegate;
@property (nonatomic, assign) BOOL selected;

- (void)setSelectImg:(UIImage *)selectImg unselectImg:(UIImage *)unselectImg title:(NSString *)title desc:(NSString *)desc;

- (void)setSelectImg:(UIImage *)selectImg unselectImg:(UIImage *)unselectImg title:(NSString *)title;

- (void)setSelectImg:(UIImage *)selectImg unselectImg:(UIImage *)unselectImg title:(NSString *)title expand:(BOOL)expand;

- (void)setSelectImg:(UIImage *)selectImg unselectImg:(UIImage *)unselectImg;

- (void)setPointOn:(BOOL)isOn;
- (void)setUsedStatus:(BOOL) used;

@end
