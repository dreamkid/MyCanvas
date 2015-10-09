package com.demo.hezd.mycanvas.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MyCanvas extends View {
    private static final int STORE_PIC = 0x1;
    private Paint paint;
    private Canvas cacheCanvas;
    private Bitmap cachebBitmap;
    private Path path;

    private int clr_bg, clr_fg;


    public MyCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);

        clr_bg = Color.WHITE;
        clr_fg = Color.BLACK;

        paint = new Paint();
        paint.setAntiAlias(true); // 抗锯齿
        paint.setStrokeWidth(3); // 线条宽度
        paint.setStyle(Paint.Style.STROKE); // 画轮廓
        paint.setColor(clr_fg); // 颜色

        path = new Path();
        // 创建一张屏幕大小的位图，作为缓冲
        int width = getScreenInfo(context).widthPixels-100;
        int height = getScreenInfo(context).heightPixels-100;
        cachebBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        cacheCanvas = new Canvas(cachebBitmap);
        cacheCanvas.drawColor(clr_bg);
    }

    public static DisplayMetrics getScreenInfo(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    public MyCanvas(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(clr_bg);
        // 绘制上一次的，否则不连贯
        canvas.drawBitmap(cachebBitmap, 0, 0, null);
        canvas.drawPath(path, paint);
    }

    /**
     * 清空画布
     */
    public void clear() {
        path.reset();
        cacheCanvas.drawColor(clr_bg);
        invalidate();
    }

    /**
     * 将画布的内容保存到文件
     * @param filename
     * @throws FileNotFoundException
     */
    public void saveToFile(Handler handler,String filename) throws FileNotFoundException {
        File f = new File(filename);
        if(f.exists())
            throw new RuntimeException("文件：" + filename + " 已存在！");

        FileOutputStream fos = new FileOutputStream(new File(filename));
        //将 bitmap 压缩成其他格式的图片数据
        cachebBitmap.compress(CompressFormat.PNG, 50, fos);
        try {
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Message msg = Message.obtain();
        msg.what = STORE_PIC;
        msg.obj = filename;
        handler.sendMessage(msg);
    }

    private float cur_x, cur_y;
    private boolean isMoving;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN : {
                cur_x = x;
                cur_y = y;
                path.moveTo(cur_x, cur_y);
                isMoving = true;
                break;
            }

            case MotionEvent.ACTION_MOVE : {
                if (!isMoving)
                    break;

                // 二次曲线方式绘制
                path.quadTo(cur_x, cur_y, x, y);
                // 下面这个方法貌似跟上面一样
                // path.lineTo(x, y);
                cur_x = x;
                cur_y = y;
                break;
            }

            case MotionEvent.ACTION_UP : {
                // 鼠标弹起保存最后状态
                cacheCanvas.drawPath(path, paint);
                path.reset();
                isMoving = false;
                break;
            }
        }

        // 通知刷新界面
        invalidate();

        return true;
    }

}