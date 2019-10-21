//
//  BEFaceBeautyView.m
//  BytedEffects
//
//  Created by QunZhang on 2019/8/19.
//  Copyright © 2019 ailab. All rights reserved.
//

#import <Masonry.h>

#import "BEFaceBeautyView.h"
#import "BEButtonViewCell.h"
#import "UICollectionViewCell+BEAdd.h"

@interface BEFaceBeautyView () <UICollectionViewDelegateFlowLayout, UICollectionViewDataSource> {
    NSInteger _selectItem;
}

@property (nonatomic, assign) BEEffectNode type;
@property (nonatomic, strong) NSArray<BEButtonItemModel *> *items;
@property (nonatomic, strong) UICollectionView *cv;
@end


@implementation BEFaceBeautyView

#pragma mark - public
- (void)setType:(BEEffectNode)type items:(NSArray<BEButtonItemModel *> *)items {
    self.type = type;
    self.items = items;
}

- (void)setItems:(NSArray<BEButtonItemModel *> *)items {
    _items = items;
    [self addSubview:self.cv];
    [self.cv mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self);
    }];
    
    [self onClose];
}

- (void)setSelectNode:(BEEffectNode)node {
    if (self.items != nil) {
        for (int i = 0; i < self.items.count; i++) {
            if (self.items[i].ID == node) {
                _selectItem = i;
                [self.cv reloadData];
                return;
            }
        }
    }
}

- (void)onClose {
    _selectItem = 0;
    [self.cv reloadData];
}

- (void)test {
    NSLog(@"test %ld", (long)_type);
}

#pragma mark - UICollectionViewDelegate
- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    if (_selectItem != indexPath.row) {
        [[self.cv cellForItemAtIndexPath:[NSIndexPath indexPathForRow:_selectItem inSection:0]] setSelected:NO];
    }
    _selectItem = indexPath.row;
    [self.delegate onItemSelect:self.type item:self.items[indexPath.row]];
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    return CGSizeMake(70, 65);
}

#pragma mark - UICollectionViewDataSource
- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.items.count;
}


- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    BEButtonViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:[BEButtonViewCell be_identifier] forIndexPath:indexPath];
    BEButtonItemModel *item = self.items[indexPath.row];
    item.cell = cell;
    
    [cell setSelectImg:[UIImage imageNamed:item.selectImg]
           unselectImg:[UIImage imageNamed:item.unselectImg]
                 title:item.title
                 desc:@""];
    [cell setSelected:(_selectItem == indexPath.row)];
    
    [cell setPointOn:item.intensity > 0];

    return cell;
}

#pragma mark - getter
- (UICollectionView *)cv {
    if (!_cv) {
        UICollectionViewFlowLayout *fl = [UICollectionViewFlowLayout new];
        fl.sectionInset = UIEdgeInsetsMake(0, 10, 0, 10);
        fl.scrollDirection = UICollectionViewScrollDirectionHorizontal;
        fl.minimumLineSpacing = 10;
        _cv = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:fl];
        [_cv registerClass:[BEButtonViewCell class] forCellWithReuseIdentifier:[BEButtonViewCell be_identifier]];
        _cv.backgroundColor = [UIColor clearColor];
        _cv.showsHorizontalScrollIndicator = NO;
        _cv.showsVerticalScrollIndicator = NO;
        _cv.allowsMultipleSelection = NO;
        _cv.dataSource = self;
        _cv.delegate = self;
    }
    return _cv;
}

@end
