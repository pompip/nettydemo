package cn.pompip.shell;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessCMD {
    ExecutorService executorService = Executors.newFixedThreadPool(4);
    class InputRunnable implements Runnable{
        InputStream inputStream;

        public InputRunnable(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GB2312")));
            try{
//                bufferedReader.lines().forEach(line->{
//                    System.out.println(line);
//                });



                while (!Thread.interrupted()){
                    String line = bufferedReader.readLine();
                    if (line!=null){
                        if (line.isEmpty()){
                            continue;
                        }
                        System.out.println(line);
                    }else {
                        break;
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    class OutputRunnable implements Runnable{
        OutputStream outputStream;

        public OutputRunnable(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, Charset.forName("GB2312")));
            try {

                Scanner scanner = new Scanner(System.in);
                String line;
                while ((line = scanner.nextLine())!=null){

                    bufferedWriter.write(line+"\n");
                    bufferedWriter.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public void exe() throws Exception {

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("adb");
        Process process = processBuilder.start();


//        Process process = Runtime.getRuntime().exec("adb shell");
        InputStream inputStream = process.getInputStream();

        executorService.submit(new InputRunnable(inputStream));
        executorService.submit(new InputRunnable(process.getErrorStream()));
        executorService.submit(new OutputRunnable(process.getOutputStream()));



    }

    public static void main(String[] args) throws Exception {
        new ProcessCMD().exe();
    }
}
