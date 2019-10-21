// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import "NSArray+BEAdd.h"

@implementation NSArray (BEAdd)

- (NSArray *)be_map:(id (^)(id))block {
    NSUInteger n = self.count;
    if (n == 0) {
        return @[];
    }
    NSMutableArray *arr = [NSMutableArray arrayWithCapacity:n];
    [self enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        [arr addObject:block(obj)];
    }];
    return [NSArray arrayWithArray:arr];
}

- (id)be_objectAtIndex:(NSUInteger)index {
    return index < self.count ? self[index] : nil;
}

@end
