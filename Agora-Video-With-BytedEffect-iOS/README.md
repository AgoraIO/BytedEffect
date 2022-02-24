# Agora Video With BytedEffect

*其他语言版本： [简体中文](README.zh.md)*

This tutorial enables you to quickly get started in your development efforts to create an Android app with real-time video calls, voice calls, and interactive broadcasting. With this sample app you can:

Join and leave a channel.
Choose between the front or rear camera.
Real time Sticky/Effect/Filter for video(powered by BytedEffect SDK)

Agora function implementation please refer to [Agora Document](https://docs.agora.io/en/Interactive%20Broadcast/API%20Reference/oc/docs/headers/Agora-Objective-C-API-Overview.html)

Due to the need to use third-party capture when using beauty function, please refer to [Customized Media Source API](https://docs.agora.io/en/Interactive%20Broadcast/raw_data_video_android?platform=Android)  or [Configuring the External Data API](https://docs.agora.io/en/Interactive%20Broadcast/raw_data_video_android?platform=Android)

## 1.Quick Start

This section shows you how to prepare, build, and run the sample application.

### 1.1 Obtain an App ID

To build and run the sample application, get an App ID:

1. Create a developer account at [agora.io](https://dashboard.agora.io/signin/). Once you finish the signup process, you will be redirected to the Dashboard.
2. Navigate in the Dashboard tree on the left to **Projects** > **Project List**.
3. Save the **App ID** from the Dashboard for later use.
4. Generate a temp **Access Token** (valid for 24 hours) from dashboard page with given channel name, save for later use.

5. Open `Agora iOS Tutorial Objective-C.xcodeproj` and edit the `AppID.m` file. Update `<#Your App Id#>` with your app ID, and assign the token variable with the temp Access Token generated from dashboard.

    ```
    NSString *const appID = @"<#Your App ID#>";
    // assign token to nil if you have not enabled app certificate
    NSString *const token = @"<#Temp Token#>";
    ```

1. Update CocoaPods by running:

```
pod install
```

2. Connect your iPhone or iPad device and run the project. Ensure a valid provisioning profile is applied or your project will not run.



## 2.How to use the Agora Module capturer function.

## 2.1 Features
- [x] 	Capturer
	- [x] Camera Capturer
		- [x] Support for front and rear camera switching
		- [x] Support for dynamic resolution switching
		- [x] Support I420, NV12, BGRA pixel format output
		- [x] Support Exposure, ISO
		- [ ] Support ZoomScale
		- [ ] Support Torch
		- [ ] Support watermark
	- [x] Audio Capturer
		- [x] Support single and double channel
		- [x] Support Mute
	- [x]  Video Adapter Filter (For processing the video frame direction required by different modules)
		- [x] Support VideoOutputOrientationModeAdaptative for RTC function
		- [x] Support ...FixedLandscape and ...FixedLandscape for CDN live streaming
- [x] Renderer
	- [x] gles
		- [x] Support glContext Shared
		- [x] Support mirror
		- [x] Support fit、hidden zoom mode


### 2.3 Usage example 

#### 2.3.1 Objective-C

##### How to use Capturer

```objc
// init process manager
self.processingManager = [[VideoProcessingManager alloc] init];
    
// init capturer, it will push pixelbuffer to rtc channel
AGMCapturerVideoConfig *videoConfig = [AGMCapturerVideoConfig defaultConfig];
videoConfig.sessionPreset = AVCaptureSessionPreset1280x720;
videoConfig.fps = 30;
self.capturerManager = [[CapturerManager alloc] initWithVideoConfig:videoConfig delegate:self.processingManager];
    
// add FaceUnity filter and add to process manager
self.videoFilter = [FUManager shareManager];
self.videoFilter.enabled = YES;
[self.processingManager addVideoFilter:self.videoFilter];
[self.capturerManager startCapture];
```

##### Custom Filter

Create a class that implements the `VideoFilterDelegate` protocol `FUManager` , Implement the `processFrame:` method to handle the videoframe .

```objc

#pragma mark - VideoFilterDelegate
/// process your video frame here
- (CVPixelBufferRef)processFrame:(CVPixelBufferRef)frame {
    if(self.enabled) {
        CVPixelBufferRef buffer = [self renderItemsToPixelBuffer:frame];
        return buffer;
    }
    return frame;
}

```

## FAQ

- Please do not use the raw data interface provided by Agora to integrate beauty features
- Videosource internal is a strong reference, you must set nil when not in use, otherwise it will cause a circular reference
- If you encounter a big head problem, please contact technical support

## Developer Environment Requirements
* XCode 8.0 +
* Real devices (iPhone or iPad)
* iOS simulator is NOT supported

## Connect Us

- You can find full API document at [Document Center](https://docs.agora.io/en/)
- You can file bugs about this demo at [issue](https://github.com/AgoraIO/Agora-iOS-Tutorial-Swift-1to1/issues)

## License

The MIT License (MIT).


