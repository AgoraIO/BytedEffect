//
//  BEFaceBeautyViewController.m
//  BytedEffects
//
//  Created by QunZhang on 2019/8/19.
//  Copyright © 2019 ailab. All rights reserved.
//

#import <Masonry.h>

#import "BEFaceBeautyViewController.h"
#import "BEFaceBeautyView.h"
#import "BEStudioConstants.h"
#import "BEEffectDataManager.h"

@interface BEFaceBeautyViewController () <BEFaceBeautyViewDelegate>

@property (nonatomic, strong)BEFaceBeautyView *beautyView;

@end


@implementation BEFaceBeautyViewController

#pragma mark - public
- (instancetype)initWithType:(BEEffectNode)type {
    if (self = [super init]) {
        [self setType:type];
    }
    return self;
}

- (void)setType:(BEEffectNode)node {
    [self.beautyView removeFromSuperview];
    
    [self.beautyView setType:node items:[BEEffectDataManager buttonItemArray:node]];
    [self.view addSubview:self.beautyView];
    [self.beautyView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self.view);
    }];
}

- (void)setSelectNode:(BEEffectNode)node {
    [self.beautyView setSelectNode:node];
}

- (void)test {
    [self.beautyView test];
}

#pragma mark - BEFaceBeautyViewDelegate
- (void)onItemSelect:(BEEffectNode)type item:(BEButtonItemModel *)item {
    [[NSNotificationCenter defaultCenter]
     postNotificationName:BEEffectButtonItemSelectNotification
     object:nil
     userInfo:@{ BEEffectNotificationUserInfoKey:@[@(type), item] }];
}

#pragma mark - CloseableDelegate
- (void)onClose {
    [self.beautyView onClose];
}

#pragma mark - getter
- (BEFaceBeautyView *)beautyView {
    if (!_beautyView) {
        _beautyView = [BEFaceBeautyView new];
        _beautyView.delegate = self;
    }
    return _beautyView;
}
@end
