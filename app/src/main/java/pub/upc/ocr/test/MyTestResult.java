package pub.upc.ocr.test;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import pub.upc.ocr.photo.ResultInterInface;

/**
 * @Author: waiter
 * @Date: 18-12-19 16:53
 * @Description: 测试自定义结果处理
 */
public class MyTestResult implements ResultInterInface {
    @Override
    public String getResult() {
        System.out.println("这里进行结果处理操作");
        return "匹配";
    }

    @Override
    public Boolean isDrawBitmap() {
        //根据需要选择是否绘制图片
        //可以加判断if。。。。然后return
        return true;
    }
    /**
     * 因图片绘制的复杂性，每个功能的绘制要求可能都不同，所以提供自定义绘图
     */
    @Override
    public Bitmap drawBitmap(Bitmap bitmap) {

        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        //不填充
        paint.setStyle(Paint.Style.STROKE);
        //线的宽度
        paint.setStrokeWidth(10);


        canvas.drawRect(200, 200, 1800, 1300, paint);

        return mutableBitmap;

    }
}
