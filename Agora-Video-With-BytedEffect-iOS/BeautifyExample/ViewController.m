//
//  ViewController.m
//  BeautifyExample
//
//  Created by LSQ on 2020/8/3.
//  Copyright © 2020 Agora. All rights reserved.
//


#import "ViewController.h"
#import <AgoraRtcKit/AgoraRtcEngineKit.h>
#import "KeyCenter.h"
#import "BEImageUtils.h"

#import "ByteDanceFilter.h"

@interface ViewController () <AgoraRtcEngineDelegate, AgoraVideoFrameDelegate>

@property (nonatomic, strong) ByteDanceFilter *videoFilter;
@property (nonatomic, strong) BEImageUtils *imageUtils;

@property (nonatomic, strong) AgoraRtcEngineKit *rtcEngineKit;
@property (nonatomic, strong) IBOutlet UIView *localView;

@property (weak, nonatomic) IBOutlet UIView *remoteView;

@property (nonatomic, strong) IBOutlet UIButton *switchBtn;
@property (nonatomic, strong) IBOutlet UIButton *remoteMirrorBtn;
@property (nonatomic, strong) IBOutlet UILabel *beautyStatus;
@property (nonatomic, strong) IBOutlet UIView *missingAuthpackLabel;
@property (nonatomic, strong) AgoraRtcVideoCanvas *videoCanvas;
@property (nonatomic, assign) AgoraVideoMirrorMode localVideoMirrored;
@property (nonatomic, assign) AgoraVideoMirrorMode remoteVideoMirrored;


@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.remoteView.hidden = YES;
    
    // 初始化 rte engine
    AgoraRtcEngineConfig *config = [[AgoraRtcEngineConfig alloc] init];
    config.appId = [KeyCenter AppId];
    config.channelProfile = AgoraChannelProfileLiveBroadcasting;
    self.rtcEngineKit = [AgoraRtcEngineKit sharedEngineWithConfig:config delegate:self];
    
    [self.rtcEngineKit setVideoFrameDelegate:self];
    [self.rtcEngineKit setClientRole:AgoraClientRoleBroadcaster];
    AgoraCameraCapturerConfiguration *captuer = [[AgoraCameraCapturerConfiguration alloc] init];
    captuer.cameraDirection = AgoraCameraDirectionFront;
    [self.rtcEngineKit setCameraCapturerConfiguration:captuer];
    
    [self.rtcEngineKit enableVideo];
    [self.rtcEngineKit enableAudio];
    
    AgoraVideoEncoderConfiguration *configuration = [[AgoraVideoEncoderConfiguration alloc] init];
//    configuration.dimensions = CGSizeMake(360, 640);
//    configuration.dimensions = CGSizeMake(720, 1280);

    [self.rtcEngineKit setVideoEncoderConfiguration: configuration];
    AgoraRtcChannelMediaOptions *option = [[AgoraRtcChannelMediaOptions alloc] init];
    option.clientRoleType = [AgoraRtcIntOptional of: AgoraClientRoleBroadcaster];
    option.publishMicrophoneTrack = [AgoraRtcBoolOptional of:YES];
    option.publishCameraTrack = [AgoraRtcBoolOptional of:YES];
    [self.rtcEngineKit joinChannelByToken:nil channelId:self.channelName uid:0 mediaOptions:option joinSuccess:^(NSString * _Nonnull channel, NSUInteger uid, NSInteger elapsed) { }];
    
    
    // set up local video to render your local camera preview
    self.videoCanvas = [AgoraRtcVideoCanvas new];
    self.videoCanvas.uid = 0;
    // the view to be binded
    self.videoCanvas.view = self.localView;
    self.videoCanvas.renderMode = AgoraVideoRenderModeHidden;
    self.videoCanvas.mirrorMode = AgoraVideoMirrorModeDisabled;
    [self.rtcEngineKit setupLocalVideo:self.videoCanvas];
    [self.rtcEngineKit startPreview];
   
    
    // add FaceUnity filter and add to process manager
    self.videoFilter = [ByteDanceFilter shareManager];
    self.videoFilter.enabled = YES;
    
    self.imageUtils = [[BEImageUtils alloc] init];
}

/// release
- (void)dealloc {
    [self.rtcEngineKit leaveChannel:nil];
    [self.rtcEngineKit stopPreview];
    [AgoraRtcEngineKit destroy];
}


- (IBAction)switchCamera:(UIButton *)button
{
    [self.rtcEngineKit switchCamera];
}

- (IBAction)toggleRemoteMirror:(UIButton *)button
{
    self.remoteVideoMirrored = self.remoteVideoMirrored == AgoraVideoMirrorModeEnabled ? AgoraVideoMirrorModeDisabled : AgoraVideoMirrorModeEnabled;
    [self.rtcEngineKit setLocalRenderMode:AgoraVideoRenderModeHidden mirror:self.remoteVideoMirrored];
}

- (void)rtcEngine:(AgoraRtcEngineKit *)engine didJoinedOfUid:(NSUInteger)uid elapsed:(NSInteger)elapsed
{
    NSLog(@"join uid === %lu", uid);
}

- (BOOL)onCaptureVideoFrame:(AgoraOutputVideoFrame *)videoFrame {
    CVPixelBufferRef pixelBuffer = videoFrame.pixelBuffer;
    BEPixelBufferInfo *pixelBufferInfo = [self.imageUtils getCVPixelBufferInfo:videoFrame.pixelBuffer];
    if (pixelBufferInfo.format != BE_BGRA) {
        pixelBuffer = [self.imageUtils transforCVPixelBufferToCVPixelBuffer:pixelBuffer outputFormat:BE_BGRA];
    }
    
    pixelBuffer = [self.videoFilter processFrame: pixelBuffer
                                       timeStamp: videoFrame.renderTimeMs];
    videoFrame.pixelBuffer = pixelBuffer;
    return YES;
}

- (AgoraVideoFormat)getVideoPixelFormatPreference{
    return AgoraVideoFormatBGRA;
}
- (AgoraVideoFrameProcessMode)getVideoFrameProcessMode{
    return AgoraVideoFrameProcessModeReadWrite;
}

- (BOOL)getMirrorApplied{
    return YES;
}

- (BOOL)getRotationApplied{
    return NO;
}

/// firstRemoteVideoDecoded
- (void)rtcEngine:(AgoraRtcEngineKit *)engine firstRemoteVideoDecodedOfUid:(NSUInteger)uid size: (CGSize)size elapsed:(NSInteger)elapsed {

    if (self.remoteView.hidden) {
        self.remoteView.hidden = NO;
    }
    
    AgoraRtcVideoCanvas *videoCanvas = [[AgoraRtcVideoCanvas alloc] init];
    videoCanvas.uid = uid;
    // Since we are making a simple 1:1 video chat app, for simplicity sake, we are not storing the UIDs. You could use a mechanism such as an array to store the UIDs in a channel.
    
    videoCanvas.view = self.remoteView;
    videoCanvas.renderMode = AgoraVideoRenderModeHidden;
    [self.rtcEngineKit setupRemoteVideo:videoCanvas];
    // Bind remote video stream to view
    
}


@end
