// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import <UIKit/UIKit.h>

@protocol BEEffectSwitchTabViewDelegate <NSObject>

- (void)switchTabDidSelectedAtIndex:(NSInteger)index;

@end
@interface BEEffectSwitchTabView : UIView

@property (nonatomic, weak) id<BEEffectSwitchTabViewDelegate> delegate;
@property (nonatomic, readonly) NSInteger selectedIndex;
@property (nonatomic, assign) float proportion;
@property (nonatomic, assign) BOOL shouldIgnoreAnimation;

- (instancetype)initWithStickerCategories:(NSArray *)categories;
- (void)refreshWithStickerCategories:(NSArray *)categories;

- (void)selectItemAtIndex:(NSInteger)index animated:(BOOL)animated;

@end
