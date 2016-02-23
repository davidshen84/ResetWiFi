package org.shen.xi.resetwifi;

import java.io.Closeable;
import java.util.List;

import eu.chainfire.libsuperuser.Shell.OnCommandResultListener;

/**
 * Created on 2/23/2016.
 */
public interface Shell extends Closeable {

  void open();

  boolean hasPrivilege();

  void execute(List<String> commands, OnCommandResultListener listener);
}
