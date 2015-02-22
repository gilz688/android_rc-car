package ph.edu.msuiit.rccarclient.models;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static org.mockito.Mockito.*;

public class DiscoveryClientTest {
    private InetAddress mockAddress;

    @Before
    public void setUp(){
        mockAddress = mock(InetAddress.class);
    }

    @Test
    public void testStartDiscovery() throws Exception {
        DiscoveryClient client = spy(new DiscoveryClient(mockAddress));
        Mockito.doNothing().when(client).sendDiscoveryRequest(any(DatagramSocket.class));
        Mockito.doNothing().when(client).listenForResponses(any(DatagramSocket.class));
        client.startDiscovery();

        InOrder inOrder = inOrder(client);

        // verify if methods sendDiscoveryRequest() and listenForResponses() were executed
        inOrder.verify(client,times(1)).sendDiscoveryRequest(any(DatagramSocket.class));
        inOrder.verify(client,times(1)).listenForResponses(any(DatagramSocket.class));
    }

    @Test(timeout = 500)
    public void testListenForResponses() throws Exception {
        final String deviceName = "RC Car Server";
        InetAddress localAddress = mock(InetAddress.class);
        final int serverPort = 12345;

        DiscoveryClient client = new DiscoveryClient(mockAddress);

        // set mock OnServerFoundListener
        DiscoveryClient.OnServerFoundListener mockListener = mock(DiscoveryClient.OnServerFoundListener.class);
        client.setOnServerFoundListener(mockListener);

        // configure dummy socket to return device name from given address & port
        DummySocket dummySocket = new DummySocket(localAddress,serverPort);
        dummySocket.setReturnData(deviceName);

        // run tested method and verify
        client.listenForResponses(dummySocket);
        verify(mockListener,times(1)).onServerFound(deviceName,localAddress,serverPort);
    }

    @Test(timeout = 500)
    public void testListenForResponsesLoopback() throws Exception {
        final String deviceName = "discover";
        InetAddress localAddress = InetAddress.getLocalHost();
        final int serverPort = 12345;

        DiscoveryClient client = new DiscoveryClient(mockAddress);

        // set mock OnServerFoundListener
        DiscoveryClient.OnServerFoundListener mockListener = mock(DiscoveryClient.OnServerFoundListener.class);
        client.setOnServerFoundListener(mockListener);

        // configure dummy socket to return device name from given address & port
        DummySocket dummySocket = new DummySocket(localAddress,serverPort);
        dummySocket.setReturnData(deviceName);

        // run tested method and verify
        client.listenForResponses(dummySocket);
        verify(mockListener, never()).onServerFound(anyString(), any(InetAddress.class), anyInt());
    }

    @Test
    public void testSendDiscoveryRequest() throws Exception {

    }

    class DummySocket extends DatagramSocket{
        private byte[] returnData;
        private InetAddress address;
        private int port;

        public DummySocket(InetAddress address, int port) throws SocketException {
            super();
            this.address = address;
            this.port = port;
        }

        public void setReturnData(String string) {
            this.returnData = string.getBytes();
        }

        @Override
        public synchronized void receive(DatagramPacket pack) throws IOException {
            pack.setData(returnData);
            pack.setAddress(address);
            pack.setPort(port);
            Thread.currentThread().interrupt();
        }
    }
}