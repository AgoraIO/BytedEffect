// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import "UICollectionViewCell+BEAdd.h"
#import "BEEffectResponseModel.h"
#import "BECloseableProtocol.h"
#import "BEButtonItemModel.h"

@class BEEffectContentCollectionViewCell;

NS_ASSUME_NONNULL_BEGIN

@interface BEEffectContentCollectionViewCellFactory : NSObject

+ (Class)contentCollectionViewCellWithPanelTabType:(BEEffectPanelTabType)type;

@end

@interface BEEffectContentCollectionViewCell : UICollectionViewCell
@property (nonatomic, assign) BOOL shouldClearStatus;

-(void)setCellUnSelected;
@end

@interface BEEffectFaceBeautyCollectionViewCell : BEEffectContentCollectionViewCell
-(void)setCellUnSelected;
@end

@interface BEEffecFiltersCollectionViewCell : BEEffectContentCollectionViewCell
-(void)setCellUnSelected;
- (void)setSelectItem:(NSString *)filterPath;
@end

@interface BEEffectMakeupCollectionViewCell : BEEffectContentCollectionViewCell
-(void)setCellUnSelected;
@end

@interface BEEffectStickersCollectionViewCell : BEEffectContentCollectionViewCell
-(void)setCellUnSelected;
@end

@interface BEEffectFaceBeautyViewCell : BEEffectContentCollectionViewCell <BECloseableProtocol>

@property (nonatomic, assign) BEEffectNode type;

- (void) setCellUnSelected;
- (void)setSelectNode:(BEEffectNode)node;
- (void)test;

@end

NS_ASSUME_NONNULL_END
