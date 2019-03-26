package cn.pompip.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class ResultRunnable implements Runnable {
    private final static String TAG = "ResultRunnable";
    private InputStream inputStream;
    private CommandResult commandResult;
//    private boolean running;

    public ResultRunnable(InputStream inputStream, CommandResult commandResult) {
        this.inputStream = inputStream;
        this.commandResult = commandResult;
    }

    @Override
    public void run() {
        try {
            BufferedReader successResult = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GB2312")));
            StringBuilder successMsg = new StringBuilder();
            while (!Thread.interrupted()) {
                String s = successResult.readLine();
                if (s != null) {
                    successMsg.append(s);
                    commandResult.successMsg = successMsg.toString();
                    ResultLog.i(TAG, s);
                } else {
                    break;
                }
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
