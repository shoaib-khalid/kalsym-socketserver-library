package com.kalsym.socketserver;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * Simplistic telnet server.
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 *
 * @version $Rev: 2080 $, $Date: 2010-01-26 18:04:19 +0900 (Tue, 26 Jan 2010) $
 */
public class SocketServer {

    private final ProcessSocketServerRequest request;
    public static Map<String, String> connMap = null;
    private final int listenPort;
    private final int readTimeout;
    private final int maxThread;
    private final long maxThreadAliveTime;

    public SocketServer(ProcessSocketServerRequest request,
            Map<String, String> connMap, int listenPort, int readTimeout,
            int maxThread, long maxThreadAliveTime) {
        this.request = request;
        this.connMap = connMap;
        this.listenPort = listenPort;
        this.readTimeout = readTimeout;
        this.maxThread = maxThread;
        this.maxThreadAliveTime = maxThreadAliveTime;
    }

    public void startServer() {
        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Configure the pipeline factory.
        bootstrap.setPipelineFactory(new SocketServerPipelineFactory(request, connMap, readTimeout, maxThread, maxThreadAliveTime));
        //bootstrap.
        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(listenPort));
    }
}
