package cn.pompip.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class ErrorRunnable implements Runnable {
    private final static String TAG = "ErrorRunnable";

    private InputStream inputStream;
    private CommandResult commandResult;

    public ErrorRunnable(InputStream inputStream, CommandResult commandResult) {
        this.inputStream = inputStream;
        this.commandResult = commandResult;
    }

    @Override
    public void run() {
        BufferedReader errorResult = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GB2312")));
        StringBuilder errorMsg = new StringBuilder();
        String s;
        while (true) {
            try {
                if ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                    commandResult.errorMsg = errorMsg.toString();
                    ResultLog.i(TAG, s);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        }

    }
}
