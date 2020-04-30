// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.

#import "BEEffectDataManager.h"
#import "BEResourceHelper.h"
#import "BEMacro.h"

@interface BEEffectDataManager ()

@property (nonatomic, assign) BEEffectDataManagerType type;
@property (nonatomic, strong) dispatch_queue_t operationQueue;
@property (nonatomic, strong) BEEffectResponseModel *responseModel;

@end

static NSArray* stickersArray = nil;

@implementation BEEffectDataManager

- (void) initStickerDict {
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        stickersArray = @[
           // @"控雨", @"icon_rain", @"rain",
            NSLocalizedString(@"sticker_change_face", nil), @"icon_change_face", @"change_face",  NSLocalizedString(@"sticker_change_face_tip", nil),
            
//            @"京剧武旦", @"icon_wudan", @"wudan",
//            @"青衣", @"icon_qingyi", @"qingyi",
//            @"花脸", @"icon_hualian", @"hualian",
//            @"老生", @"icon_laosheng", @"laosheng",
//            @"丑角色", @"icon_choujue", @"choujuese",
            NSLocalizedString(@"sticker_line_dance", nil), @"icon_line_dance", @"line_dance", @"",

//            @"羽毛", @"icon_yumao", @"a159dab11e52bc53fcbe5b496cbf0d0b",
             NSLocalizedString(@"sticker_xiyue", nil), @"icon_xiyue", @"739661e875e3086700024d34eb5ee92c", @"",
//            @"魔界妆", @"icon_mojie", @"3b4b645fcb557ba5e451037c81a21e60",
//            @"神龙", @"icon_shenlong", @"0c435f3a8187f61b394145575d151e48",
             NSLocalizedString(@"sticker_huaxianzi", nil), @"icon_huaxian", @"6bc53e0a429951da45d55f91f01a9403",  NSLocalizedString(@"sticker_huaxianzi_tip", nil),
            
            NSLocalizedString(@"sticker_caixiaomao", nil), @"icon_xiaomao", @"e31f163f969a35655b1953c4cdf49d77", NSLocalizedString(@"sticker_caixiaomao_tip", nil),
            
            NSLocalizedString(@"sticker_xiaoemo", nil), @"icon_emo", @"01dd809c056708f5ad97a1327ea2ae95",  @"",
            
            NSLocalizedString(@"sticker_shengrikuaile", nil), @"icon_happy_birthday", @"95a9aeb2c7f99d3d8f7931ea2cbe11ce",   NSLocalizedString(@"sticker_shengrikuaile_tip", nil),
            
            NSLocalizedString(@"sticker_maomao", nil), @"icon_maomao", @"12dc4ebc9802e812dea2a64ddb0e03d8", @"",
            
            NSLocalizedString(@"sticker_wumeiniang", nil), @"icon_wumeiniang", @"6eeefb66259e73e5b10090271a278d21", NSLocalizedString(@"sticker_wumeiniang_tip", nil),
            
            NSLocalizedString(@"sticker_liuxing", nil), @"icon_liuxing", @"d765b60b9c046618ccf95973212bcb52", NSLocalizedString(@"sticker_liuxing_tip", nil),
            
            NSLocalizedString(@"sticker_yinghua", nil), @"icon_yinghuazhuang", @"788130eaaa4093084d9683ff3d349a18", @"",
            NSLocalizedString(@"sticker_tujie", nil), @"icon_tujie", @"80c48bd4e99eb65cb2085029de50e7ca", @"",
            
            NSLocalizedString(@"sticker_dielianjin", nil), @"icon_dienianjin", @"14fc8ac081ffea4c797a30693906e604",  NSLocalizedString(@"sticker_dielianjin_tip", nil),
            
            NSLocalizedString(@"sticker_dielianfen", nil), @"icon_dienianfen", @"e8dc3a7dda4872dd3e87ba62a13da865", NSLocalizedString(@"sticker_dielianfen_tip", nil),

            //      @"萌小只", @"icon_mengxiaozhi", @"cd30943149315188f208c31064c6f0b8",
             NSLocalizedString(@"sticker_huahai", nil), @"icon_huahai", @"f24643fd56b51e1b73052b12e6228e4c",  NSLocalizedString(@"sticker_huahai_tip", nil),
            
             NSLocalizedString(@"sticker_miluzhuang", nil), @"icon_milu", @"ba8dd4129cfbdaa5d3fbc20f2440ed27",  @"",
            
            NSLocalizedString(@"sticker_nyekong", nil), @"icon_yekongyanhuo", @"d2158449000686c8387035d73194c16c",  NSLocalizedString(@"sticker_yekong_tip", nil),
            
            NSLocalizedString(@"sticker_haiwang", nil), @"icon_haiwang", @"b36de00c4a0159ee21026293284b4b3d",  NSLocalizedString(@"sticker_haiwang_tip", nil),
            
           NSLocalizedString(@"sticker_tanchizhu", nil), @"icon_zhubizi", @"c2babd85f326bd7606c306466fb8c68f",NSLocalizedString(@"sticker_tanchizhu_tip", nil),
            
            NSLocalizedString(@"sticker_xinxing", nil), @"icon_fashexinxin", @"959c694d54d12b7564e841cc414e9fce", NSLocalizedString(@"sticker_xinxing_tip", nil),
            
           NSLocalizedString(@"sticker_milutoushi", nil), @"icon_milutou", @"9142ffa02be322ec339ac7dc76f7d0f3", @"",
            //      @"跳舞", @"icon_tiaowu", @"25f92cfbd89e643f902632f3a0799e62",
//            @"女神面具", @"icon_nvshenmianju", @"229c8eb45ae6a16ed510b77df8f0b794",
            
            NSLocalizedString(@"sticker_chelianmao", nil), @"icon_chelianmao", @"47127c515e75a6198c17d9833403de06", @"",
            
            NSLocalizedString(@"sticker_chuipaopao", nil), @"icon_chuipaopao", @"3c9b2bd6b54272e61db451314b102eff", @"",

            NSLocalizedString(@"sticker_mengmengxiaolu", nil), @"icon_mengmengxiaolu", @"3465b8b40b1c45476d1570656a632bea", NSLocalizedString(@"sticker_mengmengxiaolu_tip", nil),

            NSLocalizedString(@"sticker_duiwohaqi", nil), @"icon_duiwohaqi", @"ca46adae688d22d885cbc5bd0b4ab595", NSLocalizedString(@"sticker_duiwohaqi_tip", nil),

            NSLocalizedString(@"sticker_tuaixin", nil), @"icon_chuiaixin", @"4d0ca76cbb4b967cc9f8b6447c6470d8",  NSLocalizedString(@"sticker_tuaixin_tip", nil),

            NSLocalizedString(@"sticker_xiaohuanggou", nil), @"icon_xiaohuanggou", @"7841f11c0ac01478044e3f4bea3ced9d", @"",
            
            NSLocalizedString(@"sticker_woshishui", nil), @"icon_woshishui", @"725b308b77aa3349a73d72a73f4cc786", NSLocalizedString(@"sticker_woshishui_tip", nil),

            NSLocalizedString(@"sticker_nvjingling", nil), @"icon_nvjingling", @"170283d9c2f6b7a282f843e88520b117", NSLocalizedString(@"sticker_nvjingling_tip", nil),

            NSLocalizedString(@"sticker_sanzhilanmao", nil), @"icon_sanzhilanmao", @"623a287f5dd0bc5e914716778edf5834", @"",
            
            NSLocalizedString(@"sticker_huaxin", nil), @"icon_aixinxin", @"2e500c659d4ca514ca144f619add02f7", @"",

            NSLocalizedString(@"sticker_yanzhisaomiao", nil), @"icon_saomiao", @"1ada96a8bdfe03333a8192b32163e7b2", NSLocalizedString(@"sticker_yanzhisaomiao_tip", nil),

            NSLocalizedString(@"sticker_tiezhi", nil), @"icon_tiezhizhuang", @"b6cc340e0e089e2fb96c4a9f9d6ee238", @"",

            NSLocalizedString(@"sticker_caomei", nil), @"icon_caomeizhuang", @"fd5bbc5eae69875246ddde4ebe107132", @"",

            NSLocalizedString(@"sticker_xinhua", nil), @"icon_xinhualufang", @"fe664d3d2cccf9acc524885508b0ea0a", NSLocalizedString(@"sticker_xinhua_tip", nil),

//            NSLocalizedString(@"sticker_wodeshenghao", nil), @"icon_wodeshengao", @"58fdea9d870c6608e0c49b72d77ef95b", NSLocalizedString(@"sticker_wodeshenghao_tip", nil),

            NSLocalizedString(@"sticker_wpikaqiu", nil), @"icon_pikaqiu", @"973855381b6e36fc862848be4eb2d209",NSLocalizedString(@"sticker_pikaqiu_tip", nil),

            NSLocalizedString(@"sticker_pangliang", nil), @"icon_shaonvlian", @"ec7672de92b66fdcca6a0755df0ed199",  NSLocalizedString(@"sticker_panglian_tip", nil),

//            NSLocalizedString(@"sticker_caomei", nil), @"icon_xuejiumao", @"55d13281ffb818ba409ba4185f49a04d", @"",
//            @"开心喵", @"icon_xuejiumao", @"55d13281ffb818ba409ba4185f49a04d", @"",

            NSLocalizedString(@"sticker_pikaqiu2", nil), @"icon_pikaqweiba", @"d8399c8dfcf73e829cd608e549de4d7d", NSLocalizedString(@"sticker_pikaqiu2_tip", nil),

            NSLocalizedString(@"sticker_bixintu", nil), @"icon_bixintu", @"752cb99a67bc396852e95d86e5da0d66", NSLocalizedString(@"sticker_bixintu_tip", nil),

            NSLocalizedString(@"sticker_xiaohuahua", nil), @"icon_fenhongxiaohua", @"c1b490a853b627a0e0b99f3f0638d89d", NSLocalizedString(@"sticker_xiaohuahua_tip", nil),

            NSLocalizedString(@"sticker_lentu", nil), @"icon_lentubaby", @"d679ae8fbb673d0133909a67faf1423b", NSLocalizedString(@"sticker_lengtu_tip", nil),

//            @"呀", @"icon_yayaya", @"fc1be604bcbfb5286980e2403088ceb4",
            NSLocalizedString(@"sticker_yanjingli", nil), @"icon_yanjlimdaidongxi", @"006baecf13b35f5f27d099b138383484", NSLocalizedString(@"sticker_yanjingli_tip", nil),

            //@"测试", @"icon_chaiquan", @"785d609238ce49d16a450f67bdc5f3fc",
        ];
    });
}


+ (instancetype)dataManagerWithType:(BEEffectDataManagerType)type {
    BEEffectDataManager *manager = [[self alloc] init];
    manager.type = type;
    return manager;
}

+ (NSArray<BEEffectCategoryModel *> *)effectCategoryModelArray {
    static NSArray *effectCategoryModelArray;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        effectCategoryModelArray = @[
                               [BEEffectCategoryModel categoryWithType:BEEffectPanelTabBeautyFace title:NSLocalizedString(@"tab_face_beautification", nil)],
                               [BEEffectCategoryModel categoryWithType:BEEffectPanelTabBeautyReshape title:NSLocalizedString(@"tab_beauty_reshape", nil)],
                               [BEEffectCategoryModel categoryWithType:BEEffectPanelTabBeautyBody title:NSLocalizedString(@"tab_beauty_body", nil)],
                               [BEEffectCategoryModel categoryWithType:BEEffectPanelTabMakeup title:NSLocalizedString(@"tab_face_makeup", nil)],
                               [BEEffectCategoryModel categoryWithType:BEEffectPanelTabFilter title:NSLocalizedString(@"tab_filter", nil)],
                               ];
    });
    return effectCategoryModelArray;
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        _operationQueue = dispatch_queue_create("com.effect.operation.queue", DISPATCH_QUEUE_CONCURRENT);
    }
    return self;
}

#pragma mark - public

- (void)fetchDataWithCompletion:(BEEffectDataFetchCompletion)completion {
    switch (self.type) {
        case BEEffectDataManagerTypeFilter:
            [self _fetchFilterDataWithCompletion:completion];
            break;
//        case BEEffectDataManagerTypeMakeup:
//            [self _fetchMakeupDataWithCompletion:completion];
//            break;
        case BEEffectDataManagerTypeSticker:
            [self _fetchStickerDataWithCompletion:completion];
            break;
        case BEEffectDataManagerTypeAnimoji:
            [self _fetchAnimojiDataWithCompletion: completion];
            break;
        default:
            break;
    }
}

+ (NSArray<BEButtonItemModel *> *)buttonItemArray:(BEEffectNode)type {
    switch (type & ~MASK) {
        case BETypeBeautyFace:
            return [self be_beautyFaceItemArray];
        case BETypeBeautyReshape:
            return [self be_beautyReshapeItemArray];
        case BETypeBeautyBody:
            return [self be_beautyBodyItemArray];
        case BETypeMakeup:
            if ((type & MASK) == 0) {
                return [self be_makeupItemArray];
            } else {
                return [self be_makeupOptionWithType:((type & MASK) >> SUB_OFFSET)];
            }
        default:
            return @[];
    }
}

+ (NSArray<BEButtonItemModel *> *)buttonItemArrayWithDefaultIntensity {
    NSMutableArray<BEButtonItemModel *> *array = [NSMutableArray array];
    [array addObjectsFromArray:[self be_beautyFaceItemArray]];
    [array addObjectsFromArray:[self be_beautyReshapeItemArray]];
//    [array addObjectsFromArray:[self be_beautyBodyItemArray]];
    
    for (BEButtonItemModel *model in array) {
        model.intensity = [[[self defaultValue] objectForKey:@(model.ID)] floatValue];
    }
    
    return array;
}

+ (NSDictionary *)composerNodeDic {
    static dispatch_once_t onceToken;
    static NSDictionary *composerNodeDict;
    dispatch_once(&onceToken, ^{
        composerNodeDict = @{
                                      // 美颜
                                      @(BETypeBeautyFaceSharpe):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/beauty_IOS_camera"
                                           key:@"sharp"],
                                      
                                      @(BETypeBeautyFaceSmooth):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/beauty_IOS_camera"
                                           key:@"smooth"],
                                      
                                      @(BETypeBeautyFaceWhiten):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/beauty_IOS_camera"
                                           key:@"whiten"],                                
                                      
                                      @(BETypeBeautyFaceBrightenEye):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/beauty_4Items"                                           key:@"BEF_BEAUTY_BRIGHTEN_EYE"],
                                      
                                      @(BETypeBeautyFaceRemovePouch):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/beauty_4Items"                                           key:@"BEF_BEAUTY_REMOVE_POUCH"],
                                      
                                      @(BETypeBeautyFaceRemoveSmileFolds):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/beauty_4Items"                                           key:@"BEF_BEAUTY_SMILES_FOLDS"],
                                      
                                      @(BETypeBeautyFaceWhitenTeeth):
                                               [[BEComposerNodeModel alloc]
                                                initWithPath:@"/beauty_4Items"
                                                key:@"BEF_BEAUTY_WHITEN_TEETH"],
                                      
                                      // 美形
                                      @(BETypeBeautyReshapeFaceOverall):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/reshape_camera"
                                           key:@"Internal_Deform_Overall"],
                                      
                                      @(BETypeBeautyReshapeFaceCut):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/reshape_camera"
                                           key:@"Internal_Deform_CutFace"],
                                      
                                      @(BETypeBeautyReshapeFaceSmall):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/reshape_camera"
                                           key:@"Internal_Deform_Face"],
                                      
                                      @(BETypeBeautyReshapeEye):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/reshape_camera"
                                           key:@"Internal_Deform_Eye"],
                                      
                                      @(BETypeBeautyReshapeEyeRotate):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/reshape_camera"
                                           key:@"Internal_Deform_RotateEye"],
                                      
                                      @(BETypeBeautyReshapeCheek):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/reshape_camera"
                                           key:@"Internal_Deform_Zoom_Cheekbone"],
                                      
                                      @(BETypeBeautyReshapeJaw):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/reshape_camera"
                                           key:@"Internal_Deform_Zoom_Jawbone"],
                                      
                                      @(BETypeBeautyReshapeNoseLean):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/reshape_camera"
                                           key:@"Internal_Deform_Nose"],
                                      
                                      @(BETypeBeautyReshapeNoseLong):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/reshape_camera"
                                           key:@"Internal_Deform_MovNose"],
                                      
                                      @(BETypeBeautyReshapeChin):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/reshape_camera"
                                           key:@"Internal_Deform_Chin"],
                                      
                                      @(BETypeBeautyReshapeForehead):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/reshape_camera"
                                           key:@"Internal_Deform_Forehead"],
                                      
                                      @(BETypeBeautyReshapeMouthZoom):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/reshape_camera"
                                           key:@"Internal_Deform_ZoomMouth"],
                                      
                                      @(BETypeBeautyReshapeMouthSmile):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/reshape_camera"
                                           key:@"Internal_Deform_MouthCorner"],
                                      
                                      @(BETypeBeautyReshapeEyeSpacing):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/reshape_camera"
                                           key:@"Internal_Eye_Spacing"],
                                      
                                      @(BETypeBeautyReshapeEyeMove):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/reshape_camera"
                                           key:@"Internal_Deform_Eye_Move"],
                                      
                                      @(BETypeBeautyReshapeMouthMove):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/reshape_camera"
                                           key:@"Internal_Deform_MovMouth"],
                                      
                                      
                                      
                                      // 美体
                                      @(BETypeBeautyBodyThin):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/body/thin"
                                           key:@""],
                                      
                                      @(BETypeBeautyBodyLegLong):
                                          [[BEComposerNodeModel alloc]
                                           initWithPath:@"/body/longleg"
                                           key:@""],
                                      
                                      // 美妆
                                      @(BETypeMakeupLip):
                                          [BEComposerNodeModel
                                           initWithPathArray:@[@"/lip/fuguhong", @"/lip/shaonvfen", @"/lip/yuanqiju", @"/lip/xiyouse", @"/lip/xiguahong", @"/lip/sironghong", @"/lip/zangjuse", @"/lip/meizise", @"/lip/shanhuse", @"/lip/doushafen" ]
                                           keyArray:@[@"Internal_Makeup_Lips", @"", @"", @""]],
                                      
                                      @(BETypeMakeupBlusher):
                                          [BEComposerNodeModel
                                           initWithPathArray:@[@"/blush/weixun", @"/blush/richang", @"/blush/mitao", @"/blush/tiancheng", @"/blush/qiaopi", @"/blush/xinji", @"/blush/shaishang"]
                                           keyArray:@[@"Internal_Makeup_Blusher", @"", @"", @""]],
                                      
        //                              @(BETypeMakeupEyelash):
        //                                  [BEComposerNodeModel
        //                                   initWithPathArray:@[@"/eyelash/nongmi", @"/eyelash/shanxing", @"/eyelash/wumei", @"/eyelash/wumei"]
        //                                   keyArray:@[@"Internal_Makeup_Eye", @"", @"", @""]],
                                      
                                      
                                        @(BETypeMakeupFacial):
                                            [BEComposerNodeModel
                                             initWithPathArray:@[@"/facial/xiurong01", @"/facial/xiurong02", @"/facial/xiurong03", @"/facial/xiurong04"]
                                             keyArray:@[@"Internal_Makeup_Facial", @"", @"", @""]],
                                      
                                      @(BETypeMakeupPupil):
                                          [BEComposerNodeModel
                                           initWithPathArray:@[@"/pupil/hunxuezong", @"/pupil/kekezong", @"/pupil/mitaofen", @"/pupil/shuiguanghei", @"/pupil/xingkonglan", @"/pupil/chujianhui"]
                                           keyArray:@[@"Internal_Makeup_Pupil", @"", @"", @""]],
                                      
                                      @(BETypeMakeupHair):
                                          [BEComposerNodeModel
                                           initWithPathArray:@[@"/hair/anlan", @"/hair/molv", @"/hair/shenzong"]
                                           keyArray:@[@"", @"", @""]],
                                      
                                      @(BETypeMakeupEyeshadow):
                                          [BEComposerNodeModel
                                           initWithPathArray:@[@"/eyeshadow/dadizong", @"/eyeshadow/wanxiahong", @"/eyeshadow/shaonvfen",@"/eyeshadow/qizhifen",@"/eyeshadow/meizihong",@"/eyeshadow/jiaotangzong",@"/eyeshadow/yuanqiju",@"/eyeshadow/naichase"]
                                           keyArray:@[@"Internal_Makeup_Eye", @"", @"", @""]],
                                      
                                      @(BETypeMakeupEyebrow):
                                          [BEComposerNodeModel
                                           initWithPathArray:@[@"/eyebrow/BR01", @"/eyebrow/BK01", @"/eyebrow/BK02", @"/eyebrow/BK03"]
                                           keyArray:@[@"Internal_Makeup_Brow", @"", @"", @""]],
                                      
                                      };
    });
    return composerNodeDict;
}

+ (NSDictionary<NSNumber *,NSNumber *> *)defaultValue {
    static dispatch_once_t onceToken;
    static NSDictionary *dic;
    dispatch_once(&onceToken, ^{
        dic = @{
            // face
            @(BETypeBeautyFaceSmooth): @(0.6),
            @(BETypeBeautyFaceWhiten): @(0.3),
            @(BETypeBeautyFaceSharpe): @(0.7),
            // reshape
            @(BETypeBeautyReshapeFaceOverall): @(0.5),
            @(BETypeBeautyReshapeFaceSmall): @(0.0),
            @(BETypeBeautyReshapeFaceCut): @(0.0),
            @(BETypeBeautyReshapeEye): @(0.3),
            @(BETypeBeautyReshapeEyeRotate): @(0.0),
            @(BETypeBeautyReshapeCheek): @(0.0),
            @(BETypeBeautyReshapeJaw): @(0.0),
            @(BETypeBeautyReshapeNoseLean): @(0.0),
            @(BETypeBeautyReshapeNoseLong): @(0.25),
            @(BETypeBeautyReshapeChin): @(0.0),
            @(BETypeBeautyReshapeForehead): @(0.0),
            @(BETypeBeautyReshapeMouthZoom): @(0.0),
            @(BETypeBeautyReshapeMouthSmile): @(0.0),
            @(BETypeBeautyReshapeEyeSpacing): @(0.0),
            @(BETypeBeautyReshapeEyeMove): @(0.0),
            @(BETypeBeautyReshapeMouthMove): @(0.0),
            // body
            @(BETypeBeautyBodyThin): @(1.0),
            @(BETypeBeautyBodyLegLong): @(1.0),
            // makeup
            @(BETypeMakeupLip): @(0.3),
            @(BETypeMakeupHair): @(0.5),
            @(BETypeMakeupBlusher): @(0.3),
            @(BETypeMakeupFacial): @(0.3),
            @(BETypeMakeupEyebrow): @(0.3),
            @(BETypeMakeupEyeshadow): @(0.4),
            @(BETypeMakeupPupil): @(0.4),
            // filter
            @(BETypeFilter): @(0.8)
        };
    });
    return dic;
}

#pragma mark - private
+ (NSArray<BEButtonItemModel *> *)be_beautyFaceItemArray {
    static NSArray<BEButtonItemModel *> *array;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        array = @[
                  [BEButtonItemModel
                   initWithID:BETypeClose
                   selectImg:@"iconCloseButtonSelected.png"
                   unselectImg:@"iconCloseButtonNormal.png"
                   title:NSLocalizedString(@"close", nil)
                   desc:@""],
                  
                  [BEButtonItemModel
                   initWithID:BETypeBeautyFaceSmooth
                   selectImg:@"iconFaceBeautySkinSelected.png"
                   unselectImg:@"iconFaceBeautySkinNormal.png"
                   title:NSLocalizedString(@"beauty_face_smooth", nil)
                   desc:@""],
                  
                  [BEButtonItemModel
                   initWithID:BETypeBeautyFaceWhiten
                   selectImg:@"iconFaceBeautyWhiteningSelected.png"
                   unselectImg:@"iconFaceBeautyWhiteningNormal.png"
                   title:NSLocalizedString(@"beauty_face_whiten", nil)
                   desc:@""],
                  
                  [BEButtonItemModel
                   initWithID:BETypeBeautyFaceSharpe
                   selectImg:@"iconFaceBeautySharpSelected.png"
                   unselectImg:@"iconFaceBeautySharpNormal.png"
                   title:NSLocalizedString(@"beauty_face_sharpen", nil)
                   desc:@""],
                  
                  [BEButtonItemModel
                   initWithID:BETypeBeautyFaceBrightenEye
                   selectImg:@"iconFaceBeautySkinSelected.png"
                   unselectImg:@"iconFaceBeautySkinNormal.png"
                   title:NSLocalizedString(@"beauty_face_brighten_eye", nil)
                   desc:@""],
                  
                  [BEButtonItemModel
                   initWithID:BETypeBeautyFaceRemovePouch
                   selectImg:@"iconFaceBeautySkinSelected.png"
                   unselectImg:@"iconFaceBeautySkinNormal.png"
                   title:NSLocalizedString(@"beauty_face_remove_pouch", nil)
                   desc:@""],
                  
                  [BEButtonItemModel
                   initWithID:BETypeBeautyFaceRemoveSmileFolds
                   selectImg:@"iconFaceBeautySharpSelected.png"
                   unselectImg:@"iconFaceBeautySharpNormal.png"
                   title:NSLocalizedString(@"beauty_face_smile_folds", nil)
                   desc:@""],
                  
                  [BEButtonItemModel
                   initWithID:BETypeBeautyFaceWhitenTeeth
                   selectImg:@"iconFaceBeautySharpSelected.png"
                   unselectImg:@"iconFaceBeautySharpNormal.png"
                   title:NSLocalizedString(@"beauty_face_whiten_teeth", nil)
                   desc:@""],
                  
                  ];
        
                 
    });
    return array;
}

+ (NSArray<BEButtonItemModel *> *)be_beautyReshapeItemArray {
    static NSArray<BEButtonItemModel *> *array;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        array = @[
                  [BEButtonItemModel
                   initWithID:BETypeClose
                   selectImg:@"iconCloseButtonSelected.png"
                   unselectImg:@"iconCloseButtonNormal.png"
                   title:NSLocalizedString(@"close", nil)
                   desc:@""],
                  [BEButtonItemModel
                   initWithID:BETypeBeautyReshapeFaceOverall
                   selectImg:@"iconFaceBeautyLiftingSelected.png"
                   unselectImg:@"iconFaceBeautyLiftingNormal.png"
                   title:NSLocalizedString(@"beauty_reshape_face_overall", nil)
                   desc:@""],
                  [BEButtonItemModel
                   initWithID:BETypeBeautyReshapeFaceCut
                   selectImg:@"iconBeautyReshapeFaceCutSelect"
                   unselectImg:@"iconBeautyReshapeFaceCutNormal"
                   title:NSLocalizedString(@"beauty_reshape_face_cut", nil)
                   desc:@""],
                  [BEButtonItemModel
                   initWithID:BETypeBeautyReshapeFaceSmall
                   selectImg:@"iconBeautyReshapeFaceSmallSelect"
                   unselectImg:@"iconBeautyReshapeFaceSmallNormal"
                   title:NSLocalizedString(@"beauty_reshape_face_small", nil)
                   desc:@""],
                  [BEButtonItemModel
                   initWithID:BETypeBeautyReshapeEye
                   selectImg:@"iconFaceBeautyBigEyeSelected.png"
                   unselectImg:@"iconFaceBeautyBigEyeNormal.png"
                   title:NSLocalizedString(@"beauty_reshape_eye", nil)
                   desc:@""],
                  [BEButtonItemModel
                   initWithID:BETypeBeautyReshapeEyeRotate
                   selectImg:@"iconBeautyReshapeEyeRotateSelect"
                   unselectImg:@"iconBeautyReshapeEyeRotateNormal"
                   title:NSLocalizedString(@"beauty_reshape_eye_rotate", nil)
                   desc:@""],
                  [BEButtonItemModel
                   initWithID:BETypeBeautyReshapeCheek
                   selectImg:@"iconBeautyReshapeCheekSelect"
                   unselectImg:@"iconBeautyReshapeCheekNormal"
                   title:NSLocalizedString(@"beauty_reshape_cheek", nil)
                   desc:@""],
                  [BEButtonItemModel
                   initWithID:BETypeBeautyReshapeJaw
                   selectImg:@"iconBeautyReshapeJawSelect"
                   unselectImg:@"iconBeautyReshapeJawNormal"
                   title:NSLocalizedString(@"beauty_reshape_jaw", nil)
                   desc:@""],
                  [BEButtonItemModel
                   initWithID:BETypeBeautyReshapeNoseLean
                   selectImg:@"iconBeautyReshapeNoseLeanSelect"
                   unselectImg:@"iconBeautyReshapeNoseLeanNormal"
                   title:NSLocalizedString(@"beauty_reshape_nose_lean", nil)
                   desc:@""],
                  [BEButtonItemModel
                   initWithID:BETypeBeautyReshapeNoseLong
                   selectImg:@"iconBeautyReshapeNoseLongSelect"
                   unselectImg:@"iconBeautyReshapeNoseLongNormal"
                   title:NSLocalizedString(@"beauty_reshape_nose_long", nil)
                   desc:@""],
                  [BEButtonItemModel
                   initWithID:BETypeBeautyReshapeChin
                   selectImg:@"iconBeautyReshapeChinSelect"
                   unselectImg:@"iconBeautyReshapeChinNormal"
                   title:NSLocalizedString(@"beauty_reshape_chin", nil)
                   desc:@""],
                  [BEButtonItemModel
                   initWithID:BETypeBeautyReshapeForehead
                   selectImg:@"iconBeautyReshapeForeheadSelect"
                   unselectImg:@"iconBeautyReshapeForeheadNormal"
                   title:NSLocalizedString(@"beauty_reshape_forehead", nil)
                   desc:@""],
                  [BEButtonItemModel
                   initWithID:BETypeBeautyReshapeMouthZoom
                   selectImg:@"iconBeautyReshapeMouthZoomSelect"
                   unselectImg:@"iconBeautyReshapeMouthZoomNormal"
                   title:NSLocalizedString(@"beauty_reshape_mouth_zoom", nil)
                   desc:@""],
                  [BEButtonItemModel
                   initWithID:BETypeBeautyReshapeMouthSmile
                   selectImg:@"iconBeautyReshapeMouthSmileSelect"
                   unselectImg:@"iconBeautyReshapeMouthSmileNormal"
                   title:NSLocalizedString(@"beauty_reshape_mouth_smile", nil)
                   desc:@""],
                 [BEButtonItemModel
                 initWithID:BETypeBeautyReshapeEyeSpacing
                 selectImg:@"iconBeautyReshapeEyeRotateSelect"
                 unselectImg:@"iconBeautyReshapeEyeRotateNormal"
                 title:NSLocalizedString(@"beauty_reshape_eye_spacing", nil)
                  desc:@""],
                [BEButtonItemModel
                initWithID:BETypeBeautyReshapeEyeMove
                selectImg:@"iconBeautyReshapeEyeRotateSelect"
                unselectImg:@"iconBeautyReshapeEyeRotateNormal"
                title:NSLocalizedString(@"beauty_reshape_eye_move", nil)
                 desc:@""],
                [BEButtonItemModel
                initWithID:BETypeBeautyReshapeMouthMove
                selectImg:@"iconBeautyReshapeMouthZoomSelect"
                unselectImg:@"iconBeautyReshapeMouthZoomNormal"
                title:NSLocalizedString(@"beauty_reshape_mouth_move", nil)
                 desc:@""],
            ];
    });
    return array;
}

+ (NSArray<BEButtonItemModel *> *)be_beautyBodyItemArray {
    static NSArray<BEButtonItemModel *> *array;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        array = @[
                  [BEButtonItemModel
                   initWithID:BETypeClose
                   selectImg:@"iconCloseButtonSelected.png"
                   unselectImg:@"iconCloseButtonNormal.png"
                   title:NSLocalizedString(@"close", nil)
                   desc:@""],
                  
                  [BEButtonItemModel
                   initWithID:BETypeBeautyBodyThin
                   selectImg:@"iconBeautyBodyThinSelect"
                   unselectImg:@"iconBeautyBodyThinNormal"
                   title:NSLocalizedString(@"beauty_body_thin", nil)
                   desc:@""],
                  
                  [BEButtonItemModel
                   initWithID:BETypeBeautyBodyLegLong
                   selectImg:@"iconBeautyBodyLegLongSelect"
                   unselectImg:@"iconBeautyBodyLegLongNormal"
                   title:NSLocalizedString(@"beauty_body_leg_long", nil)
                   desc:@""],
                  
                  ];
    });
    return array;
}

+ (NSArray<BEButtonItemModel *> *)be_makeupItemArray {
    static NSArray<BEButtonItemModel *> *array;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        array = @[
                  [BEButtonItemModel
                   initWithID:BETypeClose
                   selectImg:@"iconCloseButtonSelected.png"
                   unselectImg:@"iconCloseButtonNormal.png"
                   title:NSLocalizedString(@"close", nil)
                   desc:@""],
                  
                  [BEButtonItemModel
                   initWithID:BETypeMakeupBlusher
                   selectImg:@"iconFaceMakeUpBlusherSelected.png"
                   unselectImg:@"iconFaceMakeUpBlusherNormal.png"
                   title:NSLocalizedString(@"makeup_blusher", nil)
                   desc:@""],
                  
                  [BEButtonItemModel
                   initWithID:BETypeMakeupLip
                   selectImg:@"iconFaceMakeUpLipsSelected.png"
                   unselectImg:@"iconFaceMakeUpLipsNormal.png"
                   title:NSLocalizedString(@"makeup_lip", nil)
                   desc:@""],
                  
//                  [BEButtonItemModel
//                   initWithID:BETypeMakeupEyelash
//                   selectImg:@"iconFaceMakeUpEyelashSelected.png"
//                   unselectImg:@"iconFaceMakeUpEyelashNormal.png"
//                   title:NSLocalizedString(@"makeup_eyelash", nil)
//                   desc:@""],

                  [BEButtonItemModel
                        initWithID:BETypeMakeupFacial
                        selectImg:@"iconFaceMakeUpFacialSelected.png"
                        unselectImg:@"iconFaceMakeUpFacialNormal.png"
                        title:NSLocalizedString(@"makeup_facial", nil)
                        desc:@""],
                  
                  [BEButtonItemModel
                   initWithID:BETypeMakeupPupil
                   selectImg:@"iconFaceMakeUpPupilSelected.png"
                   unselectImg:@"iconFaceMakeUpPupilNormal.png"
                   title:NSLocalizedString(@"makeup_pupil", nil)
                   desc:@""],
                  
                  [BEButtonItemModel
                   initWithID:BETypeMakeupHair
                   selectImg:@"iconHairSelected.png"
                   unselectImg:@"iconHairNormal.png"
                   title:NSLocalizedString(@"makeup_hair", nil)
                   desc:@""],
                  
                  [BEButtonItemModel
                   initWithID:BETypeMakeupEyeshadow
                   selectImg:@"iconFaceMakeUpEyeshadowSelected.png"
                   unselectImg:@"iconFaceMakeUpEyeshadowNormal.png"
                   title:NSLocalizedString(@"makeup_eyeshadow", nil)
                   desc:@""],
                  
                  [BEButtonItemModel
                   initWithID:BETypeMakeupEyebrow
                   selectImg:@"iconFaceMakeUpEyebrowSelected.png"
                   unselectImg:@"iconFaceMakeUpEyebrowNormal.png"
                   title:NSLocalizedString(@"makeup_eyebrow", nil)
                   desc:@""],
                  
                  ];
    });
    return array;
}

+ (NSArray<BEButtonItemModel *> *)be_makeupOptionWithType:(NSInteger)type {
    static NSArray<NSArray<BEButtonItemModel *> *> *array;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        array = @[
                  
                  @[
                      [BEButtonItemModel
                       initWithID:BETypeClose
                       selectImg:@"iconCloseButtonSelected"
                       unselectImg:@"iconCloseButtonNormal"
                       title:NSLocalizedString(@"close", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupLip_1
                       selectImg:@"iconFaceMakeUpLipsSelected"
                       unselectImg:@"iconFaceMakeUpLipsNormal"
                       title:NSLocalizedString(@"lip_fuguhong", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupLip_2
                       selectImg:@"iconFaceMakeUpLipsSelected"
                       unselectImg:@"iconFaceMakeUpLipsNormal"
                       title:NSLocalizedString(@"lip_shaonvfen", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupLip_3
                       selectImg:@"iconFaceMakeUpLipsSelected"
                       unselectImg:@"iconFaceMakeUpLipsNormal"
                       title:NSLocalizedString(@"lip_yuanqiju", nil)
                       desc:@""],

                      [BEButtonItemModel
                       initWithID:BETypeMakeupLip_4
                       selectImg:@"iconFaceMakeUpLipsSelected"
                       unselectImg:@"iconFaceMakeUpLipsNormal"
                       title:NSLocalizedString(@"lip_xiyouse", nil)
                       desc:@""],

                      [BEButtonItemModel
                       initWithID:BETypeMakeupLip_5
                       selectImg:@"iconFaceMakeUpLipsSelected"
                       unselectImg:@"iconFaceMakeUpLipsNormal"
                       title:NSLocalizedString(@"lip_xiguahong", nil)
                       desc:@""],

                      [BEButtonItemModel
                       initWithID:BETypeMakeupLip_6
                       selectImg:@"iconFaceMakeUpLipsSelected"
                       unselectImg:@"iconFaceMakeUpLipsNormal"
                       title:NSLocalizedString(@"lip_sironghong", nil)
                       desc:@""],

                      [BEButtonItemModel
                       initWithID:BETypeMakeupLip_7
                       selectImg:@"iconFaceMakeUpLipsSelected"
                       unselectImg:@"iconFaceMakeUpLipsNormal"
                       title:NSLocalizedString(@"lip_zangjuse", nil)
                       desc:@""],

                      [BEButtonItemModel
                       initWithID:BETypeMakeupLip_8
                       selectImg:@"iconFaceMakeUpLipsSelected"
                       unselectImg:@"iconFaceMakeUpLipsNormal"
                       title:NSLocalizedString(@"lip_meizise", nil)
                       desc:@""],

                      [BEButtonItemModel
                          initWithID:BETypeMakeupLip_9
                          selectImg:@"iconFaceMakeUpLipsSelected"
                          unselectImg:@"iconFaceMakeUpLipsNormal"
                          title:NSLocalizedString(@"lip_shanhuse", nil)
                          desc:@""],

                      [BEButtonItemModel
                          initWithID:BETypeMakeupLip_10
                          selectImg:@"iconFaceMakeUpLipsSelected"
                          unselectImg:@"iconFaceMakeUpLipsNormal"
                          title:NSLocalizedString(@"lip_doushafen", nil)
                          desc:@""],

                      ],
                  
                  @[
                      [BEButtonItemModel
                       initWithID:BETypeClose
                       selectImg:@"iconCloseButtonSelected.png"
                       unselectImg:@"iconCloseButtonNormal.png"
                       title:NSLocalizedString(@"close", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupBlusher_1
                       selectImg:@"iconFaceMakeUpBlusherSelected"
                       unselectImg:@"iconFaceMakeUpBlusherNormal"
                       title:NSLocalizedString(@"blusher_weixun", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupBlusher_2
                       selectImg:@"iconFaceMakeUpBlusherSelected"
                       unselectImg:@"iconFaceMakeUpBlusherNormal"
                       title:NSLocalizedString(@"blusher_richang", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupBlusher_3
                       selectImg:@"iconFaceMakeUpBlusherSelected"
                       unselectImg:@"iconFaceMakeUpBlusherNormal"
                       title:NSLocalizedString(@"blusher_mitao", nil)
                       desc:@""],

                      [BEButtonItemModel
                       initWithID:BETypeMakeupBlusher_4
                       selectImg:@"iconFaceMakeUpBlusherSelected"
                       unselectImg:@"iconFaceMakeUpBlusherNormal"
                       title:NSLocalizedString(@"blusher_tiancheng", nil)
                       desc:@""],

                      [BEButtonItemModel
                         initWithID:BETypeMakeupBlusher_5
                         selectImg:@"iconFaceMakeUpBlusherSelected"
                         unselectImg:@"iconFaceMakeUpBlusherNormal"
                         title:NSLocalizedString(@"blusher_qiaopi", nil)
                         desc:@""],

                      [BEButtonItemModel
                         initWithID:BETypeMakeupBlusher_6
                         selectImg:@"iconFaceMakeUpBlusherSelected"
                         unselectImg:@"iconFaceMakeUpBlusherNormal"
                         title:NSLocalizedString(@"blusher_xinji", nil)
                         desc:@""],

                      [BEButtonItemModel
                         initWithID:BETypeMakeupBlusher_7
                         selectImg:@"iconFaceMakeUpBlusherSelected"
                         unselectImg:@"iconFaceMakeUpBlusherNormal"
                         title:NSLocalizedString(@"blusher_shaishang", nil)
                         desc:@""],

                      ],
                  
                  @[
                      [BEButtonItemModel
                       initWithID:BETypeClose
                       selectImg:@"iconCloseButtonSelected"
                       unselectImg:@"iconCloseButtonNormal"
                       title:NSLocalizedString(@"close", nil)
                       desc:@""],


                      [BEButtonItemModel
                       initWithID:BETypeMakeupEyelash_1
                       selectImg:@"iconFaceMakeUpEyelashSelected"
                       unselectImg:@"iconFaceMakeUpEyelashNormal"
                       title:NSLocalizedString(@"eyelash_nongmi", nil)
                       desc:@""],

                      [BEButtonItemModel
                       initWithID:BETypeMakeupEyelash_2
                       selectImg:@"iconFaceMakeUpEyelashSelected"
                       unselectImg:@"iconFaceMakeUpEyelashNormal"
                       title:NSLocalizedString(@"eyelash_shanxing", nil)
                       desc:@""],

                      [BEButtonItemModel
                       initWithID:BETypeMakeupEyelash_3
                       selectImg:@"iconFaceMakeUpEyelashSelected"
                       unselectImg:@"iconFaceMakeUpEyelashNormal"
                       title:NSLocalizedString(@"eyelash_wumei", nil)
                       desc:@""],

                      [BEButtonItemModel
                              initWithID:BETypeMakeupEyelash_4
                              selectImg:@"iconFaceMakeUpEyelashSelected"
                              unselectImg:@"iconFaceMakeUpEyelashNormal"
                              title:NSLocalizedString(@"eyelash_wumei", nil)
                              desc:@""],


                      ],




                  @[
                      [BEButtonItemModel
                       initWithID:BETypeClose
                       selectImg:@"iconCloseButtonSelected"
                       unselectImg:@"iconCloseButtonNormal"
                       title:NSLocalizedString(@"close", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupPupil_1
                       selectImg:@"iconFaceMakeUpPupilSelected"
                       unselectImg:@"iconFaceMakeUpPupilNormal"
                       title:NSLocalizedString(@"pupil_hunxuezong", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupPupil_2
                       selectImg:@"iconFaceMakeUpPupilSelected"
                       unselectImg:@"iconFaceMakeUpPupilNormal"
                       title:NSLocalizedString(@"pupil_kekezong", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupPupil_3
                       selectImg:@"iconFaceMakeUpPupilSelected"
                       unselectImg:@"iconFaceMakeUpPupilNormal"
                       title:NSLocalizedString(@"pupil_mitaofen", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupPupil_4
                       selectImg:@"iconFaceMakeUpPupilSelected"
                       unselectImg:@"iconFaceMakeUpPupilNormal"
                       title:NSLocalizedString(@"pupil_shuiguanghei", nil)
                       desc:@""],

                      [BEButtonItemModel
                         initWithID:BETypeMakeupPupil_5
                         selectImg:@"iconFaceMakeUpPupilSelected"
                         unselectImg:@"iconFaceMakeUpPupilNormal"
                         title:NSLocalizedString(@"pupil_xingkonglan", nil)
                         desc:@""],

                      [BEButtonItemModel
                         initWithID:BETypeMakeupPupil_6
                         selectImg:@"iconFaceMakeUpPupilSelected"
                         unselectImg:@"iconFaceMakeUpPupilNormal"
                         title:NSLocalizedString(@"pupil_chujianhui", nil)
                         desc:@""],

                      ],
                  
                  @[
                      [BEButtonItemModel
                       initWithID:BETypeClose
                       selectImg:@"iconCloseButtonSelected"
                       unselectImg:@"iconCloseButtonNormal"
                       title:NSLocalizedString(@"close", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupHair_1
                       selectImg:@"iconHairSelected"
                       unselectImg:@"iconHairNormal"
                       title:NSLocalizedString(@"hair_anlan", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupHair_2
                       selectImg:@"iconHairSelected"
                       unselectImg:@"iconHairNormal"
                       title:NSLocalizedString(@"hair_molv", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupHair_3
                       selectImg:@"iconHairSelected"
                       unselectImg:@"iconHairNormal"
                       title:NSLocalizedString(@"hair_shenzong", nil)
                       desc:@""],
                      
                      ],
                  
                  @[
                      [BEButtonItemModel
                       initWithID:BETypeClose
                       selectImg:@"iconCloseButtonSelected"
                       unselectImg:@"iconCloseButtonNormal"
                       title:NSLocalizedString(@"close", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupEyeshadow_1
                       selectImg:@"iconFaceMakeUpEyeshadowSelected"
                       unselectImg:@"iconFaceMakeUpEyeshadowNormal"
                       title:NSLocalizedString(@"eye_dadizong", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupEyeshadow_2
                       selectImg:@"iconFaceMakeUpEyeshadowSelected"
                       unselectImg:@"iconFaceMakeUpEyeshadowNormal"
                       title:NSLocalizedString(@"eye_wanxiahong", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupEyeshadow_3
                       selectImg:@"iconFaceMakeUpEyeshadowSelected"
                       unselectImg:@"iconFaceMakeUpEyeshadowNormal"
                       title:NSLocalizedString(@"eye_shaonvfen", nil)
                       desc:@""],

                      [BEButtonItemModel
                       initWithID:BETypeMakeupEyeshadow_4
                       selectImg:@"iconFaceMakeUpEyeshadowSelected"
                       unselectImg:@"iconFaceMakeUpEyeshadowNormal"
                       title:NSLocalizedString(@"eye_qizhifen", nil)
                       desc:@""],

                      [BEButtonItemModel
                          initWithID:BETypeMakeupEyeshadow_5
                          selectImg:@"iconFaceMakeUpEyeshadowSelected"
                          unselectImg:@"iconFaceMakeUpEyeshadowNormal"
                          title:NSLocalizedString(@"eye_meizihong", nil)
                          desc:@""],


                      [BEButtonItemModel
                          initWithID:BETypeMakeupEyeshadow_6
                          selectImg:@"iconFaceMakeUpEyeshadowSelected"
                          unselectImg:@"iconFaceMakeUpEyeshadowNormal"
                          title:NSLocalizedString(@"eye_jiaotangzong", nil)
                          desc:@""],


                      [BEButtonItemModel
                          initWithID:BETypeMakeupEyeshadow_7
                          selectImg:@"iconFaceMakeUpEyeshadowSelected"
                          unselectImg:@"iconFaceMakeUpEyeshadowNormal"
                          title:NSLocalizedString(@"eye_yuanqiju", nil)
                          desc:@""],

                      [BEButtonItemModel
                          initWithID:BETypeMakeupEyeshadow_8
                          selectImg:@"iconFaceMakeUpEyeshadowSelected"
                          unselectImg:@"iconFaceMakeUpEyeshadowNormal"
                          title:NSLocalizedString(@"eye_naichase", nil)
                          desc:@""],

                      ],
                  
                  @[
                      [BEButtonItemModel
                       initWithID:BETypeClose
                       selectImg:@"iconCloseButtonSelected"
                       unselectImg:@"iconCloseButtonNormal"
                       title:NSLocalizedString(@"close", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                          initWithID:BETypeMakeupEyebrow_1
                          selectImg:@"iconFaceMakeUpEyebrowSelected"
                          unselectImg:@"iconFaceMakeUpEyebrowNormal"
                          title:NSLocalizedString(@"eyebrow_BR01", nil)
                          desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupEyebrow_2
                       selectImg:@"iconFaceMakeUpEyebrowSelected"
                       unselectImg:@"iconFaceMakeUpEyebrowNormal"
                       title:NSLocalizedString(@"eyebrow_BK01", nil)
                       desc:@""],
                      
                      [BEButtonItemModel
                       initWithID:BETypeMakeupEyebrow_3
                       selectImg:@"iconFaceMakeUpEyebrowSelected"
                       unselectImg:@"iconFaceMakeUpEyebrowNormal"
                       title:NSLocalizedString(@"eyebrow_BK02", nil)
                       desc:@""],

                      [BEButtonItemModel
                       initWithID:BETypeMakeupEyebrow_4
                       selectImg:@"iconFaceMakeUpEyebrowSelected"
                       unselectImg:@"iconFaceMakeUpEyebrowNormal"
                       title:NSLocalizedString(@"eyebrow_BK03", nil)
                       desc:@""],
                      
                      ],
                  @[
                      [BEButtonItemModel
                       initWithID:BETypeClose
                       selectImg:@"iconCloseButtonSelected"
                       unselectImg:@"iconCloseButtonNormal"
                       title:NSLocalizedString(@"close", nil)
                       desc:@""],


                      [BEButtonItemModel
                       initWithID:BETypeMakeupFacial_1
                       selectImg:@"iconFaceMakeUpFacialSelected"
                       unselectImg:@"iconFaceMakeUpFacialNormal"
                       title:NSLocalizedString(@"facial_xiurong1", nil)
                       desc:@""],

                      [BEButtonItemModel
                       initWithID:BETypeMakeupFacial_2
                       selectImg:@"iconFaceMakeUpFacialSelected"
                       unselectImg:@"iconFaceMakeUpFacialNormal"
                       title:NSLocalizedString(@"facial_xiurong2", nil)
                       desc:@""],

                      [BEButtonItemModel
                       initWithID:BETypeMakeupFacial_3
                       selectImg:@"iconFaceMakeUpFacialSelected"
                       unselectImg:@"iconFaceMakeUpFacialNormal"
                       title:NSLocalizedString(@"facial_xiurong3", nil)
                       desc:@""],

                      [BEButtonItemModel
                      initWithID:BETypeMakeupFacial_4
                      selectImg:@"iconFaceMakeUpFacialSelected"
                      unselectImg:@"iconFaceMakeUpFacialNormal"
                      title:NSLocalizedString(@"facial_xiurong4", nil)
                      desc:@""],
                  ],

                  ];
    });
    return array[type - 1];
}

#pragma mark - Private

- (void) _fetchAnimojiDataWithCompletion:(BEEffectDataFetchCompletion)completion {
    static NSArray* _animojiArray = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _animojiArray = @[
        NSLocalizedString(@"animoji_boy", nil), @"icon_change_face", @"animoji_boy", @"",
        NSLocalizedString(@"animoji_girl", nil), @"icon_change_face", @"mm", @""
        ];
    });
    
    
    @weakify(self);
    [self runAsync:^{
        @strongify(self);
        
        NSString *stickerPath = [[BEResourceHelper new] stickerPath:@""];
        NSError *error = nil;
        NSMutableArray <BEEffectStickerGroup *> *stickersGroupArr = [NSMutableArray array];
        BEEffectResponseModel *responseModel = [BEEffectResponseModel new];
        
        NSMutableArray <BEEffectSticker*> * stickerArray = [NSMutableArray array];
        
        BEEffectSticker *clear = [BEEffectSticker new];
        clear.title = NSLocalizedString(@"filter_normal", nil);
        clear.filePath = @"";
        clear.imageName = @"icon_clear";
        clear.toastString = @"";
        
        [stickerArray addObject:clear];

        for (int i = 0; i < _animojiArray.count; i += 4){
            BEEffectSticker *sticker = [BEEffectSticker new];
            sticker.title = _animojiArray[i];
            sticker.filePath = [stickerPath stringByAppendingPathComponent: _animojiArray[i + 2]];
            sticker.imageName = _animojiArray[i + 1];
            sticker.toastString = _animojiArray[i + 3];

            [stickerArray addObject:sticker];
        }
        
        BEEffectStickerGroup *group = [BEEffectStickerGroup new];
        group.title = @"animoji";
        group.stickers = stickerArray.copy;
        
        [stickersGroupArr addObject:group];
        responseModel.stickerGroup = stickersGroupArr;
//        self.responseModel = responseModel;
        
        dispatch_async(dispatch_get_main_queue(), ^{
            BEBLOCK_INVOKE(completion, responseModel, error);
        });
    }];
}

- (void) _fetchStickerDataWithCompletion:(BEEffectDataFetchCompletion)completion{
    [self initStickerDict];
    @weakify(self);
    [self runAsync:^{
        @strongify(self);
        
        NSString *stickerPath = [[BEResourceHelper new] stickerPath:@""];
        NSError *error = nil;
        NSMutableArray <BEEffectStickerGroup *> *stickersGroupArr = [NSMutableArray array];
        BEEffectResponseModel *responseModel = [BEEffectResponseModel new];
        
        NSMutableArray <BEEffectSticker*> * stickerArray = [NSMutableArray array];
        
        BEEffectSticker *clear = [BEEffectSticker new];
        clear.title = NSLocalizedString(@"filter_normal", nil);
        clear.filePath = @"";
        clear.imageName = @"icon_clear";
        clear.toastString = @"";
        
        [stickerArray addObject:clear];

        for (int i = 0; i < stickersArray.count; i += 4){
            BEEffectSticker *sticker = [BEEffectSticker new];
            sticker.title = stickersArray[i];
            sticker.filePath = [stickerPath stringByAppendingPathComponent: stickersArray[i + 2]];
            sticker.imageName = stickersArray[i + 1];
            sticker.toastString = stickersArray[i + 3];

            [stickerArray addObject:sticker];
        }
        
        BEEffectStickerGroup *group = [BEEffectStickerGroup new];
        group.title = @"sticker";
        group.stickers = stickerArray.copy;
        
        [stickersGroupArr addObject:group];
        responseModel.stickerGroup = stickersGroupArr;
        self.responseModel = responseModel;
        
        dispatch_async(dispatch_get_main_queue(), ^{
            BEBLOCK_INVOKE(completion, self.responseModel, error);
        });
    }];
    
}

- (void)_fetchFilterDataWithCompletion:(BEEffectDataFetchCompletion)completion {
    @weakify(self);
    [self runAsync:^{
        @strongify(self);
        NSString *resourcePath = [[BEResourceHelper new] filterPath:@""];
        NSError *error = nil;
        NSArray *filterCategoryResourcePaths = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:resourcePath error:&error];
        NSMutableArray *filterGroupArr = [NSMutableArray array];
        BEEffectResponseModel *responseModel = [BEEffectResponseModel new];
        for (NSString *path in filterCategoryResourcePaths) {
            NSString *fullPath = [resourcePath stringByAppendingPathComponent:path];
            NSArray *filterResourcePaths = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:fullPath error:&error];
            NSMutableArray <BEEffect *>*filterArray = [NSMutableArray array];
            filterResourcePaths = [filterResourcePaths sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
            for (NSString *filterPath in filterResourcePaths) {
                BEEffect *filter = [BEEffect new];
                filter.filePath = [fullPath stringByAppendingPathComponent:filterPath];
                [filterArray addObject:filter];
            }
            if ([path isEqualToString:@"Filter"]) {
                NSArray *filterNames = @[
                                         NSLocalizedString(@"filter_chalk", nil),
                                         NSLocalizedString(@"filter_cream", nil),
                                         NSLocalizedString(@"filter_oxgen", nil),
                                         NSLocalizedString(@"filter_campan", nil),
                                         NSLocalizedString(@"filter_lolita", nil),
                                         NSLocalizedString(@"filter_mitao", nil),
                                         NSLocalizedString(@"filter_makalong", nil),
                                         NSLocalizedString(@"filter_paomo", nil),
                                         NSLocalizedString(@"filter_yinhua", nil),
                                         NSLocalizedString(@"filter_musi", nil),
                                         NSLocalizedString(@"filter_wuyu", nil),
                                         NSLocalizedString(@"filter_beihaidao", nil),
                                         NSLocalizedString(@"filter_riza", nil),
                                         NSLocalizedString(@"filter_xiyatu", nil),
                                         NSLocalizedString(@"filter_jingmi", nil),
                                         NSLocalizedString(@"filter_jiaopian", nil),
                                         NSLocalizedString(@"filter_nuanyang", nil),
                                         NSLocalizedString(@"filter_jiuri", nil),
                                         NSLocalizedString(@"filter_hongchun", nil),
                                         NSLocalizedString(@"filter_julandiao", nil),
                                         NSLocalizedString(@"filter_tuise", nil),
                                         NSLocalizedString(@"filter_heibai", nil),
                                         NSLocalizedString(@"filter_Po1", nil),
                                         NSLocalizedString(@"filter_Po2", nil),
                                         NSLocalizedString(@"filter_Po3", nil),
                                         NSLocalizedString(@"filter_Po4", nil),
                                         NSLocalizedString(@"filter_Po5", nil),
                                         NSLocalizedString(@"filter_Po6", nil),
                                         NSLocalizedString(@"filter_Po7", nil),
                                         NSLocalizedString(@"filter_Po8", nil),
                                         NSLocalizedString(@"filter_Po9", nil),
                                         NSLocalizedString(@"filter_Po10", nil),
                                         NSLocalizedString(@"filter_L1", nil),
                                         NSLocalizedString(@"filter_L2", nil),
                                         NSLocalizedString(@"filter_L3", nil),
                                         NSLocalizedString(@"filter_L4", nil),
                                         NSLocalizedString(@"filter_L5", nil),
                                         NSLocalizedString(@"filter_F1", nil),
                                         NSLocalizedString(@"filter_F2", nil),
                                         NSLocalizedString(@"filter_F3", nil),
                                         NSLocalizedString(@"filter_F4", nil),
                                         NSLocalizedString(@"filter_F5", nil),
                                         NSLocalizedString(@"filter_S1", nil),
                                         NSLocalizedString(@"filter_S2", nil),
                                         NSLocalizedString(@"filter_S3", nil),
                                         NSLocalizedString(@"filter_S4", nil),
                                         NSLocalizedString(@"filter_S5", nil),
                                         ];
                NSArray *filterCNName = @[
          @"柔白",@"奶油",@"氧气",@"桔梗",@"洛丽塔",@"蜜桃",@"马卡龙",@"泡沫",@"樱花",@"浅暖",@"物语",
          @"北海道",@"日杂",@"西雅图",@"静谧",@"胶片",@"暖阳",@"旧日",@"红唇",@"橘蓝调",@"褪色",@"黑白",
          @"温柔",@"恋爱超甜",@"初见",@"暗调",@"奶茶",@"soft",@"夕阳",@"冷氧",@"海边人像",@"高级灰",@"海岛",
          @"浅夏",@"夜色",@"红棕",@"清透",@"自然2",@"苏打",@"加州",@"食色",@"川味",@"美式胶片",@"红色复古",
          @"旅途",@"暖黄",@"蓝调胶片" ];
                [filterArray enumerateObjectsUsingBlock:^(BEEffect * filter, NSUInteger idx, BOOL * _Nonnull stop) {
                    filter.title = idx < filterNames.count ? filterNames[idx] : @"";
                    filter.imageName = idx < filterCNName.count ? filterCNName[idx] : @"";
                }];
                BEEffect *normal = [BEEffect new];
                normal.title =  NSLocalizedString(@"filter_normal", nil);
                normal.imageName = @"正常";
                normal.filePath = @"";
                [filterArray insertObject:normal atIndex:0];
            }
            BEEffectGroup *group = [BEEffectGroup new];
            group.title = path;
            group.filters = filterArray.copy;
            [filterGroupArr addObject:group];
        }
        responseModel.filterGroups = filterGroupArr;
        self.responseModel = responseModel;
        dispatch_async(dispatch_get_main_queue(), ^{
            BEBLOCK_INVOKE(completion, self.responseModel, error);
        });
    }];
}


#pragma mark -

- (void)runAsync:(void(^)(void))block {
    dispatch_async(self.operationQueue, ^{
        block ? block() : 0;
    });
}

@end
