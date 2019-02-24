package pub.upc.ocr.photo;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * @Author: waiter
 * @Date: 18-12-19 16:33
 * @Description: 结果处理接口
 */
public interface ResultInterInface extends Serializable {
    /**
     * 获取结果（匹配或不匹配或者别的）
     * @return 返回字符串
     */
    String getResult();

    /**
     * 判断是否需要画图
     * @return
     */
    Boolean isDrawBitmap();

    /**
     * 图片绘制接口
     * @param bitmap 传入第二张图片
     * @return 返回已绘制的图片
     */
    Bitmap drawBitmap(Bitmap bitmap);
}
