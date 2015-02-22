package ph.edu.msuiit.rccarclient.discovery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.InetAddress;

import ph.edu.msuiit.rccarclient.discovery.proto.DiscoveryInteractor;
import ph.edu.msuiit.rccarclient.discovery.proto.DiscoveryPresenter;
import ph.edu.msuiit.rccarclient.discovery.proto.DiscoveryView;
import ph.edu.msuiit.rccarclient.models.Device;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DiscoveryPresenterImplTest {
    @Mock
    private DiscoveryView mockView;
    @Mock
    private DiscoveryInteractor mockInteractor;

    private DiscoveryPresenter presenter;

    @Before
    public void setUp(){
        presenter = spy(new DiscoveryPresenterImpl(mockView,mockInteractor));
    }

    @Test
    public void testOnRefreshClicked() throws Exception {
        presenter.onRefreshClicked();
        InOrder inOrder = inOrder(mockView,presenter);

        verify(mockView, times(1)).hideError();
        inOrder.verify(mockView, times(1)).emptyDeviceList();
        inOrder.verify(mockInteractor, times(1)).startDiscovery();
    }

    @Test
    public void testDiscoveryStarted() throws Exception {
        presenter.discoveryStarted();
        verify(mockView, times(1)).showProgress();
        verify(mockView, never()).hideProgress();
    }

    @Test
    public void testOnDeviceFound() throws Exception {
        final String deviceName = "RC Car Server";
        final InetAddress address = InetAddress.getLocalHost();
        final int port = 12345;

        ArgumentCaptor<Device> argument = ArgumentCaptor.forClass(Device.class);
        presenter.onDeviceFound(deviceName, address, port);
        verify(mockView, times(1)).addDevice(argument.capture());
        assertEquals(deviceName, argument.getValue().getName());
        assertEquals(address, argument.getValue().getIpAddress());
    }

    @Test
    public void testDiscoveryError() throws Exception {
        String error = "WiFi not connected.";
        presenter.discoveryError(error);
        verify(mockView,times(1)).showError(error);
        verify(mockView,never()).hideError();
    }

    @Test
    public void testDiscoveryEnded() throws Exception {
        presenter.discoveryEnded();
        verify(mockView,times(1)).hideProgress();
    }

    @Test
    public void testOnStart() throws Exception {
        presenter.onStart();
        verify(mockInteractor,times(1)).startDiscovery();
    }

    @Test
    public void testOnEnd() throws Exception {

    }

    @Test
    public void testOnClickWifiSettings() throws Exception {
        presenter.onClickWifiSettings();
        verify(mockInteractor,times(1)).openWifiSettings();
    }

    @Test
    public void testOnItemClicked() throws Exception {
        Device mockDevice = mock(Device.class);
        presenter.onItemClicked(mockDevice);
        verify(mockInteractor,times(1)).connectToServer(mockDevice);
    }
}