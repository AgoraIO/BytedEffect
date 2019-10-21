// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import "BEAnimationUtils.h"

CGFloat BERadiansToDegrees(CGFloat radians)
{
    return radians * 180.0 / M_PI;
}

CGFloat BEDegreesToRadians(CGFloat degrees)
{
    return degrees / 180.f * M_PI;
}
