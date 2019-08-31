package orb.slam2.android;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author buptzhaofang@163.com Mar 24, 2016 1:00:15 PM
 */
public class DataSetModeActivity extends Activity implements OnClickListener {

  Button ChooseDataSet, ChooseCalibration, ChooseVOC, finish, ChooseDataSet2;
  TextView DatasetTxt, CalibrationTxt, VOCPathText, DatasetTxt2;

  private static final int REQUEST_CODE_1 = 1;  
  private static final int REQUEST_CODE_2 = 2;   
  private static final int REQUEST_CODE_3 = 3;   
  private static final int REQUEST_CODE_4 = 4;
  private Intent fileChooserIntent;
  public static final String EXTRA_FILE_CHOOSER = "file_chooser";
  private static final int REQUEST_CAMERA_PERMISSION = 200;
  private String PICPath, VOCPath, TUMPath, PICPath2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
    setContentView(R.layout.activity_dataset_mode);
    ChooseDataSet = (Button) findViewById(R.id.choose_dataset);
    ChooseDataSet2 = (Button) findViewById(R.id.choose_dataset_2);
    ChooseCalibration = (Button) findViewById(R.id.choose_calibration);
    ChooseVOC = (Button) findViewById(R.id.choose_voc);
    finish = (Button) findViewById(R.id.finish);
    finish.setOnClickListener(this);
    ChooseDataSet.setOnClickListener(this);
    ChooseDataSet2.setOnClickListener(this);
    ChooseCalibration.setOnClickListener(this);
    ChooseVOC.setOnClickListener(this);
    DatasetTxt = (TextView) findViewById(R.id.dataset_path_txt);
    DatasetTxt2 = (TextView) findViewById(R.id.dataset_path_txt_2);
    CalibrationTxt = (TextView) findViewById(R.id.cal_path_txt);
    VOCPathText = (TextView) findViewById(R.id.voc_path_txt);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
          new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET,
              Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
              Manifest.permission.LOCATION_HARDWARE, Manifest.permission.READ_EXTERNAL_STORAGE},
          REQUEST_CAMERA_PERMISSION);
      Log.i("DataMode", "request for permission");
    }
    fileChooserIntent = new Intent(this,
        FileChooserActivity.class);
  }

  @Override
  public void onClick(View v) {
    // TODO Auto-generated method stub
    switch (v.getId()) {
      case R.id.choose_dataset_2:
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
          startActivityForResult(fileChooserIntent, REQUEST_CODE_4);
        } else
          Toast.makeText(DataSetModeActivity.this, "can't find SDcard", Toast.LENGTH_LONG).show();
        break;
      case R.id.choose_dataset:
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
          startActivityForResult(fileChooserIntent, REQUEST_CODE_1);
        else
          Toast.makeText(DataSetModeActivity.this, "can't find SDcard", Toast.LENGTH_LONG).show();
        break;
      case R.id.choose_calibration:
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
          startActivityForResult(fileChooserIntent, REQUEST_CODE_2);
        else
          Toast.makeText(DataSetModeActivity.this, "can't find SDcard", Toast.LENGTH_LONG).show();
        break;
      case R.id.choose_voc:
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
          startActivityForResult(fileChooserIntent, REQUEST_CODE_3);
        else
          Toast.makeText(DataSetModeActivity.this, "can't find SDcard", Toast.LENGTH_LONG).show();
        break;
      case R.id.finish:
        if (!TextUtils.isEmpty(PICPath) && !TextUtils.isEmpty(TUMPath) && !TextUtils.isEmpty(VOCPath)) {
          Bundle bundle = new Bundle();
          bundle.putString("voc", VOCPath);
          bundle.putString("calibration", TUMPath);
          bundle.putString("images", PICPath);
          bundle.putString("images2", PICPath2);
          Intent intent = new Intent(DataSetModeActivity.this, ORBSLAMForDataSetActivity.class);
          intent.putExtras(bundle);
          startActivity(intent);
          finish();
        } else {
          Toast.makeText(DataSetModeActivity.this, "None of image path or Calibration path can be empty!", Toast.LENGTH_LONG).show();
        }

        break;
    }
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_CANCELED) {
      Toast.makeText(DataSetModeActivity.this, "no return value", Toast.LENGTH_LONG).show();
      return;
    }
    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_1) {
      //获取路径名
      PICPath = data.getStringExtra(EXTRA_FILE_CHOOSER);
      DatasetTxt.setText("dataset path is " + PICPath);
      return;
    }
    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_4) {
      PICPath2 = data.getStringExtra(EXTRA_FILE_CHOOSER);
      DatasetTxt2.setText("dataset path is " + PICPath2);
      return;
    }
    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_2) {
      //获取路径名
      TUMPath = data.getStringExtra(EXTRA_FILE_CHOOSER);
      CalibrationTxt.setText("calibration path is " + TUMPath);
      return;
    }
    if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_3) {
      //获取路径名
      VOCPath = data.getStringExtra(EXTRA_FILE_CHOOSER);
      VOCPathText.setText("calibration path is " + VOCPath);
      return;
    }

  }

}
