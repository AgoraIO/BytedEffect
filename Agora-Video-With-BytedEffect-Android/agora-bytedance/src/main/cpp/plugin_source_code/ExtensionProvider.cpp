//
// Created by 张涛 on 2020/4/26.
//

#include "ExtensionProvider.h"
#include "../logutils.h"
#include "ByteDanceProcessor.h"

namespace agora {
    namespace extension {
        ExtensionProvider* ExtensionProvider::instance_;
        ExtensionProvider::ExtensionProvider() {
            PRINTF_INFO("ExtensionProvider create");
            byteDanceProcessor_ = new agora::RefCountedObject<ByteDanceProcessor>();
        }

        ExtensionProvider::~ExtensionProvider() {
            PRINTF_INFO("ExtensionProvider destroy");
            instance_ = nullptr;
        }

        int ExtensionProvider::setExtensionVendor(std::string vendor) {
            PRINTF_INFO("ExtensionProvider vendor %s", vendor.c_str());
            byteDanceProcessor_->setExtensionVendor(vendor.c_str());
            return 0;
        }

        agora_refptr<agora::rtc::IVideoFilter> ExtensionProvider::createVideoFilter() {
            PRINTF_INFO("ExtensionProvider::createVideoFilter");
            auto videoFilter = new agora::RefCountedObject<agora::extension::ExtensionVideoFilter>(byteDanceProcessor_);
            return videoFilter;
        }

        agora_refptr<agora::rtc::IAudioFilter> ExtensionProvider::createAudioFilter() {
            PRINTF_ERROR("ExtensionProvider::createAudioFilter");
            return new agora::RefCountedObject<agora::extension::AdjustVolumeAudioFilter>();
        }

        agora_refptr<agora::rtc::IVideoSinkBase> ExtensionProvider::createVideoSink() {
            return nullptr;
        }

        ExtensionProvider::PROVIDER_TYPE ExtensionProvider::getProviderType() {
            return ExtensionProvider::PROVIDER_TYPE::LOCAL_VIDEO_FILTER;
        }

        void ExtensionProvider::setExtensionControl(rtc::IExtensionControl* control){
            byteDanceProcessor_->setExtensionControl(control);
        }
    }
}
