//
//  BEComposerNodeModel.m
//  BytedEffects
//
//  Created by QunZhang on 2019/8/19.
//  Copyright © 2019 ailab. All rights reserved.
//

#import "BEComposerNodeModel.h"

@implementation BEComposerNodeModel

+ (instancetype)initWithPathArray:(NSArray<NSString *> *)pathArray keyArray:(NSArray<NSString *> *)keyArray {
    BEComposerNodeModel *model = [[self alloc] init];
    model.pathArray = pathArray;
    model.keyArray = keyArray;
    return model;
}

- (instancetype)initWithPath:(NSString *)path key:(NSString *)key value:(CGFloat)value {
    if (self = [super init]) {
        self.path = path;
        self.key = key;
        self.value = value;
    }
    return self;
}

- (instancetype)initWithPath:(NSString *)path key:(NSString *)key {
    if (self = [super init]) {
        self.path = path;
        self.key = key;
        self.value = 0;
    }
    return self;
}

@end
