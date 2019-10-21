//
//  TextSliderView.m
//  oc_demo
//
//  Created by QunZhang on 2019/8/3.
//  Copyright © 2019 wuruoye. All rights reserved.
//

#import "BETextSliderView.h"

@interface BETextSliderView () {
    BOOL _isShowText;
    BOOL _isInTouch;
    
    // 基本布局数据
    CGFloat _width;
    CGFloat _height;
    CGFloat _realPaddingLeft;
    CGFloat _realPaddingRight;
    
    // 上升/下降动画的速度相关
    CGFloat _animationSlop;
    CGFloat _animationProgress;
    
    // 文字偏移范围，即文字的高度
    CGFloat _maxTextOffset;
    CGFloat _minTextOffset;
    
    // 字体大小范围
    CGFloat _maxTextSize;
    CGFloat _minTextSize;
    
    // 实时绘制相关数据
    CGFloat _currentX;
    CGFloat _currentY;
    CGFloat _currentTextOffset;
    CGSize _currentTextSize;
    NSMutableDictionary *_textAttribute;
    NSMutableParagraphStyle *_textStyle;
}

@end

@implementation BETextSliderView

# pragma mark - 初始化操作

- (instancetype)init {
    self = [super init];
    if (self) {
        [self initDefaultSize];
    }
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self initDefaultSize];
        [self initSize:frame];
    }
    return self;
}


// 设置默认各数据的默认值
- (void)initDefaultSize {
    _activeLineColor = [UIColor whiteColor];
    _inactiveLineColor = [UIColor grayColor];
    _circleColor = [UIColor whiteColor];
    _textColor = [UIColor grayColor];
    
    _lineHeight = DEFAULT_LINE_HEIGHT;
    _circleRadius = DEFAULT_CIRCLE_RADIUS;
    _textSize = DEFAULT_TEXT_SIZE;
    _paddingLeft = DEFAULT_PADDING_LEFT;
    _paddingRight = DEFAULT_PADDING_RIGHT;
    _paddingBottom = DEFAULT_PADDING_BOTTOM;
    _textOffset = DEFAULT_TEXT_OFFSET;
    _animationTime = DEFAULT_ANIMATION_TIME;
    
    _progress = 0;
}

- (void)initSize:(CGRect)frame {
    _width = frame.size.width;
    _height = frame.size.height;
    
    _realPaddingLeft = _circleRadius/2 + _paddingLeft;
    _realPaddingRight = _circleRadius/2 + _paddingRight;
    
    _minTextOffset = _paddingBottom + _lineHeight / 2;
    _maxTextOffset = _textOffset + _minTextOffset;
    _currentTextOffset = _minTextOffset;
    
    _maxTextSize = _textSize;
    _minTextSize = 0.F;
    
    _animationSlop = 1.F / _animationTime;
    _animationProgress = 0.F;
    
}

# pragma mark - 绘制操作
- (void)drawRect:(CGRect)rect {
    [super drawRect:rect];
    [self checkSize:rect];
    
    // 初始化环境
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetShouldAntialias(context, true);
    CGContextClearRect(context, rect);
    
    // 依次画出每一个部分
    [self computeSize];
    [self drawLine:context];
    [self drawCircle:context];
    [self drawText:context];
    
    // 是否需要继续绘制动画
    if (_isShowText) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self setNeedsDisplay];
        });
    }
}

- (void)drawLine:(CGContextRef)context {
    CGContextSetLineWidth(context, _lineHeight);
    CGContextSetLineCap(context, kCGLineCapRound);
    
    CGFloat offset = -_lineHeight / 2 + _lineHeight * _progress;
    
    // draw left line
    CGContextClipToRect(context, CGRectMake(0, 0, _currentX + offset, _height));
    CGContextSetStrokeColorWithColor(context, [_activeLineColor CGColor]);
    CGContextMoveToPoint(context, _realPaddingLeft, _currentY);
    CGContextAddLineToPoint(context, _width - _realPaddingRight, _currentY);
    CGContextDrawPath(context, kCGPathFillStroke);
    CGContextResetClip(context);
    
    // draw right line
    CGContextClipToRect(context, CGRectMake(_currentX + offset, 0, _width, _height));
    CGContextSetStrokeColorWithColor(context, [_inactiveLineColor CGColor]);
    CGContextMoveToPoint(context, _realPaddingLeft, _currentY);
    CGContextAddLineToPoint(context, _width - _realPaddingRight, _currentY);
    CGContextDrawPath(context, kCGPathFillStroke);
    CGContextResetClip(context);
}

- (void)drawCircle:(CGContextRef)context {
    CGFloat radius = _isShowText ? MAX(MAX(_currentTextSize.width, _currentTextSize.height), _circleRadius) : _circleRadius;
    // x/y 在原来的基础上向左/向上偏移半个半径，使圆形居中
    CGFloat x = _currentX - radius / 2;
    CGFloat y = _height - _currentTextOffset - radius / 2;
    
    CGContextSetFillColorWithColor(context, [_circleColor CGColor]);
    CGContextAddEllipseInRect(context, CGRectMake(x, y, radius, radius));
    
    CGContextDrawPath(context, kCGPathFill);
}

-(void)drawText:(CGContextRef)context {
    if (!_isShowText) {
        return;
    }
    
    // x/y 在原来的基础上向左/向上偏移半个宽/高，使文字矩形居中
    CGFloat x = _currentX - _currentTextSize.width / 2;
    CGFloat y = _height - _currentTextOffset - _currentTextSize.height / 2;
    
    NSString *text = @((int)(_progress * 100)).stringValue;
    CGRect rect = CGRectMake(x, y, _currentTextSize.width, _currentTextSize.height);
    [text drawWithRect:rect options:NSStringDrawingTruncatesLastVisibleLine | NSStringDrawingUsesLineFragmentOrigin attributes:_textAttribute context:nil];
}

#pragma mark - 监听手势操作

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [super touchesBegan:touches withEvent:event];
    
    // 设置延时显示 text
    _isInTouch = true;
    [self performSelector:@selector(startShowText) withObject:nil afterDelay:0.5];
    
    UITouch *touch = [touches anyObject];
    [self dispatchX:[touch locationInView:self].x];
}

- (void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [super touchesMoved:touches withEvent:event];
    
    // 获取手的位置
    UITouch *touch = [touches anyObject];
    [self dispatchX:[touch locationInView:self].x];
}

- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [super touchesEnded:touches withEvent:event];
    // 取消延迟显示
    _isInTouch = false;
    [NSObject cancelPreviousPerformRequestsWithTarget:self];
}

- (void)touchesCancelled:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
    [super touchesCancelled:touches withEvent:event];
    // 取消延迟显示
    _isInTouch = false;
    [NSObject cancelPreviousPerformRequestsWithTarget:self];
}

# pragma mark - 工具方法
// 设置展示文字，并请求重绘
- (void)startShowText {
    _isShowText = true;
    [self setNeedsDisplay];
}

// 剪裁 x ，使 x 的值处于绘制区域
- (CGFloat)clip:(CGFloat)x {
    if (x < _realPaddingLeft) {
        x = _realPaddingLeft;
    }
    if (x > _width - _realPaddingRight) {
        x = _width - _realPaddingRight;
    }
    return x;
}

// 根据 x 的位置，计算出当前的进度，将其分发出去，并请求重绘
- (void)dispatchX:(CGFloat)x {
    x = [self clip:x];
    CGFloat progress = (x - _realPaddingLeft) / (_width - _realPaddingLeft - _realPaddingRight);
    
    if (_progress != progress) {
        _progress = progress;
        // 震动反馈
        if (progress == 0 || progress == 1) {
            if (@available(iOS 10.0, *)) {
                UIImpactFeedbackGenerator *generator = [[UIImpactFeedbackGenerator alloc] initWithStyle: UIImpactFeedbackStyleLight];
                [generator prepare];
                [generator impactOccurred];
            } else {
                // Fallback on earlier versions
            }
        }
        // 分发结果
        if ([self.delegate respondsToSelector:@selector(progressDidChange:)]) {
            [self.delegate progressDidChange:_progress];
        }
        // 请求重绘
        if (!_isShowText) {
            [self setNeedsDisplay];
        }
    }
}

// 根据当前的进度，计算当前 x y 的值，并在需要展示文字时，计算文字展示动画的进度并设置对应的值
- (void)computeSize {
    CGFloat width = _width - _realPaddingLeft - _realPaddingRight;
    _currentX = width * _progress + _realPaddingLeft;
    _currentY = _height - _paddingBottom - _lineHeight / 2;
    
    if (_isShowText) {
        _animationProgress += _isInTouch ? _animationSlop : -_animationSlop;
        if (_animationProgress > 1) {
            _animationProgress = 1;
        } else if (_animationProgress < 0) {
            _isShowText = false;
            _animationProgress = 0;
        }
        
        _currentTextOffset = (_maxTextOffset - _minTextOffset) * _animationProgress + _minTextOffset;
        CGFloat textSize = (_maxTextSize - _minTextSize) * _animationProgress + _minTextSize;
        
        NSString *text = @(100).stringValue;
        if (!_textStyle) {
            _textStyle = [[NSMutableParagraphStyle defaultParagraphStyle] mutableCopy];
            _textStyle.alignment = NSTextAlignmentCenter;
        }
        if (!_textAttribute) {
            _textAttribute = [NSMutableDictionary dictionary];
            _textAttribute[NSForegroundColorAttributeName] = _textColor;
            _textAttribute[NSFontAttributeName] = [UIFont systemFontOfSize:textSize];
            _textAttribute[NSParagraphStyleAttributeName] = _textStyle;
        } else {
            _textAttribute[NSForegroundColorAttributeName] = _textColor;
            _textAttribute[NSFontAttributeName] = [UIFont systemFontOfSize:textSize];
        }
        CGSize size = [text sizeWithAttributes:_textAttribute];
        _currentTextSize = size;
    }
}

- (void)checkSize:(CGRect)rect {
    if (_width != CGRectGetWidth(rect) || _height != CGRectGetHeight(rect)) {
        [self initSize:rect];
    }
}

#pragma mark - setter
- (void)setTextOffset:(CGFloat)textOffset {
    _textOffset = textOffset;
    [self initSize:self.frame];
}

- (void)setTextSize:(CGFloat)textSize {
    _textSize = textSize;
    [self initSize:self.frame];
}

- (void)setCircleRadius:(CGFloat)circleRadius {
    _circleRadius = circleRadius;
    [self initSize:self.frame];
}

- (void)setAnimationTime:(NSInteger)animationTime {
    _animationTime = animationTime;
    [self initSize:self.frame];
}

- (void)setPaddingLeft:(CGFloat)paddingLeft {
    _paddingLeft = paddingLeft;
    [self initSize:self.frame];
}

- (void)setPaddingRight:(CGFloat)paddingRight {
    _paddingRight = paddingRight;
    [self initSize:self.frame];
}

- (void)setPaddingBottom:(CGFloat)paddingBottom {
    _paddingBottom = paddingBottom;
    [self initSize:self.frame];
}

- (void)setProgress:(CGFloat)progress {
    _progress = progress;
    [self setNeedsDisplay];
}

@end
