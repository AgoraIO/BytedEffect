// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
#import "BEVideoRecorderViewController.h"
#import <UIKit/UIKit.h>
#import <Masonry.h>
#import <Toast/UIView+Toast.h>
#import "BEGLView.h"
#import "BEFrameProcessor.h"
#import "BEVideoCapture.h"
#import "BECameraContainerView.h"
#import "BEModernEffectPickerView.h"
#import "BEModernStickerPickerView.h"
#import "BEEffectDataManager.h"
#import "BEStudioConstants.h"
#import "BEResourceHelper.h"
#import "BEGlobalData.h"
#import "BEMacro.h"

typedef enum : NSUInteger {
    BefEffectNone = 0,
    BefEffectFaceBeauty,
    BefEffectSticker,
    BefEffectAnimoji
}BefEffectMainStatue;


@interface BEVideoRecorderViewController ()<BEVideoCaptureDelegate, BECameraContainerViewDelegate, BEModernStickerPickerViewDelegate, BETapDelegate, BEDefaultTapDelegate>
{
    BEFrameProcessor *_processor;
    BefEffectMainStatue lastEffectStatue;
}

@property (nonatomic, assign) AVCaptureVideoOrientation referenceOrientation; // 视频播放方向
@property (nonatomic, strong) BEGLView *glView;
@property (nonatomic, strong) BEVideoCapture *capture;
@property (nonatomic, assign) int orientation;
@property (nonatomic, copy) AVCaptureSessionPreset captureSessionPreset;

@property (nonatomic, strong) BECameraContainerView *cameraContainerView;
@property (nonatomic, strong) BEModernEffectPickerView *effectPickerView;
@property (nonatomic, strong) BEModernStickerPickerView *stickerPickerView;
@property (nonatomic, strong) BEModernStickerPickerView *animojiPickerView;

@property (nonatomic, strong) BEEffectDataManager *stickerDataManager;
@property (nonatomic, strong) BEEffectDataManager *animojiDataManager;
@property (nonatomic, copy) NSArray<BEEffectSticker*> *stickers;
@property (nonatomic, assign) BOOL isSavingCurrentImage;

@property (nonatomic, assign) BOOL touchExposureEnable;
@property (nonatomic, strong) NSTimer *timer;

@property (nonatomic, strong) NSString *savedStickerPath;
@property (nonatomic, strong) NSString *savedAnimojiPath;
@end

@implementation BEVideoRecorderViewController

- (void)dealloc
{
    [self releaseTimer];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _captureSessionPreset = AVCaptureSessionPreset1280x720;
    lastEffectStatue = BefEffectNone;
    [self addObserver];
//    [self _createCamera];
    [self _setupUI];
//    [self be_initData];
//    [self setupTimer];
    
//    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
////        [self.cameraContainerView showBottomView:self.effectPickerView show:YES];
//        [self.effectPickerView setDefaultEffect];
//    });
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:YES animated:NO];
}

- (void)viewWillDisappear:(BOOL)animated;
{
    [super viewWillDisappear:animated];
    [self releaseTimer];
}
 

- (void)viewSafeAreaInsetsDidChange{
    [super viewSafeAreaInsetsDidChange];
}

#pragma mark - Private
- (void)_setupUI {
    NSArray<NSString *> *array = [_processor availableFeatures];
    for (NSString *s in array) {
        if ([s isEqualToString:@"3DStickerV3"]) {
            BEGlobalData.animojiEnable = true;
            break;
        }
    }
    
    self.cameraContainerView = [[BECameraContainerView alloc] initWithFrame:self.view.bounds];
    self.cameraContainerView.delegate = self;
    [self.view addSubview:self.cameraContainerView];
    [self.cameraContainerView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self.view);
    }];
}

- (void)be_initData {
    BOOL exclusive = [[NSUserDefaults standardUserDefaults] boolForKey:BEFUserDefaultExclusive];
    [_processor setComposerMode:exclusive ? 0 : 1];
    [_cameraContainerView setExclusive:exclusive];
}

#pragma mark - Notification
- (void)addObserver {
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onListenFilterChanged:)
                                                 name:BEEffectFilterDidChangeNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onListenFilterIntensityChanged:)
                                                 name:BEEffectFilterIntensityDidChangeNotification
                                               object:nil];

    //授权成功
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onListenAuthorizationChanged:)
                                                 name:BEEffectCameraDidAuthorizationNotification
                                               object:nil];
    
    //返回主界面
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onListenReturnToMainUI:)
                                                 name:BEEffectDidReturnToMainUINotification
                                               object:nil];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onNormalButton:)
                                                 name:BEEffectNormalButtonNotification
                                               object:nil];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onUpdateComposerNdoes:)
                                                 name:BEEffectUpdateComposerNodesNotification
                                               object:nil];

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onUpdateComposerNodeIntensity:)
                                                 name:BEEffectUpdateComposerNodeIntensityNotification
                                               object:nil];

    //曝光补偿滑杆的值改变
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onExporsureValueChanged:)
                                                 name:BEEffectExporsureValueChangedNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(onSdkError:)
                                                 name:BESdkErrorNotification
                                               object:nil];

//    if (![UIDevice currentDevice].generatesDeviceOrientationNotifications) {
//        [[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications];
//    }
//    [[NSNotificationCenter defaultCenter] addObserver:self
//                                             selector:@selector(handleDeviceOrientationChange:)
//                                                 name:UIDeviceOrientationDidChangeNotification
//                                               object:nil];
}

#pragma mark - obverser handler

- (void)onSdkError:(NSNotification *)aNote {
    NSString *msg = aNote.userInfo[BEEffectNotificationUserInfoKey];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [_glView makeToast:msg duration:(NSTimeInterval)(3.0) position:CSToastPositionCenter];
    });
}

- (void)onExporsureValueChanged:(NSNotification *) aNote{
    float value = [aNote.userInfo[BEEffectNotificationUserInfoKey] floatValue];

    [_capture pause];
    [_capture setExposure:value];
    [_capture resume];
}

- (void)handleDeviceOrientationChange:(NSNotification *)notification {
    UIDeviceOrientation orientation = [UIDevice currentDevice].orientation;
    switch (orientation) {
        case UIDeviceOrientationPortrait:
            [_capture setOrientation:AVCaptureVideoOrientationPortrait];
            [self.effectPickerView reloadCollectionViews];
            break;
        case UIDeviceOrientationLandscapeLeft:
            [_capture setOrientation:AVCaptureVideoOrientationLandscapeRight];
            [self.effectPickerView reloadCollectionViews];
            break;
        case UIDeviceOrientationLandscapeRight:
            [_capture setOrientation:AVCaptureVideoOrientationLandscapeLeft];
            [self.effectPickerView reloadCollectionViews];
        default:
            break;
    }
}

- (void)onUpdateComposerNdoes:(NSNotification *)aNote {
    NSArray<NSNumber *> *nodes = aNote.userInfo[BEEffectNotificationUserInfoKey];
    
    [_capture pause];
//    [self cleanUpLastEffectWithCurrentStatus:BefEffectFaceBeauty];
    NSArray<NSString *> *paths = [self composerNodesToPaths:nodes];
    [_processor updateComposerNodes:paths];
    [_capture resume];
     
}

- (void)onUpdateComposerNodeIntensity:(NSNotification *)aNote {
    BEEffectNode node = [aNote.userInfo[BEEffectNotificationUserInfoKey][0] longValue];
    CGFloat intensity = [aNote.userInfo[BEEffectNotificationUserInfoKey][1] floatValue];
    
    [_capture pause];
    BEComposerNodeModel *model = [self composerNodeToNode:[NSNumber numberWithLong:node]];
    if (model != nil) {
        [_processor updateComposerNodeIntensity:model.path key:model.key intensity:intensity];
    } else {
        NSLog(@"model not found, node: %ld", node);
    }
    [_capture resume];
}

- (void)onNormalButton:(NSNotification *)aNote
{
    BOOL isUp = [aNote.userInfo[BEEffectNotificationUserInfoKey] boolValue];
    [_processor setEffectOn:isUp];
}

- (void)onListenReturnToMainUI:(NSNotification *)aNote{
    [self.cameraContainerView showBottomButton];
}

- (void) onListenAuthorizationChanged:(NSNotification *)aNote{
    CGRect displayViewRect = [UIScreen mainScreen].bounds;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        _glView.frame = displayViewRect;});
}

- (void)onListenFilterChanged:(NSNotification *)aNote {
    NSString *path = aNote.userInfo[BEEffectNotificationUserInfoKey];
    [_capture pause];
    
    [_processor setFilterPath:path];
//    CGFloat intensity = [[BEEffectDataManager defaultValue] objectForKey:@(BETypeFilter)].floatValue;
//    [self.effectPickerView setSliderProgress:intensity];
//    [self.effectPickerView setFilterPath:path];
//    [_processor setFilterIntensity:intensity];
//    [self cleanUpLastEffectWithCurrentStatus:BefEffectFaceBeauty];
    
    [_capture resume];
}

- (void)onListenFilterIntensityChanged:(NSNotification *)aNote {
    float intensity = [aNote.userInfo[BEEffectNotificationUserInfoKey] floatValue];
    [_capture pause];
    [_processor setFilterIntensity:intensity];
    [_capture resume];
}

- (void)animojiLoadData {
    @weakify(self)
    void (^completion)(BEEffectResponseModel *, NSError *) =  ^(BEEffectResponseModel *responseModel, NSError *error) {
        @strongify(self)
        if (!error){
//            self.stickers = responseModel.stickerGroup.firstObject.stickers;
            [self.animojiPickerView refreshWithStickers: responseModel.stickerGroup.firstObject.stickers];
        }
    };
    [self.animojiDataManager fetchDataWithCompletion:^(BEEffectResponseModel *responseModel, NSError *error) {
        completion(responseModel, error);
    }];
}

- (void)stickersLoadData{
    @weakify(self)
    void (^completion)(BEEffectResponseModel *, NSError *) =  ^(BEEffectResponseModel *responseModel, NSError *error) {
        @strongify(self)
        if (!error){
            self.stickers = responseModel.stickerGroup.firstObject.stickers;
            [self.stickerPickerView refreshWithStickers:self.stickers];
        }
    };
    [self.stickerDataManager fetchDataWithCompletion:^(BEEffectResponseModel *responseModel, NSError *error) {
        completion(responseModel, error);
    }];
}

- (BEEffectDataManager *)stickerDataManager {
    if (!_stickerDataManager) {
        _stickerDataManager = [BEEffectDataManager dataManagerWithType:BEEffectDataManagerTypeSticker];
    }
    return _stickerDataManager;
}

- (BEEffectDataManager *)animojiDataManager {
    if (!_animojiDataManager) {
        _animojiDataManager = [BEEffectDataManager dataManagerWithType:BEEffectDataManagerTypeAnimoji];
    }
    return _animojiDataManager;
}

#pragma mark - private
- (void)_setStickerUnSelected {
    [_capture pause];
    [self.stickerPickerView onClose];
//    [_processor effectManagerSetInitalStatus];
    [_processor setStickerPath:@""];
    [_capture resume];
}

//去除所有的美颜效果
- (void)_setEffectPickerUnSelected {
    [_capture pause];
    
    //清除美妆和美颜的效果
    [_processor updateComposerNodes:@[]];
    [_processor setFilterPath:@""];
    
    [self.effectPickerView onClose];

    [_capture resume];
}

- (void)_setAnimojiPickerUnselected {
    [_capture pause];
    
    [self.animojiPickerView onClose];
    [_processor setStickerPath:@""];
    
    [_capture resume];
}

- (BOOL)be_isExclusive {
    return _processor.composerMode == 0;
}

- (void)cleanUpLastEffectWithCurrentStatus:(BefEffectMainStatue)currentStatus{
    if (currentStatus == BefEffectFaceBeauty) {
        [self.stickerPickerView onClose];
        [self.animojiPickerView onClose];
        [self.stickerPickerView interceptTap:NO];
        if (self.savedStickerPath != nil && ![self.savedStickerPath isEqualToString:@""]) {
            self.savedStickerPath = nil;
            [_processor setStickerPath:@""];
        }
        if (self.savedAnimojiPath != nil && ![self.savedAnimojiPath isEqualToString:@""]) {
            self.savedAnimojiPath = nil;
            [_processor setStickerPath:@""];
        }
    } else if (currentStatus == BefEffectSticker) {
        if ([self be_isExclusive]) {
            [self.effectPickerView onClose];
        }
    } else if (currentStatus == BefEffectAnimoji) {
        [self.effectPickerView onClose];
        [self.stickerPickerView onClose];
        [_processor updateComposerNodes:@[]];
    }
//    if (![self be_isExclusive]) {
//        return;
//    }
//
//    if (currentStatus != lastEffectStatue){
//        switch (lastEffectStatue) {
//            case BefEffectNone:
//                break;
//            case BefEffectFaceBeauty:
//                [self _setEffectPickerUnSelected];
//                break;
//            case BefEffectSticker:
//                [self _setStickerUnSelected];
//                break;
//            default:
//                break;
//        }
//        lastEffectStatue = currentStatus;
//    }
}

- (NSArray<NSString *> *)composerNodesToPaths:(NSArray<NSNumber *> *)nodes {
    NSMutableArray<NSString *> *array = [NSMutableArray array];
    for (NSNumber *node in nodes) {
        BEComposerNodeModel *model = [self composerNodeToNode:node];
        if (model == nil) {
            NSLog(@"model not found, node: %ld", [node longValue]);
            continue;
        }
        [array addObject:model.path];
    }
    return array;
}

- (BEComposerNodeModel *)composerNodeToNode:(NSNumber *)node {
    NSDictionary *dict = [BEEffectDataManager composerNodeDic];
    NSNumber *realNode = [NSNumber numberWithLong:([node longValue] & ~SUB_MASK)];
    BEComposerNodeModel *model = [dict objectForKey:realNode];
    if (model == nil) {
        return nil;
    }
    BEComposerNodeModel *tmp = [BEComposerNodeModel new];
    if (([node longValue] & SUB_MASK)) {
//        tmp.path = [[_processor resourceHelper] composerNodePath:model.pathArray[(([node longValue] & SUB_MASK) - 1)]];
//        tmp.key = model.keyArray[(([node longValue] & SUB_MASK) - 1)];
        tmp.path = [[_processor resourceHelper] composerNodePath:model.pathArray[(([node longValue] & SUB_MASK) - 1)]];
        tmp.key = model.keyArray[0];
    } else {
        tmp.path = [[_processor resourceHelper] composerNodePath:model.path];
        tmp.key = model.key;
    }
    return tmp;
}

#pragma mark - Pickers
- (BEModernEffectPickerView *)effectPickerView {
    if (!_effectPickerView) {
        _effectPickerView = [[BEModernEffectPickerView alloc] initWithFrame:(CGRect)CGRectMake(0, 0, self.view.frame.size.width, 220)];
        _effectPickerView.onTapDelegate = self;
        _effectPickerView.onDefaultTapDelegate = self;
    }
    return _effectPickerView;
}


- (BEModernStickerPickerView *)stickerPickerView {
    if (!_stickerPickerView) {
        _stickerPickerView = [[BEModernStickerPickerView alloc] initWithFrame:(CGRect)CGRectMake(0, 0, self.view.frame.size.width, 200)];
        _stickerPickerView.layer.backgroundColor = [UIColor colorWithRed:0/255.0 green:0/255.0 blue:0/255.0 alpha:0.6].CGColor;
        _stickerPickerView.delegate = self;
        _stickerPickerView.type = BETypeSticker;
        _stickerPickerView.onTapDelegate = self;
        
        [self stickersLoadData];
    }
    return _stickerPickerView;
}

- (BEModernStickerPickerView *)animojiPickerView{
    if (!_animojiPickerView) {
        _animojiPickerView = [[BEModernStickerPickerView alloc] initWithFrame:(CGRect)CGRectMake(0, 0, self.view.frame.size.width, 200)];
        _animojiPickerView.layer.backgroundColor = [UIColor colorWithRed:0/255.0 green:0/255.0 blue:0/255.0 alpha:0.6].CGColor;
        _animojiPickerView.delegate = self;
        _animojiPickerView.type = BETypeAnimoji;
        
        [self animojiLoadData];
    }
    return _animojiPickerView;
}

#pragma mark - BEModernStickerPickerViewDelegate
- (void)stickerPicker:(BEModernStickerPickerView *)pickerView didSelectStickerPath:(NSString *)path toastString:(NSString *)toast type:(BEEffectNode)type {
    [_capture pause];
    BOOL availablePath = path != nil && ![path isEqualToString:@""];
    BEGlobalData.beautyEnable = !availablePath;
    if (type == BETypeSticker) {
        self.savedStickerPath = path;
        if ([self be_isExclusive]) {
            if (!availablePath) {
                [self.effectPickerView recoverEffect];
            } else {
                [self cleanUpLastEffectWithCurrentStatus:BefEffectSticker];
                [_processor setStickerPath:path];
            }
        } else {
            [_processor setStickerPath:path];
        }
        [_glView hideAllToasts];
        
        if (toast.length > 0 ){
            [_glView makeToast:toast duration:(NSTimeInterval)(3.0) position:CSToastPositionCenter];
        }
    } else if (type == BETypeAnimoji) {
        if (self.savedAnimojiPath != path) {
            self.savedAnimojiPath = path;
            if (availablePath) {
                [self cleanUpLastEffectWithCurrentStatus:BefEffectAnimoji];
                [self.stickerPickerView interceptTap:YES];
                [_processor setStickerPath:path];
            } else {
                [_processor setStickerPath:path];
                [self.stickerPickerView interceptTap:NO];
                if (self.savedStickerPath != nil && ![self.savedStickerPath isEqualToString:@""]) {
                    [self.stickerPickerView recoverState:self.savedStickerPath];
                    [_processor setStickerPath:self.savedStickerPath];
                }
                if (![self be_isExclusive] || (self.savedStickerPath == nil || [self.savedStickerPath isEqualToString:@""])) {
                    [self.effectPickerView recoverEffect];
                }
            }
        }
    }
    [_capture resume];
}

#pragma mark - BECameraContainerViewDelegate

- (void) onSwitchCameraClicked:(UIButton *) sender {
    sender.enabled = NO;
    [self.capture switchCamera];
    sender.enabled = YES;
}

#pragma mark - BEDefaultTapDelegate
- (void)onDefaultTap {
    [self cleanUpLastEffectWithCurrentStatus:BefEffectFaceBeauty];
}

#pragma mark - BETapDelegate
- (void)onTap {
    NSString *toast;
    if (self.savedAnimojiPath != nil && ![self.savedAnimojiPath isEqualToString:@""]) {
        toast = NSLocalizedString(@"tip_close_animoji_first", nil);
    } else {
        toast = NSLocalizedString(@"tip_close_sticker_first", nil);
    }
    [_glView makeToast:toast duration:(NSTimeInterval)(2.0) position:CSToastPositionCenter];
}


//显示特效界面
- (void)onEffectButtonClicked:(UIButton *)sender{
    [self.cameraContainerView showBottomView:self.effectPickerView show:YES];
}

//显示贴纸界面
- (void)onStickerButtonClicked:(UIButton *)sender{
    [self.cameraContainerView showBottomView:self.stickerPickerView show:YES];
}

- (void)onAnimojiButtonClicked:(UIButton *)sender{
    [self.cameraContainerView showBottomView:self.animojiPickerView show:YES];
}


- (void)onSegmentControlChanged:(UISegmentedControl *)sender {
    NSString *key = [self.cameraContainerView segmentItems][sender.selectedSegmentIndex];
    self.captureSessionPreset = BEVideoRecorderSegmentContentAndSessionPresetMapping()[key];
    [self.capture stopRunning];
    self.capture.sessionPreset = self.captureSessionPreset;
    CGRect displayFrame = [_capture getZoomedRectWithRect:[UIScreen mainScreen].bounds scaleToFit:YES];
    _glView.frame = displayFrame;
    //[self _createCamera]; //这里重新创建一个camera会出现很多crash的问题，所以这里直接修改参数，然后开始running
    //[self.capture resume];
    [self.capture startRunning];
}

- (void)onSaveButtonClicked:(UIButton*)sender{
    _isSavingCurrentImage = YES;
}

- (void)onExclusiveSwitchChanged:(UISwitch *)sender {
    BOOL exclusive = sender.isOn;
    
    if ([[NSUserDefaults standardUserDefaults] boolForKey:BEFUserDefaultExclusive] == exclusive) {
        return;
    }
    [[NSUserDefaults standardUserDefaults] setBool:exclusive forKey:BEFUserDefaultExclusive];
    [_glView hideAllToasts];
    NSString *toast = NSLocalizedString(@"exclusive_tip", nil);
    if (toast.length > 0 ) {
        [_glView makeToast:toast duration:(NSTimeInterval)(3.0) position:CSToastPositionCenter];
    }
}

#pragma mark - VideoCaptureDelegate
/*
 * 相机帧经过美化处理后再绘制到屏幕上
 */
- (void)videoCapture:(BEVideoCapture *)camera didOutputSampleBuffer:(CMSampleBufferRef)sampleBuffer {
    CVImageBufferRef imageRef = CMSampleBufferGetImageBuffer(sampleBuffer);
    CMTime sampleTime = CMSampleBufferGetPresentationTimeStamp(sampleBuffer);
    double timeStamp = (double)sampleTime.value/sampleTime.timescale;
    BEProcessResult *result =  [_processor process:imageRef timeStamp:timeStamp];
    dispatch_sync(dispatch_get_main_queue(), ^{
        [_glView renderWithTexture:result.texture
                              size:result.size
                           flipped:YES
               applyingOrientation:_orientation
              savingCurrentTexture:_isSavingCurrentImage];
        
            _isSavingCurrentImage = NO;

    });
}

- (void)videoCapture:(BEVideoCapture *)camera didFailedToStartWithError:(VideoCaptureError)error {

}


// 测光逻辑：
// 无人脸时，测光点默认为(0.5, 0.5), 点击屏幕可改变测光点。
// 有人脸时候，测光点为人脸中心点，通过人脸sdk计算得到，这时候点击屏幕也可以改变测光点，不过3秒后测光点会自动改回人脸中心点。继续点击屏幕，定时器重新启动
// 从无人脸到有人脸，曝光点自动设置回默认值(0.5, 0.5)
#pragma mark - VideoMetadataDelegate

-(void)captureOutput:(BEVideoCapture *)camera didOutputMetadataObjects:(NSArray<__kindof AVMetadataObject *> *)metadataObjects
{
    BOOL detectedFace = 0;
    CGPoint point = CGPointMake(0.5, 0.5);
    for (AVMetadataFaceObject *face in metadataObjects)
    {
//        NSLog(@"Face detected with ID: %li", (long)face.faceID);
//        NSLog(@"Face bounds: %@", NSStringFromCGRect(face.bounds));
        
        float faceMiddleWidth = (face.bounds.origin.x + face.bounds.size.width) / 2;
        float faceMiddleHeight = (face.bounds.origin.y + face.bounds.size.height) / 2;
        
        point = CGPointMake(faceMiddleWidth, faceMiddleHeight);
        detectedFace ++;
        break;
    }
   
    //半脸情况下避免测光点在过于边缘的位置导致的过曝，在靠近屏幕边缘时候测光点改回中心位置
    if(point.x > 0.8 || point.x < 0.2 || point.y< 0.05 ||point.y > 0.95)
    {
        point = CGPointMake(0.5, 0.5);
    }
    
    [self didChangeExporsureDetectPoint:point fromFace:detectedFace>0];
    
}

-(void) didChangeExporsureDetectPoint:(CGPoint)point fromFace:(BOOL)fromFace {
    if([_timer isValid]) return;
    
//    NSLog(@"detected face point x: %f, y: %f", point.x, point.y);
    
    if(!_touchExposureEnable && !fromFace)
    {
        [_capture setExposurePointOfInterest:CGPointMake(0.5f, 0.5f)];
        [_capture setFocusPointOfInterest:CGPointMake(0.5f, 0.5f)];
        _touchExposureEnable = YES;
        return;
    }
    
    _touchExposureEnable = !fromFace;
    
    if(_touchExposureEnable) return;

    if (point.x == 0 && point.y == 0) {
        return;
    }
    
//    NSLog(@"ExposurePointOfInterest: (%f, %f)", point.x, point.y);
    [_capture setExposurePointOfInterest:point];
    [_capture setFocusPointOfInterest:point];

}


- (void)setupTimer {
    [self releaseTimer];
    _timer = [NSTimer scheduledTimerWithTimeInterval:3 target:self selector:@selector(updateTouchState) userInfo:nil repeats:NO];
    [_timer invalidate];
}

- (void)resetTimer {
    __weak typeof(self)weakSelf = self;
    [weakSelf.timer invalidate];
    weakSelf.timer = [NSTimer scheduledTimerWithTimeInterval:3 target:self selector:@selector(updateTouchState) userInfo:nil repeats:NO];
}

- (void)updateTouchState {
    if(_touchExposureEnable)
    {
        _touchExposureEnable = NO;
         [_timer invalidate];
    }
}

- (void)releaseTimer {
    if (_timer) {
        [_timer invalidate];
        _timer = nil;
    }
}


-(void)touchesBegan:(NSSet*)touches withEvent:(UIEvent *)event {
    
    UITouch * touch = [[event allTouches] anyObject];
    CGPoint loc = [touch locationInView:self.view];

    if(!_touchExposureEnable )
    {
        _touchExposureEnable = YES;
        [self resetTimer];
    }
    
    CGRect bouns =  self.view.bounds;
    CGPoint point = CGPointMake(loc.x / bouns.size.width, loc.y / bouns.size.height);
    [_capture setExposurePointOfInterest:point];
    [_capture setFocusPointOfInterest:point];
    
}


#pragma mark - public
- (void)initProcessor:(EAGLContext *)context {
    _processor = [[BEFrameProcessor alloc] initWithContext:context resourceDelegate:nil];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [self.effectPickerView setDefaultEffect];
        });
}

- (BEProcessResult *)process:(CVPixelBufferRef)pixelBuffer timeStamp:(double)timeStamp {
    return [_processor process:pixelBuffer timeStamp:timeStamp];
}

@end
