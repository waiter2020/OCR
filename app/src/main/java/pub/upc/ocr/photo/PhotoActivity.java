package pub.upc.ocr.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import pub.upc.ocr.R;

import java.io.File;
import java.io.Serializable;

/**
 * @author waiter
 */
public class PhotoActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ImageView imageView;

    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        init();
    }

    private void init(){

        imageView = findViewById(R.id.photo);

        toolbar = (Toolbar) findViewById(R.id.head);
        toolbar.setTitle("查看大图");
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar!=null){
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        path = intent.getStringExtra("photo");
        Bitmap bitmap= BitmapFactory.decodeFile(path);

        imageView.setImageBitmap(bitmap);
    }

    /**
     * 顶栏的返回按键
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        super.onDestroy();
    }
}
