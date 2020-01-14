package cn.pompip;

import cn.pompip.handle.Handler;
import cn.pompip.handle.Looper;
import cn.pompip.handle.Message;
import org.junit.Test;



public class TestHandler {
    public void log(String msg) {
        System.out.println(msg);
    }

    @Test
    public void testMain() {
        Looper.prepare();
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                log(msg.toString());
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i =0;i<150;i++){
                    Message msg = new Message();
                    msg.what = i;
                    handler.sendMessage(msg);
                }
            }
        }).start();

        log("send");

        Looper.loop();
        log("end");
    }







}
