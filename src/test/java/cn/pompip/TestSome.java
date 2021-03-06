package cn.pompip;

import cn.pompip.adb.*;
import cn.pompip.utils.Log;
import cn.pompip.utils.Res;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.io.Resources.getResource;


public class TestSome {
    @Test
    public void testGetDevice() {
        Tools tools = new Tools();
        ArrayList<AndroidTask> taskArrayList = new ArrayList<>();
        taskArrayList.add(device -> {
            AndroidDevice androidDevice = new AndroidDevice(device);
            App app = new App();
//            androidDevice.installApp(app);
//            androidDevice.findLauncherActivity(app);

//            androidDevice.root();
//            androidDevice.exeCommand("su -c ' /data/data/com.bonree.mobile.base/files/busybox unzip -o /data/local/tmp/bonree.zip -d /data/local/tmp/bonree/'");
//            androidDevice.exeCommand("su & mount -o rw,remount /system & touch /system/hello4");
//            androidDevice.exeCommand("su -c chmod 777 /data/local/tmp/utils/busybox");
            ResultReceiver resultReceiver = new ResultReceiver();
            try {
                device.executeShellCommand("su",resultReceiver);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        tools.start(taskArrayList);
    }

    @Test
    public void testLog()  {
        String line = "d86dfe4 com.tencent.mm/.ui.tools.MultiStageCitySelectUI filter 26727de";
//        assert line.matches("^[\\w\\W]*com.tencent.mm/[\\w.]*\\w\\W]*$");
//       Log.i(this, line.split("com.tencent.mm/[\\w.]*")[0]);
        Matcher matcher = Pattern.compile("com.tencent.mm/[\\w.]*").matcher(line);
        while (matcher.find()){
            String group =matcher.group();
            Log.i(this,group);
        }

    }

    @Test
    public void testOdd(){
        System.out.println(0&1);
        System.out.println(0|1);
        System.out.println(0^1);

    }

    @Test public void testObjArray(){
        TestObj[] testObjs = {new TestObj(1),new TestObj(2),new TestObj(3)};
        TestSum testSum = new TestSum();

        try {
//            Class<?> cArray = Class.forName("[Lcn.pompip.TestObj");
            Class<?> testSumClass = Class.forName("cn.pompip.TestSum");
            Field field  = testSumClass.getField("testObjs");

            Object o = field.get(testSum);

            Log.i(this,o);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test public void testFile(){

        File file = Res.get("/web/chat.html");
        System.out.println(file.getAbsolutePath());
    }


}
