package ph.edu.msuiit.rccarserver.background;

import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.SocketFactory;
import ph.edu.msuiit.rccarserver.MockingUtils;
import static  org.mockito.Mockito.*;

public class TCPServerTaskTest {

    @Test
    public void testRun() throws Exception {
        //create mock output and dummy input streams
        OutputStream mockOutputStream = mock(OutputStream.class);
        String expected1 = "Hello,";
        String expected2 = "world!";
        String testInput = expected1 + '\n' + expected2 + '\n';
        InputStream inputStream = new StringInputStream(testInput);

        // create mock socket using Mockito
        SocketFactory socketFactory = MockingUtils.mockSocketFactory(mockOutputStream,inputStream);
        Socket mockSocket = socketFactory.createSocket();

        // create mock listener and set it as the current TCPServerListener
        TCPServer.TCPServerListener mockListener = mock(TCPServer.TCPServerListener.class);
        TCPServerTask task = new TCPServerTask(mockSocket);
        task.setTCPServerListener(mockListener);

        // run and verify that expected data were received
        task.run();
        verify(mockListener,times(1)).onDataReceive(expected1);
        verify(mockListener,times(1)).onDataReceive(expected2);
    }

    @Test
    public void testRunNothingSent() throws Exception {
        // create mock output and dummy input streams
        OutputStream mockOutputStream = mock(OutputStream.class);
        InputStream inputStream = new StringInputStream("");

        // create mock socket using Mockito
        SocketFactory socketFactory = MockingUtils.mockSocketFactory(mockOutputStream,inputStream);
        Socket mockSocket = socketFactory.createSocket();
        when(mockSocket.getInetAddress()).thenReturn(mock(InetAddress.class));

        // create mock listener and set it as the current TCPServerListener
        TCPServer.TCPServerListener mockListener = mock(TCPServer.TCPServerListener.class);
        TCPServerTask task = new TCPServerTask(mockSocket);
        task.setTCPServerListener(mockListener);

        // run and verify that no data was received
        task.run();
        verify(mockListener,never()).onDataReceive(anyString());
    }
}