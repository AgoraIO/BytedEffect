// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
#import "BEGLView.h"
#import "BERenderHelper.h"
#import <UIKit/UIKit.h>
#import <Masonry.h>
#import <UIView+Toast.h>

@interface BEGLView () {
    GLuint _displayTextureID;
    BOOL _shouldDeleteTextureID;
    
    GLuint _renderProgram;
    GLuint _renderLocation;
    GLuint _renderInputImageTexture;
    GLuint _renderTextureCoordinate;
    GLfloat vertex[8];
    
    unsigned char* saveTextureBuffer;
    GLuint _saveTexture;
    GLuint _frameBuffer;
    
    CGSize _savedSize;
}
@property (nonatomic, strong) CIContext *ciContext;
@property (nonatomic, strong) EAGLContext *glContext;
@property (nonatomic, assign) unsigned int screenWidth;
@property (nonatomic, assign) unsigned int screenHeight;
@property (nonatomic, strong) UIImageView *imageView;

@end

static float TEXTURE_FLIPPED[] = {0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,};
static float CUBE[] = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f,};
static float TEXTURE_RORATION_0[] = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,};

#define TTF_STRINGIZE(x) #x
#define TTF_STRINGIZE2(x) TTF_STRINGIZE(x)
#define TTF_SHADER_STRING(text) @ TTF_STRINGIZE2(text)

static NSString *const RENDER_VERTEX = TTF_SHADER_STRING
(
 attribute vec4 position;
 attribute vec4 inputTextureCoordinate;
 varying vec2 textureCoordinate;
 void main(){
    float scale = 1.05;
    mat4 s = mat4(scale, 0.0, 0.0, 0.0,
                  0.0, scale, 0.0, 0.0,
                  0.0, 0.0, scale, 0.0,
                  0.0, 0.0, 0.0, 1.0);
     textureCoordinate = inputTextureCoordinate.xy;
     gl_Position = s * position;
 }
 );

static NSString *const RENDER_FRAGMENT = TTF_SHADER_STRING
(
 precision mediump float;
 varying highp vec2 textureCoordinate;
 uniform sampler2D inputImageTexture;
 void main()
 {
     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
 }
 );

@implementation BEGLView

- (void)dealloc
{
    NSLog(@"%@", NSStringFromSelector(_cmd));
    free(saveTextureBuffer);
    glDeleteTextures(1, &_saveTexture);
    glDeleteFramebuffers(1, &_frameBuffer);
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.glContext = [[EAGLContext alloc] initWithAPI:kEAGLRenderingAPIOpenGLES2];
        self.ciContext = [CIContext contextWithEAGLContext:self.glContext];
        self.context = self.glContext;
        
        if ([EAGLContext currentContext] != self.glContext) {
            [EAGLContext setCurrentContext:self.glContext];
        }
        [self loadRenderShader];
        CGFloat scale = [UIScreen mainScreen].scale;
        UIInterfaceOrientation orientation = [[UIApplication sharedApplication] statusBarOrientation];
        if (orientation == UIInterfaceOrientationPortrait
            || orientation == UIInterfaceOrientationPortraitUpsideDown) {
            _screenWidth = [UIScreen mainScreen].bounds.size.width * scale;
            _screenHeight = [UIScreen mainScreen].bounds.size.height * scale;
        } else {
            _screenHeight = [UIScreen mainScreen].bounds.size.width * scale;
            _screenWidth = [UIScreen mainScreen].bounds.size.height * scale;
        }
        
        float ratio[2] = {1.0, 1.0};
        [self calcVertex:_screenWidth height:_screenHeight ratios:ratio];
        
        for (int i = 0; i < 4; i ++){
            vertex[i * 2] = CUBE[i * 2] / ratio[1];
            vertex[i * 2 + 1] = CUBE[i * 2 + 1] / ratio[0];
        }
        saveTextureBuffer = malloc(_screenHeight * _screenWidth * 4);
        
        glGenTextures(1, &_saveTexture);
        glGenFramebuffers(1, &_frameBuffer);
        
//        [CSToastManager setQueueEnabled:YES];
    }
    return self;
}

- (void)resetWidthAndHeight {
    CGFloat scale = [UIScreen mainScreen].scale;
    _screenWidth = [UIScreen mainScreen].bounds.size.width * scale;
    _screenHeight = [UIScreen mainScreen].bounds.size.height * scale;
}

/*
 * 将纹理绘制到屏幕上
 */
- (void)renderWithTexture:(unsigned int)name
                     size:(CGSize)size
                  flipped:(BOOL)flipped
      applyingOrientation:(int)orientation
     savingCurrentTexture:(bool)enableSaving{
    if (!glIsTexture(name)) {
        return;
    }
    _displayTextureID = name;
    if (!self.window) {
//        glDeleteTextures(1, &_displayTextureID);
        _shouldDeleteTextureID = NO;
        return;
    }
    
    if (enableSaving) {
        [self textureToImage:name withBuffer:saveTextureBuffer Width:_screenWidth height:_screenHeight];
        
        CGDataProviderRef provider = CGDataProviderCreateWithData(
                                                                  NULL,
                                                                  saveTextureBuffer,
                                                                  _screenWidth * _screenHeight * 4,
                                                                  NULL);
        
        CGColorSpaceRef colorSpaceRef = CGColorSpaceCreateDeviceRGB();
        CGBitmapInfo bitmapInfo = kCGBitmapByteOrderDefault;
        CGColorRenderingIntent renderingIntent = kCGRenderingIntentDefault;
        
        CGImageRef imageRef = CGImageCreate(_screenWidth,
                                            _screenHeight,
                                            8,
                                            4 * 8,
                                            4 * _screenWidth,
                                            colorSpaceRef,
                                            bitmapInfo,
                                            provider,
                                            NULL,
                                            NO,
                                            renderingIntent);
        
        //free(buffer);
        UIImage *uiImage = [UIImage imageWithCGImage:imageRef];
        CGDataProviderRelease(provider);
        CGColorSpaceRelease(colorSpaceRef);
        CGImageRelease(imageRef);
        
        UIImageWriteToSavedPhotosAlbum(uiImage, self, @selector(image:didFinishSavingWithError:contextInfo:), (__bridge void*)self);
    }
    
    if (!_shouldDeleteTextureID){
        _shouldDeleteTextureID = YES;
        [self display];
    }
}

- (void) textureToImage:(GLuint)texture withBuffer:(unsigned char*)buffer Width:(int)rWidth height:(int)rHeight{
    glBindTexture(GL_TEXTURE_2D, _saveTexture);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, rWidth, rHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
    
    glBindFramebuffer(GL_FRAMEBUFFER, _frameBuffer);
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, _saveTexture, 0);
    
    glUseProgram(_renderProgram);
    glVertexAttribPointer(_renderLocation, 2, GL_FLOAT, false, 0, vertex);
    glEnableVertexAttribArray(_renderLocation);
    glVertexAttribPointer(_renderTextureCoordinate, 2, GL_FLOAT, false, 0, TEXTURE_RORATION_0);
    glEnableVertexAttribArray(_renderTextureCoordinate);
    
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texture);
    glUniform1i(_renderInputImageTexture, 0);
    glViewport(0, 0, rWidth, rHeight);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    
    glDisableVertexAttribArray(_renderLocation);
    glDisableVertexAttribArray(_renderTextureCoordinate);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, 0);
    
    glReadPixels(0, 0, rWidth, rHeight, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    [self checkGLError];
}

- (void)image:(UIImage*)image didFinishSavingWithError:(NSError*)error contextInfo:(void *)contextInfo{
    if (error){
        NSLog(@"fail to save photo");
    }else {
        //NSLog(@"image = %@, error = %@, contextInfo = %@", image, error, contextInfo);
        [self makeToast:NSLocalizedString(@"ablum_have_been_saved", nil) duration:(NSTimeInterval)(3.0) position:CSToastPositionCenter];
    }
}

- (void) calcVertex:(int)iWidth height:(int)iHeight ratios:(float *)retRatio{
    int outputWidth = iWidth;
    int outputHeight = iHeight;
    
    int imageHeight = 1280;
    int imageWidth = 720;
    
    float ratio1 = (float)outputWidth / imageWidth;
    float ratio2 = (float)outputHeight / imageHeight;
    
    float ratio = MIN(ratio1, ratio2);
    
    int imageNewHeight = round(imageHeight * ratio);
    int imageNewWidth = round(imageWidth * ratio);
    
    float ratioHeight = imageNewHeight / (float)outputHeight;
    float ratioWidth = imageNewWidth / (float)outputWidth;
    
    retRatio[0] = ratioWidth;
    retRatio[1] = ratioHeight;
}

- (void)drawRect:(CGRect)rect {
    [super drawRect:rect];
    
    CGSize size = self.bounds.size;
    if (!CGSizeEqualToSize(size, _savedSize)) {
        [self resetWidthAndHeight];
        _savedSize = size;
    }
    
    if (_shouldDeleteTextureID) {
        
        glClearColor(0.0, 0.0, 0.0, 0.0);
        glClear(GL_COLOR_BUFFER_BIT| GL_DEPTH_BUFFER_BIT);
        
        glUseProgram(_renderProgram);
        
        glVertexAttribPointer(_renderLocation, 2, GL_FLOAT, false, 0, vertex);
        glEnableVertexAttribArray(_renderLocation);
        glVertexAttribPointer(_renderTextureCoordinate, 2, GL_FLOAT, false, 0, TEXTURE_FLIPPED);
        glEnableVertexAttribArray(_renderTextureCoordinate);
        
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, _displayTextureID);
        glUniform1i(_renderInputImageTexture, 0);
        glViewport(0, 0, _screenWidth, _screenHeight);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        
        glDisableVertexAttribArray(_renderLocation);
        glDisableVertexAttribArray(_renderTextureCoordinate);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        glUseProgram(0);
        glFlush();
        
//        glDeleteTextures(1, &_displayTextureID);
        _shouldDeleteTextureID = NO;
    }
}

/*
 * load resize shader
 */
- (void) loadRenderShader{
    GLuint vertexShader = [BERenderHelper compileShader:RENDER_VERTEX withType:GL_VERTEX_SHADER];
    GLuint fragmentShader = [BERenderHelper compileShader:RENDER_FRAGMENT withType:GL_FRAGMENT_SHADER];
    
    _renderProgram = glCreateProgram();
    glAttachShader(_renderProgram, vertexShader);
    glAttachShader(_renderProgram, fragmentShader);
    glLinkProgram(_renderProgram);
    
    GLint linkSuccess;
    glGetProgramiv(_renderProgram, GL_LINK_STATUS, &linkSuccess);
    if (linkSuccess == GL_FALSE){
        NSLog(@"BERenderHelper link shader error");
    }
    
    glUseProgram(_renderProgram);
    _renderLocation = glGetAttribLocation(_renderProgram, "position");
    _renderTextureCoordinate = glGetAttribLocation(_renderProgram, "inputTextureCoordinate");
    _renderInputImageTexture = glGetUniformLocation(_renderProgram, "inputImageTexture");
    
    if (vertexShader)
        glDeleteShader(vertexShader);
    
    if (fragmentShader)
        glDeleteShader(fragmentShader);
}


- (void)releaseContext {
    [EAGLContext setCurrentContext:nil];
}


#pragma mark - getter
- (UIImageView*)imageView{
    if(!_imageView){
        _imageView = [[UIImageView alloc] init];
    }
    return _imageView;
}

- (void)checkGLError {
    int error = glGetError();
    if (error != GL_NO_ERROR) {
        NSLog(@"%d", error);
        @throw [NSException exceptionWithName:@"GLError" reason:@"error " userInfo:nil];
    }
}
@end
