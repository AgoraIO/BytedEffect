// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import "BEModernEffectPickerControlFactory.h"
#import <Masonry/Masonry.h>
#import "BEAnimationUtils.h"
#import "BEEffectTitleCollectionViewCell.h"
#import "NSArray+BEAdd.h"

//@implementation BEModernEffectPickerControlFactory
//
//+ (UILabel *)createLabel {
//    UILabel *label = [UILabel new];
//    label.font = [UIFont boldSystemFontOfSize:15];
//    label.textColor = [UIColor blackColor];
//    label.adjustsFontSizeToFitWidth = YES;
//    label.minimumScaleFactor = 0.5;
//    return label;
//}
//
//+ (UISwitch *)createSwitch {
//    UISwitch *switcher = [UISwitch new];
//    switcher.onTintColor = BEColorWithRGBHex(0x2EC3C0);
//    switcher.on = NO;
//    return switcher;
//}
//
//@end

@interface BEModernFaceActionView () <UICollectionViewDelegate, UICollectionViewDataSource>

@property (nonatomic, strong) UICollectionView *collectionView;

@end


@implementation BEModernFaceActionView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self){
        [self addSubview:self.collectionView];
        [self.collectionView mas_makeConstraints:^(MASConstraintMaker *make){
            make.edges.equalTo(self);
        }];
    }
    return self;
}

#pragma mark - UICollectionViewDataSource
- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return faceActionTypes().count;
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView{
    return 1;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    BEEffectTitleCollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:[BEEffectTitleCollectionViewCell be_identifier] forIndexPath:indexPath];
    [cell renderWithTitle:[faceActionTypes() be_objectAtIndex:indexPath.row]];
    [cell setTitleLabelFont:[UIFont systemFontOfSize:13]];
    cell.backgroundColor = [UIColor colorWithRed:0/255.0 green:0/255.0 blue:0/255.0 alpha:0.6];
    return cell;
}

#pragma mark - getter
- (UICollectionView *)collectionView {
    if (!_collectionView) {
        UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc] init];
        flowLayout.minimumLineSpacing = 3;
        flowLayout.minimumInteritemSpacing = 0.1;
        flowLayout.sectionInset = UIEdgeInsetsMake(0, 0, 0, 0);
        flowLayout.scrollDirection = UICollectionViewScrollDirectionHorizontal;
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:flowLayout];
        _collectionView.backgroundColor = [UIColor clearColor];
        [_collectionView registerClass:[BEEffectTitleCollectionViewCell class] forCellWithReuseIdentifier:[BEEffectTitleCollectionViewCell be_identifier]];
        _collectionView.showsHorizontalScrollIndicator = NO;
        _collectionView.showsVerticalScrollIndicator = NO;
        _collectionView.dataSource = self;
        _collectionView.delegate = self;
        _collectionView.allowsMultipleSelection = YES;
    }
    return _collectionView;
}

#pragma mark - UICollectionViewDelegate
- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    return CGSizeMake(36, 23);
}

#pragma mark - 表情数组
static NSArray *faceActionTypes(){
    static dispatch_once_t onceToken;
    static NSArray* types;
    
    dispatch_once(&onceToken, ^{
        types = @[
                  NSLocalizedString(@"closing_eye", nil),
                  NSLocalizedString(@"open_mouth", nil),
                  NSLocalizedString(@"shake_head", nil),
                  NSLocalizedString(@"nod", nil),
                  NSLocalizedString(@"raise_eyebrow", nil),
                  NSLocalizedString(@"pout", nil),];
        
    });
    return types;
}

@end
