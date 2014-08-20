package de.danielbasedow.prospecter.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.danielbasedow.prospecter.core.Instance;
import de.danielbasedow.prospecter.core.schema.SchemaConfigurationError;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        ServerConfig config;
        try {
            config = mapper.readValue(new File(args[0]), ServerConfig.class);
        } catch (IOException e) {
            LOGGER.error("unable to read config file '" + args[0] + "'", e);
            return;
        }

        final Instance instance = new Instance(config.getHomeDir());

        final EventLoopGroup boss = new NioEventLoopGroup(1);
        final EventLoopGroup worker = new NioEventLoopGroup();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                LOGGER.info("shutting down");
                boss.shutdownGracefully();
                worker.shutdownGracefully();
                instance.shutDown();
            }
        });
        try {
            instance.initialize();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpApiServerInitializer(instance));
            Channel channel = bootstrap.bind(config.getBindInterface(), config.getPort()).sync().channel();

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SchemaConfigurationError e) {
            e.printStackTrace();
        } finally {
            LOGGER.info("shutting down");
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
