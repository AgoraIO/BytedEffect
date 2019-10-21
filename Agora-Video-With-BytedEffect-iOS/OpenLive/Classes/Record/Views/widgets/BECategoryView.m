//
//  BECategoryView.m
//  BytedEffects
//
//  Created by QunZhang on 2019/10/10.
//  Copyright © 2019 ailab. All rights reserved.
//

#import <Masonry.h>

#import "BECategoryView.h"

const int TAB_HEIGHT = 40;

@interface BECategoryView () <UICollectionViewDelegateFlowLayout>

@property (nonatomic, strong) BEEffectSwitchTabView *tabView;

@end

@implementation BECategoryView

#pragma mark - public
- (void)selectItemAtIndex:(NSInteger)index animated:(BOOL)animated {
    [self.tabView selectItemAtIndex:index animated:animated];
}

- (BEEffectSwitchTabView *)switchTabView {
    return self.tabView;
}

#pragma mark - UICollectionViewDelegate
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    return self.contentView.bounds.size;
}

#pragma mark - UIScrollViewDelegate
- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
    NSInteger wouldSelectIndex = scrollView.contentOffset.x / scrollView.frame.size.width;
    if (self.tabView.selectedIndex != wouldSelectIndex) {
        [self.tabView selectItemAtIndex:wouldSelectIndex animated:YES];
    }
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate {
    if (!decelerate) {
        NSInteger wouldSelectIndex = scrollView.contentOffset.x / scrollView.frame.size.width;
        if (self.tabView.selectedIndex != wouldSelectIndex) {
            [self.tabView selectItemAtIndex:wouldSelectIndex animated:YES];
        }
    }
}

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView {
    self.tabView.shouldIgnoreAnimation = NO;
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    if (self.tabView.shouldIgnoreAnimation) {
        return;
    }
    CGFloat offsetX = self.contentView.contentOffset.x;
    CGFloat proportion = offsetX / self.contentView.frame.size.width;
    self.tabView.proportion = proportion;
}

#pragma mark - getter
- (BEEffectSwitchTabView *)tabView {
    if (!_tabView) {
        _tabView = [[BEEffectSwitchTabView alloc] initWithStickerCategories:@[]];
        _tabView.delegate = self.tabDelegate;
        [self addSubview:_tabView];
        [_tabView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.right.top.equalTo(self);
            make.height.mas_equalTo(TAB_HEIGHT);
        }];
        [self.contentView invalidateIntrinsicContentSize];
        [self.contentView layoutIfNeeded];
    }
    return _tabView;
}

#pragma mark - setter
- (void)setTitles:(NSArray *)titles {
    _titles = titles;
    [self.tabView refreshWithStickerCategories:titles];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(.2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [self.tabView selectItemAtIndex:0 animated:NO];
    });
}

- (void)setContentView:(UICollectionView *)contentView {
    if (_contentView == contentView) {
        return;
    }
    if (_contentView != nil) {
        [_contentView removeFromSuperview];
    }
    _contentView = contentView;
    contentView.delegate = self;
    [self addSubview:contentView];
    [contentView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.left.right.bottom.equalTo(self);
        make.top.equalTo(self).with.offset(TAB_HEIGHT);
    }];
}

@end
