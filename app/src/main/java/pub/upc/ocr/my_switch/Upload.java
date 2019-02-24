package pub.upc.ocr.my_switch;

import android.util.Log;
import pub.upc.ocr.photo.AppData;
import pub.upc.ocr.photo.UploadInterface;

import java.io.*;
import java.net.Socket;

/**
 * Created by  waiter on 18-11-30  下午11:05.
 *
 * @author waiter
 */
public class Upload implements UploadInterface {

    private StringBuffer result=null;

    /**
     * 上传
     */
    @Override
    public void upLoad(String path, int i)  {
        result = new StringBuffer();
        try {
            Socket socket = new Socket("121.251.252.203", 9114);
            File file = new File(path);
            BufferedInputStream br = new BufferedInputStream(new FileInputStream(file));


            OutputStream outputStream = socket.getOutputStream();

            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = br.read(buf)) != -1) {
                // 向BufferedOutputStream中写入数据
                outputStream.write(buf, 0, len);
            }


            outputStream.flush();// 刷新缓冲流
            socket.shutdownOutput();// 禁用此套接字的输出流

            // 3.获取输入流，取得服务器的信息
            InputStream is = socket.getInputStream();
            BufferedReader brs = new BufferedReader(new InputStreamReader(is));

            String info = null;
            while ((info = brs.readLine()) != null) {
                result.append(info);
            }
            System.out.println("收到返回字符串！");
         //   socket.shutdownInput();// 禁用此套接字的输入流
            Log.d("dsd",result.toString());
            MyMatrix myMatrix = Transform2.stringToMatirix(result.toString());
            if (i==0){
                AppData.setMyMatrix2(myMatrix);
            }else {
                AppData.setMyMatrix1(myMatrix);
            }

            // 4.关闭资源
            outputStream.close();
           // bos.close();
            br.close();
            is.close();
            brs.close();
            socket.close();
        }catch (Exception e){
            System.out.println("服务器连接异常！！！");
            e.printStackTrace();
        }
    }
}
