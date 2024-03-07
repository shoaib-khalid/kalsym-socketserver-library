package com.kalsym.socketserver;

import java.util.Map;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * Handles a server-side channel.
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 *
 * @version $Rev: 2121 $, $Date: 2010-02-02 09:38:07 +0900 (Tue, 02 Feb 2010) $
 */
public class SocketServerHandler extends SimpleChannelUpstreamHandler {

    private final ProcessSocketServerRequest request;
    private final int maxThread;
    public static Map<String, String> connMap = null;

    public SocketServerHandler(ProcessSocketServerRequest request,
            Map<String, String> connMap, int maxThread) {
        this.request = request;
        this.connMap = connMap;
        this.maxThread = maxThread;
    }

    @Override
    public void messageReceived(
            ChannelHandlerContext ctx, MessageEvent e) {

        // Cast to a String first.
        // We know it is a String because we put some codec in TelnetPipelineFactory.
        String request = (String) e.getMessage();

        // Generate and write a response.
        String response = "";

//        int max_thread_size=Integer.parseInt(ConfigReader.GetProperty("max_thread"));
        int max_thread_size = maxThread;

        if (connMap.size() > max_thread_size) {
            //reject
            response = this.request.doRejectMO(request);
        } else {
            //process
            response = this.request.doProcessMO(request);
        }
        //send the reponse to client
        ChannelFuture future = e.getChannel().write(response);
        future.addListener(ChannelFutureListener.CLOSE);

    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        String timestamp = String.valueOf(System.nanoTime());
        connMap.put(e.getChannel().getId().toString(), timestamp);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx,
            ChannelStateEvent e) {
        connMap.remove(e.getChannel().getId().toString());
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx, ExceptionEvent e) {
        try {
            System.out.println("Unexpected exception from downstream:" + e.getCause());
            connMap.remove(e.getChannel().getId().toString());
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
        }
    }

}
