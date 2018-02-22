package server;

import network.Network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.Observable;
import java.util.Observer;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class server {

    private LinkedList<Client> wplayer;
    private LinkedList<Client> playing;
    private LinkedList<Integer> table;
    private boolean play;
    private Deck deck;
    private Network net;
    private int color;
    private int bet;
    private int players;
    private Observe god;
    private int scoreA;
    private int scoreB;
    private int port;
    private int turn;
    private int player;

    private server(int port) {
        player = 0;
        turn = 0;
        players = 0;
        color = 0;
        bet = 0;
        scoreA = 0;
        scoreB = 0;
        this.port = port;
        play = false;
        deck = new Deck();
        deck.initDeck();
        net = new Network();
        god = new Observe();
        wplayer = new LinkedList<Client>();
        playing = new LinkedList<Client>();
        table = new LinkedList<Integer>();
    }

    private void calculateScore() throws InterruptedException {
        int win = 0;
        int card = 0;
        int tmp;
        boolean atout;
        boolean cut = false;
        int sum = 0;

        for (int i = 0; i < playing.size(); i++) {
            if (color == ((playing.get(i).getLcard() - 1) / 8)) {
                atout = true;
                tmp = deck.getAtout()[((playing.get(i).getLcard() - 1) % 8)];
            } else {
                atout = false;
                tmp = deck.getNoAtout()[((playing.get(i).getLcard() - 1) % 8)];
            }
            if (!cut && (card < tmp) && !atout) {
                win = i;
                card = tmp;
            }
            else if (atout) {
                if (!cut) {
                    cut = true;
                    win = i;
                    card = tmp;
                }
                else {
                    if (card < tmp) {
                        win = i;
                        card = tmp;
                    }
                }
            }
            sum += tmp;
        }
        if ((playing.get(win).getId() % 2) == 0) {
            scoreA += sum;
            for (Client aPlaying : playing) {
                SendMessage("Team A win " + sum + " point !", 1, scoreA, scoreB, aPlaying);
            }
        }
        else {
            scoreB += sum;
            for (Client aPlaying : playing) {
                SendMessage("Team B win " + sum + " point !", 1, scoreA, scoreB, aPlaying);
            }
        }
        net.table.clear();
        if (!playing.get(0).getHand().isEmpty())
        {
            player = win;
            SendMessage(net.PICK, 2, scoreA,scoreB, playing.get(player));
        }
        else {
            if (scoreA >= 1000 || scoreB >= 1000) {
                String winner = (scoreA >= 1000) ? "Team A win the game with " : "Team B win the game with ";
                int score = (scoreA >= 1000) ?  scoreA : scoreB;
                for (Client aPlaying : playing) {
                    SendMessage(winner + score + " point", 1, scoreA, scoreB, aPlaying);
                }
                scoreB = 0;
                scoreA = 0;
                play = false;
                lobby();
            }
            else {
                String winner = "";
                if ((playing.get(win).getId() % 2) == 0) {
                    scoreA += bet + 10;
                    winner = "Team A win this round and have " + scoreA + " point";
                }
                else {
                    scoreB += bet + 10;
                    winner = "Team B win this round and have " + scoreB + " point";
                }
                for (Client aPlaying : playing) {
                    SendMessage(winner, 1, scoreA, scoreB, aPlaying);
                }
                start();
            }
        }

    }

    private void chooseCard(Network net) throws InterruptedException {
        table.add(net.hand.get((Integer.parseInt(net.message) - 1)));
        playing.get(net.id).setLcard(net.hand.get((Integer.parseInt(net.message) - 1)));
        playing.get(net.id).getHand().remove((Integer.parseInt(net.message) - 1));
        for (Client aPlaying : playing) {
            SendMessage("Game\n", 0, scoreA, scoreB, aPlaying);
        }
        turn += 1;
        player = (player + 1) == 4 ? 0 : (player + 1);
        if (turn < 4)
            SendMessage(net.PICK, 2, scoreA,scoreB, playing.get(player));
        else {
            turn = 0;
            calculateScore();
        }
    }

    private void Deconnection() throws InterruptedException {
        for (int i = 0; i < playing.size(); i++) {
            if (!playing.get(i).getCh().isActive() && playing.get(i).isPlay()) {
                playing.get(i).setPlay(false);
                play = false;
                players -= 1;
                playing.remove(i);
                for (Client aPlaying : playing) {
                    if (aPlaying.isPlay())
                        SendMessage("One player disconnected ...\n", 1, scoreA, scoreB, aPlaying);
                }
                System.out.println("One player disconnected ...");
            }
        }
        lobby();
    }

    private void chooseBet(Network net) throws InterruptedException {
        int tmp = Integer.parseInt(net.message);
        net.bet.add(tmp);
        if (bet < tmp) {
            bet = tmp;
            color = net.color.get(net.color.size()- 1);
        }
        turn += 1;
        if (bet != 160)
            player = (player + 1) == 4 ? 0 : (player + 1);
        if (turn < 4 && bet != 160)
            SendMessage(net.CONTRACT, 3, scoreA, scoreB, playing.get(player));
        else {
            turn = 0;
            for (int i = 0; i < net.bet.size(); i++)
                if (bet != net.bet.get(i)) {
                    net.bet.set(i, -1);
                    net.color.set(i, -1);
                }
                color -= 1;
            SendMessage(net.PICK, 2, scoreA, scoreB, playing.get(player));
        }
    }


    private void  chooseColor(Network net) throws InterruptedException {
        int tmp = Integer.parseInt(net.message);
        net.color.add(tmp);
        SendMessage(net.BET, 5, scoreA, scoreB, playing.get(player));
    }

    private void chooseContrat(Network net) throws InterruptedException {
        if (Integer.parseInt(net.message) == 1) {
            SendMessage(net.COLOR, 4, scoreA, scoreB, playing.get(player));
        }
        else {
            turn += 1;
            net.bet.add(-1);
            net.color.add(-1);
            player = (player + 1) == 4 ? 0 : (player + 1);
            if (turn < 4)
                SendMessage(net.CONTRACT, 3, scoreA, scoreB, playing.get(player));
            else {
                turn = 0;
                for (int i = 0; i < net.bet.size(); i++) {
                    if (bet != net.bet.get(i)) {
                        net.bet.set(i, -1);
                        net.color.set(i, -1);
                    }
                }
                if (bet == 0) {
                    for (Client clt : playing) {
                        clt.getHand().clear();
                    }
                    start();
                }
                else
                    SendMessage(net.PICK, 2, scoreA, scoreB, playing.get(player));
            }
        }
    }

    public class Observe implements Observer {
        public void update(Observable o, Object arg) {
            net = (Network) arg;
            if (play) {
                if (net.state == 2)
                    try {
                        chooseCard(net);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                else if (net.state == 10)
                    try {
                        Deconnection();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                else if (net.state == 3)
                    try {
                        chooseContrat(net);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                else if (net.state == 4)
                    try {
                        chooseColor(net);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                else if (net.state == 5)
                    try {
                        chooseBet(net);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        }
    }

    private boolean playerStatut() {
        for (Client aPlaying : playing) {
            if (!aPlaying.isPlay())
                return false;
        }
        return true;
    }

    private void SendMessage(String message, int state, int scorea, int scoreb, Client client) throws InterruptedException {
        net.message = message;
        net.id = client.getId();
        net.hand = client.getHand();
        net.table = table;
        net.state = state;
        net.scoreA = scorea;
        net.scoreB = scoreb;
        client.getCh().writeAndFlush(net);
        TimeUnit.MILLISECONDS.sleep(100);

    }

    private void game() throws InterruptedException {
        for (Client aPlaying : playing) {
            SendMessage("Game\n", 0, scoreA, scoreB, aPlaying);
        }
        SendMessage(net.CONTRACT, 3, scoreA,scoreB, playing.get(0));
    }

    private void start() throws InterruptedException {
        int index = 0;

        net.table.clear();
        net.bet.clear();
        net.color.clear();
        bet = 0;
        color = 0;
        player = 0;
        turn = 0;
        for (Client aPlaying : playing) {
            aPlaying.getHand().clear();
            aPlaying.setLcard(0);
        }
        deck.suffleDeck();
        for (int i = 0; i < 4; i++) {
            playing.get(i).setPlay(true);
            for (int j = 0; j < 8; j++) {
                playing.get(i).addHand(deck.getDeck().get(index));
                index += 1;
            }
        }
        game();
    }


    private void lobby() throws InterruptedException {
        if (!play) {
            if (playing.size() < 4) {
                for (int i = 0; i < wplayer.size(); i++) {
                    Client tmp = wplayer.poll();
                    playing.add(tmp);
                }
            }
            if (!playerStatut()) {
                players += 1;
                System.out.println("One player has connected, waiting for " + (4 - players) + " more players");
                for (int i = 0; i < playing.size(); i++) {
                    SendMessage("One player has connected, waiting for " + (4 - players) + " more players",
                            1, scoreA, scoreB, playing.get(i));
                }
            }
            if (playing.size() == 4) {
                play = true;
                System.out.println("Start the game");
                for (Client aPlaying : playing) {
                    SendMessage("Start the game\n", 1, scoreA, scoreB,aPlaying);
                }
                start();
            }
        }
    }


    private void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                            @Override
                            public void initChannel (SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingResolver(Network.class.getClassLoader())));
                            ch.pipeline().addLast(new ObjectEncoder());
                            ServerHandler tmp = new ServerHandler();
                            ch.pipeline().addLast(tmp);
                            wplayer.add(new Client(players, ch, tmp, god));
                            SendMessage("Welcome to game\n", 1, scoreA, scoreB, wplayer.getFirst());
                            lobby();
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            ChannelFuture f = b.bind(port).sync(); // (7)

            f.channel().closeFuture().sync();
        }
        finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {

        int port = 2222;
        try {
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException e) {

        }

        Enumeration ee = NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses();
        ee.nextElement();
        System.out.println("Game server Start: IP: " + ((InetAddress) ee.nextElement()).getHostAddress() + " Port: " + port);
        new server(port).run();
    }

}

