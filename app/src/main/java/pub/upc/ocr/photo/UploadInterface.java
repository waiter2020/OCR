package pub.upc.ocr.photo;

import java.io.Serializable;

/**
 * @Author: waiter
 * @Date: 18-12-19 16:21
 * @Description:
 */
public interface UploadInterface extends Serializable {

    /**
     * 上传自定义上传操作接口
     * @param url 图片路径
     * @param i 第几张图片，1为第一张，0为第二张
     */
    void upLoad(String url,int i);
}
