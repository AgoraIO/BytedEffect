// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import "UICollectionViewCell+BEAdd.h"
@class BEEffectSticker;

NS_ASSUME_NONNULL_BEGIN

@interface  BEModernStickerCollectionViewCell: UICollectionViewCell

-(void)configureWithSticker:(BEEffectSticker *)sticker;
- (void)configureWithUIImage:(UIImage *)image;
@end
NS_ASSUME_NONNULL_END
