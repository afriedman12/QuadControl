package processing.test.quadcopter;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.support.v7.app.AppCompatActivity;

import processing.android.PFragment;
import processing.android.CompatUtils;
import processing.core.PApplet;

public class MainActivity extends AppCompatActivity {
  private PApplet sketch;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FrameLayout frame = new FrameLayout(this);
    frame.setId(CompatUtils.getUniqueViewId());
    setContentView(frame, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
                                                     ViewGroup.LayoutParams.MATCH_PARENT));
    
    sketch = new Quadcopter();
    
    PFragment fragment = new PFragment(sketch);
    fragment.setView(frame, this);

    //connect to BTH
    BluetoothDevice device = BTManager.init(this);

    ConnectThread connectThread = new ConnectThread(device);
    connectThread.start();
  }
  
  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[],
                                         int[] grantResults) {
    if (sketch != null) {
      sketch.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }  
  
  @Override
  public void onNewIntent(Intent intent) {
    if (sketch != null) {
      sketch.onNewIntent(intent);
    }  
  }
}
