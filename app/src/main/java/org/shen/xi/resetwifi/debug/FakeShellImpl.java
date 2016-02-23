package org.shen.xi.resetwifi.debug;

import android.util.Log;

import org.shen.xi.resetwifi.BuildConfig;
import org.shen.xi.resetwifi.Shell;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2/23/2016.
 */
public class FakeShellImpl implements Shell {
  private static final String TAG = FakeShellImpl.class.getSimpleName();

  @Override
  public void open() {
    Log.d(TAG, "open fake shell");
  }

  @Override
  public boolean hasPrivilege() {
    return BuildConfig.hasRoot;
  }

  @Override
  public void execute(List<String> commands, eu.chainfire.libsuperuser.Shell.OnCommandResultListener listener) {
    listener.onCommandResult(-1, 0, Collections.singletonList("true"));
  }

  @Override
  public void close() throws IOException {
    Log.d(TAG, "close fake shell");
  }
}
