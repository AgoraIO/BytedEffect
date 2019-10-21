// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import "BEModernEffectPickerView.h"
#import "BEEffectContentCollectionViewCell.h"
#import "BEEffectDataManager.h"
#import "BEEffectSwitchTabView.h"
#import <Masonry/Masonry.h>
#import "UIResponder+BEAdd.h"
#import "BEModernEffectPickerControlFactory.h"
#import "BEStudioConstants.h"
#import "BEFaceBeautyView.h"
#import "BEFaceBeautyViewController.h"
#import "BEButtonViewCell.h"
#import "BETextSliderView.h"
#import "BECategoryView.h"

@interface BEModernEffectPickerView ()<UICollectionViewDelegate, UICollectionViewDataSource, BEEffectSwitchTabViewDelegate, UIGestureRecognizerDelegate, TextSliderViewDelegate>

@property (nonatomic, strong) UIView *vBackground;
@property (nonatomic, strong) UICollectionView *contentCollectionView;
@property (nonatomic, strong) BECategoryView *categoryView;
//@property (nonatomic, strong) BEEffectSwitchTabView *switchTabView;
@property (nonatomic, strong) BETextSliderView *textSlider;
@property (nonatomic, strong) UIButton *btnNormal;
@property (nonatomic, strong) UIButton *btnDefault;

@property (nonatomic, strong) UIButton *btnBack;
@property (nonatomic, strong) UILabel *lTitle;
@property (nonatomic, strong) BEFaceBeautyViewController *vcMakeupOption;

@property (nonatomic, copy) NSArray <BEEffectCategoryModel *> *categories;
@property (nonatomic, strong) NSMutableSet *registeredCellClass;

@property (nonatomic, assign) BEEffectNode currentSelectItem;
/**
 集合，保存当前选择的所有特效 id
 */
@property (nonatomic, strong) NSMutableSet<NSNumber *> *selectComposerNodes;
/**
 字典，保存某一个层级的特效种选择的某一种类，如选择了口红特效中的胡萝卜红，键为口红 id，
 值为胡萝卜红 id，用于显示 option view 的时候保存状态
 */
@property (nonatomic, strong) NSMutableDictionary<NSNumber *, NSNumber *> *selectNodes;
/**
 字典，保存所有的 ButtonItemModel
 */
@property (nonatomic, strong) NSMutableDictionary<NSNumber *, BEButtonItemModel*> *cellWithIntensitySelectedModels;

@property (nonatomic, assign) float filterIntensity;
@property (nonatomic, strong) NSMutableArray<NSNumber*> *clearStatus;
@end

@implementation BEModernEffectPickerView

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        
        [self addSubview:self.vBackground];
        self.categoryView.contentView = self.contentCollectionView;
        [self addSubview:self.categoryView];
        
        [self addSubview:self.btnNormal];
        [self addSubview:self.textSlider];
        
        [self addSubview:self.lTitle];
        [self addSubview:self.btnBack];
        [self addSubview:self.btnDefault];
        
        [self.vBackground mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.bottom.right.equalTo(self);
            make.top.equalTo(self).with.offset(40);
        }];
        [self.btnNormal mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.mas_equalTo(CGSizeMake(40, 40));
            make.right.equalTo(self);
            make.top.mas_equalTo(0);
        }];
        [self.btnDefault mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.mas_equalTo(CGSizeMake(40, 40));
            make.right.equalTo(self.btnNormal.mas_left);
            make.centerY.equalTo(self.btnNormal);
        }];
        [self.textSlider mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.equalTo(self.mas_top).with.offset(-20);
            make.centerX.mas_equalTo(self);
            make.height.mas_equalTo(60);
            make.width.mas_equalTo(220);
        }];
        
        [self.categoryView mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.vBackground);
        }];
        [self.lTitle mas_makeConstraints:^(MASConstraintMaker *make) {
            make.edges.equalTo(self.categoryView.switchTabView);
        }];
        [self.btnBack mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.top.bottom.equalTo(self.categoryView.switchTabView);
            make.width.mas_equalTo(self.btnBack.mas_height);
        }];
    }
    [self loadData];
    [self addObserver];
    
    _cellWithIntensitySelectedModels =  [NSMutableDictionary dictionary];
    _selectComposerNodes = [NSMutableSet set];
    _selectNodes = [NSMutableDictionary<NSNumber *,NSNumber *> dictionary];

    return self;
}

- (void)loadData {
    self.categories = [BEEffectDataManager effectCategoryModelArray];
//    [self.switchTabView refreshWithStickerCategories:self.categories];
    self.categoryView.titles = self.categories;
    
    // 注册新的contentCellClass
    for (BEEffectCategoryModel *model in self.categories) {
        Class cellClass = [BEEffectContentCollectionViewCellFactory contentCollectionViewCellWithPanelTabType:model.type];
        NSString *classStr = NSStringFromClass(cellClass);
        if (![self.registeredCellClass containsObject:classStr]) {
            [self.contentCollectionView registerClass:[cellClass class] forCellWithReuseIdentifier:[cellClass be_identifier]];
            [self.registeredCellClass addObject:classStr];
        }
    }
    
    _clearStatus  = [NSMutableArray array];
    for (int i = 0; i < self.categories.count; i ++)
        [_clearStatus addObject:[NSNumber numberWithBool:false]];
    [self.contentCollectionView reloadData];
}

#pragma mark - Notification

- (void)addObserver {
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onItemSelect:)
                                                 name:BEEffectButtonItemSelectNotification
                                               object:nil];
}

- (void)onItemSelect:(NSNotification *)aNote {
    BEEffectNode parent = [aNote.userInfo[BEEffectNotificationUserInfoKey][0] longValue];
    BEButtonItemModel *model = aNote.userInfo[BEEffectNotificationUserInfoKey][1];
    [self onItemSelect:model parent:parent];
}

- (void)onItemSelect:(BEButtonItemModel *)model parent:(BEEffectNode)parent {
    BEEffectNode type = model.ID;
    
    // 保存选择状态
    [self.selectNodes setObject:@(type) forKey:@(parent)];
    
    if (type == BETypeClose) {
        // 关闭
        NSInteger mask;
        if ((parent & MASK) == 0) {
            mask = ~MASK;
        } else {
            mask = ~SUB_MASK;
        }
        [self be_removeObjectsFromSet:self.selectComposerNodes mask:mask type:parent];
        [self be_removeObjectFromDict:self.selectNodes mask:mask type:parent];
        
        //关闭的时候，将当前tab下的使用过的slider的值变为0，然后将他们变为未使用的状态
        for (NSNumber *buttonType in _cellWithIntensitySelectedModels){
            BEButtonItemModel *model = [_cellWithIntensitySelectedModels objectForKey:buttonType];
            if ((model.ID & ~MASK) ==  parent){
                if (model.cell != nil) {
                    [model.cell setPointOn:NO];
                }
                model.intensity = 0.0;
            }
        }
    } else {
        // 美体选中即生效
        if (parent == BETypeBeautyBody) {
            model.intensity = 1;
            if (model.cell != nil) {
                [model.cell setPointOn:YES];
            }
        }
        if (parent == BETypeMakeup) {
            // 美妆二级菜单
            [self be_showMakeupOptions:type title:model.title isShow:YES animation:YES];
            return;
        } else {
            if ((parent & ~MASK) == BETypeMakeup) {
                // 美妆三级菜单
                [self be_removeObjectsFromSet:self.selectComposerNodes mask:~SUB_MASK type:parent];
            }
            
            [self.selectComposerNodes addObject:@(type)];
        }
    }
    
    //有强度的cell来保存这些值
    if (parent == BETypeBeautyFace
        || parent == BETypeBeautyReshape
        || parent == BETypeBeautyBody) {
        [_cellWithIntensitySelectedModels setObject:model forKey:@(type)];
    }
    
    self.textSlider.progress = model.intensity;
    self.currentSelectItem = type;
    [[NSNotificationCenter defaultCenter]
                 postNotificationName:BEEffectUpdateComposerNodesNotification
                 object:nil
                 userInfo:@{ BEEffectNotificationUserInfoKey: [self.selectComposerNodes copy] }];
}

#pragma mark - public
- (void)setSliderProgress:(CGFloat)progress {
    self.textSlider.progress = progress;
}

- (void)reloadCollectionViews {
    [self.categoryView selectItemAtIndex:0 animated:NO];
    [self.contentCollectionView reloadData];
    [self.contentCollectionView selectItemAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] animated:NO scrollPosition:UICollectionViewScrollPositionLeft];
}

#pragma mark - Private
- (void)be_removeObjectsFromSet:(NSMutableSet<NSNumber *> *)set mask:(NSInteger)mask type:(BEEffectNode)type {
    NSMutableArray<NSNumber *> *arr = [NSMutableArray array];
    for (NSNumber *number in set) {
        if (([number longValue] & mask) == type) {
            [arr addObject:number];
        }
    }
    for (NSNumber *number in arr) {
        [set removeObject:number];
    }
}

- (void)be_removeObjectFromDict:(NSMutableDictionary<NSNumber *, NSNumber *> *)dict mask:(NSInteger)mask type:(BEEffectNode)type {
    NSMutableArray<NSNumber *> *arr = [NSMutableArray array];
    for (NSNumber *number in dict) {
        if (([number longValue] & mask) == type) {
            [arr addObject:number];
        }
    }
    [dict removeObjectsForKeys:arr];
}

- (void)be_showMakeupOptions:(BEEffectNode)type title:(NSString *)title isShow:(BOOL)show animation:(BOOL)animation {
    if (show) {
        self.lTitle.text = title;
        self.categoryView.switchTabView.alpha = 0;
        self.contentCollectionView.alpha = 0;
        [self.vcMakeupOption setType:type];
        NSNumber *node = [self.selectNodes objectForKey:@(type)];
        if (node != nil) {
            [self.vcMakeupOption setSelectNode:[node longValue]];
        }
        [self.be_topViewController addChildViewController:self.vcMakeupOption];
        [self addSubview:self.vcMakeupOption.view];
        
        if (animation) {
            [self.vcMakeupOption.view mas_makeConstraints:^(MASConstraintMaker *make) {
                make.top.equalTo(self.contentCollectionView.mas_bottom);
                make.left.right.equalTo(self.contentCollectionView);
                make.height.equalTo(self.contentCollectionView);
            }];
            [self layoutIfNeeded];
            [UIView animateWithDuration:0.3 animations:^{
                self.btnBack.alpha = 1;
                self.lTitle.alpha = 1;
                [self.vcMakeupOption.view mas_remakeConstraints:^(MASConstraintMaker *make) {
                    make.edges.equalTo(self.contentCollectionView);
                }];
                [self layoutIfNeeded];
            }];
        } else {
            [self.vcMakeupOption.view mas_remakeConstraints:^(MASConstraintMaker *make) {
                make.edges.equalTo(self.contentCollectionView);
            }];
            self.btnBack.alpha = 1;
            self.lTitle.alpha = 1;
        }
    } else {
        if (animation) {
            [UIView animateWithDuration:0.3 animations:^{
                self.btnBack.alpha = 0;
                self.lTitle.alpha = 0;
                [self.vcMakeupOption.view mas_remakeConstraints:^(MASConstraintMaker *make) {
                    make.top.equalTo(self.contentCollectionView.mas_bottom);
                    make.left.right.equalTo(self.contentCollectionView);
                }];
                [self layoutIfNeeded];
            } completion:^(BOOL finished) {
                self.categoryView.switchTabView.alpha = 1;
                self.contentCollectionView.alpha = 1;
                [self.vcMakeupOption removeFromParentViewController];
                [self.vcMakeupOption.view removeFromSuperview];
            }];
        } else {
            self.btnBack.alpha = 0;
            self.lTitle.alpha = 0;
            self.categoryView.switchTabView.alpha = 1;
            self.contentCollectionView.alpha = 1;
            [self.vcMakeupOption removeFromParentViewController];
            [self.vcMakeupOption.view removeFromSuperview];
        }
    }
}

- (void)be_setItemValue:(NSDictionary<NSNumber *, BEButtonItemModel *> *)dic node:(BEEffectNode)node parent:(BEEffectNode)parent value:(CGFloat)value {
    [self onItemSelect:[dic objectForKey:[NSNumber numberWithLong:node]] parent:parent];
    [self progressDidChange:value];
}

#pragma mark - UICollectionViewDataSource

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.categories.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NSInteger row = indexPath.row;
    
    BEEffectCategoryModel *model = self.categories[indexPath.row];
    Class cellClass = [BEEffectContentCollectionViewCellFactory contentCollectionViewCellWithPanelTabType:model.type];
    BEEffectContentCollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:[cellClass be_identifier] forIndexPath:indexPath];
    
    switch (row) {
        case 0:
            ((BEEffectFaceBeautyViewCell *)cell).type = BETypeBeautyFace;
            break;
        case 1:
            ((BEEffectFaceBeautyViewCell *)cell).type = BETypeBeautyReshape;
            break;
        case 2:
            ((BEEffectFaceBeautyViewCell *)cell).type = BETypeBeautyBody;
            break;
        case 3:
            ((BEEffectFaceBeautyViewCell *)cell).type = BETypeMakeup;
            break;
        default:
            break;
    }
    
    if (self.clearStatus[row] == [NSNumber numberWithBool:YES]){
        [cell setCellUnSelected];
        self.clearStatus[row] = [NSNumber numberWithBool:NO];
    }
    return cell;
}

#pragma mark - BEEffectSwitchTabViewDelegate
- (void)switchTabDidSelectedAtIndex:(NSInteger)index {
    if (index < 0 || index >= [self.contentCollectionView numberOfItemsInSection:0]) {
        return;
    }
    if (index == self.categories.count - 1){
        self.currentSelectItem = BETypeFilter;
    }
    
    //美体美妆，隐藏 slider
    self.textSlider.hidden = (index == 3 || index == 2);
    
    //每次切换tab的时候
    if (index < self.categories.count - 1) {
        NSNumber *number = [NSNumber
                            numberWithInteger:(index + 1) << OFFSET];
        self.currentSelectItem = (BEEffectNode)[[self.selectNodes objectForKey:number] integerValue];
        BEButtonItemModel *model = [self.cellWithIntensitySelectedModels
                                    objectForKey:[NSNumber numberWithInteger:self.currentSelectItem]];
        self.textSlider.progress = model.intensity;
    }
    
    [self.contentCollectionView scrollToItemAtIndexPath:[NSIndexPath indexPathForRow:index inSection:0] atScrollPosition:UICollectionViewScrollPositionCenteredHorizontally animated:YES];
}

#pragma mark - UIGestureRecognizerDelegate
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch {
    if (touch.view == self.btnNormal) {
        return YES;
    }
    return NO;
}

#pragma mark - TextSliderViewDelegate
- (void)progressDidChange:(CGFloat)progress {
    float value = progress;
    
    if (self.currentSelectItem == BETypeFilter) {
        [[NSNotificationCenter defaultCenter]
         postNotificationName:BEEffectFilterIntensityDidChangeNotification
         object:nil
         userInfo:@{BEEffectNotificationUserInfoKey:@(value)}];
        _filterIntensity = value;
        return;
    }
    
    BEButtonItemModel *model = [_cellWithIntensitySelectedModels objectForKey:@(self.currentSelectItem)];
    
    //确定每一个父节点被选择不会改变
    if (model.ID == BETypeClose) {
        return;
    }
    
    model.intensity = value;
    if (model.cell != nil) {
        [model.cell setUsedStatus:value > 0];
    }
    
    [[NSNotificationCenter defaultCenter]
     postNotificationName:BEEffectUpdateComposerNodeIntensityNotification
     object:nil
     userInfo:@{
                BEEffectNotificationUserInfoKey:
                    @[@(self.currentSelectItem), @(value)]
                }
     ];
}

#pragma mark - BECloseableProtocol
- (void)onClose {
    [self.selectNodes removeAllObjects];
    [self.selectComposerNodes removeAllObjects];
    
    for (int i = 0; i < self.clearStatus.count; i ++){
        self.clearStatus[i] = [NSNumber numberWithBool:YES];
    }
    
    self.textSlider.progress = 0.0;
    for (NSNumber *number in self.cellWithIntensitySelectedModels) {
        BEButtonItemModel *model = [self.cellWithIntensitySelectedModels objectForKey:number];
        if (model != nil) {
            model.intensity = 0;
        }
    }
    [self.cellWithIntensitySelectedModels removeAllObjects];
    
    [self be_showMakeupOptions:0 title:nil isShow:NO animation:NO];
    [self.contentCollectionView reloadData];
}

#pragma mark - button selector
- (void)onBtnNormalTouchDown
{
    [[NSNotificationCenter defaultCenter]
            postNotificationName:BEEffectNormalButtonNotification
            object:nil
     userInfo:@{BEEffectNotificationUserInfoKey:@(NO)}];
}

- (void)onBtnNormalTouchUp
{
    [[NSNotificationCenter defaultCenter]
            postNotificationName:BEEffectNormalButtonNotification
            object:nil
     userInfo:@{BEEffectNotificationUserInfoKey:@(YES)}];
}

- (void)onBtnBackTap {
    [self be_showMakeupOptions:0 title:nil isShow:NO animation:YES];
}

- (void)onBtnDefaultTap {
    // ugly
    // 两种思路，一种是通过各 cell 调用，完成自己所掌功能的默认值设置
    // 一种是在此处统一设置默认值，这就破坏了 model 数据的内聚
    // 前者暂时无法在 cell invisible 时获取对象，暂采用第二种方法
    NSInteger currentSelect = _currentSelectItem;
    CGFloat currentProgress = self.textSlider.progress;
    
    NSArray<BEButtonItemModel *> *array = [BEEffectDataManager buttonItemArrayWithDefaultIntensity];
    for (BEButtonItemModel *model in array) {
        if (model.ID == BETypeClose) {
            continue;
        }
        [self onItemSelect:model parent:(model.ID & ~ MASK)];
        [self progressDidChange:model.intensity];
        
        if (model.ID == currentSelect) {
            currentProgress = model.intensity;
        }
    }
    
    _currentSelectItem = currentSelect;
    self.textSlider.progress = currentProgress;
}

#pragma mark - getter && setter
- (UIView *)vBackground {
    if (!_vBackground) {
        _vBackground = [UIView new];
        _vBackground.backgroundColor = [UIColor colorWithWhite:0 alpha:0.6];
    }
    return _vBackground;
}

- (UICollectionView *)contentCollectionView {
    if (!_contentCollectionView) {
        UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc] init];
        flowLayout.minimumLineSpacing = 0;
        flowLayout.minimumInteritemSpacing = 0;
        flowLayout.sectionInset = UIEdgeInsetsMake(0, 0, 0, 5);
        flowLayout.scrollDirection = UICollectionViewScrollDirectionHorizontal;
        _contentCollectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:flowLayout];
        _contentCollectionView.backgroundColor = [UIColor clearColor];
        [_contentCollectionView registerClass:[BEEffectContentCollectionViewCell class] forCellWithReuseIdentifier:[BEEffectContentCollectionViewCell be_identifier]];
        _contentCollectionView.showsHorizontalScrollIndicator = NO;
        _contentCollectionView.showsVerticalScrollIndicator = NO;
        _contentCollectionView.pagingEnabled = YES;
        _contentCollectionView.dataSource = self;
    }
    return _contentCollectionView;
}

- (BECategoryView *)categoryView {
    if (!_categoryView) {
        _categoryView = [BECategoryView new];
        _categoryView.tabDelegate = self;
    }
    return _categoryView;
}

- (NSMutableSet *)registeredCellClass {
    if (!_registeredCellClass) {
        _registeredCellClass = [NSMutableSet set];
    }
    return _registeredCellClass;
}

- (BETextSliderView *)textSlider {
    if (!_textSlider) {
        _textSlider = [BETextSliderView new];
        _textSlider.backgroundColor = [UIColor clearColor];
        _textSlider.lineHeight = 2.5;
        _textSlider.textOffset = 25;
        _textSlider.animationTime = 250;
        _textSlider.delegate = self;
    }
    return _textSlider;
}

- (UIButton *)btnNormal
{
    if (!_btnNormal) {
        _btnNormal = [UIButton new];
        [_btnNormal setImage:[UIImage imageNamed:@"iconEffect.png"] forState:UIControlStateNormal];
        [_btnNormal addTarget:self action:@selector(onBtnNormalTouchDown)
             forControlEvents:UIControlEventTouchDown];
        [_btnNormal addTarget:self action:@selector(onBtnNormalTouchUp)
             forControlEvents:UIControlEventTouchUpInside];
        [_btnNormal addTarget:self action:@selector(onBtnNormalTouchUp)
             forControlEvents:UIControlEventTouchUpOutside];
        [_btnNormal addTarget:self action:@selector(onBtnNormalTouchUp)
             forControlEvents:UIControlEventTouchCancel];
    }
    return _btnNormal;
}

- (UIButton *)btnDefault {
    if (!_btnDefault) {
        _btnDefault = [UIButton new];
        [_btnDefault setImage:[UIImage imageNamed:@"iconEffect.png"] forState:UIControlStateNormal];
        [_btnDefault addTarget:self action:@selector(onBtnDefaultTap) forControlEvents:UIControlEventTouchUpInside];
    }
    return _btnDefault;
}

- (UIButton *)btnBack {
    if (!_btnBack) {
        _btnBack = [UIButton new];
        [_btnBack setImage:[UIImage imageNamed:@"ic_back"] forState:UIControlStateNormal];
        _btnBack.backgroundColor = [UIColor clearColor];
        _btnBack.alpha = 0;
        _btnBack.imageEdgeInsets = UIEdgeInsetsMake(8, 8, 8, 8);
        [_btnBack addTarget:self action:@selector(onBtnBackTap) forControlEvents:UIControlEventTouchUpInside];
    }
    return _btnBack;
}

- (UILabel *)lTitle {
    if (!_lTitle) {
        _lTitle = [UILabel new];
        _lTitle.textColor = [UIColor whiteColor];
        _lTitle.font = [UIFont systemFontOfSize:18];
        _lTitle.textAlignment = NSTextAlignmentCenter;
        _lTitle.alpha = 0;
    }
    return _lTitle;
}

- (BEFaceBeautyViewController *)vcMakeupOption {
    if (!_vcMakeupOption) {
        _vcMakeupOption = [BEFaceBeautyViewController new];
    }
    return _vcMakeupOption;
}

@end
