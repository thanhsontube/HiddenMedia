package ts.hn.tstest.a;

public abstract class TsThread extends Thread {
    private boolean isActive = false;

    protected abstract void handleThread();

    @Override
    public void run() {
        while (isActive) {
            handleThread();
        }
    }

    public void active() {
        isActive = true;
        this.start();
    }

    public void deactive() {
        isActive = false;
    }
}
