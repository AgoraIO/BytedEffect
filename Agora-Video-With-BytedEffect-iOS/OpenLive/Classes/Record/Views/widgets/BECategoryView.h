//
//  BECategoryView.h
//  BytedEffects
//
//  Created by QunZhang on 2019/10/10.
//  Copyright © 2019 ailab. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BEEffectSwitchTabView.h"

@interface BECategoryView : UIView

- (void)selectItemAtIndex:(NSInteger)index animated:(BOOL)animated;
- (BEEffectSwitchTabView *)switchTabView;

@property (nonatomic, weak) id<BEEffectSwitchTabViewDelegate> tabDelegate;
@property (nonatomic, strong) NSArray *titles;
@property (nonatomic, strong) UICollectionView *contentView;

@end
