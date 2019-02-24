package pub.upc.ocr.data;

import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @Author: waiter
 * @Date: 18-12-20 11:00
 * @Description:
 */
public class AppData implements Serializable {
   private ArrayList<Integer> enableList;
   private  ArrayList<Integer> disEnableList;

    public AppData() {
        enableList = new ArrayList<>();
        disEnableList = new ArrayList<>();
    }

    public ArrayList<Integer> getEnableList() {
        return enableList;
    }

    public void setEnableList(ArrayList<Integer> enableList) {
        this.enableList = enableList;
    }

    public ArrayList<Integer> getDisEnableList() {
        return disEnableList;
    }

    public void setDisEnableList(ArrayList<Integer> disEnableList) {
        this.disEnableList = disEnableList;
    }
}
