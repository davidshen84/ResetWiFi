package org.shen.xi.resetwifi;

import android.util.Log;

/**
 * A fake implementation for debug
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
    return "";
  }

  @Override
  public boolean hasRootPermission() {
    return true;
  }
}
