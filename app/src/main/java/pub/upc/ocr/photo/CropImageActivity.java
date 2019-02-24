package pub.upc.ocr.photo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import pub.upc.ocr.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author: waiter
 * @Date: 19-1-18 23:02
 * @Description:
 */
public class CropImageActivity extends AppCompatActivity {

    //地址
    String path="";
    //裁剪框
    SeniorCropImageView mseniorCropImageView;
    Context mActivity;
    ProgressBar pb;
    //图片资源
    ImageView img;
    //裁剪后的图片
    Bitmap bitmap=null;
    //保存图片按钮
    Button confirm=null;
    Button cancel = null;
    ImageButton rotate = null;
    Intent data = new Intent();
    //相册跳转需要的参数
    private final String IMAGE_TYPE="image/*";
    private final int IMAGE_CODE=111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crop_image);
        mActivity = this;
        //初始化界面组件
        initView();

        //设置裁剪框的外边距（padding）
        mseniorCropImageView.setCropRectPadding(0f);
        //保存裁剪后的图片
        bitmap=mseniorCropImageView.saveCrop();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveBitmap(mActivity, mseniorCropImageView.saveCrop());
                }catch (Exception e){
                    e.printStackTrace();
                }
                Toast.makeText(mActivity,"图片保存成功",Toast.LENGTH_LONG).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setResult(000,new Intent());
                finish();
            }
        });

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    rotate();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        Intent intent = getIntent();
        path = intent.getStringExtra("photo");
        mseniorCropImageView.setImagePath(path);
//        data.putExtra("photo",path);
//        setResult(333,data);
    }


    //初始化界面组件
    private void initView(){
        confirm=(Button) this.findViewById(R.id.confirm);
        cancel = findViewById(R.id.cancel);
        rotate  = findViewById(R.id.imageButton);
        //实例化裁剪对象
        mseniorCropImageView =(SeniorCropImageView) this.findViewById(R.id.my_crop);
    }


    private void rotate(){
        Bitmap bitmap = ((BitmapDrawable) mseniorCropImageView.getDrawable()).getBitmap();
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        mseniorCropImageView.setImageBitmap(bitmap1);

    }

    //保存图片
    public void saveBitmap(Context context, Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        data.putExtra("photo",file.getPath());
        setResult(333,data);
        finish();
    }
}

