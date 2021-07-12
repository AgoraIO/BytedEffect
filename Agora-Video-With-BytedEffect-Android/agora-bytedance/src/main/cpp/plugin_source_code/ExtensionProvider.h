//
// Created by 张涛 on 2020/4/26.
//

#ifndef AGORAWITHBYTEDANCE_EXTENSIONPROVIDER_H
#define AGORAWITHBYTEDANCE_EXTENSIONPROVIDER_H

#include "AgoraRtcKit/NGIAgoraExtensionProvider.h"
#include "ExtensionVideoFilter.h"

namespace agora {
    namespace extension {

        class ByteDanceProcessor;

        class ExtensionProvider : public agora::rtc::IExtensionProvider {
        private:
            static ExtensionProvider* instance_;
            agora_refptr<ByteDanceProcessor> byteDanceProcessor_;
        public:
            static ExtensionProvider* getInstance(){
                if (instance_ == nullptr){
                    instance_ = new agora::RefCountedObject<ExtensionProvider>();
                }
                return instance_;
            };

            ExtensionProvider();

            ~ExtensionProvider();

            PROVIDER_TYPE getProviderType() override;

            virtual void setExtensionControl(rtc::IExtensionControl* control) override;

            virtual agora_refptr<rtc::IAudioFilter> createAudioFilter() override;

            virtual agora_refptr<rtc::IVideoFilter> createVideoFilter() override;

            virtual agora_refptr<rtc::IVideoSinkBase> createVideoSink() override;

            int setExtensionVendor(std::string vendor);
        };
    }
}
#endif //AGORAWITHBYTEDANCE_EXTENSIONPROVIDER_H
