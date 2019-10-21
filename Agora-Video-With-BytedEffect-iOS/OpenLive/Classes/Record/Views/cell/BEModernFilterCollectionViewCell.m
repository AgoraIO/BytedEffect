// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import "BEModernFilterCollectionViewCell.h"
#import <Masonry/Masonry.h>
#import "BEEffectResponseModel.h"

static CGFloat const BEModernFilterCellContentPadding = 2.f;

@interface BEModernFilterCollectionViewCell ()

@property (nonatomic, strong) UIImageView *imageView;
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) BEEffect *filter;
@property (nonatomic, strong) CAShapeLayer *borderLayer;

@end

@implementation BEModernFilterCollectionViewCell

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self.contentView.layer addSublayer:self.borderLayer];
        [self.contentView addSubview:self.imageView];
        [self.contentView addSubview:self.titleLabel];
        
        [self.titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.leading.mas_equalTo(BEModernFilterCellContentPadding);
            make.trailing.equalTo(self.contentView).offset(-BEModernFilterCellContentPadding);
            make.bottom.equalTo(self.contentView).offset(-BEModernFilterCellContentPadding);
            make.height.mas_equalTo(16);
        }];
        [self.imageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.leading.mas_equalTo(BEModernFilterCellContentPadding);
            make.trailing.equalTo(self.contentView).offset(-BEModernFilterCellContentPadding);
            make.top.equalTo(self.contentView).offset(BEModernFilterCellContentPadding);
            make.bottom.equalTo(self.titleLabel.mas_top).with.offset(-10);
        }];
    }
    return self;
}

- (void)setSelected:(BOOL)selected {
    [super setSelected:selected];
    
    self.borderLayer.hidden = !selected;
}


- (void)configureWithFilter:(BEEffect *)filter {
    _filter = filter;
    
    self.titleLabel.text = filter.title;
    self.imageView.image = [UIImage imageNamed:filter.imageName];
}

#pragma mark - getter

- (UIImageView *)imageView {
    if (!_imageView) {
        _imageView = [UIImageView new];
        _imageView.backgroundColor = [UIColor lightGrayColor];
    }
    return _imageView;
}

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.textColor = [UIColor whiteColor];
        _titleLabel.textAlignment = NSTextAlignmentCenter;
        _titleLabel.font = [UIFont systemFontOfSize:13];
        _titleLabel.numberOfLines = 0;
        _titleLabel.backgroundColor = [UIColor clearColor];
    }
    return _titleLabel;
}

- (CAShapeLayer *)borderLayer {
    if (!_borderLayer) {
        _borderLayer = [CAShapeLayer layer];
        _borderLayer.frame = self.contentView.bounds;
        CGRect layerRect = CGRectInset(self.contentView.bounds, BEModernFilterCellContentPadding/2, BEModernFilterCellContentPadding/2);
        _borderLayer.path = [UIBezierPath bezierPathWithRect:layerRect].CGPath;
        _borderLayer.strokeColor = [UIColor purpleColor].CGColor;
        _borderLayer.lineWidth = BEModernFilterCellContentPadding;
        _borderLayer.hidden = YES;
        _borderLayer.fillColor = nil;
    }
    return _borderLayer;
}
@end
