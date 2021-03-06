package com.github.gilz688.rccarserver;

import com.github.gilz688.rccarserver.proto.ServerInteractor;
import com.github.gilz688.rccarserver.proto.ServerPresenter;
import com.github.gilz688.rccarserver.proto.ServerView;

public class ServerPresenterImpl implements ServerPresenter {
    private ServerView mView;
    private ServerInteractor mInteractor;
    private ServerConfiguration mConfiguration;

    public ServerPresenterImpl(ServerView view, ServerInteractor interactor, ServerConfiguration configuration){
        mView = view;
        mInteractor = interactor;
        mConfiguration = configuration;
    }

    @Override
    public void onClickStopListening(){
        mInteractor.disableRCService();
        mInteractor.stopRCServer();
        mView.hideListeningView();
    }

    @Override
    public void onClickStartListening(){
        mInteractor.enableRCService();
        mView.showListeningView();
    }


    @Override
    public void onStart(){
        mView.showAddress(mConfiguration.getWifiIpAddress());
        mView.showSSID(mConfiguration.getWifiSSID());
        if(mInteractor.isRCServiceEnabled())
            mView.showListeningView();
        else
            mView.hideListeningView();
    }

    @Override
    public void onStop(){

    }
}
