package client;


import network.Network;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

import static java.lang.System.exit;

public class client {

    public static String host;
    public static int port;

    private static void disableWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);

            Class cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) throws Exception {

        try {
            if (args.length > 0) {
                host = args[0];
                port = Integer.parseInt(args[1]);
            }
        } catch (Exception e) {
            System.out.println("Usage .jar host port");
            exit (0);
        }
        disableWarning();
        EventLoopGroup group = new NioEventLoopGroup();
        try
        {
            Bootstrap b = new Bootstrap();
            b.group(group);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingResolver(Network.class.getClassLoader())));
                    ch.pipeline().addLast(new ObjectEncoder());
                    ch.pipeline().addLast(new ClientHandler());
                }
            });
            try {
                b.connect(host, port).sync().channel().closeFuture().sync();
            } catch (Exception e) {
                System.out.println("Connection failed");
                exit (0);
            }
        }
        finally
        {
            group.shutdownGracefully();
        }
    }
}
