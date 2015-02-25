package ph.edu.msuiit.rccarserver.background;

import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class TCPServerTest {

    @Test
    public void testStartServer() throws Exception {
        // Create and spy TCPServer object using Mockito
        TCPServer server = spy(new TCPServer());

        // start server
        server.startServer();

        // verify that isStopped() returns false
        assertEquals(false,server.isStopped());

        // stop server and wait for thread to stop
        server.stopServer();
        server.join();

        // verify that isStopped() returns true
        assertEquals(true,server.isStopped());

        // verify using Mockito that method run() was executed once
        verify(server,times(1)).run();
    }

    @Test
    public void testStopServer() throws Exception {
        // create and stop server
        TCPServer server = new TCPServer();

        // verify that isStopped() returns false
        assertEquals(false,server.isStopped());

        server.startServer();

        // verify that isStopped() returns true
        assertEquals(true,server.isStopped());

        // stop server and wait for thread to stop
        server.stopServer();
        server.join();
    }
}