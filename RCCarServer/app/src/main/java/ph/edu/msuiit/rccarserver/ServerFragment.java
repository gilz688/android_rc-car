package ph.edu.msuiit.rccarserver;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ph.edu.msuiit.rccarserver.proto.ServerPresenter;
import ph.edu.msuiit.rccarserver.proto.ServerView;
import ph.edu.msuiit.rccarserver.tcp.TCPService;

public class ServerFragment extends Fragment implements ServerView, View.OnClickListener {

    public static final String TAG = "ServerFragment";
    private TCPService mService;
    private ServerPresenter mPresenter;
    private boolean isListening;
    private boolean mBound;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            TCPService.TCPServiceBinder binder = (TCPService.TCPServiceBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mBound = false;
        }
    };

    private TextView tvWaiting;
    private TextView tvAddress;
    private TextView tvWifiSSID;
    private Button btnListening;

    public ServerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_server, container, false);
        ServerConfiguration config = new ServerConfiguration((WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE));
        mPresenter = new ServerPresenterImpl(this, new ServerInteractorImpl(getActivity(),mConnection), config);
        btnListening = (Button) rootView.findViewById(R.id.btnStart);
        btnListening.setOnClickListener(this);
        tvWaiting = (TextView) rootView.findViewById(R.id.tvWaiting);
        tvAddress = (TextView) rootView.findViewById(R.id.tvAddress);
        tvWifiSSID = (TextView) rootView.findViewById(R.id.tvWifiSSID);
        mPresenter.onStart();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(isListening)
            mPresenter.onClickStartListening();
        else
            mPresenter.onClickStopListening();

        isListening = !isListening;
    }

    @Override
    public void hideListeningView(){
        tvWaiting.setVisibility(View.INVISIBLE);
        btnListening.setText(getResources().getString(R.string.start_btn));
    }

    @Override
    public void showListeningView(){
        tvWaiting.setVisibility(View.VISIBLE);
        btnListening.setText(getResources().getString(R.string.cancel_btn));
    }

    @Override
    public void showAddress(String address){
        String label = this.getResources().getString(R.string.ip_address);
        tvAddress.setText(label + " " + address);
    }

    @Override
    public void showSSID(String ssid){
        String label = this.getResources().getString(R.string.wifi_ssid);
        tvWifiSSID.setText(label + "\"" + ssid + "\"");
    }
}