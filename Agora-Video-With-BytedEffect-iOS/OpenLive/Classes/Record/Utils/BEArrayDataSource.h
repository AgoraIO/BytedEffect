//
//  BEArrayDataSource.h
//  BytedEffects
//
//  Created by QunZhang on 2019/10/7.
//  Copyright © 2019 ailab. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef UICollectionViewCell *(^CellForIndex)(NSIndexPath *indexPath, NSObject *data);
typedef void(^ConfigCellWithData)(UICollectionViewCell *cell, NSObject *data);
typedef CGSize(^CellSizeForIndex)(NSIndexPath *indexPath, NSObject *data);
typedef void(^CellDidSelected)(NSIndexPath *indexpath, NSObject *data);

@interface BEArrayDataSource : NSObject <UICollectionViewDelegateFlowLayout, UICollectionViewDataSource>

+ (instancetype)initWithSingleData:(NSArray<NSObject *> *)data;
+ (instancetype)initWithData:(NSArray<NSArray<NSObject *> *> *)data;
+ (instancetype)initWithData:(NSArray<NSArray<NSObject *> *> *)data cellForIndex:(CellForIndex)cellForIndex configCellWithData:(ConfigCellWithData)configCellWithData cellSizeForIndex:(CellSizeForIndex)cellSizeForIndex cellDidSelected:(CellDidSelected)cellDidSelected;

@property (nonatomic, strong) NSArray<NSArray<NSObject *> *> *data;
@property (nonatomic, strong) CellForIndex cellForIndex;
@property (nonatomic, strong) ConfigCellWithData configCellWithData;
@property (nonatomic, strong) CellSizeForIndex cellSizeForIndex;
@property (nonatomic, strong) CellDidSelected cellDidSelected;

@end
