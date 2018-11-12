//package com.base.danmaku;
//
//import com.wmlives.heihei.R;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.text.StaticLayout;
//import android.util.AttributeSet;
//import android.view.SurfaceView;
//import android.view.TextureView;
//import android.widget.LinearLayout;
//
//public class DanmakuSurfaceView extends TextureView {
//
//    public DanmakuSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        // TODO Auto-generated constructor stub
//    }
//
//    public DanmakuSurfaceView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        // TODO Auto-generated constructor stub
//    }
//
//    public DanmakuSurfaceView(Context context) {
//        super(context);
//        // TODO Auto-generated constructor stub
//    }
//
//    private void drawItem(Canvas canvas,DanmakuItem item)
//    {
//        switch(item.type)
//        {
//        case DanmakuItem.TYPE_NORMAL :
//            break;
//        case DanmakuItem.TYPE_COLOR_BG :
//            break;
//        case DanmakuItem.TYPE_GIFT :
//            break;
//        case DanmakuItem.TYPE_SYSTEM :
//            break;
//        }
//    }
//    
//    private void drawNormalItem(Canvas canvas,DanmakuItem item)
//    {
//        String text = item.text;
//        Paint paint = new Paint();
//        paint.setColor(getResources().getColor(R.color.hh_color_a));
//        int x = item.x + item.paddingLeft;
//        int y = item.y + item.paddingTop;
//        canvas.drawText(text, x, y, paint);
//    }
//    
//    private void drawColorBgItem(Canvas canvas,DanmakuItem item)
//    {
//        
//    }
//    
//    private void drawGiftItem(Canvas canvas,DanmakuItem item)
//    {
//        
//    }
//    
//    private void drawSystemItem(Canvas canvas,DanmakuItem item)
//    {
//        
//    }
//    
//}
