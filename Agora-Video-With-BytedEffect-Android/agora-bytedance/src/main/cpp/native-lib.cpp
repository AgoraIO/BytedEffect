#include <jni.h>
#include <string>
#include <plugin_source_code/error_code.h>
//#include "AgoraRtcKit/IAgoraService.h"
#include "plugin_source_code/ExtensionProvider.h"
#include "logutils.h"
#include "plugin_source_code/JniHelper.h"
//#include "AgoraRtcKit/AgoraRefPtr.h"
#include "plugin_source_code/JniHelper.h"
#include "plugin_source_code/EGLCore.h"

using namespace agora::extension;
//static agora::extension::ExtensionProvider* extensionProvider = nullptr;

#define CHECK_EXTENSION_PROVIDER_INT if(!extensionProvider) { \
                                PRINTF_ERROR("Agora extension call api: %s err: %d", __FUNCTION__, ERROR_CODE::ERR_NOT_INIT_EXTENSION_PROVIDER); \
                                return ERROR_CODE::ERR_NOT_INIT_EXTENSION_PROVIDER; \
                             }

#define CHECK_EXTENSION_PROVIDER_VOID if(!extensionProvider) { \
                                PRINTF_ERROR("Agora extension call api: %s err: %d", __FUNCTION__, ERROR_CODE::ERR_NOT_INIT_EXTENSION_PROVIDER); \
                                return; \
                             }

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    PRINTF_INFO("JNI_OnLoad");
    JniHelper* jniHelper = JniHelper::createJniHelper(vm);

    JNIEnv *env;
    int status = vm->GetEnv((void**)&env, JNI_VERSION_1_6);

    jclass clz = env->FindClass("io/agora/extension/ExtensionManager");
    jniHelper->agoraByteDanceNativeClz = reinterpret_cast<jclass>(env->NewGlobalRef(clz));
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved) {
    PRINTF_INFO("JNI_OnUnload");
//    CHECK_EXTENSION_PROVIDER_VOID;
    agora::extension::ExtensionProvider* extensionProvider = agora::extension::ExtensionProvider::getInstance();
    if (extensionProvider) {
        delete(extensionProvider);
//        extensionProvider = nullptr;
    }
    JniHelper::release();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_agora_extension_ExtensionManager_nativeGetExtensionProvider(
        JNIEnv* env,
        jclass clazz, jobject context, jstring jVendor) {
    if (AndroidContextHelper::getContext() == nullptr){
        jobject globalContext = env->NewGlobalRef(context);
        AndroidContextHelper::setContext(globalContext);
//        extensionProvider = new agora::RefCountedObject<agora::extension::ExtensionProvider>();
    }
    const char *vendor = env->GetStringUTFChars(jVendor, nullptr);
    ExtensionProvider* provider = agora::extension::ExtensionProvider::getInstance();
    provider->setExtensionVendor(vendor);
    env->ReleaseStringUTFChars(jVendor, vendor);
    return reinterpret_cast<intptr_t>(provider);
}
