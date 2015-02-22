package ph.edu.msuiit.rccarclient.discovery;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.provider.Settings;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.Robolectric;

import java.net.InetAddress;

import ph.edu.msuiit.rccarclient.CustomRobolectricRunner;
import ph.edu.msuiit.rccarclient.discovery.proto.DiscoveryInteractor;
import ph.edu.msuiit.rccarclient.models.Device;
import ph.edu.msuiit.rccarclient.tcp.TCPActivity;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(CustomRobolectricRunner.class)
public class DiscoveryInteractorImplTest {

    private Activity activity;
    private DiscoveryInteractor interactor;

    @Before
    public void setUp(){
        activity = spy(Robolectric.newInstanceOf(Activity.class));
        interactor = new DiscoveryInteractorImpl(activity);
    }

    @Test
    public void testStartDiscovery() throws Exception {
        final String expectedAction = DiscoveryService.INTENT_START_DISCOVERY;
        final String expectedClassName = DiscoveryService.class.getName();

        interactor.startDiscovery();
        ArgumentCaptor<Intent> argument = ArgumentCaptor.forClass(Intent.class);
        verify(activity, times(1)).startService(argument.capture());

        Intent intent = argument.getValue();
        assertEquals(expectedAction,intent.getAction());
        assertEquals(expectedClassName, intent.getComponent().getClassName());
    }

    @Test
    public void testConnectToServer() throws Exception {
        final String expectedClassName = TCPActivity.class.getName();
        final String expectedDeviceName = "RC Car Server";
        final InetAddress expectedAddress = mock(InetAddress.class);

        interactor.connectToServer(new Device(expectedDeviceName,expectedAddress));

        ArgumentCaptor<Intent> argument = ArgumentCaptor.forClass(Intent.class);
        verify(activity,times(1)).startActivity(argument.capture());

        Intent intent = argument.getValue();
        assertEquals(expectedClassName, intent.getComponent().getClassName());
        Device device =  (Device) intent.getParcelableExtra("device");
        assertEquals(expectedDeviceName, device.getName());
        assertEquals(expectedAddress, device.getIpAddress());
    }

    @Test
    public void testOpenWifiSettings() throws Exception {
        final String expectedAction = Settings.ACTION_WIFI_SETTINGS;

        interactor.openWifiSettings();

        ArgumentCaptor<Intent> argument = ArgumentCaptor.forClass(Intent.class);
        verify(activity,times(1)).startActivity(argument.capture());

        Intent intent = argument.getValue();
        assertEquals(expectedAction, intent.getAction());
    }
}