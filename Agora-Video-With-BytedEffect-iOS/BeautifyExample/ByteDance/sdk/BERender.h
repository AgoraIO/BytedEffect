// Copyright (C) 2019 Beijing Bytedance Network Technology Co., Ltd.
#import "bef_effect_ai_public_define.h"
#import <OpenGLES/ES2/glext.h>
#import <UIKit/UIKit.h>
#import <CoreVideo/CoreVideo.h>

/// input buffer type
typedef NS_ENUM(NSInteger, BEFormatType) {
    // unknow format
    BE_UNKNOW,
    // 8bit R G B A
    BE_RGBA,
    // 8bit B G R A
    BE_BGRA,
    // video range, 8bit Y1 Y2 Y3 Y4... U1 V1...
    BE_YUV420V,
    // full range, 8bit Y1 Y2 Y3 Y4... U1 V1...
    BE_YUV420F
};

@interface BEPixelBufferInfo : NSObject

@property (nonatomic, assign) BEFormatType format;
@property (nonatomic, assign) int width;
@property (nonatomic, assign) int height;
@property (nonatomic, assign) int bytesPerRow;

@end

@interface BERender : NSObject

/// use several cached texture
@property(nonatomic, assign) bool useCacheTexture;

/// init output texture binding CVPixelBuffer
/// @param width with
/// @param height height
/// @param format format
- (BOOL)initOutputTextureAndCVPixelBufferWithWidth:(int)width height:(int)height format:(BEFormatType)format;

/// get output CVPixelBuffer binding output texture
- (CVPixelBufferRef)getOutputPixelBuffer;

/// transfor CVPixelBuffer to buffer
/// @param pixelBuffer CVPixelBuffer
/// @param outputFormat format of buffer
- (unsigned char *)transforCVPixelBufferToBuffer:(CVPixelBufferRef)pixelBuffer outputFormat:(BEFormatType)outputFormat;

/// transfor CVPixelBuffer to texture
/// @param pixelBuffer original CVPixelBuffer
- (GLuint)transforCVPixelBufferToTexture:(CVPixelBufferRef)pixelBuffer;

/// transfor texture to buffer
/// @param texture texture
/// @param width with of texture
/// @param height height of texture
/// @param outputFormat format of buffer
/// @param bytesPerRowPointer pointer of bytesPerRow, would be changed according to outputFormat
- (unsigned char *)transforTextureToBuffer:(GLuint)texture width:(int)width height:(int)height outputFormat:(BEFormatType)outputFormat bytesPerRowPointer:(int *)bytesPerRowPointer;

/// transfor buffer to texture
/// @param buffer buffer
/// @param width with of buffer
/// @param height height of buffer
/// @param bytesPerRow bytesPerRow of buffer
/// @param inputFormat format of buffer
- (GLuint)transforBufferToTexture:(unsigned char *)buffer width:(int)width height:(int)height bytesPerRow:(int)bytesPerRow inputFormat:(BEFormatType)inputFormat;

/// transfor buffer to CVPixelBuffer without CVPixelBuffer
/// @param buffer buffer
/// @param width with of buffer
/// @param height height of buffer
/// @param bytesPerRow bytesPerRow of buffer
/// @param inputFormat format of buffer
/// @param outputFormat format of CVPixelBuffer
- (CVPixelBufferRef)transforBufferToCVPixelBuffer:(unsigned char *)buffer width:(int)width height:(int)height bytesPerRow:(int)bytesPerRow inputFormat:(BEFormatType)inputFormat outputFormat:(BEFormatType)outputFormat;

/// transfor buffer to CVPixelBuffer with exsiting CVPixelBuffer
/// @param buffer buffer
/// @param pixelBuffer CVPixelBuffer
/// @param width wiht of buffer
/// @param height height of buffer
/// @param bytesPerRow bytesPerRow of buffer
/// @param inputFormat format of buffer
/// @param outputFormat format of CVPixelBuffer
- (CVPixelBufferRef)transforBufferToCVPixelBuffer:(unsigned char *)buffer pixelBuffer:(CVPixelBufferRef)pixelBuffer width:(int)width height:(int)height bytesPerRow:(int)bytesPerRow inputFormat:(BEFormatType)inputFormat outputFormat:(BEFormatType)outputFormat;

/// transfor buffer to UIImage
/// @param buffer buffer
/// @param width width of buffer
/// @param height height of buffer
/// @param bytesPerRow bytesPerRow of buffer
/// @param inputFormat format of buffer
- (UIImage *)transforBufferToUIImage:(unsigned char *)buffer width:(int)width height:(int)height bytesPerRow:(int)bytesPerRow inputFormat:(BEFormatType)inputFormat;

/// generate output texture
/// @param width with of texture
/// @param height height of texture
- (GLuint)getOutputTexture:(int)width height:(int)height;

/// get format of CVPixelBuffer
/// @param pixelBuffer CVPixelBuffer
- (BEFormatType)getCVPixelBufferFormat:(CVPixelBufferRef)pixelBuffer;

/// get glFormat from BEFormatType
/// @param format BEFormatType
- (GLenum)getGlFormat:(BEFormatType)format;

/// get CVPixelBuffer info
/// @param pixelBuffer pixelBuffer
- (BEPixelBufferInfo *)getCVPixelBufferInfo:(CVPixelBufferRef)pixelBuffer;
@end
