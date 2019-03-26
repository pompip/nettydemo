package cn.pompip.shell;

import java.io.DataOutputStream;
import java.io.IOException;

//import android.util.Log;

public class CMD {
    public static final String TAG = "CommandExecution";
    public final static String COMMAND_SU = "su";
    public final static String COMMAND_SH = "cmd";
    public final static String COMMAND_EXIT = "exit\n";
    public final static String COMMAND_LINE_END = "\n";

    public static CommandResult execCommand(String command, boolean isRoot) {
        String[] commands = {command};
        return execCommand(commands, isRoot);
    }

    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        CommandResult commandResult = new CommandResult();
        if (commands == null || commands.length == 0) {
            return commandResult;
        }

        DataOutputStream os = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            new Thread(new ResultRunnable(process.getInputStream(), commandResult)).start();
            new Thread(new ErrorRunnable(process.getErrorStream(), commandResult)).start();
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
            Log.i(TAG, commandResult.result + " +++ " + commandResult.successMsg + " +++ " + commandResult.errorMsg);
        } catch (Exception e) {
            String errmsg = e.getMessage();
            if (errmsg != null) {
                Log.e(TAG, errmsg);
            } else {
                e.printStackTrace();
            }
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null)
                process.destroy();
        }
        return commandResult;
    }


    static class Log {
        static void e(String TAG, String msg) {
            System.out.println(TAG + " : " + msg);
        }

        static void i(String TAG, String msg) {
            System.out.println(TAG + " : " + msg);
        }
    }
}

