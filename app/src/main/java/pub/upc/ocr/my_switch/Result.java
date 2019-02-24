package pub.upc.ocr.my_switch;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import pub.upc.ocr.photo.AppData;
import pub.upc.ocr.photo.ResultInterInface;

/**
 * @Author: waiter
 * @Date: 18-12-19 16:40
 * @Description:
 */
public class Result implements ResultInterInface {
    private int[][] compare=null;
    @Override
    public String getResult() {


        compare = Judge.compare(AppData.getMyMatrix1(), AppData.getMyMatrix2());
        if (compare==null) {

            return "匹配";
        } else {
            return "不匹配";
        }
    }

    @Override
    public Boolean isDrawBitmap() {
        return compare != null;
    }

    @Override
    public Bitmap drawBitmap(Bitmap bitmap) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        //不填充
        paint.setStyle(Paint.Style.STROKE);
        //线的宽度
        paint.setStrokeWidth(3);

        for (int[] a:compare) {
            canvas.drawRect(a[0], a[1], a[2], a[3], paint);
        }
        return mutableBitmap;
    }
}
