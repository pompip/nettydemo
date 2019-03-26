package cn.pompip.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

//import android.util.Log;

public class CMD {
    public static final String TAG = "CommandExecution";

    public final static String COMMAND_SU = "su";
    public final static String COMMAND_SH = "cmd";
    public final static String COMMAND_EXIT = "exit\n";
    public final static String COMMAND_LINE_END = "\n";

    public static CommandResult execCommand(String command, boolean isRoot) {
        String[] commands = { command };
        return execCommand(commands, isRoot);
    }

    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        CommandResult commandResult = new CommandResult();
        if (commands == null || commands.length == 0)
            return commandResult;
        Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command != null) {
                    os.write(command.getBytes());
                    os.writeBytes(COMMAND_LINE_END);
                    os.flush();
                }
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();
            commandResult.result = process.waitFor();
            // 获取错误信息
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GB2312")));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream(),Charset.forName("GB2312")));
            String s;
            while ((s = successResult.readLine()) != null){
                successMsg.append(s);
                Log.i(TAG,s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
                Log.i(TAG,s);
            }
            commandResult.successMsg = successMsg.toString();
            commandResult.errorMsg = errorMsg.toString();
//            Log.i(TAG, commandResult.result + " +++ " + commandResult.successMsg + " +++ " + commandResult.errorMsg);
        } catch (IOException e) {
            String errmsg = e.getMessage();
            if (errmsg != null) {
                Log.e(TAG, errmsg);
            } else {
                e.printStackTrace();
            }
        } catch (Exception e) {
            String errmsg = e.getMessage();
            if (errmsg != null) {
                Log.e(TAG, errmsg);
            } else {
                e.printStackTrace();
            }
        } finally {
            try {
                if (os != null)
                    os.close();
                if (successResult != null)
                    successResult.close();
                if (errorResult != null)
                    errorResult.close();
            } catch (IOException e) {
                String errmsg = e.getMessage();
                if (errmsg != null) {
                    Log.e(TAG, errmsg);
                } else {
                    e.printStackTrace();
                }
            }
            if (process != null)
                process.destroy();
        }
        return commandResult;
    }

    public static class CommandResult {
        public int result = -1;
        public String errorMsg;
        public String successMsg;
    }
    static class Log{
         static void e(String TAG,String msg){
             System.out.println(TAG+" : "+msg);
        }

        static void i(String TAG,String msg){
            System.out.println(TAG+" : "+msg);
        }
    }
}

