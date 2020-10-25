package com.github.gilz688.rccarserver;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.gilz688.rccarserver.background.RCCarService;
import com.github.gilz688.rccarserver.databinding.FragmentServerBinding;
import com.github.gilz688.rccarserver.proto.ServerPresenter;
import com.github.gilz688.rccarserver.proto.ServerView;

import java.util.Objects;

public class ServerFragment extends Fragment implements ServerView, View.OnClickListener {

    public static final String TAG = "ServerFragment";
    private RCCarService mService;
    private ServerPresenter mPresenter;
    private boolean isListening;
    private boolean mBound;

    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            RCCarService.TCPServiceBinder binder = (RCCarService.TCPServiceBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mBound = false;
        }
    };

    private FragmentServerBinding binding;

    public ServerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServerBinding.inflate(inflater);
        ServerConfiguration config = new ServerConfiguration((WifiManager) Objects.requireNonNull(getActivity()).getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        mPresenter = new ServerPresenterImpl(this, new ServerInteractorImpl(getActivity(), mConnection), config);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if (isListening)
            mPresenter.onClickStopListening();
        else
            mPresenter.onClickStartListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onStart();
        String action = Objects.requireNonNull(getActivity()).getIntent().getAction();
        Log.d(TAG, "onResume:" + action);
    }

    @Override
    public void hideListeningView() {
        isListening = false;
        binding.btnStart.setText(R.string.text_enable_rc_car_server);
    }

    @Override
    public void showListeningView() {
        isListening = true;
        binding.btnStart.setText(R.string.text_disable_rc_car_server);
    }

    @Override
    public void showAddress(String address) {
        String label = this.getResources().getString(R.string.ip_address);
        binding.tvAddress.setText(String.format("%s %s", label, address));
    }

    @Override
    public void showUdpPort(int tcpPort) {
        String label = this.getResources().getString(R.string.udp_port);
        binding.tvUdpPort.setText(String.format("%s %s", label, tcpPort));
    }

    @Override
    public void showTcpPort(int tcpPort) {
        String label = this.getResources().getString(R.string.tcp_port);
        binding.tvTcpPort.setText(String.format("%s %s", label, tcpPort));
    }
}