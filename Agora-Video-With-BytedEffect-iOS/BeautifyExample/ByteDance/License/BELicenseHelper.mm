//
//  BELicenseHelper.m
//  BECore
//
//  Created by bytedance on 2021/9/2.
//

#import "BELicenseHelper.h"
#import "BytedLicenseDefine.h"
#import "bef_effect_ai_license_wrapper.h"
#import "BEHttpRequestProvider.h"
#import <vector>
#import <iostream>
#import "bef_effect_ai_api.h"

#define LICENSE_NAME ((const char *)"labcv_test_20211101_20220131_com.bytedance.labcv.demo_4.2.1.licbag")

using namespace std;

static NSString *OFFLIN_LICENSE_PATH = @"LicenseBag";
static NSString *OFFLIN_BUNDLE = @"bundle";
static NSString *LICENSE_URL = @"https://cv-tob.bytedance.com/v1/api/sdk/tob_license/getlicense";
static NSString *KEY = @"jiaoyang_test";
static NSString *SECRET = @"04273924-9a77-11eb-94da-0c42a1b32a30";
static LICENSE_MODE_ENUM LICENSE_MODE = ONLINE_LICENSE;

@interface BELicenseHelper() {
    std::string         _licenseFilePath;
    LICENSE_MODE_ENUM   _licenseMode;
    IBytedLicenseProvider* _licenseProvider;
    BEHttpRequestProvider* _requestProvider;
}
@end

@implementation BELicenseHelper

static BELicenseHelper* _instance = nil;


+(instancetype) shareInstance
{
    static dispatch_once_t onceToken ;
    dispatch_once(&onceToken, ^{
        _instance = [[super allocWithZone:NULL] init] ;
    }) ;
    
    return _instance ;
}

+(id) allocWithZone:(struct _NSZone *)zone
{
    return [BELicenseHelper shareInstance] ;
}
 
-(id) copyWithZone:(struct _NSZone *)zone
{
    return [BELicenseHelper shareInstance] ;
}

- (id)init {
    self = [super init];
    if (self) {
        _errorCode = 0;
        _licenseMode = LICENSE_MODE;
        _licenseProvider = bef_effect_ai_get_license_wrapper_instance();
        if (_licenseMode == ONLINE_LICENSE)
        {
            _licenseProvider->setParam("mode", "ONLINE");
            _licenseProvider->setParam("url", "https://cv-tob.bytedance.com/v1/api/sdk/tob_license/getlicense");
            _licenseProvider->setParam("key", "jiaoyang_test");
            _licenseProvider->setParam("secret", "04273924-9a77-11eb-94da-0c42a1b32a30");
            NSString *licenseName = [NSString stringWithFormat:@"/%s", LICENSE_NAME];
            NSString *licensePath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
            licensePath = [licensePath stringByAppendingString:licenseName];
            _licenseProvider->setParam("licensePath", [licensePath UTF8String]);
        }
        else
        {
            _licenseProvider->setParam("mode", "OFFLINE");
            NSString *licenseName = [NSString stringWithFormat:@"/%s", LICENSE_NAME];
            NSString* licensePath = [[NSBundle mainBundle] pathForResource:OFFLIN_LICENSE_PATH ofType:OFFLIN_BUNDLE];
            licensePath = [licensePath stringByAppendingString:licenseName];
            _licenseProvider->setParam("licensePath", [licensePath UTF8String]);
        }
        _requestProvider = new BEHttpRequestProvider;
        _licenseProvider->registerHttpProvider(_requestProvider);
    }

    return self;
}

-(void)dealloc {
    delete _licenseProvider;
    delete _requestProvider;
}

- (const char *)licensePath {
    _errorCode = 0;
    _errorMsg = @"";
    std::map<std::string, std::string> params;
    _licenseProvider->getLicenseWithParams(params, false, [](const char* retmsg, int retSize, ErrorInfo error, void* userdata){
        BELicenseHelper* pThis = CFBridgingRelease(userdata);
        pThis.errorCode = error.errorCode;
        pThis.errorMsg = [[NSString alloc] initWithCString:error.errorMsg.c_str() encoding:NSUTF8StringEncoding];
    }, (void*)CFBridgingRetain(self));

    if (![self checkLicenseResult: @"getLicensePath"])
        return "";

    _licenseFilePath = _licenseProvider->getParam("licensePath");
    return _licenseFilePath.c_str();
}

- (const char *)updateLicensePath {
    _errorCode = 0;
    _errorMsg = @"";
    std::map<std::string, std::string> params;
    _licenseProvider->updateLicenseWithParams(params, false, [](const char* retmsg, int retSize, ErrorInfo error, void* userdata){
        BELicenseHelper* pThis = CFBridgingRelease(userdata);
        pThis.errorCode = error.errorCode;
        pThis.errorMsg = [[NSString alloc] initWithCString:error.errorMsg.c_str() encoding:NSUTF8StringEncoding];
    }, (void*)CFBridgingRetain(self));

    if (![self checkLicenseResult: @"updateLicensePath"])
        return "";
    
    _licenseFilePath = _licenseProvider->getParam("licensePath");
    return _licenseFilePath.c_str();
}

- (LICENSE_MODE_ENUM) licenseMode{
    return _licenseMode;
}

- (bool)checkLicenseResult:(NSString*) msg {
    if (_errorCode != 0) {
        if ([_errorMsg length] > 0) {
            NSLog(@"%a error: %d, %a", msg, _errorCode, _errorMsg);
            [[NSNotificationCenter defaultCenter] postNotificationName:@"kBESdkErrorNotification" object:nil
                                                      userInfo:@{@"data": _errorMsg}];
        } else {
            NSLog(@"%a error: %d", msg, _errorCode);
            [[NSNotificationCenter defaultCenter] postNotificationName:@"kBESdkErrorNotification" object:nil
                                                        userInfo:@{@"data": [NSString stringWithFormat:@"%a error: %d", msg, _errorCode]}];
        }
        return false;
    }
    return true;
}

- (bool)checkLicenseOK:(const char *) filePath {
    bef_effect_handle_t effectHandle = 0;
    int ret = bef_effect_ai_create(&effectHandle);
    // this property will be held by a singleton, and only got once,
    // so it is necessary to set use_builtin_sensor at the first time
    bef_effect_ai_use_builtin_sensor(effectHandle, YES);

    ret = bef_effect_ai_check_online_license(effectHandle, filePath);
    bef_effect_ai_destroy(effectHandle);
    
    if (ret != 0 && ret != -11 && ret != 1)
    {
        return false;
    }
    
    return true;
}
@end
