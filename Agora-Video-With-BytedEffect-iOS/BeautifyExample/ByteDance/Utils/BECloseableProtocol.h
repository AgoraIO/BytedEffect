//
//  BECloseableProtocol.h
//  BytedEffects
//
//  Created by QunZhang on 2019/8/23.
//  Copyright © 2019 ailab. All rights reserved.
//
#import <Foundation/Foundation.h>

@protocol BECloseableProtocol <NSObject>

@required
- (void)onClose;

@end
