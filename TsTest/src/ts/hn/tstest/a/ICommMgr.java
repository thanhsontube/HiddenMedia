package ts.hn.tstest.a;

import android.os.Messenger;

public interface ICommMgr {
    /**
     * put data to queue
     */
    void addData(Messenger messenger, TsData data);
    
    void addData(int key, TsData data);

    /**
     * set callback to control processing of Threads.
     * 
     * @param callback
     */

    void setCallbackVideo(ICallback callback);

    void setCallbackAudio(ICallback callback);

    void setCallbackPhoto(ICallback callback);

}
