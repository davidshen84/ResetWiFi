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
  private OutputStreamWriter out = null;
  private BufferedReader in = null;
  private boolean hasRootPermission = false;

  public void start() {
    try {
      Process process = Runtime.getRuntime().exec("su\n");
      out = new OutputStreamWriter(process.getOutputStream());
      in = new BufferedReader(new InputStreamReader((process.getInputStream())));

      out.write("id -u\n");
      out.flush();
      String id = in.readLine();
      hasRootPermission = id.equals("0");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Execute a command in the root process
   *
   * @param cmd       command to execute
   * @param readStdIn read from stdin
   */
  public String execute(String cmd, boolean readStdIn) {
    if (out == null) return "";

    try {
      out.write(cmd + "\n");
      out.flush();

      return readStdIn ? in.readLine() : "";
    } catch (IOException e) {
      Log.e(TAG, String.format("cannot execute %s", cmd));
      try {
        out.close();
      } catch (IOException ignored) {
      }

      try {
        in.close();
      } catch (IOException ignored) {
      }

      out = null;
      in = null;

      return "";
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
    if (out != null && hasRootPermission) {
      try {
        out.write("exit\n");
        out.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}