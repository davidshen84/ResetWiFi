package org.shen.xi.resetwifi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import javax.inject.Inject;

/**
 * Created on 3/28/2016.
 */
public class PermissionManagerImpl implements PermissionManager {

  private Context context;

  @Inject
  public PermissionManagerImpl(Context context) {

    this.context = context;
  }

  @Override
  public boolean hasRequiredPermissions() {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
      && ContextCompat.checkSelfPermission(context, Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED;
  }
}
