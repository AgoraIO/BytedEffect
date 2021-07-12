//
// Created by 张涛 on 2020/4/26.
//

#ifndef AGORAWITHBYTEDANCE_EXTENSIONVIDEOFILTER_H
#define AGORAWITHBYTEDANCE_EXTENSIONVIDEOFILTER_H

#include "AgoraRtcKit/NGIAgoraMediaNode.h"
#include <AgoraRtcKit/AgoraRefCountedObject.h>
#include "AgoraRtcKit/AgoraRefPtr.h"
#include "ByteDanceProcessor.h"

namespace agora {
    namespace extension {
        class ExtensionVideoFilter : public agora::rtc::IVideoFilter {
        public:
            ExtensionVideoFilter(agora_refptr<ByteDanceProcessor> byteDanceProcessor);

            ~ExtensionVideoFilter();

            bool adaptVideoFrame(const agora::media::base::VideoFrame &capturedFrame,
                                 agora::media::base::VideoFrame &adaptedFrame) override;

            void setEnabled(bool enable) override;

            bool isEnabled() override;

            size_t setProperty(const char *key, const void *buf, size_t buf_size) override;

            size_t getProperty(const char *key, void *buf, size_t buf_size) override;

        private:
            agora_refptr<ByteDanceProcessor> byteDanceProcessor_;
            bool isInitOpenGL = false;
        protected:
            ExtensionVideoFilter() = default;

        };

        class AdjustVolumeAudioFilter : public agora::rtc::IAudioFilter {
        public:
            AdjustVolumeAudioFilter(const char* id = nullptr,
                                    agora::rtc::IExtensionControl* control = nullptr);
            virtual ~AdjustVolumeAudioFilter() = default;
            bool adaptAudioFrame(const media::base::AudioPcmFrame& inAudioPcmFrame,
                                 media::base::AudioPcmFrame& adaptedPcmFrame) override;
            void setEnabled(bool enable) override { enabled_ = enable; }
            bool isEnabled() const override { return enabled_; }
            int setProperty(const char* key, const void* buf, int buf_size) override;
            int getProperty(const char* key, void* buf, int buf_size) const override { return ERR_OK; }
            void setVolume(int volume) { volume_ = volume / 100.0f; }
            const char* getName() const override { return "ByteDance"; }

        private:
            static int16_t FloatS16ToS16(float v);

        private:
            std::atomic_bool enabled_ = {true};
            std::atomic<float> volume_ = {1.0f};
            std::string id_;
            agora::rtc::IExtensionControl* control_ = nullptr;
        };
    }
}


#endif //AGORAWITHBYTEDANCE_EXTENSIONVIDEOFILTER_H
