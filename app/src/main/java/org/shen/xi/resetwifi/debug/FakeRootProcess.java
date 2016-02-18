package org.shen.xi.resetwifi.debug;

import android.util.Log;

import org.shen.xi.resetwifi.BuildConfig;
import org.shen.xi.resetwifi.RootProcess;

/**
 * A fake implementation for testing
 */
public class FakeRootProcess implements RootProcess {
  private static final String TAG = FakeRootProcess.class.getSimpleName();

  @Override
  public void start() {
    Log.d(TAG, "start fake root process");
  }

  @Override
  public void stop() {
    Log.d(TAG, "stop fake root process");
  }

  @Override
  public String execute(String command, boolean readStdIn) {
    Log.d(TAG, String.format("would have executed: %s", command));

    if (command.contains("networkHistory.txt")) {
      return Boolean.toString(BuildConfig.hasNetworkHistory);
    }

    return "";
  }

  @Override
  public boolean hasRootPermission() {
    return BuildConfig.hasRoot;
  }
}
