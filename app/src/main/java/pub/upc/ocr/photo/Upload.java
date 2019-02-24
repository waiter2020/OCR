package pub.upc.ocr.photo;

/**
 * Created by  waiter on 18-11-30  下午11:05.
 *
 * @author waiter
 */
public class Upload implements Runnable{

    private String path;
    private Integer i;
    private UploadInterface uploadInterface;
    public Upload( UploadInterface uploadInterface,String path, Integer i) {
        this.path = path;
        this.i = i;
        this.uploadInterface = uploadInterface;
    }

    @Override
    public void run() {
        //调用上传
        uploadInterface.upLoad(path,i);

    }


}
