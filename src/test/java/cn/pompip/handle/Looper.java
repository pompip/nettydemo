package cn.pompip.handle;

public class Looper {
    static ThreadLocal<Looper> mThreadLocal = new ThreadLocal<>();
    MessageQueue messageQueue;

    private Looper() {
        messageQueue = new MessageQueue();
    }

    public static Looper myLooper() {
        return mThreadLocal.get();
    }

    public static void prepare() {
        if (mThreadLocal.get() != null) {
            throw new RuntimeException("only one Looper can be created");
        }
        mThreadLocal.set(new Looper());
    }

    public static void loop() {
        Looper looper = myLooper();
        if (looper == null) {
            throw new RuntimeException("no Looper");
        }
        MessageQueue messageQueue = looper.messageQueue;
        for (; ; ) {
            Message msg = messageQueue.next();
            if (msg == null || msg.target == null) {
                continue;
            }
            msg.target.dispatchMessage(msg);
        }
    }
}
