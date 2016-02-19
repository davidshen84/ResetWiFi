package org.shen.xi.resetwifi;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by david on 2/1/2016.
 * <p/>
 * Use `su` command to get root permission
 */
public class SuRootProcess implements RootProcess {
  private static final String TAG = SuRootProcess.class.getSimpleName();
  private boolean hasRootPermission = false;
  private Process process;

  public void start() {
    try {
      process = Runtime.getRuntime().exec("su\n");
      String id = execute_read("id -u");
      hasRootPermission = id.equals("0");
    } catch (IOException ignore) {
      Log.w(TAG, "cannot get root permission");
    }
  }

  /**
   * Execute a command in the root process
   *
   * @param cmd       command to execute
   * @param readStdIn read from stdin
   */
  public String execute(String cmd, boolean readStdIn) {
    if (process == null)
      throw new IllegalStateException("root process is not started");

    try {
      if (readStdIn) {
        return execute_read(cmd);
      } else {
        execute_noread(cmd);
      }
    } catch (IOException ignore) {
    }

    return "";
  }

  private String execute_read(String cmd) throws IOException {
    try (
      BufferedReader in = new BufferedReader(new InputStreamReader((process.getInputStream())));
      OutputStreamWriter out = new OutputStreamWriter(process.getOutputStream())) {
      out.write(cmd + "\n");
      out.flush();

      return in.readLine();
    }
  }

  private void execute_noread(String cmd) throws IOException {
    try (
      OutputStreamWriter out = new OutputStreamWriter(process.getOutputStream())) {
      out.write(cmd + "\n");
      out.flush();
    }
  }

  public boolean hasRootPermission() {
    return hasRootPermission;
  }

  @Override
  protected void finalize() throws Throwable {
    stop();
    super.finalize();
  }

  public void stop() {
    if (process != null) {
      try {
        execute_noread("exit");
      } catch (IOException ignore) {
      }
    }
  }
}
