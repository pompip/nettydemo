package cn.pompip.adb;

import com.android.ddmlib.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Tools {


    public void start(final List<AndroidTask> androidTaskList) {
        AndroidDebugBridge.init(true);
        AndroidDebugBridge adb = AndroidDebugBridge.createBridge();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AndroidDebugBridge.addDeviceChangeListener(new AndroidDebugBridge.IDeviceChangeListener() {
            @Override
            public void deviceConnected(IDevice device) {
                System.out.println("deviceConnected");
                androidTaskList.forEach(androidTask -> {
                    androidTask.start(device);
                });
                countDownLatch.countDown();
            }

            @Override
            public void deviceDisconnected(IDevice device) {
                System.out.println("deviceDisconnected");
            }

            @Override
            public void deviceChanged(IDevice device, int changeMask) {
                System.out.println("deviceChanged");
                androidTaskList.forEach(androidTask -> {
                    androidTask.start(device);
                });
                countDownLatch.countDown();

            }
        });


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }





}
