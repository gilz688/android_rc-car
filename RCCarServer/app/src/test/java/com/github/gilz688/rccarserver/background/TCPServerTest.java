package com.github.gilz688.rccarserver.background;

import org.junit.Test;
import static org.junit.Assert.*;

public class TCPServerTest {

    @Test
    public void testStartServer() throws Exception {
        TCPServer server = new TCPServer();

        // start server
        server.startServer();

        // verify that isStopped() returns false while running
        assertEquals(false, server.isStopped());

        // stop server and wait for thread to stop
        server.stopServer();
        server.join(2000);

        // verify that isStopped() returns true
        assertEquals(true, server.isStopped());
    }

    @Test
    public void testStopServer() throws Exception {
        // create and stop server
        TCPServer server = new TCPServer();

        // verify that isStopped() returns true before start
        assertEquals(true, server.isStopped());

        server.startServer();

        // verify that isStopped() returns false after start
        assertEquals(false, server.isStopped());

        // stop server and wait for thread to stop
        server.stopServer();
        server.join(2000);

        // verify that isStopped() returns true
        assertEquals(true, server.isStopped());
    }
}
