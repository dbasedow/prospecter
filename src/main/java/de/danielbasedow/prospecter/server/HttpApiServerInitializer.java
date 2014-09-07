package de.danielbasedow.prospecter.server;

import de.danielbasedow.prospecter.core.Instance;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpApiServerInitializer extends ChannelInitializer<SocketChannel> {

    private final Instance instance;

    public HttpApiServerInitializer(Instance instance) {
        this.instance = instance;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new HttpRequestDecoder());
        pipeline.addLast(new HttpObjectAggregator(1048576));
        pipeline.addLast(new HttpResponseEncoder());

        pipeline.addLast(new HttpApiRequestHandler(instance));
    }
}
