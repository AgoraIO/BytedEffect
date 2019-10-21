// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import <Foundation/Foundation.h>

@interface NSArray<__covariant ObjectType> (BEAdd)

- (NSArray *)be_map:(id(^)(ObjectType obj))block;

- (ObjectType)be_objectAtIndex:(NSUInteger)index;

@end
