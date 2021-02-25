//
//  BETimeRecoder.m
//  BytedEffects
//
//  Created by QunZhang on 2020/2/22.
//  Copyright © 2020 ailab. All rights reserved.
//

#import "BETimeRecoder.h"

@interface BETimeRecoder ()

@property (nonatomic, strong) NSMutableDictionary<NSString *, NSNumber *> *startTime;

@end

@implementation BETimeRecoder

- (instancetype)init
{
    self = [super init];
    if (self) {
        _startTime = [NSMutableDictionary dictionary];
    }
    return self;
}

- (void)record:(NSString *)tag {
    [self.startTime setObject:[NSNumber numberWithDouble:[NSDate date].timeIntervalSince1970] forKey:tag];
}

- (void)stop:(NSString *)tag {
    NSNumber *start = [self.startTime objectForKey:tag];
    if (start == nil) {
        [self be_startNotFound:tag];
        return;
    }
    [self.startTime removeObjectForKey:tag];
    double s = [start doubleValue];
    double e = [NSDate date].timeIntervalSince1970;
    [self be_recordOnce:tag interval:e - s];
}

#pragma mark - private
- (void)be_startNotFound:(NSString *)tag {
    NSLog(@"call record with tag %@ first", tag);
}

- (void)be_recordOnce:(NSString *)tag interval:(double)interval {
    NSLog(@"TimeRecoder %@ %f", tag, interval * 1000);
}

@end
