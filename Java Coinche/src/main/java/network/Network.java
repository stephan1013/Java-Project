package network;


import io.netty.handler.codec.haproxy.HAProxyCommand;

import javax.swing.text.MaskFormatter;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles a server-side channel.
 */

public class Network implements Serializable {

    public int id;
    public int scoreA;
    public int scoreB;
    public int state;
    public List<Integer> hand;
    public List<Integer> table;
    public List<Integer> bet;
    public List<Integer> color;
    public String message;
    public final String CONTRACT = "Contract : Take(1), Pass(2)";
    public final String COLOR = "Choose color : HEARTS(1), SPADES(2), DIAMONDS(3), CLUBS(4) ";
    public final String BET = "Bet :(80,90,100,110,120,130,140,150,160) You must bet more than the last bet";
    public final String PICK = "Put a card : ";

    public Network() {

        id = 0;
        scoreA = 0;
        scoreB = 0;
        state = 0;
        bet = new LinkedList<Integer>();
        color = new LinkedList<Integer>();
        hand = new LinkedList<Integer>();
        table = new LinkedList<Integer>();
        message = "";
    }
}