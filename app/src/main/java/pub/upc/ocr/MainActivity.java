package pub.upc.ocr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.gson.Gson;
import pub.upc.ocr.adapter.ListAdapter;
import pub.upc.ocr.data.AppData;
import pub.upc.ocr.my_switch.Result;
import pub.upc.ocr.my_switch.Upload;
import pub.upc.ocr.photo.TakePhotoActivity;
import pub.upc.ocr.test.MyTestResult;
import pub.upc.ocr.test.MyTestUpload;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author waiter
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private Context mContext;
    private Toolbar toolbar;
    private GridLayout mGridLayout;
    private SharedPreferences sharedPreferences;
    private int columnCount;
    private int screenWidth;
    private int screenHight;

    private ListAdapter listAdapter;

    private AppData appData;
    @SuppressLint("UseSparseArrays")
    private Map<Integer,LinearLayout> linearLayouts = new HashMap<>();
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.my_switch:
                intent = new Intent(MainActivity.this, TakePhotoActivity.class);
                intent.putExtra("title", "开关识别");
                intent.putExtra("uploadInterface", new Upload());
                intent.putExtra("result", new Result());
                startActivity(intent);
                break;
            case R.id.my_test:
                intent = new Intent(MainActivity.this, TakePhotoActivity.class);
                intent.putExtra("title", "测试功能");
                intent.putExtra("uploadInterface", new MyTestUpload());
                intent.putExtra("result", new MyTestResult());
                startActivity(intent);
                break;
            default:
        }
    }

    @Override
    public boolean onLongClick(View view) {
        LinearLayout parent = (LinearLayout) view.getParent();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Holo_Light_Dialog);
        final String[] model = {"删除"};
        builder.setTitle("请选择操作");
        //    设置一个下拉的列表选择项
        builder.setItems(model, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Integer id = parent.getId();
                ArrayList<Integer> disEnableList = appData.getDisEnableList();
                ArrayList<Integer> enableList = appData.getEnableList();

                enableList.remove(id);
                disEnableList.add(id);

                appData.setDisEnableList(disEnableList);
                appData.setEnableList(enableList);
                saveDate();
                initGrid();
            }
        });
        builder.show();
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        init();
        initData();
        initGrid();

    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.head);
        toolbar.setTitle("OCR");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(tooBarListener);
    }
    Toolbar.OnMenuItemClickListener tooBarListener =new Toolbar.OnMenuItemClickListener(){

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.add_menu:
                    showAddMenu();
                    break;
                    default:
            }
            return true;
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void initData() {
        mGridLayout = (GridLayout) findViewById(R.id.grid_layout);
        sharedPreferences = this.getSharedPreferences("main", Context.MODE_PRIVATE);

        String date = sharedPreferences.getString("app_date", null);
        if (date == null) {
            appData = new AppData();
            ArrayList<Integer> disEnableList = appData.getDisEnableList();
            for (int i = 0; i < mGridLayout.getChildCount() ; i++) {
                LinearLayout linearLayout = (LinearLayout) mGridLayout.getChildAt(i);
                disEnableList.add(linearLayout.getId());
            }
            appData.setDisEnableList(disEnableList);
            saveDate();
        } else {
            Gson gson = new Gson();
            appData = gson.fromJson(date, AppData.class);
        }
    }

    private void saveDate() {
        Gson gson = new Gson();
        String s = gson.toJson(appData);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("app_date", s);
        edit.apply();
    }


    private void initMenu() {
        ArrayList<Integer> disEnableList = appData.getDisEnableList();
        disEnableList.forEach(e->{
            mGridLayout.removeView(linearLayouts.get(e));
        });
        ArrayList<Integer> enableList = appData.getEnableList();
        enableList.forEach(e->{
            mGridLayout.addView(linearLayouts.get(e));
        });
    }

    private void initGrid() {
        columnCount = mGridLayout.getColumnCount();
        int rowCount = mGridLayout.getRowCount();
        WindowManager windowManager = this.getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHight = dm.heightPixels;
        for (int i = 0; i < mGridLayout.getChildCount(); i++) {
            LinearLayout linearLayout = (LinearLayout) mGridLayout.getChildAt(i);
            ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
            layoutParams.width = screenWidth / columnCount;
            layoutParams.height = screenHight / rowCount;
            linearLayout.setLayoutParams(layoutParams);

            View imageBtn = linearLayout.getChildAt(0);
            imageBtn.setOnClickListener(this);
            imageBtn.setOnLongClickListener(this);
            linearLayouts.put(linearLayout.getId(),linearLayout);
        }
        mGridLayout.removeAllViews();
        initMenu();
    }

    private void showAddMenu() {
        LayoutInflater from = LayoutInflater.from(this);
        final View inflate = from.inflate(R.layout.addmenu, null);

        ListView menuList = inflate.findViewById(R.id.menu_list);


        listAdapter = new ListAdapter(mContext, linearLayouts,appData.getDisEnableList());
        menuList.setAdapter(listAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(inflate);
        builder.setTitle("请选择添加的功能");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<Integer, Boolean> isCheck = listAdapter.getIsCheck();
                ArrayList<Integer> disEnableList = appData.getDisEnableList();
                ArrayList<Integer> enableList = appData.getEnableList();
                isCheck.forEach((key, value) -> {
                    if (value) {
                        if (!enableList.contains(key)) {
                            enableList.add(key);
                        }
                        disEnableList.remove(key);
                    } else {
                        enableList.remove(key);
                        if (!disEnableList.contains(key)) {
                            disEnableList.add(key);
                        }
                    }
                });
                appData.setDisEnableList(disEnableList);
                appData.setEnableList(enableList);
                saveDate();
                initGrid();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
    }
}
