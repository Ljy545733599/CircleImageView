package com.linjunyi.circleimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * 圆形头像框控件
 *
 * @author ljy545733599@163.com
 */
public class CircleImageView extends AppCompatImageView {

    /**
     * 绘图的Paint
     **/
    private Paint mBitmapPaint;

    /**
     * 圆角的半径
     **/
    private int mRadius;

    /**
     * 3x3 矩阵，主要用于缩小放大
     **/
    private Matrix mMatrix;

    /**
     * 渲染图像，使用图像为绘制图形着色
     **/
    private BitmapShader mBitmapShader;

    /**
     * view的宽度
     **/
    private int mWidth;

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 初始化变量
        mMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 取view长宽的较小值，除以2作为圆的半径,并重新设置View的大小
        mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
        mRadius = mWidth / 2;
        setMeasuredDimension(mWidth, mWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        setUpShader();

        canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint);
    }

    /**
     * 初始化BitmapShader
     */
    private void setUpShader() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        Bitmap bmp = drawableToBitmap(drawable);
        if (bmp == null) {
            return;
        }
        // 将bmp作为着色器，就是在指定区域内绘制bmp

        if (mBitmapShader == null) {
            mBitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        }

        float scale = getScale(bmp);

        // shader的变换矩阵，对图片进行缩放
        mMatrix.setScale(scale, scale);
        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);
        // 设置shader
        mBitmapPaint.setShader(mBitmapShader);
    }

    /**
     * 获取缩放比例
     */
    private float getScale(Bitmap bmp) {
        float scale;
        int bSize = Math.min(bmp.getWidth(), bmp.getHeight());
        scale = mWidth * 1.0f / bSize;
        return scale;
    }

    /**
     * drawable转bitmap
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        // 如果drawable是BitmapDrawable的一个实例，则直接返回getBitmap
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }

        // 获取drawable固有的宽高
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 创建一个bitmap为canvas的操作对象，绘制的内容显示在bitmap上
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // 确定绘制的区域
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }
}