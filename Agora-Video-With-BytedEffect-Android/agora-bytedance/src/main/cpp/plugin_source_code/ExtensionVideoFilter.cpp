//
// Created by 张涛 on 2020/4/26.
//

#include "ExtensionVideoFilter.h"
#include "../logutils.h"
#include "ExtensionProvider.h"
#include <sstream>

namespace agora {
    namespace extension {

        ExtensionVideoFilter::ExtensionVideoFilter(agora_refptr<ByteDanceProcessor> byteDanceProcessor) {
            byteDanceProcessor_ = byteDanceProcessor;
        }

        ExtensionVideoFilter::~ExtensionVideoFilter() {
            byteDanceProcessor_->releaseOpenGL();
        }

        bool ExtensionVideoFilter::adaptVideoFrame(const agora::media::base::VideoFrame &capturedFrame,
                             agora::media::base::VideoFrame &adaptedFrame) {
//            PRINTF_INFO("adaptVideoFrame");
            if (!isInitOpenGL) {
                isInitOpenGL = byteDanceProcessor_->initOpenGL();
            }
            byteDanceProcessor_->processFrame(capturedFrame);
            adaptedFrame = capturedFrame;
            return true;
        }

        size_t ExtensionVideoFilter::setProperty(const char *key, const void *buf,
                                                 size_t buf_size) {
            PRINTF_INFO("arsenal  %s  %s", key, buf);
            std::string stringParameter((char*)buf);
            byteDanceProcessor_->setParameters(stringParameter);
            return 0;
        }

        size_t ExtensionVideoFilter::getProperty(const char *key, void *buf, size_t buf_size) {
            return 0;
        }

        void ExtensionVideoFilter::setEnabled(bool enable) {
            int a = 10;
        }

        bool ExtensionVideoFilter::isEnabled() {
            return true;
        }

        using limits_int16 = std::numeric_limits<int16_t>;
        AdjustVolumeAudioFilter::AdjustVolumeAudioFilter(const char* id,
                                                         agora::rtc::IExtensionControl* control)
                : id_(id ? id : ""), control_(control) {}

        int16_t AdjustVolumeAudioFilter::FloatS16ToS16(float v) {
            static const float kMaxRound = (limits_int16::max)() - 0.5f;
            static const float kMinRound = (limits_int16::min)() + 0.5f;
            if (v > 0) {
                return v >= kMaxRound ? (limits_int16::max)() : static_cast<int16_t>(v + 0.5f);
            }
            return v <= kMinRound ? (limits_int16::min)() : static_cast<int16_t>(v - 0.5f);
        }

        bool AdjustVolumeAudioFilter::adaptAudioFrame(const media::base::AudioPcmFrame& inAudioPcmFrame,
                                                      media::base::AudioPcmFrame& adaptedPcmFrame) {
//            PRINTF_ERROR("adaptAudioFrame");
            size_t length = inAudioPcmFrame.samples_per_channel_ * inAudioPcmFrame.num_channels_;
            for (int idx = 0; idx < length; idx++) {
                adaptedPcmFrame.data_[idx] = FloatS16ToS16(inAudioPcmFrame.data_[idx] * volume_);
            }
            return true;
        }

        int AdjustVolumeAudioFilter::setProperty(const char* key, const void* buf, int buf_size) {
            std::string str_volume = "100";
            if (std::string(key) == "volume") {
                str_volume = std::string(static_cast<const char*>(buf), buf_size);
            }

            int int_volume_ = atoi(str_volume.c_str());
            setVolume(int_volume_);
            return ERR_OK;
        }
    }
}
