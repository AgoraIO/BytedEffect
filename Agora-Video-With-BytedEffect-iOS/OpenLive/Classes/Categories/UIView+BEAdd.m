// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import "UIView+BEAdd.h"

@implementation UIView (AWELayout)

- (void)setBe_top:(CGFloat)be_top {
    self.frame = CGRectMake(self.be_left, be_top, self.be_width, self.be_height);
}

- (CGFloat)be_top {
    return self.frame.origin.y;
}

- (void)setBe_bottom:(CGFloat)be_bottom {
    self.frame = CGRectMake(self.be_left, be_bottom - self.be_height, self.be_width, self.be_height);
}

- (CGFloat)be_bottom {
    return self.frame.origin.y + self.frame.size.height;
}

- (void)setBe_left:(CGFloat)be_left {
    self.frame = CGRectMake(be_left, self.be_top, self.be_width, self.be_height);
}

- (CGFloat)be_left {
    return self.frame.origin.x;
}

- (void)setBe_right:(CGFloat)be_right {
    self.frame = CGRectMake(be_right - self.be_width, self.be_top, self.be_width, self.be_height);
}

- (CGFloat)be_right {
    return self.frame.origin.x + self.frame.size.width;
}

- (void)setBe_width:(CGFloat)be_width {
    self.frame = CGRectMake(self.be_left, self.be_top, be_width, self.be_height);
}

- (CGFloat)be_width {
    return self.frame.size.width;
}

- (void)setBe_height:(CGFloat)be_height {
    self.frame = CGRectMake(self.be_left, self.be_top, self.be_width, be_height);
}

- (CGFloat)be_height {
    return self.frame.size.height;
}

- (CGFloat)be_centerX {
    return self.center.x;
}

- (void)setBe_centerX:(CGFloat)be_centerX {
    self.center = CGPointMake(be_centerX, self.center.y);
}

- (CGFloat)be_centerY {
    return self.center.y;
}

- (void)setBe_centerY:(CGFloat)be_centerY {
    self.center = CGPointMake(self.center.x, be_centerY);
}

- (CGSize)be_size {
    return self.frame.size;
}

- (void)setBe_size:(CGSize)be_size {
    self.frame = CGRectMake(self.be_left, self.be_top, be_size.width, be_size.height);
}

- (CGPoint)be_origin {
    return self.frame.origin;
}

- (void)setBe_origin:(CGPoint)be_origin {
    self.frame = CGRectMake(be_origin.x, be_origin.y, self.be_width, self.be_height);
}

@end
