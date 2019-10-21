//
//  BEFaceBeautyView.h
//  BytedEffects
//
//  Created by QunZhang on 2019/8/19.
//  Copyright © 2019 ailab. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "BECloseableProtocol.h"
#import "BEButtonItemModel.h"

@protocol BEFaceBeautyViewDelegate <NSObject>

@required
- (void)onItemSelect:(BEEffectNode)type item:(BEButtonItemModel *)item;

@end

@interface BEFaceBeautyView : UIView <BECloseableProtocol>

- (void)setType:(BEEffectNode)type items:(NSArray<BEButtonItemModel *> *)items;

- (void)setSelectNode:(BEEffectNode)node;

- (void)test;

@property (nonatomic, weak) id<BEFaceBeautyViewDelegate> delegate;

@end
