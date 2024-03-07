package com.kalsym.socketserver;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import static org.jboss.netty.channel.Channels.pipeline;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 *
 * @version $Rev: 2080 $, $Date: 2010-01-26 18:04:19 +0900 (Tue, 26 Jan 2010) $
 *
 */
public class SocketServerPipelineFactory implements
        ChannelPipelineFactory {

    private static Timer timer = new HashedWheelTimer();
    private final ChannelHandler timeoutHandler;
    private final int corePoolSize;
    private final long keepAliveTime;
    private final ProcessSocketServerRequest request;
    public static Map<String, String> connMap = null;

    public SocketServerPipelineFactory(ProcessSocketServerRequest request,
            Map<String, String> connMap, int readTimeout, int maxThread,
            long maxThreadAliveTime) {
        timeoutHandler = new ReadTimeoutHandler(timer, readTimeout);
        this.corePoolSize = maxThread;
        this.keepAliveTime = maxThreadAliveTime;
        this.request = request;
        this.connMap = connMap;
    }

    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        //set read timeout
        pipeline.addLast("timeout", this.timeoutHandler);

        // Add the text line codec combination first,
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());

        //corePoolSize - the maximum number of active threads
        //maxChannelMemorySize - the maximum total size of the queued events per channel. Specify 0 to disable.
        //maxTotalMemorySize - the maximum total size of the queued events for this pool Specify 0 to disable.
        //keepAliveTime - the amount of time for an inactive thread to shut itself down
        //unit - the TimeUnit of keepAliveTime
        long maxChannelMemorySize = 0;
        long maxTotalMemorySize = 0;
        pipeline.addLast("executor", new ExecutionHandler(new OrderedMemoryAwareThreadPoolExecutor(corePoolSize, maxChannelMemorySize, maxTotalMemorySize, keepAliveTime, TimeUnit.SECONDS)));

        // and then business logic.
        pipeline.addLast("handler", new SocketServerHandler(request, connMap, corePoolSize));

        return pipeline;
    }
}
