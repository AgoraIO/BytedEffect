//
//  ViewController.m
//  BeautifyExample
//
//  Created by LSQ on 2020/8/3.
//  Copyright © 2020 Agora. All rights reserved.
//


#import "ViewController.h"
#import <AgoraRtcKit/AgoraRtcEngineKit.h>
#import "CapturerManager.h"
#import "VideoProcessingManager.h"
#import "KeyCenter.h"

#import "ByteDanceFilter.h"

@interface ViewController () <AgoraRtcEngineDelegate>

@property (nonatomic, strong) CapturerManager *capturerManager;
@property (nonatomic, strong) ByteDanceFilter *videoFilter;
@property (nonatomic, strong) VideoProcessingManager *processingManager;
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
    self.rtcEngineKit = [AgoraRtcEngineKit sharedEngineWithAppId:[KeyCenter AppId] delegate:self];
    
    [self.rtcEngineKit setChannelProfile:AgoraChannelProfileLiveBroadcasting];
    [self.rtcEngineKit setClientRole:AgoraClientRoleBroadcaster];
    [self.rtcEngineKit enableVideo];
    AgoraVideoEncoderConfiguration* config = [[AgoraVideoEncoderConfiguration alloc] initWithSize:CGSizeMake(720, 1280) frameRate:30 bitrate:0 orientationMode:AgoraVideoOutputOrientationModeAdaptative];
    [self.rtcEngineKit setVideoEncoderConfiguration:config];
    
    // init process manager
    self.processingManager = [[VideoProcessingManager alloc] init];
    
    // init capturer, it will push pixelbuffer to rtc channel
    AGMCapturerVideoConfig *videoConfig = [AGMCapturerVideoConfig defaultConfig];
    videoConfig.sessionPreset = AVCaptureSessionPreset1280x720;
    videoConfig.fps = 30;
    self.capturerManager = [[CapturerManager alloc] initWithVideoConfig:videoConfig delegate:self.processingManager];
    
    // add FaceUnity filter and add to process manager
    self.videoFilter = [ByteDanceFilter shareManager];
    self.videoFilter.enabled = YES;
    [self.processingManager addVideoFilter:self.videoFilter];
    
    [self.capturerManager startCapture];
    
    // set up local video to render your local camera preview
    self.videoCanvas = [AgoraRtcVideoCanvas new];
    self.videoCanvas.uid = 0;
    // the view to be binded
    self.videoCanvas.view = self.localView;
    self.videoCanvas.renderMode = AgoraVideoRenderModeHidden;
    self.videoCanvas.mirrorMode = AgoraVideoMirrorModeDisabled;
    [self.rtcEngineKit setupLocalVideo:self.videoCanvas];
    
    // set custom capturer as video source
    [self.rtcEngineKit setVideoSource:self.capturerManager];
    
    [self.rtcEngineKit joinChannelByToken:nil channelId:self.channelName info:nil uid:0 joinSuccess:^(NSString * _Nonnull channel, NSUInteger uid, NSInteger elapsed) {
        
    }];
}

/// release
- (void)dealloc {

    [self.capturerManager stopCapture];
    [self.rtcEngineKit leaveChannel:nil];
    [self.rtcEngineKit stopPreview];
    [self.rtcEngineKit setVideoSource:nil];
    [AgoraRtcEngineKit destroy];
}


- (IBAction)switchCamera:(UIButton *)button
{
    [self.capturerManager switchCamera];
}

- (IBAction)toggleRemoteMirror:(UIButton *)button
{
    self.remoteVideoMirrored = self.remoteVideoMirrored == AgoraVideoMirrorModeEnabled ? AgoraVideoMirrorModeDisabled : AgoraVideoMirrorModeEnabled;
    AgoraVideoEncoderConfiguration* config = [[AgoraVideoEncoderConfiguration alloc] initWithSize:CGSizeMake(720, 1280) frameRate:30 bitrate:0 orientationMode:AgoraVideoOutputOrientationModeAdaptative];
    config.mirrorMode = self.remoteVideoMirrored;
    [self.rtcEngineKit setVideoEncoderConfiguration:config];
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
