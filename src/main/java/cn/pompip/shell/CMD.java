package cn.pompip.shell;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

//import android.util.Log;

public class CMD {
    public  enum Command {
        cmd, adb, su, sh
    }

    public static final String TAG = "CommandExecution";
    public final static String COMMAND_EXIT = "exit\n";
    public final static String COMMAND_LINE_END = "\n";

    public static CommandResult execCommand(String command, Command c) {
        String[] commands = {command};
        return execCommand(commands, c);
    }

    public static CommandResult execCommand(String[] commands, Command c) {
        CommandResult commandResult = new CommandResult();
        if (commands == null || commands.length == 0) {
            return commandResult;
        }
        DataOutputStream os = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("adb".equals(c.name())?"adb shell":c.name());
            Thread resultTHread = new Thread(new ResultRunnable(process.getInputStream(), commandResult));
            resultTHread.start();
            Thread errorThread = new Thread(new ErrorRunnable(process.getErrorStream(), commandResult));
            errorThread.start();
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command != null) {
                    os.write(command.getBytes());
                    os.writeBytes(COMMAND_LINE_END);
                    os.flush();
                }
            }
            Scanner scanner = new Scanner(System.in);
            while (!Thread.interrupted()) {
                String line = scanner.nextLine();
                if ("exitc".equals(line)){
                   Thread.currentThread().interrupt();
                }
                os.write(line.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();
            commandResult.result = process.waitFor();
            resultTHread.interrupt();
            errorThread.interrupt();

            ResultLog.i(TAG, commandResult.result + " +++ " + commandResult.successMsg + " +++ " + commandResult.errorMsg);
        } catch (Exception e) {
            e.printStackTrace();
            String errmsg = e.getMessage();
            if (errmsg != null) {
                ResultLog.e(TAG, errmsg);
            }
        } finally {
            if (process != null)
                process.destroy();
        }
        return commandResult;
    }



    public static void main(String[] args) {
        System.out.println(Command.adb);
        CMD.execCommand(new String[]{"adb devices"}, Command.adb);
    }
}

