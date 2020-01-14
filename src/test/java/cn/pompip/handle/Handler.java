package cn.pompip.handle;

public class Handler {
    MessageQueue messageQueue;
    Looper mLooper;

    public Handler() {
        mLooper = Looper.myLooper();
        if (mLooper == null) {
            throw new RuntimeException("please call Looper.prepare()");
        }
        messageQueue = mLooper.messageQueue;
    }

    public final void sendMessage(Message msg) {
        MessageQueue queue = messageQueue;
        if (queue != null) {
            msg.target = this;
            queue.enqueueMessage(msg);
        } else {
            throw new RuntimeException("no queue");
        }
    }

    public void handleMessage(Message msg) {

    }

    public final void dispatchMessage(Message msg) {
        handleMessage(msg);
    }
}
