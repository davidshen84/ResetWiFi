package org.shen.xi.resetwifi.debug;

import android.util.Log;

import org.shen.xi.resetwifi.WifiManagerWrapper;

/**
 * Created on 2/18/2016.
 * <p/>
 * fake implementation for testing
 */
public class FakeWifiManagerWrapper implements WifiManagerWrapper {
  private static final String TAG = FakeWifiManagerWrapper.class.getSimpleName();

  @Override
  public boolean isOn() {
    return true;
  }

  @Override
  public void on() {
    Log.d(TAG, "would have turned on WiFi");
  }

  @Override
  public void off() {
    Log.d(TAG, "would have turned off WiFi");
  }
}
