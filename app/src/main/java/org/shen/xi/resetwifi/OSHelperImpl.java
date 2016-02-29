package org.shen.xi.resetwifi;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.inject.Inject;

import eu.chainfire.libsuperuser.Shell.OnCommandResultListener;

/**
 * Created on 2/26/2016.
 */
public class OSHelperImpl implements OSHelper {
  private static final String NetworkHistoryTxtFilepath = "/data/misc/wifi/networkHistory.txt";
  private final String testFileExistsCommand = String.format("[ -e %s ] && echo true || echo false", NetworkHistoryTxtFilepath);
  private Shell shell;


  @Inject
  public OSHelperImpl(Shell shell) {
    this.shell = shell;
  }

  public void open() {
    shell.open();
  }

  @Override
  public boolean hasPrivilege() {
    return shell.hasPrivilege();
  }

  @Override
  public void hasNetworkHistoryTxt(final OnCommandResultListener listener) {
    shell.execute(Collections.singletonList(testFileExistsCommand), listener);
  }

  @Override
  public void removeNetworkHistoryTxt(OnCommandResultListener listener) {
    String command = String.format("rm -f %s", NetworkHistoryTxtFilepath);
    shell.execute(Arrays.asList(command, testFileExistsCommand), listener);
  }

  @Override
  public void close() throws IOException {
    shell.close();
  }
}
