package org.shen.xi.resetwifi;

import com.google.common.base.Joiner;

import java.io.IOException;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created on 2/22/2016.
 * <p/>
 * Example code: https://github.com/Chainfire/libsuperuser/blob/master/libsuperuser_example/src/eu/chainfire/libsuperuser_example/MainActivity.java
 */
public class SuperUserProcess implements RootProcess {

  @Override
  public void start() throws IOException {
  }

  @Override
  public void stop() throws IOException {
  }

  @Override
  public String execute(String command, boolean readStdIn) {
    List<String> strings = Shell.SU.run(command);

    return Joiner.on("").join(strings);
  }

  @Override
  public boolean hasRootPermission() {
    return Shell.SU.available();
  }
}
