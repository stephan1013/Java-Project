package server;

import java.util.Observable;
import network.Network;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Observer;

public class ServerHandler extends SimpleChannelInboundHandler<Network> {

    private class Handler extends  Observable {
        public Network net = new Network();
        public boolean connect;
        public void modify() {
            setChanged();
            notifyObservers(net);
        }
    }

    private Handler check = new Handler();

    public void setCheck(Observer obs) {
    check.addObserver(obs);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        check.connect = true;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Network msg) throws Exception {
        check.net = msg;
        check.modify();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        check.connect = false;
        check.net.state = 10;
        check.net.id = 0;
        check.net.message = "Deconnected";
        check.modify();
    }
}