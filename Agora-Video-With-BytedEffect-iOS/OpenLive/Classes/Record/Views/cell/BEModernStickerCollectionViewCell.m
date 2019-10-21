// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import <Foundation/Foundation.h>
#import "BEModernStickerCollectionViewCell.h"
#import <Masonry/Masonry.h>
#import "BEEffectResponseModel.h"

static CGFloat const BEModernStickerCellContentPadding = 2.0f;

@interface BEModernStickerCollectionViewCell ()

@property (nonatomic, strong) UIImageView *imageView;
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) BEEffectSticker *sticker;
@property (nonatomic, strong) CAShapeLayer *borderLayer;


@end

@implementation BEModernStickerCollectionViewCell

- (instancetype) initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    
    if (self){
        [self.contentView.layer addSublayer:self.borderLayer];
        [self.contentView addSubview:self.imageView];
        [self.contentView addSubview:self.titleLabel];
        
        [self.titleLabel mas_makeConstraints:^(MASConstraintMaker *make) {
            make.leading.mas_equalTo(BEModernStickerCellContentPadding);
            make.trailing.equalTo(self.contentView).offset(-BEModernStickerCellContentPadding);
            make.bottom.equalTo(self.contentView).offset(-BEModernStickerCellContentPadding);
            make.height.mas_equalTo(16);
        }];
        [self.imageView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.leading.mas_equalTo(BEModernStickerCellContentPadding);
            make.trailing.equalTo(self.contentView).offset(-BEModernStickerCellContentPadding);
            make.top.equalTo(self.contentView).offset(BEModernStickerCellContentPadding);
            make.bottom.equalTo(self.titleLabel.mas_top);
        }];
        
    }
    
    return self;
}

- (void) setSelected:(BOOL)selected{
    [super setSelected:selected];
    
    self.borderLayer.hidden = !selected;
}

#pragma mark - public
- (void) configureWithSticker:(BEEffectSticker *)sticker{
    _sticker = sticker;
    
    self.titleLabel.text = sticker.title;
    self.imageView.image = [UIImage imageNamed:sticker.imageName];
}

- (void)configureWithUIImage:(UIImage *)image{
    self.imageView.image = image;
}
#pragma make - getter

- (UIImageView *)imageView{
    if (!_imageView){
        _imageView = [UIImageView new];
        _imageView.backgroundColor = [UIColor clearColor];
        _imageView.contentMode = UIViewContentModeScaleAspectFit;
        //_imageView.clipsToBounds = YES;
    }
    
    return _imageView;
}

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.textColor = [UIColor whiteColor];
        _titleLabel.textAlignment = NSTextAlignmentCenter;
        _titleLabel.font = [UIFont systemFontOfSize:13];
        _titleLabel.numberOfLines = 2;
        _titleLabel.backgroundColor = [UIColor clearColor];
    }
    return _titleLabel;
}

- (CAShapeLayer *)borderLayer {
    if (!_borderLayer) {
        _borderLayer = [CAShapeLayer layer];
        _borderLayer.frame = self.contentView.bounds;
        CGRect layerRect = CGRectInset(self.contentView.bounds, BEModernStickerCellContentPadding/2, BEModernStickerCellContentPadding/2);
        _borderLayer.path = [UIBezierPath bezierPathWithRect:layerRect].CGPath;
        _borderLayer.strokeColor = [UIColor whiteColor].CGColor;
        _borderLayer.lineWidth = BEModernStickerCellContentPadding;
        _borderLayer.hidden = YES;
        _borderLayer.fillColor = nil;
    }
    return _borderLayer;
}

@end
