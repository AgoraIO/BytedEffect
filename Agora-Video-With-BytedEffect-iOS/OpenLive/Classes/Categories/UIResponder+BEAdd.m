// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import "UIResponder+BEAdd.h"

@implementation UIResponder (BEAdd)

- (UIViewController *)be_topViewController {
    UIViewController *topVC;
    UIResponder *responder = self;
    while (responder) {
        if ([responder isKindOfClass:[UIViewController class]]) {
            topVC = (UIViewController *)responder;
            break;
        }
        responder = [responder nextResponder];
    }
    return topVC;
}

@end
