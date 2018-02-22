package server;

/**
 * Discards any incoming data.
 */


import io.netty.channel.socket.SocketChannel;
import network.Network;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Observable;
import java.util.Observer;

public class Client {

    private int id;
    private List<Integer> hand;
    private ServerHandler handler;
    private SocketChannel ch;
    private boolean play;
    private Observe watch;

    private int lcard;

    public class Observe extends Observable implements Observer {
        public void update(Observable o, Object arg) {
            net = (Network)arg;
            modify();
        }

        Network net = new Network();

        public void modify() {
            setChanged();
            notifyObservers(net);
        }

    }

    public Client(int id, SocketChannel ch, ServerHandler hand, Observer obs) {
        this.id = id;
        this.lcard = 0;
        this.ch = ch;
        this.handler = hand;
        this.play = false;
        this.hand = new LinkedList<Integer>();
        this.watch = new Observe();
        this.watch.addObserver(obs);
        this.handler.setCheck(this.watch);
    }

    public int getLcard() {
        return lcard;
    }

    public void setLcard(int card) {
        this.lcard = card;
    }

    public SocketChannel getCh() {
        return ch;
    }

    public void setCh(SocketChannel ch) {
        this.ch = ch;
    }

    public int getId() {
        return id;
    }

    public boolean isPlay() {
        return play;
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    public List<Integer> getHand() {
        return hand;
    }

    public void addHand(int card) {
        this.hand.add(card);
    }
}
