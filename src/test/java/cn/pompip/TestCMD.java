package cn.pompip;

import cn.pompip.shell.CMD;
import org.junit.Test;

public class TestCMD {
    @Test
    public void testCmd(){
        CMD.execCommand(new String[]{"adb shell","su","ls"},false);
    }
}
