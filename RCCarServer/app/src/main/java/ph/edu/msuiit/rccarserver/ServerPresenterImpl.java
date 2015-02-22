package ph.edu.msuiit.rccarserver;

import ph.edu.msuiit.rccarserver.proto.ServerInteractor;
import ph.edu.msuiit.rccarserver.proto.ServerPresenter;
import ph.edu.msuiit.rccarserver.proto.ServerView;

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
        mInteractor.startDiscoveryServer();
        mInteractor.startTCPServer();
        mView.hideListeningView();
    }

    @Override
    public void onClickStartListening(){
        mInteractor.stopDiscoveryServer();
        mInteractor.stopDiscoveryServer();
        mView.showListeningView();
    }


    @Override
    public void onStart(){
        mView.showAddress(mConfiguration.getWifiIpAddress());
        mView.showSSID(mConfiguration.getWifiSSID());
    }

    @Override
    public void onStop(){
        mInteractor.stopDiscoveryServer();
        mInteractor.stopDiscoveryServer();
    }
}
