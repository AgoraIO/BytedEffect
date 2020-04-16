//
//  BEButtonItemModel.h
//  BytedEffects
//
//  Created by QunZhang on 2019/8/19.
//  Copyright © 2019 ailab. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "BEComposerNodeModel.h"
#import "BEButtonViewCell.h"

@class BEButtonViewCell;
@interface BEButtonItemModel : NSObject

+ (instancetype)initWithID:(BEEffectNode)ID selectImg:(NSString *)selectImg unselectImg:(NSString *)unselectImg title:(NSString *)title desc:(NSString *)desc;

+ (instancetype)initWithId:(BEEffectNode)ID;


- (instancetype)initWithSelectImg:(NSString *)selectImg unselectImg:(NSString *)unselectImg title:(NSString *)title desc:(NSString *)desc;

@property (nonatomic, assign) BEEffectNode ID;
@property (nonatomic, strong) NSString *selectImg;
@property (nonatomic, strong) NSString *unselectImg;
@property (nonatomic, strong) NSString *title;
@property (nonatomic, strong) NSString *desc;

/// intensity 只对二级菜单有效，三级菜单共用其父级 intensity，本身保持为 0
@property (nonatomic, assign) CGFloat intensity;
@property (nonatomic, weak) BEButtonViewCell *cell;

@end
