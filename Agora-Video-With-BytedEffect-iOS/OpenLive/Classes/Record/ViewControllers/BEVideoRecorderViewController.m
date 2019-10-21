// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
#import "BEVideoRecorderViewController.h"
#import <UIKit/UIKit.h>
#import <Masonry.h>
#import <Toast/UIView+Toast.h>
#import "BEMacro.h"
#import "BEGLView.h"
#import "BEFrameProcessor.h"
#import "BEVideoCapture.h"
#import "BECameraContainerView.h"
#import "BEModernEffectPickerView.h"
#import "BEModernStickerPickerView.h"
#import "BEEffectDataManager.h"
#import "BEStudioConstants.h"

typedef enum : NSUInteger {
    BefEffectNone = 0,
    BefEffectDetect,
    BefEffectFaceBeauty,
    BefEffectSticker,
}BefEffectMainStatue;


@interface BEVideoRecorderViewController ()<BEVideoCaptureDelegate, BECameraContainerViewDelegate, BEModernStickerPickerViewDelegate>
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

@property (nonatomic, strong) BEEffectDataManager *stickerDataManager;
@property (nonatomic, copy) NSArray<BEEffectSticker*> *stickers;
@property (nonatomic, strong) AVCaptureVideoPreviewLayer *previewLayer;
@property (nonatomic, assign) BOOL isSavingCurrentImage;
@end

@implementation BEVideoRecorderViewController

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _captureSessionPreset = AVCaptureSessionPreset1280x720;
    lastEffectStatue = BefEffectNone;
    [self _setupUI];
//    [self _createCamera];
    [self addObserver];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:YES animated:NO];
}

- (void)viewSafeAreaInsetsDidChange{
    [super viewSafeAreaInsetsDidChange];
}

#pragma mark - Private
- (void)_setupUI {
    self.cameraContainerView = [[BECameraContainerView alloc] initWithFrame:self.view.bounds];
    self.cameraContainerView.delegate = self;
    [self.view addSubview:self.cameraContainerView];
    [self.cameraContainerView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self.view);
    }];
}

- (void)_createCamera {
    _capture = [[BEVideoCapture alloc] init];
    _capture.isOutputWithYUV = NO;
    _capture.delegate = self;
    
    _glView = [[BEGLView alloc] initWithFrame: [UIScreen mainScreen].bounds];
    [self.view insertSubview:_glView belowSubview:_cameraContainerView];
    [_glView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.edges.equalTo(self.view);
    }];
    EAGLContext *context = [[EAGLContext alloc] initWithAPI:kEAGLRenderingAPIOpenGLES2 sharegroup:self.glView.context.sharegroup];
    [EAGLContext setCurrentContext:context];
    _processor = [[BEFrameProcessor alloc] initWithContext:context videoSize:_capture.videoSize];
    _processor.cameraPosition = _capture.devicePosition;
    _capture.sessionPreset = self.captureSessionPreset;
    
    [_capture startRunning];
}

//- (void)_faceBeautyPickerDidSelectFaceBeautyData:(BEFaceBeautyModel *)data
//{
//
//    BEEffectFaceBeautyType type = data.detailType;
//    
//    float value = [data getValueWithType:data.detailType];
//    [_processor setIndensity:value  type:type];
//}


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
    
    if (![UIDevice currentDevice].generatesDeviceOrientationNotifications) {
        [[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications];
    }
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleDeviceOrientationChange:)
                                                 name:UIDeviceOrientationDidChangeNotification
                                               object:nil];
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
    [self cleanUpLastEffectWithCurrentStatus:BefEffectFaceBeauty];
    [_processor updateComposerNodes:nodes];
    [_capture resume];
     
}

- (void)onUpdateComposerNodeIntensity:(NSNotification *)aNote {
    BEEffectNode node = [aNote.userInfo[BEEffectNotificationUserInfoKey][0] longValue];
    CGFloat intensity = [aNote.userInfo[BEEffectNotificationUserInfoKey][1] floatValue];
    
    [_capture pause];
    [_processor updateComposerNodeIntensity:node intensity:intensity];
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
    [self.effectPickerView setSliderProgress:0.5];
//    self.effectPickerView.sli.value = 0.5;
//    [[self beautyModel] setModelWithtType:BEEffectFaceFilter value:0.5];
    [_processor setFilterIntensity:0.5];
    [self cleanUpLastEffectWithCurrentStatus:BefEffectFaceBeauty];
    
    [_capture resume];
}

- (void)onListenFilterIntensityChanged:(NSNotification *)aNote {
    float intensity = [aNote.userInfo[BEEffectNotificationUserInfoKey] floatValue];
    [_capture pause];
    [_processor setFilterIntensity:intensity];
    [_capture resume];
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

#pragma mark - private
- (void)_setStickerUnSelected{
    [_capture pause];
    [self.stickerPickerView onClose];
    [_processor effectManagerSetInitalStatus];
    [_capture resume];
}

//去除所有的美颜效果
- (void)_setEffectPickerUnSelected{
    [_capture pause];
    
    //清除美妆和美颜的效果
    [_processor updateComposerNodes:@[]];
    [_processor setFilterPath:@""];
    
    [self.effectPickerView onClose];

    [_capture resume];
}

- (void)cleanUpLastEffectWithCurrentStatus:(BefEffectMainStatue)currentStatus{
    if (currentStatus != lastEffectStatue){
        switch (lastEffectStatue) {
            case BefEffectNone:
                break;
            case BefEffectFaceBeauty:
                [self _setEffectPickerUnSelected];
                break;
            case BefEffectSticker:
                [self _setStickerUnSelected];
                break;
            default:
                break;
        }
        lastEffectStatue = currentStatus;
    }
}

#pragma mark - Pickers
- (BEModernEffectPickerView *)effectPickerView {
    if (!_effectPickerView) {
        _effectPickerView = [[BEModernEffectPickerView alloc] initWithFrame:(CGRect)CGRectMake(0, 0, self.view.frame.size.width, 205)];
    }
    return _effectPickerView;
}


- (BEModernStickerPickerView *)stickerPickerView{
    if (!_stickerPickerView) {
        _stickerPickerView = [[BEModernStickerPickerView alloc] initWithFrame:(CGRect)CGRectMake(0, 0, self.view.frame.size.width, 205)];
        _stickerPickerView.layer.backgroundColor = [UIColor colorWithRed:0/255.0 green:0/255.0 blue:0/255.0 alpha:0.6].CGColor;
        _stickerPickerView.delegate = self;
        
        [self stickersLoadData];
    }
    return _stickerPickerView;
}

#pragma mark - BEModernStickerPickerViewDelegate
- (void)stickerPicker:(BEModernStickerPickerView *)pickerView didSelectStickerPath:(NSString *)path toastString:(NSString *)toast{
    [_capture pause];
    [self cleanUpLastEffectWithCurrentStatus:BefEffectSticker];
    [_processor setStickerPath:path];
    [_glView hideAllToasts];
    
    if (toast.length > 0 ){
        [_glView makeToast:toast duration:(NSTimeInterval)(3.0) position:CSToastPositionCenter];
    }
    [_capture resume];
}

#pragma mark - BECameraContainerViewDelegate

- (void) onSwitchCameraClicked:(UIButton *) sender {
    sender.enabled = NO;
    [self.capture switchCamera];
    sender.enabled = YES;
}


//显示特效界面
- (void)onEffectButtonClicked:(UIButton *)sender{
    [self.cameraContainerView showBottomView:self.effectPickerView show:YES];
}

//显示贴纸界面
- (void)onStickerButtonClicked:(UIButton *)sender{
    [self.cameraContainerView showBottomView:self.stickerPickerView show:YES];
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

#pragma mark - public
- (void)initProcessor:(EAGLContext *)context {
    _processor = [[BEFrameProcessor alloc] initWithContext:context videoSize:CGSizeZero];
}

- (BEProcessResult *)process:(CVPixelBufferRef)pixelBuffer timeStamp:(double)timeStamp {
    return [_processor process:pixelBuffer timeStamp:timeStamp];
}

@end
