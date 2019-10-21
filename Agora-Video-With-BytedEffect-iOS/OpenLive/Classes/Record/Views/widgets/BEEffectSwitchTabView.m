// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import "BEEffectSwitchTabView.h"
#import "BEEffectTitleCollectionViewCell.h"
#import "BEEffectDataManager.h"
#import "BEEffectResponseModel.h"
#import "NSArray+BEAdd.h"
#import <Masonry/Masonry.h>

@interface BEEffectSwitchTabView ()<UICollectionViewDelegate, UICollectionViewDataSource>

@property (nonatomic, strong) UICollectionView *collectionView;
@property (nonatomic, copy) NSArray <BEEffectCategoryModel *>*categories;
@property (nonatomic, copy) NSDictionary *categoryNameDict;
@property (nonatomic, assign) NSInteger selectedIndex;
@property (nonatomic, strong) UIView *indicatorLine;

@end

@implementation BEEffectSwitchTabView

- (instancetype)initWithStickerCategories:(NSArray *)categories {
    if (self = [super init]) {
        [self addSubview:self.collectionView];
        [self.collectionView addSubview:self.indicatorLine];
        [self.collectionView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];
        self.categories = categories;
    }
    return self;
}

- (void)refreshWithStickerCategories:(NSArray *)categories {
    self.categories = categories;
    [self.collectionView reloadData];
}

- (void)selectItemAtIndex:(NSInteger)index animated:(BOOL)animated
{
    if (index >= self.categories.count) {
        return;
    }
    [self.collectionView selectItemAtIndexPath:[NSIndexPath indexPathForRow:index inSection:0] animated:animated scrollPosition:UICollectionViewScrollPositionCenteredHorizontally];
    self.selectedIndex = index;
}

#pragma mark - UICollectionViewDataSource

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.categories.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    BEEffectTitleCollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:[BEEffectTitleCollectionViewCell be_identifier] forIndexPath:indexPath];
    [cell renderWithTitle:[self.categories be_objectAtIndex:indexPath.row].title];
    return cell;
}

#pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    self.shouldIgnoreAnimation = YES;
    self.selectedIndex = indexPath.row;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    return CGSizeMake([self itemWidthForIndexPath:indexPath.row] + 40, 40);
}

#pragma mark - Utils

- (CGRect)indicatorLineFrameForIndex:(NSInteger)index {
    UICollectionViewLayoutAttributes *attributes = [self.collectionView layoutAttributesForItemAtIndexPath:[NSIndexPath indexPathForRow:index inSection:0]];
    return CGRectMake(attributes.frame.origin.x + attributes.frame.size.width / 4, self.bounds.size.height - 2, attributes.frame.size.width / 2, 2);
}

- (CGFloat)itemWidthForIndexPath:(NSInteger)index {
    
    NSString *title = self.categories[index].title;
    NSStringDrawingOptions opts = NSStringDrawingUsesLineFragmentOrigin |
    NSStringDrawingUsesFontLeading;
    NSDictionary *attributes = @{NSFontAttributeName: [UIFont boldSystemFontOfSize:15]};
    CGSize textSize = [title boundingRectWithSize:CGSizeMake(MAXFLOAT, self.bounds.size.height)
                                          options:opts
                                       attributes:attributes
                                          context:nil].size;
    return textSize.width;
}

- (void)updateIndicatorLineFrameWithProportion:(CGFloat)proportion
{
    NSInteger proportionInteger = floor(proportion) < 0 ? 0 : floor(proportion) ;
    CGFloat proportionDecimal = proportion - floor(proportion);
    
    CGFloat indicatorWidth = 0;
    CGFloat indicatorOffsetX = 0;
    if (proportion < 0) {
        UICollectionViewLayoutAttributes *firstTitleLayoutAttribute = [self.collectionView layoutAttributesForItemAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0]];
        indicatorWidth = firstTitleLayoutAttribute.bounds.size.width / 2;
        indicatorOffsetX = proportion * firstTitleLayoutAttribute.bounds.size.width + indicatorWidth / 2;
        
    } else if (proportionInteger >= self.categories.count - 1) {
        NSInteger numberOfCell = [self.collectionView numberOfItemsInSection:0];
        UICollectionViewLayoutAttributes *lastTitleLayoutAttribute = [self.collectionView layoutAttributesForItemAtIndexPath:[NSIndexPath indexPathForRow:numberOfCell - 1 inSection:0]];
        indicatorWidth = lastTitleLayoutAttribute.bounds.size.width / 2;
        indicatorOffsetX = lastTitleLayoutAttribute.frame.origin.x + indicatorWidth / 2 + proportionDecimal * lastTitleLayoutAttribute.bounds.size.width;
        
    } else {
        NSIndexPath *currentIndexPath = [NSIndexPath indexPathForRow:proportionInteger inSection:0];
        NSIndexPath *nextIndexPath = [NSIndexPath indexPathForRow:proportionInteger + 1 inSection:0];
        
        UICollectionViewLayoutAttributes *currentLayoutAttribute = [self.collectionView layoutAttributesForItemAtIndexPath:currentIndexPath];
        UICollectionViewLayoutAttributes *nextLayoutAttribute = [self.collectionView layoutAttributesForItemAtIndexPath:nextIndexPath];
        
        indicatorWidth = currentLayoutAttribute.bounds.size.width / 2 * (1 - proportionDecimal) + nextLayoutAttribute.bounds.size.width / 2 * (proportionDecimal);
        
        CGFloat currentCenter = currentLayoutAttribute.frame.origin.x + currentLayoutAttribute.frame.size.width / 2;
        CGFloat nextCenter = nextLayoutAttribute.frame.origin.x + nextLayoutAttribute.frame.size.width / 2;
        indicatorOffsetX = currentCenter + (nextCenter - currentCenter) * proportionDecimal - indicatorWidth / 2;
    }
    self.indicatorLine.frame = CGRectMake(indicatorOffsetX, 40-2, indicatorWidth, 2);
}

#pragma mark - getter && setter

- (UICollectionView *)collectionView {
    if (!_collectionView) {
        UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc] init];
        flowLayout.minimumLineSpacing = 0;
        flowLayout.minimumInteritemSpacing = 0;
        flowLayout.sectionInset = UIEdgeInsetsMake(0, 0, 0, 5);
        flowLayout.scrollDirection = UICollectionViewScrollDirectionHorizontal;
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:flowLayout];
        _collectionView.backgroundColor = [UIColor clearColor];
        [_collectionView registerClass:[BEEffectTitleCollectionViewCell class] forCellWithReuseIdentifier:[BEEffectTitleCollectionViewCell be_identifier]];
        _collectionView.showsHorizontalScrollIndicator = NO;
        _collectionView.showsVerticalScrollIndicator = NO;
        _collectionView.dataSource = self;
        _collectionView.delegate = self;
    }
    return _collectionView;
}

- (UIView *)indicatorLine {
    if (!_indicatorLine) {
        _indicatorLine = [[UIView alloc] init];
        _indicatorLine.backgroundColor = [UIColor colorWithRed:255/255.0 green:255/255.0 blue:255/255.0 alpha:1.0];
    }
    return _indicatorLine;
}

- (void)setSelectedIndex:(NSInteger)selectedIndex {
    _selectedIndex = selectedIndex;
    
    self.indicatorLine.frame = [self indicatorLineFrameForIndex:selectedIndex];
    [self.collectionView scrollToItemAtIndexPath:[NSIndexPath indexPathForRow:selectedIndex inSection:0] atScrollPosition:UICollectionViewScrollPositionCenteredHorizontally animated:YES];
    
    if ([self.delegate respondsToSelector:@selector(switchTabDidSelectedAtIndex:)]) {
        [self.delegate switchTabDidSelectedAtIndex:selectedIndex];
    }
}

- (void)setProportion:(float)proportion {
    _proportion = proportion;
    
    [self updateIndicatorLineFrameWithProportion:proportion];
}

@end
