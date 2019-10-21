//
//  BEButtonView.m
//  BytedEffects
//
//  Created by QunZhang on 2019/8/13.
//  Copyright © 2019 ailab. All rights reserved.
//

#import <Masonry.h>
#import "BEMacro.h"
#import "BEButtonView.h"

@interface BEButtonView ()

@property (nonatomic, strong) UIImageView *iv;
@property (nonatomic, strong) UILabel *lTitle;
@property (nonatomic, strong) UILabel *lDesc;

@property (nonatomic, strong) UIImage *selectImg;
@property (nonatomic, strong) UIImage *unselectImg;
@property (nonatomic, strong) UIColor *selectColor;
@property (nonatomic, strong) UIColor *unselectColor;

@end


@implementation BEButtonView

#pragma mark - init

- (instancetype)init
{
    self = [super init];
    if (self) {
        _selectColor = BEColorWithRGBHex(0xFFFFFF);
        _unselectColor = BEColorWithRGBHex(0x9C9C9C);
        [self be_setClickListener];
    }
    return self;
}

- (void)be_setClickListener
{
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(onClicked)];
    self.userInteractionEnabled = YES;
    [self addGestureRecognizer:tap];
}

- (void)onClicked
{
    if ([self.delegate respondsToSelector:@selector(onButtonClicked:)]) {
        [self.delegate onButtonClicked:self];
    }
}

#pragma mark - public

- (void)setSelectImg:(UIImage *)selectImg unselectImg:(UIImage *)unselectImg
{
    [self.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    _selectImg = selectImg;
    _unselectImg = unselectImg;
    self.iv.image = unselectImg;
    
    [self addSubview:self.iv];
    [self.iv mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self);
    }];
}

- (void)setSelectImg:(UIImage *)selectImg unselectImg:(UIImage *)unselectImg title:(NSString *)title
{
    [self setSelectImg:selectImg unselectImg:unselectImg title:title expand:NO];
}

- (void)setSelectImg:(UIImage *)selectImg unselectImg:(UIImage *)unselectImg title:(NSString *)title expand:(BOOL)expand
{
    [self.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    _selectImg = selectImg;
    _unselectImg = unselectImg;
    self.iv.image = unselectImg;
    self.lTitle.text = title;
    
    [self addSubview:self.iv];
    [self addSubview:self.lTitle];
    
    [self.lTitle mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self);
        make.left.right.equalTo(self);
        make.height.mas_equalTo(13);
    }];
    if (expand) {
        [self.iv mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self);
            make.bottom.equalTo(self.lTitle.mas_top).with.offset(-5);
            make.centerX.equalTo(self);
            make.width.mas_equalTo(self.iv.mas_height);
        }];
    } else {
        [self.iv mas_remakeConstraints:^(MASConstraintMaker *make) {
            make.size.mas_equalTo(CGSizeMake(40, 40));
            make.top.equalTo(self);
            make.centerX.equalTo(self);
        }];
    }
}

- (void)setSelectImg:(UIImage *)selectImg unselectImg:(UIImage *)unselectImg title:(NSString *)title desc:(NSString *)desc
{
    [self.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    _selectImg = selectImg;
    _unselectImg = unselectImg;
    self.iv.image = unselectImg;
    self.lDesc.text = desc;
    self.lTitle.text = title;
    
    [self addSubview:self.iv];
    [self addSubview:self.lTitle];
    [self addSubview:self.lDesc];
    
    [self.iv mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.size.mas_equalTo(CGSizeMake(40, 40));
        make.top.equalTo(self);
        make.centerX.equalTo(self);
    }];
    [self.lDesc mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self);
        make.centerX.equalTo(self);
        make.height.mas_equalTo(13);
    }];
    [self.lTitle mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.bottom.equalTo(self.lDesc.mas_top).with.offset(-3);
        make.height.mas_equalTo(10);
        make.centerX.equalTo(self);
    }];
    
}

- (void)setPointOn:(BOOL)isOn {
    if (isOn)
        _lDesc.text = @".";
    else
        _lDesc.text = @"";
}

- (void)setUsedStatus:(BOOL) used{
    self.lDesc.hidden = !used;
}
#pragma mark - setter

- (void)setSelected:(BOOL)selected
{
    [self.iv setImage:selected ? _selectImg : _unselectImg];
    self.lTitle.textColor = selected ? _selectColor : _unselectColor;
    self.lDesc.textColor = selected ? _selectColor : _unselectColor;
    _selected = selected;
}

#pragma mark - getter

- (UIImageView *)iv
{
    if (!_iv) {
        _iv = [UIImageView new];
        _iv.contentMode = UIViewContentModeScaleAspectFill;
        _iv.clipsToBounds = YES;
    }
    return _iv;
}

- (UILabel *)lTitle
{
    if (!_lTitle) {
        _lTitle = [UILabel new];
        _lTitle.font = [UIFont systemFontOfSize:13];
        _lTitle.textColor = _unselectColor;
        _lTitle.textAlignment = NSTextAlignmentCenter;
    }
    return _lTitle;
}

- (UILabel *)lDesc
{
    if (!_lDesc) {
        _lDesc = [UILabel new];
        _lDesc.font = [UIFont systemFontOfSize:11];
        _lDesc.textColor = _unselectColor;
    }
    return _lDesc;
}

@end
