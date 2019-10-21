// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import "NSString+BEAdd.h"

@implementation NSString (BEUtil)

- (NSString *)be_transformToPinyin {
    NSMutableString *mutableString = [NSMutableString stringWithString:self];
    CFStringTransform((CFMutableStringRef)mutableString, NULL, kCFStringTransformToLatin, false);
    mutableString = (NSMutableString *)[mutableString stringByFoldingWithOptions:NSDiacriticInsensitiveSearch locale:[NSLocale currentLocale]];
    NSString *str = [mutableString stringByReplacingOccurrencesOfString:@" " withString:@""];
    return str;
}

@end
