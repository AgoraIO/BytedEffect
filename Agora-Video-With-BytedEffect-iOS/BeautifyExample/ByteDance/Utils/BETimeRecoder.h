//
//  BETimeRecoder.h
//  BytedEffects
//
//  Created by QunZhang on 2020/2/22.
//  Copyright © 2020 ailab. All rights reserved.
//

#import <Foundation/Foundation.h>

#if TIME_LOG
#define RECORD_TIME(NAME) double _##NAME = [NSDate date].timeIntervalSince1970;
#else
#define RECORD_TIME(NAME)
#endif

#if TIME_LOG
#define STOP_TIME(NAME) NSLog(@"TimeRecoder %s %f", #NAME, ([NSDate date].timeIntervalSince1970 - _##NAME) * 1000);
#else
#define STOP_TIME(NAME)
#endif

@interface BETimeRecoder : NSObject

- (void)record:(NSString *)tag;
- (void)stop:(NSString *)tag;

@end
