package orb.slam2.android;

import java.io.File;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import orb.slam2.android.nativefunc.OrbNdkHelper;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * ORB Activity For DataSetMode
 * 
 * @author buptzhaofang@163.com Mar 24, 2016 4:13:32 PM
 *
 */
public class ORBSLAMForDataSetActivity extends Activity implements OnClickListener,
		Renderer {
	ImageView imgSource, imgDealed;
	Button start, stop;
	String vocPath, calibrationPath, ImgPath, ImgPath2;
	LinearLayout linear;
	ArrayList<int[]> imageLeftArray;
	ArrayList<int[]> imageRightArray;
	private static final int INIT_FINISHED=0x00010001;
	private static final int UPDATE_MSG = 2;
	private AssetManager mAssetMgr = null;

	private final int CONTEXT_CLIENT_VERSION = 3;
	private GLSurfaceView mGLSurfaceView;

	static {
		System.loadLibrary("ORB_SLAM2_EXCUTOR");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_dataset_orb);
		imgDealed = (ImageView) findViewById(R.id.img_dealed);
		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);
		start.setOnClickListener(this);
		stop.setOnClickListener(this);
		vocPath = getIntent().getStringExtra("voc");
		calibrationPath = getIntent().getStringExtra("calibration");
		ImgPath = getIntent().getStringExtra("images");
		ImgPath2 = getIntent().getStringExtra("images2");
		mGLSurfaceView = new GLSurfaceView(this);
		mGLSurfaceView.setEGLContextClientVersion(2);
		linear = (LinearLayout) findViewById(R.id.surfaceLinear);
			//mGLSurfaceView.setEGLContextClientVersion(CONTEXT_CLIENT_VERSION);
		mGLSurfaceView.setRenderer(this);
		linear.addView(mGLSurfaceView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		imageLeftArray = new ArrayList<>();
		imageRightArray = new ArrayList<>();
		if (TextUtils.isEmpty(vocPath) || TextUtils.isEmpty(calibrationPath)) {
			Toast.makeText(this, "null param,return!", Toast.LENGTH_LONG)
					.show();
			finish();
		} else {
			Toast.makeText(ORBSLAMForDataSetActivity.this, "init has been started!",
					Toast.LENGTH_LONG).show();
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					OrbNdkHelper.initSystemWithParameters(vocPath,
							calibrationPath);
							Log.e("information==========>",
									"init has been finished!");
							frameHandler.sendEmptyMessage(UPDATE_MSG);
							myHandler.sendEmptyMessage(INIT_FINISHED);
				}
			}).start();
		}

	}
	Handler frameHandler = new Handler() {
		@Override
        public void handleMessage(Message msg) {   
             switch (msg.what) {   
                  case UPDATE_MSG:   
                	  Toast.makeText(ORBSLAMForDataSetActivity.this,
								"init has been finished!",
								Toast.LENGTH_LONG).show();
          			new Thread(new Runnable() {

        				@Override
        				public void run() {
        					if (!TextUtils.isEmpty(ImgPath)) {
								File[] filenameL;
								File[] filenameR;
        						File dir = new File(ImgPath);
								File dir2 = new File(ImgPath2);
								filenameL = dir.listFiles();
								filenameR = dir2.listFiles();
								Arrays.sort(filenameL);
								Arrays.sort(filenameR);
								int j = 0;
        						if (dir.isDirectory()) {
									for (int i=0; i< filenameL.length; i++)  {

											currentL = BitmapFactory.decodeFile(filenameL[i].getAbsolutePath());
											currentR = BitmapFactory.decodeFile(filenameR[i].getAbsolutePath());
											timestamp = Double.parseDouble(filenameL[i].getName()
													.substring(0,filenameL[i].getName().length() - 5));

											// TODO Auto-generated method stub
											w = currentL.getWidth();
											h = currentR.getHeight();

											current_L = new int[w*h];
											current_R = new int[w*h];

											currentL.getPixels(current_L, 0, w, 0, 0, w, h);
											currentR.getPixels(current_R, 0, w, 0, 0, w, h);
											OrbNdkHelper.insertFrame(timestamp,current_L,current_R,w,h);
									}
        						}
        					} else {
        						Toast.makeText(ORBSLAMForDataSetActivity.this, "empty images",
        								Toast.LENGTH_LONG).show();
        					}
        				}
        			}).start();
                       break;


             }
             super.handleMessage(msg);   
        }   
   };
	Handler myHandler = new Handler() {  
        public void handleMessage(Message msg) {   
             switch (msg.what) {   
                  case INIT_FINISHED:
          			new Thread(new Runnable() {

        				@Override
        				public void run() {
        					if (!TextUtils.isEmpty(ImgPath)) {
								File[] filenameL;
								File[] filenameR;
								File dir = new File(ImgPath);
								File dir2 = new File(ImgPath2);
								filenameL = dir.listFiles();
								filenameR = dir2.listFiles();
								Arrays.sort(filenameL);
								Arrays.sort(filenameR);
        						if (dir.isDirectory()) {
        							for (int i=0; i< filenameL.length; i++) {	
        								// TODO Auto-generated method stub
        								int[] resultInt = OrbNdkHelper.trackRealTime();
        								resultImg = Bitmap.createBitmap(640, 502,
        										Config.RGB_565);
        								resultImg
        										.setPixels(resultInt, 0, 640, 0, 0, 640, 502);
        								runOnUiThread(new Runnable() {
        									@Override
        									public void run() {
        										// TODO Auto-generated method stub
        										imgDealed.setImageBitmap(resultImg);
        									}
        								});
        							}
        						}
        					} else {
        						Toast.makeText(ORBSLAMForDataSetActivity.this, "empty images",
        								Toast.LENGTH_LONG).show();
        					}
        				}
        			}).start();
                       break;   
             }   
             super.handleMessage(msg);   
        }   
   };

	private Bitmap tmp, resultImg,currentL, currentR;
	private double timestamp;
	int[] current_L, current_R;
	int w,h;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.start:
			break;
		case R.id.stop:
			break;
		}
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		//OrbNdkHelper.readShaderFile(mAssetMgr);
		OrbNdkHelper.glesInit();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		OrbNdkHelper.glesResize(width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		OrbNdkHelper.glesRender();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mGLSurfaceView.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mGLSurfaceView.onPause();
	}

	private boolean detectOpenGLES30() {
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();

		return (info.reqGlEsVersion >= 0x30000);
	}
}
