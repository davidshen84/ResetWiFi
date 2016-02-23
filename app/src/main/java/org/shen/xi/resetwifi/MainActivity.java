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
import com.google.common.base.Joiner;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import eu.chainfire.libsuperuser.Shell.OnCommandResultListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final String networkHistoryTxtFilepath = "/data/misc/wifi/networkHistory.txt";

  private boolean hasNetworkHistoryFile = false;
  private CheckBox checkBoxNetworkHistory;
  private TextView textViewMessage;
  private Handler mainHandler;


  @Inject
  private Shell shell;

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
    shell.open();

    // clean previous message
    textViewMessage.setText("");

    if (shell.hasPrivilege()) {
      mainHandler.sendMessage(createLogMessage("got root permission"));

      update();
    } else {
      mainHandler.sendMessage(createLogMessage("no root permission"));
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    try {
      shell.close();
    } catch (IOException ignored) {
    }
  }

  private void update() {
    // test /data/misc/wifi/networkHistory.txt
    shell.execute(Collections.singletonList(testFileExists(networkHistoryTxtFilepath)), new OnCommandResultListener() {
      @Override
      public void onCommandResult(int commandCode, int exitCode, List<String> output) {
        String result = Joiner.on("").join(output);
        hasNetworkHistoryFile = Boolean.parseBoolean(result);

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
    });
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
    // 1. turn off wifi
    if (wifiManager.isOn()) {
      wifiManager.off();
    }

    // 2. remove files
    if (checkBoxNetworkHistory.isChecked()) {
      List<String> commands = Arrays.asList(
        "rm -f " + networkHistoryTxtFilepath,
        testFileExists(networkHistoryTxtFilepath));

      shell.execute(commands, new OnCommandResultListener() {
        @Override
        public void onCommandResult(int commandCode, int exitCode, List<String> output) {
          String result = Joiner.on("").join(output);
          hasNetworkHistoryFile = Boolean.parseBoolean(result);

          Message message = hasNetworkHistoryFile
            ? createLogMessage(String.format("failed to delete %s", networkHistoryTxtFilepath))
            : createLogMessage(String.format("successfully deleted %s", networkHistoryTxtFilepath));

          mainHandler.sendMessage(message);

          // 3. turn on wifi
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
      });
    }
  }
}
