//
//  BEComposerNodeModel.h
//  BytedEffects
//
//  Created by QunZhang on 2019/8/19.
//  Copyright © 2019 ailab. All rights reserved.
//

#import <UIKit/UIKit.h>

static const NSInteger OFFSET = 24;
static const NSInteger MASK = 0xFFFFFF;
static const NSInteger SUB_OFFSET = 16;
static const NSInteger SUB_MASK = 0xFFFF;

typedef NS_ENUM(NSInteger, BEEffectNode) {
    
    // 一级菜单
    BETypeClose                    = -1,
    BETypeBeautyFace               = 1 << OFFSET,
    BETypeBeautyReshape            = 2 << OFFSET,
    BETypeBeautyBody               = 3 << OFFSET,
    BETypeMakeup                   = 4 << OFFSET,
    BETypeFilter                   = 5 << OFFSET,
    BETypeSticker                  = 6 << OFFSET,
    BETypeAnimoji                  = 7 << OFFSET,
    BETypeArscan                   = 8 << OFFSET,
    
    // 二级菜单
    // 美颜
    BETypeBeautyFaceSmooth         = BETypeBeautyFace      + (1 << SUB_OFFSET),
    BETypeBeautyFaceWhiten         = BETypeBeautyFace      + (2 << SUB_OFFSET),
    BETypeBeautyFaceSharpe         = BETypeBeautyFace      + (3 << SUB_OFFSET),
    
    // 美形
    BETypeBeautyReshapeFaceOverall      = BETypeBeautyReshape    + (1 << SUB_OFFSET),
    BETypeBeautyReshapeEye              = BETypeBeautyReshape    + (2 << SUB_OFFSET),
    BETypeBeautyReshapeFaceSmall        = BETypeBeautyReshape    + (3 << SUB_OFFSET),
    BETypeBeautyReshapeFaceCut          = BETypeBeautyReshape    + (4 << SUB_OFFSET),
    BETypeBeautyReshapeCheek            = BETypeBeautyReshape    + (5 << SUB_OFFSET),
    BETypeBeautyReshapeJaw              = BETypeBeautyReshape    + (6 << SUB_OFFSET),
    BETypeBeautyReshapeNoseLean         = BETypeBeautyReshape    + (7 << SUB_OFFSET),
    BETypeBeautyReshapeNoseLong         = BETypeBeautyReshape    + (8 << SUB_OFFSET),
    BETypeBeautyReshapeChin             = BETypeBeautyReshape    + (9 << SUB_OFFSET),
    BETypeBeautyReshapeForehead         = BETypeBeautyReshape    + (10 << SUB_OFFSET),
    BETypeBeautyReshapeEyeRotate        = BETypeBeautyReshape    + (11 << SUB_OFFSET),
    BETypeBeautyReshapeMouthZoom        = BETypeBeautyReshape    + (12 << SUB_OFFSET),
    BETypeBeautyReshapeMouthSmile       = BETypeBeautyReshape    + (13 << SUB_OFFSET),
    BETypeBeautyReshapeEyeSpacing       = BETypeBeautyReshape    + (14 << SUB_OFFSET),
    BETypeBeautyReshapeEyeMove          = BETypeBeautyReshape    + (15 << SUB_OFFSET),
    BETypeBeautyReshapeMouthMove        = BETypeBeautyReshape    + (16 << SUB_OFFSET),
    BETypeBeautyReshapeBrightenEye      = BETypeBeautyReshape    + (17 << SUB_OFFSET),
    BETypeBeautyReshapeRemovePouch      = BETypeBeautyReshape    + (18 << SUB_OFFSET),
    BETypeBeautyReshapeRemoveSmileFolds = BETypeBeautyReshape    + (19 << SUB_OFFSET),
    BETypeBeautyReshapeWhitenTeeth      = BETypeBeautyReshape    + (20 << SUB_OFFSET),
    
    // 美体
    BETypeBeautyBodyThin           = BETypeBeautyBody      + (1 << SUB_OFFSET),
    BETypeBeautyBodyLegLong        = BETypeBeautyBody      + (2 << SUB_OFFSET),
    
    // 美妆
    BETypeMakeupLip                = BETypeMakeup          + (1 << SUB_OFFSET),
    BETypeMakeupBlusher            = BETypeMakeup          + (2 << SUB_OFFSET),
    BETypeMakeupEyelash            = BETypeMakeup          + (3 << SUB_OFFSET),
    BETypeMakeupPupil              = BETypeMakeup          + (4 << SUB_OFFSET),
    BETypeMakeupHair               = BETypeMakeup          + (5 << SUB_OFFSET),
    BETypeMakeupEyeshadow          = BETypeMakeup          + (6 << SUB_OFFSET),
    BETypeMakeupEyebrow            = BETypeMakeup          + (7 << SUB_OFFSET),
    BETypeMakeupFacial             = BETypeMakeup          + (8 << SUB_OFFSET),
    
    // 美妆三级菜单
    BETypeMakeupLip_1              = BETypeMakeupLip       + 1,
    BETypeMakeupLip_2              = BETypeMakeupLip       + 2,
    BETypeMakeupLip_3              = BETypeMakeupLip       + 3,
    BETypeMakeupLip_4              = BETypeMakeupLip       + 4,
    BETypeMakeupLip_5              = BETypeMakeupLip       + 5,
    BETypeMakeupLip_6              = BETypeMakeupLip       + 6,
    BETypeMakeupLip_7              = BETypeMakeupLip       + 7,
    BETypeMakeupLip_8              = BETypeMakeupLip       + 8,
    BETypeMakeupLip_9              = BETypeMakeupLip       + 9,
    BETypeMakeupLip_10             = BETypeMakeupLip       + 10,
    BETypeMakeupBlusher_1          = BETypeMakeupBlusher   + 1,
    BETypeMakeupBlusher_2          = BETypeMakeupBlusher   + 2,
    BETypeMakeupBlusher_3          = BETypeMakeupBlusher   + 3,
    BETypeMakeupBlusher_4          = BETypeMakeupBlusher   + 4,
    BETypeMakeupBlusher_5          = BETypeMakeupBlusher   + 5,
    BETypeMakeupBlusher_6          = BETypeMakeupBlusher   + 6,
    BETypeMakeupBlusher_7          = BETypeMakeupBlusher   + 7,
    BETypeMakeupEyelash_1          = BETypeMakeupEyelash   + 1,
    BETypeMakeupEyelash_2          = BETypeMakeupEyelash   + 2,
    BETypeMakeupEyelash_3          = BETypeMakeupEyelash   + 3,
    BETypeMakeupEyelash_4          = BETypeMakeupEyelash   + 4,
    BETypeMakeupPupil_1            = BETypeMakeupPupil     + 1,
    BETypeMakeupPupil_2            = BETypeMakeupPupil     + 2,
    BETypeMakeupPupil_3            = BETypeMakeupPupil     + 3,
    BETypeMakeupPupil_4            = BETypeMakeupPupil     + 4,
    BETypeMakeupPupil_5            = BETypeMakeupPupil     + 5,
    BETypeMakeupPupil_6            = BETypeMakeupPupil     + 6,
    BETypeMakeupHair_1             = BETypeMakeupHair      + 1,
    BETypeMakeupHair_2             = BETypeMakeupHair      + 2,
    BETypeMakeupHair_3             = BETypeMakeupHair      + 3,
    BETypeMakeupEyeshadow_1        = BETypeMakeupEyeshadow + 1,
    BETypeMakeupEyeshadow_2        = BETypeMakeupEyeshadow + 2,
    BETypeMakeupEyeshadow_3        = BETypeMakeupEyeshadow + 3,
    BETypeMakeupEyeshadow_4        = BETypeMakeupEyeshadow + 4,
    BETypeMakeupEyeshadow_5        = BETypeMakeupEyeshadow + 5,
    BETypeMakeupEyeshadow_6        = BETypeMakeupEyeshadow + 6,
    BETypeMakeupEyeshadow_7        = BETypeMakeupEyeshadow + 7,
    BETypeMakeupEyeshadow_8        = BETypeMakeupEyeshadow + 8,
    BETypeMakeupEyebrow_1          = BETypeMakeupEyebrow   + 1,
    BETypeMakeupEyebrow_2          = BETypeMakeupEyebrow   + 2,
    BETypeMakeupEyebrow_3          = BETypeMakeupEyebrow   + 3,
    BETypeMakeupEyebrow_4          = BETypeMakeupEyebrow   + 4,
    BETypeMakeupFacial_1           = BETypeMakeupFacial   + 1,
    BETypeMakeupFacial_2           = BETypeMakeupFacial   + 2,
    BETypeMakeupFacial_3           = BETypeMakeupFacial   + 3,
    BETypeMakeupFacial_4           = BETypeMakeupFacial   + 4,
};

@interface BEComposerNodeModel : NSObject

+ (instancetype)initWithPathArray:(NSArray<NSString *> *)pathArray keyArray:(NSArray<NSString *> *)keyArray;

- (instancetype)initWithPath:(NSString *)path key:(NSString *)key value:(CGFloat)value;

- (instancetype)initWithPath:(NSString *)path key:(NSString *)key;

@property (nonatomic, strong) NSString *path;
@property (nonatomic, strong) NSString *key;
@property (nonatomic, assign) CGFloat value;

@property (nonatomic, strong) NSArray<NSString *> *pathArray;
@property (nonatomic, strong) NSArray<NSString *> *keyArray;

@end
