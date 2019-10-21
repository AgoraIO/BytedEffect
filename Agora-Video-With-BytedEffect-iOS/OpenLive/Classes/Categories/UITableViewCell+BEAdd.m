// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import "UITableViewCell+BEAdd.h"

@implementation UITableViewCell (BEAdd)

+ (NSString *)be_identifier {
    return NSStringFromClass([self class]);
}

@end
