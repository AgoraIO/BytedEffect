//
//  BEDeviceInfoHelper.m
//  BytedEffects
//
//  Created by Archie Zhou on 30/10/2019.
//  Copyright © 2019 ailab. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "BEDeviceInfoHelper.h"

#import <sys/utsname.h>

@implementation BEDeviceInfoHelper

+ (BOOL) isIPhoneXSeries
{
    struct utsname systemInfo;
    uname(&systemInfo);
    NSString*phoneType = [NSString stringWithCString: systemInfo.machine encoding:NSASCIIStringEncoding];
    
    NSArray *devicesX = [NSArray arrayWithObjects:@"iPhone10,3",@"iPhone10,6", @"iPhone11,8", @"iPhone11,2", @"iPhone11,4",
                         @"iPhone11,6", @"iPhone11,6", @"iPhone12,1", @"iPhone12,3", @"iPhone12,5", nil];
    
    for(int i = 0; i < devicesX.count - 1; i++)
    {
        if([phoneType  isEqualToString:devicesX[i]])
        {
             return YES;
        }
    }
 
    return NO;
}

@end
