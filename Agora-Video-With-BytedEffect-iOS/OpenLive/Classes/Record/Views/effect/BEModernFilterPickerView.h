// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import <UIKit/UIKit.h>
@class BEEffect, BEModernFilterPickerView;

@protocol BEModernFilterPickerViewDelegate <NSObject>

- (void)filterPicker:(BEModernFilterPickerView *)pickerView didSelectFilterPath:(NSString *)path;

@end

@interface BEModernFilterPickerView : UIView

@property (nonatomic, weak) id<BEModernFilterPickerViewDelegate> delegate;

- (void)refreshWithFilters:(NSArray <BEEffect *>*)filters;
- (void)setAllCellsUnSelected;
- (void)setSelectItem:(NSString *)filterPath;

@end
