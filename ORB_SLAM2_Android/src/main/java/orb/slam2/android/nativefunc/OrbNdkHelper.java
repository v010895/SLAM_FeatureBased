package orb.slam2.android.nativefunc;


/**
 *
 * @author buptzhaofang@163.com Mar 26, 2016 8:48:13 PM
 *
 */
public class OrbNdkHelper {
	
	public static native void initSystemWithParameters(String VOCPath,String calibrationPath);
	public static native void insertFrame(double curTimeStamp,int[] data,int[] dataR,int w,int h);
	public static native int[] startCurrentStereo(double curTimeStamp, int[] data, int[] data2, int w, int h);
	public static native int[] startCurrentORB(double curTimeStamp,int[] data,int w,int h);
	public native static int[] startCurrentORBForCamera(double curTimeStamp,long addr,int w,int h);
	public native static int[] trackRealTime();
	public native static void glesInit();  
    public native static void glesRender();  
    public native static void glesResize(int width, int height);

}
