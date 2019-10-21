//
//  BEFaceBeautyViewController.h
//  BytedEffects
//
//  Created by QunZhang on 2019/8/19.
//  Copyright © 2019 ailab. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "BECloseableProtocol.h"
#import "BEButtonItemModel.h"

@interface BEFaceBeautyViewController : UIViewController <BECloseableProtocol>

- (instancetype)initWithType:(BEEffectNode)type;

- (void)setType:(BEEffectNode)node;

- (void)setSelectNode:(BEEffectNode)node;

- (void)test;

@end
