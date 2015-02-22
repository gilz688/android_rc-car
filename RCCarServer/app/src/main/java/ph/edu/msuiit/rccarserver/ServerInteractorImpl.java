package ph.edu.msuiit.rccarserver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import java.util.Set;

import ph.edu.msuiit.rccarserver.tcp.TCPService;

public class ServerInteractorImpl implements ph.edu.msuiit.rccarserver.proto.ServerInteractor {
    private Activity mActivity;
    private ServiceConnection mConnection;

    public ServerInteractorImpl(Activity activity, ServiceConnection connection){
        mActivity = activity;
        mConnection = connection;
    }

    @Override
    public void startDiscoveryServer(){
        DiscoveryServer dThread;
        dThread = new DiscoveryServer("DiscoveryServer");
        dThread.start();

        // Start and Bind to TCPServer
        Intent intent = new Intent(mActivity, TCPService.class);
        mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void stopDiscoveryServer(){
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);

        for (int i = 0; i < threadArray.length; i++) {
            if (threadArray[i].getName().equalsIgnoreCase("DiscoveryServer")) {
                DiscoveryServer dThread =  (DiscoveryServer) threadArray[i];
                dThread.disconnect();
            }
        }
    }

    @Override
    public void startTCPServer(){
        // Start and Bind to TCPServer
        Intent intent = new Intent(mActivity, TCPService.class);
        mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void stopTCPServer(){
        mActivity.unbindService(mConnection);
    }
}
