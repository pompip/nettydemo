package cn.pompip;

import cn.pompip.adb.AndroidDevice;
import cn.pompip.adb.AndroidTask;
import cn.pompip.adb.App;
import cn.pompip.adb.Tools;
import cn.pompip.utils.Log;
import org.junit.Test;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TestSome {
    @Test
    public void testGetDevice() {
        Tools tools = new Tools();
        ArrayList<AndroidTask> taskArrayList = new ArrayList<>();
        taskArrayList.add(device -> {
            AndroidDevice androidDevice = new AndroidDevice(device);
            App app = new App();
//            androidDevice.installApp(app);
            androidDevice.findLauncherActivity(app);
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
}
