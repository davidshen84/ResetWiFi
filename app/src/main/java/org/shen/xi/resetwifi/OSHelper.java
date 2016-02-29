package org.shen.xi.resetwifi;

import java.io.Closeable;

import eu.chainfire.libsuperuser.Shell.OnCommandResultListener;

/**
 * Created on 2/26/2016.
 */
public interface OSHelper extends Closeable {
  /**
   * Open the underlining Shell
   */
  void open();

  /**
   * Test if the current user is root
   *
   * @return true/false
     */
  boolean hasPrivilege();

  /**
   * test if the file exist
   *
   * @param listener callback to handle shell result
   */
  void hasNetworkHistoryTxt(OnCommandResultListener listener);

  /**
   * remove the file
   *
   * @param listener callback to handle shell result,
   *                 the output will contain true/false to indicate if the file has been removed
   */
  void removeNetworkHistoryTxt(OnCommandResultListener listener);
}
