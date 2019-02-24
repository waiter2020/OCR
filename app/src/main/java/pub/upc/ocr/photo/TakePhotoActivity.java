package pub.upc.ocr.photo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import pub.upc.ocr.R;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import java.io.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * Created by waiter on ${DATE}.
 */
public class TakePhotoActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {

    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 4;


    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private static final int SELECT_PIC = 3;
    Uri imageUri;
    File mTmpFile;
    //    private Button button;
//    private Button selectPic;
    private int i = 0;
    private ImageView imageView;
    private ImageView imageView1;
    private Context mContext;
    private TextView textView;

    private Toolbar toolbar;


    private UploadInterface uploadInterface;
    private ResultInterInface resultInterInface;

    @Override
    public void onClick(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Holo_Light_Dialog);
        //builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("选择一个图片来源");
        //    指定下拉列表的显示数据

        final String[] model = {"拍照", "从相册选取"};
        //    设置一个下拉的列表选择项
        builder.setItems(model, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        takePhoto();
                        break;
                    case 1:
                        if (!getFilePermission()) {
                            return;
                        }

                        // 打开相册
                        goAlbums();
                        break;
                    default:
                }
            }
        });
        switch (view.getId()) {
            case R.id.imageView:
                i = 0;
                builder.show();
                break;
            case R.id.imageView1:
                i = 1;
                builder.show();
                break;
            default:
        }
    }

    @Override
    public boolean onLongClick(View view) {
        Intent intent = new Intent(mContext, PhotoActivity.class);
        Bitmap bitmap = drawableToBitmap(((ImageView) view).getDrawable());
        String s = saveBitmap(mContext, bitmap);
        intent.putExtra("photo", s);
        startActivity(intent);
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");

        uploadInterface = (UploadInterface) intent.getSerializableExtra("uploadInterface");
        resultInterInface = (ResultInterInface) intent.getSerializableExtra("result");

        mContext = this;
        imageView = findViewById(R.id.imageView);
        imageView1 = findViewById(R.id.imageView1);
        textView = findViewById(R.id.textView);


        toolbar = (Toolbar) findViewById(R.id.head);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        imageView.setOnClickListener(this);
        imageView1.setOnClickListener(this);
        imageView.setOnLongClickListener(this);
        imageView1.setOnLongClickListener(this);

    }


    /**
     * 转byte数组
     *
     * @param bm
     * @return
     */
    private byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    /**
     * 顶栏的返回按键
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean getFilePermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    //显示结果
    public void updateUI() {
        this.textView.setText("请等待");
        if (AppData.getT1() == null || AppData.getT2() == null) {
            this.textView.setText("请继续拍摄");
            return;
        }

        while (AppData.getT1().isAlive() || AppData.getT2().isAlive()) {

        }
        this.textView.setText(resultInterInface.getResult());
        if (resultInterInface.isDrawBitmap()) {
            Bitmap bitmap = resultInterInface.drawBitmap(drawableToBitmap(imageView.getDrawable()));
            imageView.setImageBitmap(bitmap);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/img";
        if (requestCode == CAMERA_REQUEST_CODE) {
//            Intent intent = new Intent(mContext,CropImageActivity.class);
//            intent.putExtra("photo", mTmpFile.getPath());
//            startActivityForResult(intent,666);

            Uri uri =  FileProvider.getUriForFile(mContext, "pub.upc.ocr.provider", mTmpFile);
            Intent it = new Intent("com.android.camera.action.CROP");
            //设置图片 以及格式
            it.setDataAndType(uri, "image/*");
            //是否支持裁剪
            it.putExtra("crop", true);
//            //设置比例
//            it.putExtra("aspectX", 1.4);
//            it.putExtra("aspectY", 1);
//            //设置输出的大小
            it.putExtra("outputX", 2080);
            it.putExtra("outputY", 1500);
//是否支持人脸识别
//    it.putExtra("onFaceDetection", true);
//返回

            it.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            it.putExtra("scale", true);
            it.putExtra("scaleUpIfNeeded", true);
            it.putExtra("return-data", false);
            it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            it.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(it, 2000);

        } else if (requestCode == SELECT_PIC) {
            if (data == null) {
                return;
            }
            String filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            mTmpFile = new File(path, filename + ".jpg");
            mTmpFile.getParentFile().mkdirs();
            Uri uri = data.getData();
//            String path = PhotoClipperUtil.getPath(mContext, uri);
//
//            mTmpFile = new File(path);
//            Intent intent = new Intent(mContext, CropImageActivity.class);
//            intent.putExtra("photo", mTmpFile.getPath());
//            startActivityForResult(intent, 666);
            Intent it = new Intent("com.android.camera.action.CROP");
            //设置图片 以及格式
            it.setDataAndType(uri, "image/*");
            //是否支持裁剪
            it.putExtra("crop", true);
//            //设置比例
//            it.putExtra("aspectX", 2080);
//            it.putExtra("aspectY", 1500);
            it.putExtra("outputX", 2080);
            it.putExtra("outputY", 1500);
//是否支持人脸识别
//    it.putExtra("onFaceDetection", true);
//返回

            it.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            it.putExtra("scale", true);
            it.putExtra("scaleUpIfNeeded", true);
            it.putExtra("return-data", false);
            it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            it.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(it, 2000);
        }
        if (requestCode == 2000 && resultCode == RESULT_OK) {
//            Bitmap bitmap = data.getParcelableExtra("data");
//            //mTmpFile = new File(data.getStringExtra("photo"));
//            mTmpFile = new File(saveBitmap(mContext,bitmap));
            if (mTmpFile.exists()) {
                toUpload();
            }
        }


    }


    private void toUpload() {
        try {
//            if (i == 1) {
//                button.setText("重新拍摄");
//            } else {
//                button.setText("继续拍摄");
//            }
            //i = (i + 1) % 2;
            //超过200k压缩
            if (mTmpFile.length() > 200 * 1024) {
                //2080*1500
                BitmapDrawable drawable = new BitmapDrawable(null, BitmapFactory.decodeFile(mTmpFile.getAbsolutePath()));
                BitmapDrawable drawable1 = (BitmapDrawable) zoomDrawable(drawable, 4160, 3000);
                Bitmap bitmap = drawable1.getBitmap();
                String s = saveBitmap(mContext, bitmap);
                Log.d("cdcd", s);

                Luban.with(this)
                        .load(s)
                        .ignoreBy(100)
                        .setTargetDir(new File(s).getParent())
                        .filter(new CompressionPredicate() {
                            @Override
                            public boolean apply(String path) {
                                return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                            }
                        })
                        .setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {
                                // TODO 压缩开始前调用，可以在方法内启动 loading UI
                                Toast.makeText(mContext, "压缩图片中", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSuccess(File file) {
                                // TODO 压缩成功后调用，返回压缩后的图片文件
                                Toast.makeText(mContext, "压缩图片成功", Toast.LENGTH_LONG).show();
                                doUpload(file);
                            }

                            @Override
                            public void onError(Throwable e) {
                                // TODO 当压缩过程出现问题时调用
                                Toast.makeText(mContext, "压缩图片失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }).launch();
            } else {
                doUpload(mTmpFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void doUpload(File file) {
        Upload upload = new Upload(uploadInterface, file.getAbsolutePath(), i);
        Thread thread = new Thread(upload);
        thread.start();

        if (i == 0) {
            AppData.setT2(thread);
            imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));

        } else {
            AppData.setT1(thread);
            imageView1.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));

        }

        updateUI();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        return;
                    }
                }
                takePhoto();
            }
        }
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("TakePhotoActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }

        }
    }

    private void takePhoto() {

        if (!hasPermission()) {
            return;
        }

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/img";
        if (new File(path).exists()) {
            try {
                new File(path).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mTmpFile = new File(path, filename + ".jpg");
        mTmpFile.getParentFile().mkdirs();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String authority = getPackageName() + ".provider";
            imageUri = FileProvider.getUriForFile(this, authority, mTmpFile);
        } else {
            imageUri = Uri.fromFile(mTmpFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(intent, CAMERA_REQUEST_CODE);

    }

    private boolean hasPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 缩放
     *
     * @param drawable
     * @param w
     * @param h
     * @return
     */
    private Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        return new BitmapDrawable(null, newbmp);
    }

    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = "/storage/emulated/0/";
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + "/";
        }
        try {
            filePic = new File(savePath + generateFileName() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 随机生产文件名
     *
     * @return
     */
    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }


    /**
     * 调用相册
     */
    private void goAlbums() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PIC);
    }


}
