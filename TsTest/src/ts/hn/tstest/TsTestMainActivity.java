package ts.hn.tstest;

import ts.hn.tstest.fragment.MoviePlayerActivity;
import ts.hn.tstest.fragment.MusicPlayerActivity;
import ts.hn.tstest.movie.MainActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class TsTestMainActivity extends Activity implements OnClickListener {
    private ResouceManager resource;
    private ServiceMusic tsService;
    private boolean isBound = false;

    private Messenger messengerService;
    private Messenger messengerClients;
    private TextView txtName;
    private int currentPosition = 0;

    @SuppressLint("HandlerLeak")
    private class ClientInCommingMsg extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MsConst.TS_NEXT:
                currentPosition++;
                updateTitle();
                break;

            default:
                break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ts_test_main);
        resource = ResouceManager.getInstance();
        messengerClients = new Messenger(new ClientInCommingMsg());
        initLayout();
        resource.setListMusic(TsContentProviders.getAudio(getApplicationContext()));
    }

    private void initLayout() {
        findViewById(R.id.btn_bind).setOnClickListener(this);
        findViewById(R.id.btn_unbind).setOnClickListener(this);
        findViewById(R.id.btn_play).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.btn_prev).setOnClickListener(this);
        findViewById(R.id.btn_resume).setOnClickListener(this);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtName.setText("0");

        ImageView img = (ImageView) findViewById(R.id.my_play);
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            txtName.setText("Bound to service");
            messengerService = new Messenger(service);
            isBound = true;

            // register client messenger to service
            Message msg = new Message();
            msg.what = MsConst.TS_REGISTER;
            msg.replyTo = messengerClients;

            try {
                messengerService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            messengerService = null;
            isBound = false;
        }

    };

    @Override
    public void onClick(View v) {
        Message msg;
        switch (v.getId()) {
        case R.id.btn_bind:
            bindService(ServiceMusic.getIntentService(getApplicationContext()), connection, Service.BIND_AUTO_CREATE);
            break;
        case R.id.btn_unbind:
            txtName.setText("Disconect with the service");
            msg = new Message();
            msg.what = MsConst.TS_UNREGISTER;
            msg.replyTo = messengerClients;

            try {
                messengerService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(connection);
            break;
        case R.id.btn_play:
            if (!isBound) {
                return;
            }
            updateTitle();

            msg = Message.obtain(null, MsConst.TS_PLAY_POSITION, currentPosition, 0);
            try {
                messengerService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            break;

        case R.id.btn_pause:
            if (!isBound) {
                return;
            }
            updateTitle();
            msg = Message.obtain(null, MsConst.TS_PAUSE, currentPosition, 0);
            try {
                messengerService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            break;
        case R.id.btn_resume:
            if (!isBound) {
                return;
            }
            updateTitle();
            msg = Message.obtain(null, MsConst.TS_RESUME, currentPosition, 0);
            try {
                messengerService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            break;

        case R.id.btn_stop:
            if (!isBound) {
                return;
            }
            updateTitle();
            msg = Message.obtain(null, MsConst.TS_STOP, currentPosition, 0);
            try {
                messengerService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            break;
        case R.id.btn_next:
            if (!isBound) {
                return;
            }
            if (currentPosition == resource.getListMusic().size() - 1) {
                currentPosition = -1;
            }
            currentPosition++;
            msg = Message.obtain(null, MsConst.TS_PLAY_POSITION, currentPosition, 0);
            try {
                messengerService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            updateTitle();
            break;
        case R.id.btn_prev:
            if (!isBound) {
                return;
            }
            if (currentPosition == 0) {
                return;
            }
            currentPosition--;
            msg = Message.obtain(null, MsConst.TS_PLAY_POSITION, currentPosition, 0);
            try {
                messengerService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            updateTitle();
            break;

        default:
            break;
        }

    }

    private void updateTitle() {
        String title = resource.getListMusic().get(currentPosition).name;
        txtName.setText(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ts_test_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_movie:
            startActivity(new Intent(getApplicationContext(), MoviePlayerActivity.class));
            break;

        case R.id.action_movie2:
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            break;
        case R.id.action_music:
            startActivity(new Intent(getApplicationContext(), MusicPlayerActivity.class));
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
