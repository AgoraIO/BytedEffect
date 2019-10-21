// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import "UIViewController+BEAdd.h"

@implementation UIViewController (BEAdd)

- (void)displayContentController:(UIViewController*)content
                          inView:(UIView *)view {
    if (!content) {
        return;
    }
    
    [content willMoveToParentViewController:self];
    
    [view addSubview:content.view];
    [self addChildViewController:content];
    
    [content didMoveToParentViewController:self];
}

- (void)hideContentController:(UIViewController*)content {
    if (!content) {
        return;
    }
    
    [content willMoveToParentViewController:nil];
    [content.view removeFromSuperview];
    [content removeFromParentViewController];
}
@end
