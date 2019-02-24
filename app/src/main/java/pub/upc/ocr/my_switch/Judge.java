package pub.upc.ocr.my_switch;

import java.util.ArrayList;

public class Judge {

    public static int len = 20;


    public static int[][] compare(MyMatrix a, MyMatrix b) {
        ArrayList<int[]> output = new ArrayList<>();

        int id = 0;
        int f = 1;
        for (int i = 0; i < len; ++i) {
            for (int j = 0; j < len; ++j) {
                if ((a.matrix[i][j][1] != b.matrix[i][j][1]) && (a.matrix[i][j][1] != 0) && (b.matrix[i][j][1] != 0)) {
                    int[] bbox = new int[4];
                    f = 0;
                    id = b.matrix[i][j][0];
                    //	System.out.print("差异框的id:  "+id+"   ");
                    bbox[0] = b.coordinates[id][1];
                    bbox[1] = b.coordinates[id][2];
                    bbox[2] = b.coordinates[id][3];
                    bbox[3] = b.coordinates[id][4];
                    output.add(bbox);
                  //  System.out.println(i + " " + j + "差异框的id:  " + id + "   " + bbox[0] + " " + bbox[1] + " " + bbox[2] + " " + bbox[3] + " ");
                }
            }
        }
        return f == 1 ? null : output.toArray(new int[output.size()][4]);

    }

}
