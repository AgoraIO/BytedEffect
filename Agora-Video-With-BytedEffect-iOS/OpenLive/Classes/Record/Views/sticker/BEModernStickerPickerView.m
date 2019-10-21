// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import <Foundation/Foundation.h>
#import "BEModernStickerPickerView.h"
#import "BEEffectResponseModel.h"
#import <Masonry/Masonry.h>
#import "BEModernEffectPickerControlFactory.h"
#import "BEModernStickerCollectionViewCell.h"
#import "BEModernEffectPickerControlFactory.h"
#import "BEStudioConstants.h"

@interface BEModernStickerPickerView () <UICollectionViewDelegate, UICollectionViewDataSource>

@property (nonatomic, strong) UICollectionView* collectionView;
@property (nonatomic, copy) NSArray<BEEffectSticker*> *stickers;
@property (nonatomic, weak) NSIndexPath* currentSelectedCellIndexPath;

@end

@implementation BEModernStickerPickerView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    
    if(self){
        [self addSubview:self.collectionView];
        
        [self.collectionView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self);
            make.bottom.mas_equalTo(self).with.offset(5);
            make.leading.trailing.equalTo(self);
        }];
    }
    return self;
}

- (void)refreshWithStickers:(NSArray<BEEffectSticker *> *)stickers{
    self.stickers = stickers;
    [self.collectionView reloadData];
    [self.collectionView selectItemAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] animated:NO scrollPosition:UICollectionViewScrollPositionNone];
}

#pragma mark - BECloseableProtocol
- (void)onClose {
    if (_currentSelectedCellIndexPath){
        [self.collectionView deselectItemAtIndexPath:_currentSelectedCellIndexPath animated:false];
        _currentSelectedCellIndexPath = nil;
        
        [self.collectionView selectItemAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] animated:NO scrollPosition:UICollectionViewScrollPositionNone];
    }
}

#pragma mark - UICollectionViewDataSource
- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    return self.stickers.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(nonnull NSIndexPath *)indexPath{
    BEModernStickerCollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:[BEModernStickerCollectionViewCell be_identifier] forIndexPath:indexPath];
    [cell configureWithSticker:self.stickers[indexPath.row]];
    return cell;
}

#pragma mark - UICollectionViewDelegate

-(void) collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath{
    _currentSelectedCellIndexPath =indexPath;

    if ([self.delegate respondsToSelector:@selector(stickerPicker:didSelectStickerPath:toastString:)]) {
        [self.delegate stickerPicker:self didSelectStickerPath:self.stickers[indexPath.row].filePath toastString:self.stickers[indexPath.row].toastString];
    }
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    return CGSizeMake(70, 70);
}

- (UICollectionView *)collectionView {
    if (!_collectionView) {
        UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc] init];
        flowLayout.minimumLineSpacing = 11;
        flowLayout.minimumInteritemSpacing = 12;
        flowLayout.scrollDirection = UICollectionViewScrollDirectionVertical;
        flowLayout.sectionInset = UIEdgeInsetsMake(15, 20, 5, 20);
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:flowLayout];
        _collectionView.backgroundColor = [UIColor clearColor];
        [_collectionView registerClass:[BEModernStickerCollectionViewCell class] forCellWithReuseIdentifier:[BEModernStickerCollectionViewCell be_identifier]];
        _collectionView.showsHorizontalScrollIndicator = NO;
        _collectionView.showsVerticalScrollIndicator = NO;
        _collectionView.dataSource = self;
        _collectionView.delegate = self;
    }
    return _collectionView;
}
@end
