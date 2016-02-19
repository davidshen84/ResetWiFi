package org.shen.xi.resetwifi;

import java.io.IOException;

public interface RootProcess {
  void start() throws IOException;

  void stop() throws IOException;

  String execute(String command, boolean readStdIn);

  boolean hasRootPermission();
}
