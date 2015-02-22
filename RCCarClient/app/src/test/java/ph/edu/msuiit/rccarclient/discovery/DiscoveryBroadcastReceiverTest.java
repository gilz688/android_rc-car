package ph.edu.msuiit.rccarclient.discovery;

import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;

import java.net.InetAddress;
import java.net.UnknownHostException;

import ph.edu.msuiit.rccarclient.CustomRobolectricRunner;
import ph.edu.msuiit.rccarclient.R;
import ph.edu.msuiit.rccarclient.discovery.proto.DiscoveryPresenter;

import static org.mockito.Mockito.*;

@RunWith(CustomRobolectricRunner.class)
public class DiscoveryBroadcastReceiverTest {

    private DiscoveryPresenter presenter;
    private Context context;
    private DiscoveryBroadcastReceiver receiver;

    @Before
    public void setUp() {
        presenter = mock(DiscoveryPresenter.class);
        context = Robolectric.application;
        receiver = spy(new DiscoveryBroadcastReceiver(presenter));
    }

    @Test
    public void testOnReceiveDiscoveryServerFound() throws UnknownHostException {
        final String expectedDeviceName = "RC Car Server";
        final InetAddress expectedAddress = null;
        final int expectedPort = 12345;

        Intent intent = new Intent(DiscoveryService.ACTION_DISCOVERY_SERVER_FOUND);
        intent.putExtra(DiscoveryBroadcastReceiver.EXTRA_SERVER_NAME,expectedDeviceName);
        intent.putExtra(DiscoveryBroadcastReceiver.EXTRA_SERVER_ADDRESS,expectedAddress);
        intent.putExtra(DiscoveryBroadcastReceiver.EXTRA_SERVER_PORT,expectedPort);

        receiver.onReceive(context,intent);
        verify(presenter,times(1)).onDeviceFound(expectedDeviceName,expectedAddress,expectedPort);
    }

    @Test
    public void testOnReceiveDiscoveryClientStarted() {
        Intent intent = new Intent(DiscoveryService.ACTION_DISCOVERY_CLIENT_STARTED);

        receiver.onReceive(context,intent);
        verify(presenter,times(1)).discoveryStarted();
    }

    @Test
    public void testOnReceiveDiscoveryClientEnded() {
        Intent intent = new Intent(DiscoveryService.ACTION_DISCOVERY_CLIENT_ENDED);

        receiver.onReceive(context,intent);
        verify(presenter,times(1)).discoveryEnded();
    }

    @Test
    public void testOnReceiveDiscoveryClientError() {
        String expectedError = context.getResources().getString(R.string.error_io_exception);
        Intent intent = new Intent(DiscoveryService.ACTION_DISCOVERY_CLIENT_ERROR);

        receiver.onReceive(context,intent);
        verify(presenter,times(1)).discoveryError(expectedError);
    }

    @Test
    public void testOnReceiveWifiOff() {
        String expectedError = context.getResources().getString(R.string.error_wifi_off);
        Intent intent = new Intent(DiscoveryService.ACTION_WIFI_OFF);

        receiver.onReceive(context,intent);
        verify(presenter,times(1)).discoveryError(expectedError);
    }

    @Test
    public void testOnReceiveDiscoveryServerNotFound() {
        String expectedError = context.getResources().getString(R.string.error_not_found);
        Intent intent = new Intent(DiscoveryService.ACTION_DISCOVERY_SERVER_NOT_FOUND);

        receiver.onReceive(context,intent);
        verify(presenter,times(1)).discoveryError(expectedError);
    }
}