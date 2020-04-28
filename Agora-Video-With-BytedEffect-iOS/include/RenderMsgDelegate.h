// Copyright (C) 2020 Beijing Bytedance Network Technology Co., Ltd.
#if TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR

#import <UIKit/UIKit.h>

#elif TARGET_OS_OSX

#import <AppKit/AppKit.h>

#endif

@protocol RenderMsgDelegate <NSObject>

// 消息处理，处理该消息了返回TRUE，否则返回FALSE

/// @brief 处理消息回调
/// @param unMsgID 消息ID
/// @param nArg1 附加参数1
/// @param nArg2 附加参数2
/// @param cArg3 附加参数3
/// @return 成功返回YES，失败返回NO
- (BOOL) msgProc : (unsigned int) unMsgID
             arg1: (int) nArg1
             arg2: (int) nArg2
             arg3: (const char*) cArg3;

@end

@interface IRenderMsgDelegateManager : NSObject

- (void)addDelegate : (id<RenderMsgDelegate>) pMsgDelegate;

- (void)removeDelegate : (id<RenderMsgDelegate>) pMsgDelegate;

- (BOOL) delegateProc : (unsigned int) unMsgID
                  arg1: (int) nArg1
                  arg2: (int) nArg2
                  arg3: (const char*) cArg3;

- (void)destoryDelegate;

@end
