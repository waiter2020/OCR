package pub.upc.ocr.photo;

import pub.upc.ocr.my_switch.MyMatrix;

/**
 * Created by  waiter on 18-12-1  上午9:53.
 *
 * @author waiter
 */
public class AppData {
    private static Thread t1;
    private static Thread t2;
    private static MyMatrix myMatrix1;
    private static MyMatrix myMatrix2;

    public static MyMatrix getMyMatrix2() {
        return myMatrix2;
    }

    public static void setMyMatrix2(MyMatrix myMatrix2) {
        AppData.myMatrix2 = myMatrix2;
    }

    public static MyMatrix getMyMatrix1() {
        return myMatrix1;
    }

    public static void setMyMatrix1(MyMatrix myMatrix1) {
        AppData.myMatrix1 = myMatrix1;
    }

    public static Thread getT1() {
        return t1;
    }

    public static void setT1(Thread t1) {
        AppData.t1 = t1;
    }

    public static Thread getT2() {
        return t2;
    }

    public static void setT2(Thread t2) {
        AppData.t2 = t2;
    }


}
