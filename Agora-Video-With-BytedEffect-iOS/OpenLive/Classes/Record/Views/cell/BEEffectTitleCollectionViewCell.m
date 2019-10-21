// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import "BEEffectTitleCollectionViewCell.h"
#import <Masonry/Masonry.h>

@interface BEEffectTitleCollectionViewCell()

@property (nonatomic, strong) UILabel *titleLabel;

@end

@implementation BEEffectTitleCollectionViewCell

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self.contentView addSubview:self.titleLabel];
        [self.titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self);
        }];
    }
    return self;
}

- (void)setSelected:(BOOL)selected {
    [super setSelected:selected];

    self.titleLabel.textColor = selected ? [UIColor whiteColor] : [UIColor colorWithRed:156/255.0 green:156/255.0 blue:156/255.0 alpha:1.0];
}

-(void)renderWithTitle:(NSString *)title {
    self.titleLabel.text = title;
}

- (void)setTitleLabelFont:(UIFont *)font{
    self.titleLabel.font = font;
}
#pragma mark - getter

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.textColor = [UIColor lightGrayColor];
        _titleLabel.textAlignment = NSTextAlignmentCenter;
        _titleLabel.numberOfLines = 0;
    }
    return _titleLabel;
}

@end
