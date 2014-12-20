package ts.hn.tstest.a;

public interface ICallback {
    void onStart();

    void onCopy();

    void onProcess(int percent);

    void onFinished();

    void onError(Exception e);

    void onCancelled();
}
