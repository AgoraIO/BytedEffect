//
//  BEArrayDataSource.m
//  BytedEffects
//
//  Created by QunZhang on 2019/10/7.
//  Copyright © 2019 ailab. All rights reserved.
//

#import "BEArrayDataSource.h"

@implementation BEArrayDataSource

#pragma mark - public
+ (instancetype)initWithSingleData:(NSArray<NSObject *> *)data {
    return [self initWithData:@[data]];
}

+ (instancetype)initWithData:(NSArray<NSArray<NSObject *> *> *)data {
    return [self initWithData:data
                 cellForIndex:NULL
           configCellWithData:NULL
             cellSizeForIndex:NULL
              cellDidSelected:NULL];
}

+ (instancetype)initWithData:(NSArray<NSArray<NSObject *> *> *)data cellForIndex:(CellForIndex)cellForIndex configCellWithData:(ConfigCellWithData)configCellWithData cellSizeForIndex:(CellSizeForIndex)cellSizeForIndex cellDidSelected:(CellDidSelected)cellDidSelected {
    BEArrayDataSource *instance = [self new];
    if (instance) {
        instance.cellForIndex = cellForIndex;
        instance.configCellWithData = configCellWithData;
        instance.cellSizeForIndex = cellSizeForIndex;
        instance.cellDidSelected = cellDidSelected;
    }
    return instance;
}

#pragma mark - UICollectionViewDelegateFlowLayout
- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    if (self.cellDidSelected) {
        self.cellDidSelected(indexPath, [self be_dataOfIndex:indexPath]);
    }
}

#pragma mark - UICollectionViewDataSource
- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return self.data.count;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.data[section].count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    UICollectionViewCell *cell = NULL;
    if (self.cellForIndex) {
        cell = self.cellForIndex(indexPath, [self be_dataOfIndex:indexPath]);
    }
    if (self.configCellWithData) {
        self.configCellWithData(cell, [self be_dataOfIndex:indexPath]);
    }
    return cell;
}
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    if (self.cellSizeForIndex) {
        return self.cellSizeForIndex(indexPath, [self be_dataOfIndex:indexPath]);
    }
    return CGSizeZero;
}

#pragma mark - private
- (NSObject *)be_dataOfIndex:(NSIndexPath *)indexPath {
    return self.data[indexPath.section][indexPath.row];
}

@end
