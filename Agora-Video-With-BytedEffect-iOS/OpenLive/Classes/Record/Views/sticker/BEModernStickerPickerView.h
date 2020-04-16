// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import <UIKit/UIKit.h>

#import "BECloseableProtocol.h"
#import "BEButtonItemModel.h"
#import "BEVideoRecorderViewController.h"

@class BEEffectSticker, BEModernStickerPickerView;
@protocol BEModernStickerPickerViewDelegate <NSObject>

- (void)stickerPicker:(BEModernStickerPickerView*)pickerView didSelectStickerPath:(NSString *)path toastString:(NSString*) toast type:(BEEffectNode)type;

@end

@interface BEModernStickerPickerView : UIView <BECloseableProtocol>

@property (nonatomic, weak) id<BEModernStickerPickerViewDelegate> delegate;
@property (nonatomic, weak) id<BETapDelegate> onTapDelegate;
@property (nonatomic, assign) BEEffectNode type;

- (void)refreshWithStickers:(NSArray <BEEffectSticker *>*) stickers;
- (void)interceptTap:(BOOL)intercept;
- (void)recoverState:(NSString *)path;
@end
