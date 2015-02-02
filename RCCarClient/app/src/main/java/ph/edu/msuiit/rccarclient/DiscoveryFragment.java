package ph.edu.msuiit.rccarclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.InetAddress;
import java.net.UnknownHostException;

import ph.edu.msuiit.rccarclient.models.Device;
import ph.edu.msuiit.rccarclient.models.DeviceAdapter;

/**
 * A fragment representing a list of RC Car Servers.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class DiscoveryFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private RecyclerView rvDevice;
    private DeviceAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DiscoveryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_device,
                null);
        rvDevice = (RecyclerView) root.findViewById(R.id.rvDevice);
        rvDevice.setHasFixedSize(true);
        mAdapter = new DeviceAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(root.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvDevice.setLayoutManager(llm);
        rvDevice.setAdapter(mAdapter);
        startDiscovery();
        return root;
    }

    private void startDiscovery(){
        Context context = this.getActivity();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DiscoveryClient client = new DiscoveryClient(context, wifiManager);
        client.start();
    }

    // Register mMessageReceiver to receive messages.
    private void registerDiscoveryReceiver(Context context){
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,
                new IntentFilter(DiscoveryClient.ACTION_DISCOVERY_SERVER_FOUND));
    }

    // handler for received Intents for the "server_discovery" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String serverName = intent.getStringExtra("serverName");
            int serverPort = intent.getIntExtra("serverPort", -1);
            InetAddress serverAddress = null;
            try {
                serverAddress = InetAddress.getByAddress(intent.getByteArrayExtra("serverAddress"));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            Device device = new Device(serverName,serverAddress);
            mAdapter.add(device);
            Log.d("receiver", "Got message: " + intent.getAction());
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        registerDiscoveryReceiver(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
