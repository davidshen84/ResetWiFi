package org.shen.xi.resetwifi;

public interface RootProcess {
  void start();

  void stop();

  String execute(String command, boolean readStdIn);

  boolean hasRootPermission();
}
