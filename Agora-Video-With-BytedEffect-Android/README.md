# Agora Video With BytedEffect

This tutorial enables you to quickly get started in your development efforts to create an Android app with real-time video calls, voice calls, and interactive broadcasting. With this sample app you can:

* Join and leave a channel.
* Choose between the front or rear camera.
* Real time Sticky/Effect/Filter for video(powered by BytedEffect SDK)


## Prerequisites

* Android Studio 3.1 or above.
* Android device (e.g. Nexus 5X). A real device is recommended because some simulators have missing functionality or lack the performance necessary to run the sample.

## Quick Start
This section shows you how to prepare, build, and run the sample application.

### Create an Account and Obtain an App ID
In order to build and run the sample application you must obtain an App ID:

1. Create a developer account at [agora.io](https://dashboard.agora.io/signin/). Once you finish the signup process, you will be redirected to the Dashboard.
2. Navigate in the Dashboard tree on the left to **Projects** > **Project List**.
3. Locate the file **app/src/main/res/values/strings.xml** and replace <#YOUR APP ID#> with the App ID in the dashboard.

```xml
<string name="agora_app_id"><#YOUR APP ID#></string>
```

### ByteEffect Configuration
1. Contact labcv_business@bytedance.com and get ByteEffect SDK and resource bundle

2. Modify applicationId in app/build.gradle to the id in the corresponding byte authorization certificate, and set LICENSE_NAME in bytedance/src/main/java/com/byteddance/effect/ResourceHelper.java to the name of the corresponding byte authorization certificate.

3. Put the ByteEffect resource bundle file in the bytedance/src/main/resource directory.

### Run project
Open the project with Android Studio, connect the Android test device, and run the project.

## Bytedance Extension
Bytedance Extension is available in agora extension marketplace. Using extension is more easier than using raw video data. You can refer the [demo](https://github.com/AgoraIO-Community/AgoraMarketplace/tree/master/ByteDance) to use the extension.

## Contact Us

- If you have questions, take a look at [FAQ](https://docs.agora.io/cn/faq) first
- Dive into [Agora SDK Samples](https://github.com/AgoraIO) to see more tutorials
- Take a look at [Agora Use Case](https://github.com/AgoraIO-usecase) for more complicated, real-world use cases
- More projects maintained by community can be found at [Agora Community](https://github.com/AgoraIO-Community)
- You can find full API documentation at [Document Center](https://docs.agora.io/en/)
- You can ask questions or see others' solutions in [Stack Overflow](https://stackoverflow.com/questions/tagged/agora.io)
- If you find a bug of this project, please post an issue here [issue](https://github.com/AgoraIO/FaceUnity/issues)

## License

The MIT License (MIT)
