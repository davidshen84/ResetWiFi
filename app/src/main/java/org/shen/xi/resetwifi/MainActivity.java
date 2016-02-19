package org.shen.xi.resetwifi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final String networkHistoryTxtFilepath = "/data/misc/wifi/networkHistory.txt";

  private boolean hasNetworkHistoryFile = false;
  private CheckBox checkBoxNetworkHistory;
  private TextView textViewMessage;
  private Handler mainHandler;

  @Inject
  private RootProcess rootProcess;

  @Inject
  @Named("app tracker")
  private Tracker appTracker;

  @Inject
  private WifiManagerWrapper wifiManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    checkBoxNetworkHistory = (CheckBox) findViewById(R.id.checkBoxNetworkHistory);
    textViewMessage = (TextView) findViewById(R.id.textViewMessage);
    textViewMessage.setMovementMethod(ScrollingMovementMethod.getInstance());

    mainHandler = new Handler(getMainLooper()) {
      @Override
      public void handleMessage(Message msg) {
        if (msg.obj == null || !(msg.obj instanceof String)) {
          Log.e(TAG, "cannot handle message");
          return;
        }

        textViewMessage.append((String) msg.obj);
      }
    };

    // check change wifi permission
    int accessWifiState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
    int changeWifiState = ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE);
    if (accessWifiState == PackageManager.PERMISSION_DENIED || changeWifiState == PackageManager.PERMISSION_DENIED) {
      mainHandler.sendMessage(createLogMessage("cannot access/change wifi state"));
      finish();
    } else {
      Button buttonResetWifi = (Button) findViewById(R.id.buttonResetWifi);
      buttonResetWifi.setOnClickListener(this);
    }

    Injector injector = Guice.createInjector(new MainModule(this));
    injector.injectMembers(this);
  }

  @Override
  protected void onStart() {
    super.onStart();

    appTracker.setScreenName(TAG);
    appTracker.send(new HitBuilders.ScreenViewBuilder().build());
  }

  @Override
  protected void onResume() {
    super.onResume();
    try {
      rootProcess.start();
    } catch (IOException ignored) {
    }

    // clean previous message
    textViewMessage.setText("");
    if (!rootProcess.hasRootPermission()) {
      // no root
      mainHandler.sendMessage(createLogMessage("no root permission"));
    } else {
      // has root
      mainHandler.sendMessage(createLogMessage("got root permission"));

      update();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    try {
      rootProcess.stop();
    } catch (IOException ignored) {
    }
  }

  private void update() {
    // test /data/misc/wifi/networkHistory.txt
    String fileExists = rootProcess.execute(testFileExists(networkHistoryTxtFilepath), true);
    hasNetworkHistoryFile = Boolean.parseBoolean(fileExists);

    if (hasNetworkHistoryFile) {
      mainHandler.post(new Runnable() {
        @Override
        public void run() {
          checkBoxNetworkHistory.setEnabled(hasNetworkHistoryFile);
          checkBoxNetworkHistory.setChecked(hasNetworkHistoryFile);
        }
      });
    }
  }

  private Message createLogMessage(String message) {
    Message msg = new Message();
    msg.obj = String.format("> %s\n", message);

    return msg;
  }

  private String testFileExists(String filepath) {
    return String.format("[ -e %s ] && echo true || echo false", filepath);
  }

  @Override
  public void onClick(View view) {
    // 1. disable wifi
    if (wifiManager.isOn()) {
      wifiManager.off();
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
    wifiManager.on();

    // 4. update UI after 1 sec delay
    mainHandler.postDelayed(new Runnable() {
      @Override
      public void run() {
        update();
        appTracker.send(new HitBuilders.EventBuilder("action", "reset wifi").build());
      }
    }, 1000);
  }
}
