package pub.upc.ocr.test;

import pub.upc.ocr.photo.UploadInterface;

/**
 * @Author: waiter
 * @Date: 18-12-19 16:51
 * @Description: 示例添加新功能
 */
public class MyTestUpload implements UploadInterface {
    @Override
    public void upLoad(String url, int i) {
        System.out.println("模拟自定义上传操作");
        System.out.println("结果保持及处理");
    }
}
