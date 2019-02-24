package pub.upc.ocr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import pub.upc.ocr.R;

import java.util.*;

/**
 * @Author: waiter
 * @Date: 18-12-20 13:30
 * @Description:
 */
public class ListAdapter extends BaseAdapter {

    // 数据集
    private ArrayList<Integer> list = new ArrayList<>();
    // 上下文
    private Context mContext;
    // 存储勾选框状态的map集合
    private Map<Integer, Boolean> isCheck = new HashMap<Integer, Boolean>();
    private Map<Integer,LinearLayout> layoutMap;
    // 构造方法
    public ListAdapter(Context mContext,Map<Integer,LinearLayout> layoutMap,ArrayList<Integer> list) {
        super();
        this.mContext = mContext;
        this.list=list;
        this.layoutMap = layoutMap;
    }


    // 设置数据
    public void setData(ArrayList<Integer> data) {
        this.list = data;
    }

    // 加入数据
    public void addData(Integer bean) {
        // 下标 数据
        list.add( bean);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        // 假设为null就返回一个0
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        CheckBox checkBox = new CheckBox(mContext);
        checkBox.setId(list.get(position));
        LinearLayout viewById = layoutMap.get(list.get(position));
        checkBox.setText(((TextView)viewById.getChildAt(1)).getText());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isCheck.put(compoundButton.getId(),b);
            }
        });
        return checkBox;
    }

    public Map<Integer, Boolean> getIsCheck() {
        return isCheck;
    }
}