//
//  BETimeRecoder.h
//  BytedEffects
//
//  Created by QunZhang on 2020/2/22.
//  Copyright © 2020 ailab. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface BETimeRecoder : NSObject

- (void)record:(NSString *)tag;
- (void)stop:(NSString *)tag;

@end
