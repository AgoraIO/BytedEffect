// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
#import "BEAppDelegate.h"
#import "BEVideoRecorderViewController.h"
//#import <Bugly/Bugly.h>

@interface BEAppDelegate ()

@end

@implementation BEAppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {

    self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
    [self.window makeKeyAndVisible];
    
    BEVideoRecorderViewController *recordVC = [[BEVideoRecorderViewController alloc] init];
    UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:recordVC];
    self.window.rootViewController = nav;
    
//    [Bugly startWithAppId:@"73d477ebbf"];
    
    return YES;
}

@end
