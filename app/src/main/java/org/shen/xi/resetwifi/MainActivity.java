package org.shen.xi.resetwifi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = MainActivity.class.getName();
  private static final String networkHistoryTxtFilepath = "/data/misc/wifi/networkHistory.txt";

  private boolean hasNetworkHistoryFile = false;
  private CheckBox checkBoxNetworkHistory;
  private View viewIsRoot;
  private RootProcess rootProcess;
  private TextView textViewLog;
  private Handler mainHandler;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    checkBoxNetworkHistory = (CheckBox) findViewById(R.id.checkBoxNetworkHistory);
    viewIsRoot = findViewById(R.id.textViewIsRoot);
    textViewLog = (TextView) findViewById(R.id.textViewLog);

    mainHandler = new Handler(getMainLooper()) {
      @Override
      public void handleMessage(Message msg) {
        if (msg.obj == null || msg.obj.getClass().isInstance(String.class)) {
          Log.e(TAG, "cannot handle message");
          return;
        }

        textViewLog.append((String) msg.obj);
      }
    };

    // check change wifi permission
    int accessWifiState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
    int changeWifiState = ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE);
    if (accessWifiState == PackageManager.PERMISSION_DENIED || changeWifiState == PackageManager.PERMISSION_DENIED) {

      mainHandler.sendMessage(createLogMessage("cannot access/change wifi state"));
      return;
    }

    Button buttonResetWifi = (Button) findViewById(R.id.buttonResetWifi);
    buttonResetWifi.setOnClickListener(this);
    rootProcess = RootProcess.getInstance();

    if (!rootProcess.hasRootPermission()) {
      // no root
      mainHandler.sendMessage(createLogMessage("no root permission"));
      return;
    }

    // has root
    mainHandler.sendMessage(createLogMessage("got root permission"));

    // test /data/misc/wifi/networkHistory.txt
    String fileExists = rootProcess.execute(testFileExists(networkHistoryTxtFilepath), true);
    hasNetworkHistoryFile = Boolean.parseBoolean(fileExists);
    mainHandler.post(new Runnable() {
      @Override
      public void run() {
        checkBoxNetworkHistory.setEnabled(hasNetworkHistoryFile);
        checkBoxNetworkHistory.setChecked(hasNetworkHistoryFile);
        viewIsRoot.setVisibility(View.VISIBLE);
      }
    });
  }

  private Message createLogMessage(String message) {
    Message msg = new Message();
    msg.obj = message + "\n";

    return msg;
  }

  private String testFileExists(String filepath) {
    return String.format("[ -e %s ] && echo 'true' || echo 'false'", filepath);
  }

  @Override
  public void onClick(View view) {
    Log.d(TAG, "reset wifi");
    WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    // 1. disable wifi
    if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED
      || wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
      wifiManager.setWifiEnabled(false);
    }

    // 2. remove files
    if (checkBoxNetworkHistory.isChecked()) {
      rootProcess.execute("rm -f " + networkHistoryTxtFilepath, false);
      boolean fileExists = Boolean.parseBoolean(rootProcess.execute(testFileExists(networkHistoryTxtFilepath), true));
      Message message = fileExists
        ? createLogMessage(String.format("failed to delete %s", networkHistoryTxtFilepath))
        : createLogMessage(String.format("successfully deleted %s", networkHistoryTxtFilepath));

      mainHandler.sendMessage(message);
    }

    // 3. enable wifi
    wifiManager.setWifiEnabled(true);
  }
}
