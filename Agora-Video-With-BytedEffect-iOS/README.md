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
Use RTC to collect video streams and then send them to ByteSDK SDK for beauty treatment

### Usage example 

```objc
// set delegate
[self.rtcEngineKit setVideoFrameDelegate:self];

// set Capturer Configuration
 AgoraCameraCapturerConfiguration *captuer = [[AgoraCameraCapturerConfiguration alloc] init];
captuer.cameraDirection = AgoraCameraDirectionFront;
[self.rtcEngineKit setCameraCapturerConfiguration:captuer];
    
AgoraVideoEncoderConfiguration *configuration = [[AgoraVideoEncoderConfiguration alloc] init];
configuration.dimensions = CGSizeMake(1280, 720);
[self.rtcEngineKit setVideoEncoderConfiguration: configuration];
    
// on delegate handler
- (BOOL)onCaptureVideoFrame:(AgoraOutputVideoFrame *)videoFrame {
    CVPixelBufferRef pixelBuffer = [self.videoFilter processFrame:videoFrame.pixelBuffer];
    videoFrame.pixelBuffer = pixelBuffer;
    return YES;
}

```
##### Custom Filter

Create a class that implements the `VideoFilterDelegate` protocol `ByteDanceFilter` , Implement the `processFrame:` method to handle the videoframe .

```objc

#pragma mark - VideoFilterDelegate
/// process your video frame here
- (CVPixelBufferRef)processFrame:(CVPixelBufferRef)frame timeStamp:(double)timeStamp{
    if(self.enabled) {
        BEProcessResult *result = [_processor process:frame timeStamp:timeStamp];
        return result.pixelBuffer;
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

- If you want to use plug-in integration, see [cloud market](https://docs.agora.io/cn/extension_customer/quickstart_faceunity?platform=iOS)
- You can find full API document at [Document Center](https://docs.agora.io/en/)
- You can file bugs about this demo at [issue](https://github.com/AgoraIO/Agora-iOS-Tutorial-Swift-1to1/issues)

## License

The MIT License (MIT).


