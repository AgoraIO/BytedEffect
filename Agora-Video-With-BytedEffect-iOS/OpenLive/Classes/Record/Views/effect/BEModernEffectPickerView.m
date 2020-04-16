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
#import "BEGlobalData.h"

NSInteger TYPE_NO_SELECT = -2;

@interface BEModernEffectPickerView ()<UICollectionViewDelegate, UICollectionViewDataSource, BEEffectSwitchTabViewDelegate, UIGestureRecognizerDelegate, TextSliderViewDelegate>

@property (nonatomic, strong) UIView *vBackground;
@property (nonatomic, strong) UICollectionView *contentCollectionView;
@property (nonatomic, strong) BECategoryView *categoryView;
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
@property (nonatomic, strong) NSMutableSet<NSNumber *> *selectedNodeSet;
/**
 字典，保存某一个层级的特效种选择的某一种类，如选择了口红特效中的胡萝卜红，键为口红 id，
 值为胡萝卜红 id，用于显示 option view 的时候保存状态
 */
@property (nonatomic, strong) NSMutableDictionary<NSNumber *, NSNumber *> *selectedNodeofPage;
/**
 字典，保存所有的 ButtonItemModel
 */
@property (nonatomic, strong) NSMutableDictionary<NSNumber *, BEButtonItemModel *> *buttonItemModelCache;
/**
 字典，保存所有设置过 intensity 小项 ID
 */
@property (nonatomic, strong) NSMutableSet<NSNumber *> *buttonItemModelWithIntensity;
/**
 字典，保存所有小项的默认值
 */
@property (nonatomic, strong) NSDictionary<NSNumber *, NSNumber *> *defaultValue;

@property (nonatomic, strong) NSMutableArray *savedData;

@property (nonatomic, strong) NSString *filterPath;
@property (nonatomic, assign) CGFloat filterIntensity;
@property (nonatomic, assign) BOOL closeFilter;
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
        
        [self.btnDefault mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.mas_equalTo(CGSizeMake(60, 30));
            make.right.equalTo(self.btnNormal);
            make.top.mas_equalTo(self);
        }];
        [self.btnNormal mas_makeConstraints:^(MASConstraintMaker *make) {
            make.size.mas_equalTo(CGSizeMake(60, 30));
            make.right.equalTo(self).with.offset(-5);
            make.top.equalTo(self.btnDefault.mas_bottom).with.offset(5);
        }];
        [self.vBackground mas_makeConstraints:^(MASConstraintMaker *make) {
            make.left.bottom.right.equalTo(self);
            make.top.equalTo(self.btnNormal.mas_bottom).with.offset(5);
        }];
        [self.textSlider mas_makeConstraints:^(MASConstraintMaker *make) {
//            make.top.equalTo(self.mas_top).with.offset(-20);
            make.bottom.equalTo(self.vBackground.mas_top).with.offset(-10);
            make.left.mas_equalTo(self.mas_left).mas_offset(20);
            make.height.mas_equalTo(60);
            make.width.mas_equalTo(self.bounds.size.width * 0.7);
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

    _selectedNodeSet = [NSMutableSet set];
    _selectedNodeofPage = [NSMutableDictionary<NSNumber *,NSNumber *> dictionary];
    _buttonItemModelCache = [NSMutableDictionary dictionary];
    _buttonItemModelWithIntensity = [NSMutableSet set];
    _savedData = [NSMutableArray array];

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
    
    [self.contentCollectionView reloadData];
}

#pragma mark - Notification

- (void)addObserver {
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onItemSelect:)
                                                 name:BEEffectButtonItemSelectNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onFilterSelect:)
                                                 name:BEEffectFilterDidChangeNotification
                                               object:nil];
}

- (void)onFilterSelect:(NSNotification *)aNote {
    NSString *path = aNote.userInfo[BEEffectNotificationUserInfoKey];
    self.filterPath = path;
    self.filterIntensity = [[[self defaultValue] objectForKey:@(BETypeFilter)] floatValue];
    self.textSlider.progress = self.filterIntensity;
    [[NSNotificationCenter defaultCenter]
         postNotificationName:BEEffectFilterIntensityDidChangeNotification
         object:nil
         userInfo:@{BEEffectNotificationUserInfoKey:@(self.filterIntensity)}];
}

- (void)onItemSelect:(NSNotification *)aNote {
    BEEffectNode parent = [aNote.userInfo[BEEffectNotificationUserInfoKey][0] longValue];
    BEButtonItemModel *model = aNote.userInfo[BEEffectNotificationUserInfoKey][1];
    [self onItemSelect:model parent:parent];
}

- (void)onItemSelect:(BEButtonItemModel *)model parent:(BEEffectNode)parent {
    BEEffectNode type = model.ID;
    // 保存 model
    [self.buttonItemModelCache setObject:model forKey:@(model.ID)];
    // 保存选择状态
    [self.selectedNodeofPage setObject:@(type) forKey:@(parent)];
    
    if (type == BETypeClose) {
        // 关闭
        NSInteger mask = ((parent & MASK) == 0 ? ~MASK : ~SUB_MASK);
        [self be_removeObjectsFromSet:self.selectedNodeSet mask:mask type:parent];
        [self be_removeObjectFromDict:self.selectedNodeofPage mask:mask type:parent];
        [self be_removeObjectsFromSet:self.buttonItemModelWithIntensity mask:mask type:parent];
        [self be_closeModelFromDict:self.buttonItemModelCache mask:mask type:parent];
    } else {
        // 美体选中即生效
        if (parent == BETypeBeautyBody) {
            model.intensity = 1;
        }
        if (parent == BETypeMakeup) {
            // 美妆二级菜单

            // 染发不显示滑杆
            self.textSlider.hidden = (type == BETypeMakeupHair);

            [self be_showMakeupOptions:type title:model.title isShow:YES animation:YES];
        } else {
            if ((parent & ~MASK) == BETypeMakeup) {
                // 美妆三级菜单
                [self be_removeObjectsFromSet:self.selectedNodeSet mask:~SUB_MASK type:parent];
            }
            
            [self.selectedNodeSet addObject:@(type)];
        }
    }
    
    // 初次打开额外设置默认值
    if (![self.buttonItemModelWithIntensity containsObject:@(model.ID & ~SUB_MASK)]) {
        if ((model.ID & ~MASK) == BETypeMakeup) {
            if ((model.ID & SUB_MASK)) {
                // 美妆三级菜单
                BEButtonItemModel *parentModel = [self.buttonItemModelCache objectForKey:@(parent)];
                [self be_getDefaultIntensity:parentModel];
            }
        } else {
            [self be_getDefaultIntensity:model];
        }
    }

    //有强度的cell来保存这些值
    if (parent == BETypeBeautyFace
        || parent == BETypeBeautyReshape
        || parent == BETypeBeautyBody
//        || parent == BETypeMakeup
        || parent == BETypeMakeupLip
        || parent == BETypeMakeupBlusher
        || parent == BETypeMakeupEyelash
        || parent == BETypeMakeupPupil
        || parent == BETypeMakeupEyeshadow
        || parent == BETypeMakeupEyebrow
        || parent == BETypeMakeupFacial) {
        [self.buttonItemModelWithIntensity addObject:@(type & ~SUB_MASK)];
    }
    
    CGFloat realIntensity = [self be_getRealIntensity:model];
    self.textSlider.progress = realIntensity;
    if (parent == BETypeMakeup && type != BETypeClose) {
        return ;
    }
    self.currentSelectItem = type;
    [[NSNotificationCenter defaultCenter]
                 postNotificationName:BEEffectUpdateComposerNodesNotification
                 object:nil
                 userInfo:@{ BEEffectNotificationUserInfoKey: [self.selectedNodeSet copy] }];
    
    [[NSNotificationCenter defaultCenter]
                 postNotificationName:BEEffectUpdateComposerNodeIntensityNotification
                 object:nil
                      userInfo:@{BEEffectNotificationUserInfoKey:@[@(self.currentSelectItem), @(realIntensity)]}];
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

- (void)setDefaultEffect {
    [self onBtnDefaultTap];
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

- (void)be_closeModelFromDict:(NSMutableDictionary<NSNumber *, BEButtonItemModel *> *)dict mask:(NSInteger)mask type:(BEEffectNode)type {
    //关闭的时候，将当前tab下的使用过的slider的值变为0，然后将他们变为未使用的状态
    for (NSNumber *buttonType in dict) {
        BEButtonItemModel *model = [dict objectForKey:buttonType];
        if ((model.ID & mask) ==  type) {
            model.intensity = 0.0;
        }
    }
}

- (void)be_showMakeupOptions:(BEEffectNode)type title:(NSString *)title isShow:(BOOL)show animation:(BOOL)animation {
    if (show) {
        self.lTitle.text = title;
        self.categoryView.switchTabView.alpha = 0;
        self.contentCollectionView.alpha = 0;
        [self.vcMakeupOption setType:type];
        NSNumber *node = [self.selectedNodeofPage objectForKey:@(type)];
        if (node != nil) {
            [self.vcMakeupOption setSelectNode:[node longValue]];
            self.currentSelectItem = [node longValue];
        } else {
            self.currentSelectItem = BETypeClose;
        }
        [self.be_topViewController addChildViewController:self.vcMakeupOption];
        [self addSubview:self.vcMakeupOption.view];
        
        if (animation) {
            [self.vcMakeupOption.view mas_makeConstraints:^(MASConstraintMaker *make) {
                make.top.equalTo(self.contentCollectionView.mas_bottom);
                make.left.right.equalTo(self.contentCollectionView);
                make.height.equalTo(self.contentCollectionView);
            }];
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

- (void)be_getDefaultIntensity:(BEButtonItemModel *)model {
    NSNumber *intensity = [self.defaultValue objectForKey:@(model.ID)];
    if (intensity != nil) {
        model.intensity = [intensity floatValue];
    } else {
        NSLog(@"be_getDefaultIntensity: no such id %ld default value", model.ID);
    }
}

- (CGFloat)be_getRealIntensity:(BEButtonItemModel *)model {
    if ((model.ID & SUB_MASK)) {
        model = [self.buttonItemModelCache objectForKey:@(model.ID & ~SUB_MASK)];
    }
    return model.intensity;
}

- (CGFloat)be_getRealIntensityWithID:(BEEffectNode)ID {
    if (ID & SUB_MASK) {
        return [self.buttonItemModelCache objectForKey:@(ID & ~SUB_MASK)].intensity;
    } else {
        return [self.buttonItemModelCache objectForKey:@(ID)].intensity;
    }
}

#pragma mark - UICollectionViewDataSource

-(NSInteger )numberOfSectionsInCollectionView:(UICollectionView *)collectionView{
    [collectionView.collectionViewLayout invalidateLayout];
    return 1;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.categories.count;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NSInteger row = indexPath.row;
    
    BEEffectCategoryModel *model = self.categories[indexPath.row];
    Class cellClass = [BEEffectContentCollectionViewCellFactory contentCollectionViewCellWithPanelTabType:model.type];
    BEEffectContentCollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:[cellClass be_identifier] forIndexPath:indexPath];
    
    if ([cell isKindOfClass:[BEEffectFaceBeautyViewCell class]]) {
        NSArray<NSNumber *> *array = [BEModernEffectPickerView effectNodeArray];
        BEEffectFaceBeautyViewCell *c = (BEEffectFaceBeautyViewCell *)cell;
        // 设置数据
        c.type = [array[row] longValue];
        // 设置选中单位
        BEEffectNode selectNode = [[self.selectedNodeofPage objectForKey:@(c.type)] longValue];
        if (selectNode == 0) {
            selectNode = BETypeClose;
        }
        [c setSelectNode:selectNode];
    }
    
    if ([cell isKindOfClass:[BEEffecFiltersCollectionViewCell class]]) {
        if (self.closeFilter) {
            [cell setCellUnSelected];
            self.closeFilter = NO;
        } else {
            [(BEEffecFiltersCollectionViewCell *)cell setSelectItem:self.filterPath];
        }
    }
    
    if (BEGlobalData.beautyEnable) {
        cell.contentView.userInteractionEnabled = YES;
        NSArray<UITapGestureRecognizer *> *gestures = [cell gestureRecognizers];
        for (UITapGestureRecognizer *gesture in gestures) {
            if ([gesture isKindOfClass:[UITapGestureRecognizer class]]) {
                [cell removeGestureRecognizer:gesture];
            }
        }
    } else {
        cell.contentView.userInteractionEnabled = NO;
        [cell addGestureRecognizer:[self tapGestureRecongnizer]];
    }
    return cell;
}

#pragma mark - BEEffectSwitchTabViewDelegate
- (void)switchTabDidSelectedAtIndex:(NSInteger)index {
    if (index < 0 || index >= [self.contentCollectionView numberOfItemsInSection:0]) {
        return;
    }
    if (index == self.categories.count - 1) {
        self.currentSelectItem = BETypeFilter;
    }
    
    //美体，隐藏 slider
    self.textSlider.hidden = (index == 2);
    
    //每次切换tab的时候切换 currentSelectItem
    if (index < self.categories.count - 1) {
        NSNumber *curPage = [BEModernEffectPickerView effectNodeArray][index];
        self.currentSelectItem = [[self.selectedNodeofPage objectForKey:curPage] longValue];
        self.textSlider.progress = [self.buttonItemModelCache objectForKey:@(self.currentSelectItem)].intensity;
        // 如果是美妆，则继续寻找三级菜单的选中
        if ((self.currentSelectItem & ~MASK) == BETypeMakeup) {
            self.currentSelectItem = [[self.selectedNodeofPage objectForKey:[NSNumber numberWithLong:self.currentSelectItem]] longValue];
        }
    } else if (index < self.categories.count) {
        self.textSlider.progress = self.filterIntensity;
        self.currentSelectItem = BETypeFilter;
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
    
    BEButtonItemModel *model = [self.buttonItemModelCache objectForKey:@(self.currentSelectItem)];
    
    //确定每一个父节点被选择不会改变
    if (model.ID == BETypeClose) {
        return;
    }


    //三级菜单不会标点，移动滑杆映射到二级菜单
    if ((model.ID & SUB_MASK)) {
        model = [self.buttonItemModelCache objectForKey:@(model.ID & ~SUB_MASK)];
    }

    model.intensity = value;

    if (model.cell != nil) {
        [model.cell setPointOn:value];
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
    if (self.savedData.count > 0) return;
    self.savedData[0] = [self.selectedNodeofPage mutableCopy];
    self.savedData[1] = [self.selectedNodeSet mutableCopy];
    self.savedData[2] = [self.buttonItemModelCache mutableCopy];
    self.savedData[3] = [self.buttonItemModelWithIntensity mutableCopy];
    
    NSMutableDictionary<NSNumber *, NSNumber *> *savedIntensities = [NSMutableDictionary dictionary];
    for (NSNumber *number in self.buttonItemModelCache) {
        BEButtonItemModel *model = [self.buttonItemModelCache objectForKey:number];
        if (model != nil) {
            [savedIntensities setObject:@(model.intensity) forKey:number];
            model.intensity = 0;
        }
    }
    self.savedData[4] = savedIntensities;
    self.savedData[5] = self.filterPath;
    self.savedData[6] = @(self.filterIntensity);
    self.savedData[7] = @(self.currentSelectItem);
    
    [self.selectedNodeofPage removeAllObjects];
    [self.selectedNodeSet removeAllObjects];
    [self.buttonItemModelCache removeAllObjects];
    [self.buttonItemModelWithIntensity removeAllObjects];
    self.filterPath = nil;
    self.filterIntensity = 0;
    self.currentSelectItem = BETypeClose;
    self.closeFilter = YES;
    self.textSlider.progress = 0.0;
    [self be_showMakeupOptions:0 title:nil isShow:NO animation:NO];
    [self.contentCollectionView reloadData];
}

- (void)recoverEffect {
    if (self.savedData.count == 0) return;
    
    self.selectedNodeofPage = self.savedData[0];
    self.selectedNodeSet = self.savedData[1];
    self.buttonItemModelCache = self.savedData[2];
    self.buttonItemModelWithIntensity = self.savedData[3];
    NSDictionary<NSNumber *, NSNumber *> *savedIntensities = self.savedData[4];
    self.filterPath = self.savedData[5];
    self.filterIntensity = [self.savedData[6] floatValue];
    self.currentSelectItem = [self.savedData[7] longValue];
    self.closeFilter = NO;
    
    CGFloat intensity = 0.0;
    for (NSNumber *number in self.buttonItemModelCache) {
        BEButtonItemModel *model = [self.buttonItemModelCache objectForKey:number];
        if (model != nil) {
            model.intensity = [[savedIntensities objectForKey:number] floatValue];
            if (model.ID == self.currentSelectItem) {
                intensity = model.intensity;
            }
        }
    }
    
    [[NSNotificationCenter defaultCenter]
                 postNotificationName:BEEffectUpdateComposerNodesNotification
                 object:nil
                 userInfo:@{ BEEffectNotificationUserInfoKey: [self.selectedNodeSet copy] }];
    
    for (NSNumber *number in self.selectedNodeSet) {
        [[NSNotificationCenter defaultCenter]
                     postNotificationName:BEEffectUpdateComposerNodeIntensityNotification
                     object:nil
                          userInfo:@{BEEffectNotificationUserInfoKey:@[number, @([self be_getRealIntensityWithID:[number longValue]])]}];
    }
    
    if (self.filterPath != nil && ![self.filterPath isEqualToString:@""]) {
        [[NSNotificationCenter defaultCenter]
            postNotificationName:BEEffectFilterDidChangeNotification
            object:nil
            userInfo:@{BEEffectNotificationUserInfoKey: self.filterPath}];
    }
    
    if (self.currentSelectItem == BETypeFilter) {
        intensity = self.filterIntensity;
    }
    self.textSlider.progress = intensity;
    [self.contentCollectionView reloadData];
    [self.savedData removeAllObjects];
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
    if (self.onDefaultTapDelegate != nil) {
        [self.onDefaultTapDelegate onDefaultTap];
    }
    [self.savedData removeAllObjects];
    
    // ugly
    // 两种思路，一种是通过各 cell 调用，完成自己所掌功能的默认值设置
    // 一种是在此处统一设置默认值，这就破坏了 model 数据的内聚
    // 前者暂时无法在 cell invisible 时获取对象，暂采用第二种方法
    
    // save pre-data
    NSInteger currentSelect = _currentSelectItem;
    CGFloat currentProgress = self.textSlider.progress;
    NSNumber *beautyFaceSelect = [self.selectedNodeofPage objectForKey:@(BETypeBeautyFace)];
    NSNumber *beautyReshapeSelect = [self.selectedNodeofPage objectForKey:@(BETypeBeautyReshape)];
    
    // close beauty body and makeup when set default
    [self onItemSelect:[BEButtonItemModel initWithId:BETypeClose] parent:BETypeMakeup];
    [self onItemSelect:[BEButtonItemModel initWithId:BETypeClose] parent:BETypeBeautyBody];
    if ([self.vcMakeupOption.view superview]) {
        [self be_showMakeupOptions:0 title:nil isShow:false animation:YES];
    }
    
    // close filter
    self.closeFilter = YES;
    [[NSNotificationCenter defaultCenter] postNotificationName:BEEffectFilterDidChangeNotification object:nil userInfo:@{BEEffectNotificationUserInfoKey:@""}];
    
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
    BEGlobalData.beautyEnable = YES;
    
    // recover saved data
    // when previous select node is close, remove it
    if (beautyFaceSelect == nil || [beautyFaceSelect longValue] == BETypeClose) {
        beautyFaceSelect = @(TYPE_NO_SELECT);
    }
    if (beautyReshapeSelect == nil || [beautyReshapeSelect longValue] == BETypeClose) {
        beautyReshapeSelect = @(TYPE_NO_SELECT);
    }
    [self.selectedNodeofPage setObject:beautyFaceSelect forKey:@(BETypeBeautyFace)];
    [self.selectedNodeofPage setObject:beautyReshapeSelect forKey:@(BETypeBeautyReshape)];
    // 由于 UICollectionView 复用导致 progressDidChange 函数中改变 model.cell 状态
    // 却映射到了其他位置，需要重新加载数据，刷新各 cell 状态
    [self.contentCollectionView reloadData];
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
        [_btnNormal setTitleColor:[UIColor colorWithWhite:0 alpha:0.6] forState:UIControlStateNormal];
        [_btnNormal setBackgroundColor:[UIColor colorWithWhite:1 alpha:0.4]];
        [[_btnNormal layer] setCornerRadius:5];
        [[_btnNormal layer] setMasksToBounds:YES];
        [[_btnNormal layer] setBorderColor:[UIColor colorWithWhite:0 alpha:0.6].CGColor];
        [[_btnNormal layer] setBorderWidth:1];
        [_btnNormal setTitle:NSLocalizedString(@"setting_compare", nil) forState:UIControlStateNormal];
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
        [_btnDefault setTitleColor:[UIColor colorWithWhite:0 alpha:0.6] forState:UIControlStateNormal];
        [_btnDefault setBackgroundColor:[UIColor colorWithWhite:1 alpha:0.4]];
        [[_btnDefault layer] setCornerRadius:5];
        [[_btnDefault layer] setMasksToBounds:YES];
        [[_btnDefault layer] setBorderColor:[UIColor colorWithWhite:0 alpha:0.6].CGColor];
        [[_btnDefault layer] setBorderWidth:1];
        [_btnDefault setTitle:NSLocalizedString(@"setting_reset", nil) forState:UIControlStateNormal];
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

- (UITapGestureRecognizer *)tapGestureRecongnizer {
    UITapGestureRecognizer *tapGestureRecongnizer = [[UITapGestureRecognizer alloc] initWithTarget:self.onTapDelegate action:@selector(onTap)];
    return tapGestureRecongnizer;
}

- (NSDictionary<NSNumber *,NSNumber *> *)defaultValue {
    return [BEEffectDataManager defaultValue];
}

+ (NSArray<NSNumber *> *)effectNodeArray {
    static dispatch_once_t onceToken;
    static NSArray<NSNumber *> *array;
    dispatch_once(&onceToken, ^{
        array = @[
                 [NSNumber numberWithLong:BETypeBeautyFace],
                 [NSNumber numberWithLong:BETypeBeautyReshape],
                 [NSNumber numberWithLong:BETypeBeautyBody],
                 [NSNumber numberWithLong:BETypeMakeup],
                 [NSNumber numberWithLong:BETypeFilter]];
    });
    return array;
}

@end
