package cn.pompip.handle;

public class Message {
    Handler target;
    public int what;
    public Object obj;

    @Override
    public String toString() {
        return "Message{" +
                ", what=" + what +
                ", obj=" + obj +
                '}';
    }
}
