package org.shen.xi.resetwifi;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = MainActivity.class.getName();
  private final String networkHistoryTxtFilepath = "/data/misc/wifi/networkHistory.txt";
  private boolean hasNetworkHistoryFile = false;
  private CheckBox checkBoxNetworkHistory;
  private View isRoot;
  private RootProcess rootProcess;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    checkBoxNetworkHistory = (CheckBox) findViewById(R.id.checkBoxNetworkHistory);
    isRoot = findViewById(R.id.textViewIsRoot);

    Button buttonResetWifi = (Button) findViewById(R.id.buttonResetWifi);
    buttonResetWifi.setOnClickListener(this);
    Log.d(TAG, "mark");
    rootProcess = RootProcess.getInstance();

    if (!rootProcess.hasRootPermission()) {
      // no root
      Log.i(TAG, "no root permission");
      return;
    }

    // has root
    Log.i(TAG, "got root permission");

    // test /data/misc/wifi/networkHistory.txt
    String exists = rootProcess.execute(commandTestExist(networkHistoryTxtFilepath));
    hasNetworkHistoryFile = Boolean.parseBoolean(exists);
    Handler handler = new Handler(getMainLooper());
    handler.post(new Runnable() {
      @Override
      public void run() {
        checkBoxNetworkHistory.setEnabled(hasNetworkHistoryFile);
        checkBoxNetworkHistory.setChecked(hasNetworkHistoryFile);
        isRoot.setVisibility(View.VISIBLE);
      }
    });
  }

  private String commandTestExist(String filepath) {
    return String.format("[ -e %s ] && echo 'true' || echo 'false'", filepath);
  }

  @Override
  public void onClick(View view) {
    WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    // 1. disable wifi
    if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED || wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
      wifiManager.setWifiEnabled(false);
    }

    // 2. remove files
    if (checkBoxNetworkHistory.isChecked()) {
      Log.d(TAG, rootProcess.execute("rm -f " + networkHistoryTxtFilepath));
      String execute = rootProcess.execute(commandTestExist(networkHistoryTxtFilepath));
      Log.i(TAG, String.format("found %s: %s", networkHistoryTxtFilepath, execute));
    }

    // 3. enable wifi
    wifiManager.setWifiEnabled(true);
  }
}
