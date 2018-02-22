package client;

import network.Network;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.awt.peer.SystemTrayPeer;
import java.util.Collections;
import java.util.Scanner;

public class ClientHandler extends SimpleChannelInboundHandler<Network> { // (1)

    //message
    private static final String ACE = "Ace";
    private static final String KING = "King";
    private static final String QUEEN = "Queen";
    private static final String JACK = "Jack";
    private static final String HEARTS = " of hearts ";
    private static final String SPADES = " of spades ";
    private static final String DIAMONDS = " of diamonds ";
    private static final String CLUBS = " of clubs ";
    private static final String SCOREY = "Your Score : ";
    private static final String SCOREO = "Opponent Score : ";
    private static final String TABLE = "Table :\n";
    private static final String DECK = "\n\nDeck :\n";
    private static final String EMPTY = "No cards yet\n";
    private static final String SPLIT = "*************\n\n";
    private static final String PLAYER = "PLAYER ";
    private static final String ENTER = "\n";

    private Network net = new Network();


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    private void print_card(int nb, int card_nb) {
        String card;
        int id;

        id = nb % 8;
        // nb of the card
        if (id == 1)
            card = ACE;
        else if (id == 0)
            card = KING;
        else if (id == 7)
            card = QUEEN;
        else if (id == 6)
            card = JACK;
        else
            card = String.valueOf(id + 5);
        // color of the card
        if (nb >= 1 && nb <= 8)
            card += HEARTS;
        else if (nb >= 9 && nb <= 16)
            card += SPADES;
        else if (nb >= 17 && nb <= 24)
            card += DIAMONDS;
        else if (nb >= 25 && nb <= 32)
            card += CLUBS;
        System.out.print("[" + card_nb + "]" + card + " ");
        if (card_nb == 4)
            System.out.print(ENTER);
    }


    private void display(Network msg) {
        //print score
        System.out.print(SPLIT);
        System.out.print(SCOREY + ((msg.id % 2 == 0) ? msg.scoreA : msg.scoreB) + ENTER);
        System.out.print(SCOREO + ((msg.id % 2 == 1) ? msg.scoreA : msg.scoreB) + ENTER + ENTER);
        // print contract
        if (!msg.bet.isEmpty()) {
            display_contract(msg);
            System.out.print(ENTER);
        }
        // print table
        System.out.print(TABLE);
        if (msg.table.isEmpty())
            System.out.print(EMPTY);
        else {
            int id = msg.id;
            for (int count = 0; count < msg.table.size(); count++) {
                if (id == 0)
                    id = 4;
                id--;
            }
            for (int i = 0; i < msg.table.size(); i++) {
                print_card(msg.table.get(i), id);
                if (id == 3)
                    id = -1;
                id++;
            }
        }
        //print deck
        System.out.print(DECK);
        if (msg.hand.isEmpty())
            System.out.print(EMPTY);
        else
            for (int y = 0; y < msg.hand.size(); y++)
                print_card(msg.hand.get(y), y + 1);
        System.out.print(ENTER + ENTER + SPLIT + ENTER);

    }

    private void display_contract(Network msg) {
        for (int i = 0; i < msg.bet.size(); ++i) {
            if (msg.bet.get(i) != -1) {
                System.out.print(PLAYER + i + " bet " + msg.bet.get(i) + " points");
                if (msg.color.get(i) == 1)
                    System.out.print(HEARTS + ENTER);
                else if (msg.color.get(i) == 2)
                    System.out.print(SPADES + ENTER);
                else if (msg.color.get(i) == 3)
                    System.out.print(DIAMONDS + ENTER);
                else if (msg.color.get(i) == 4)
                    System.out.print(CLUBS + ENTER);
            }
        }
    }

    private void check_and_send(ChannelHandlerContext ctx) {
        Scanner scanner = new Scanner(System.in);
        String send = new String();
        while (true) {
            System.out.print("         " + net.message + ENTER);
            send = scanner.nextLine();
            try {
                if (net.state == 2) {
                    if (send.length() == 1 && Integer.parseInt(send) <= net.hand.size() && Integer.parseInt(send) > 0)
                        break;
                    else
                        System.out.print("The card is out of range\n\n");
                } else if (net.state == 3) {
                    if (send.length() == 1 && (Integer.parseInt(send) == 1 || Integer.parseInt(send) == 2))
                        break;
                    else
                        System.out.print("Write 1 or 2 to take the contract or not\n\n");

                } else if (net.state == 4) {
                    if (send.length() == 1 && Integer.parseInt(send) > 0 && Integer.parseInt(send) < 5)
                        break;
                    else
                        System.out.print("Write the number of the color between 1 and 4\n\n");
                } else if (net.state == 5) {
                    if (send.length() < 4 && (net.bet.isEmpty() || Integer.parseInt(send) > Collections.max(net.bet))
                            && Integer.parseInt(send) <= 160 && Integer.parseInt(send) % 10 == 0)
                        break;
                    else
                        System.out.print("Write a bet between 80 and 160\n\n");
                }
            } catch (NumberFormatException e) {
                System.out.print("Write an Interger\n\n");
            }
        }
        net.message = send;
        ctx.writeAndFlush(net);
        System.out.print(ENTER);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Network msg) throws Exception {
        // print game
        if (msg.state == 0) {
            display(msg);
        }
        //print msg
        else if (msg.state == 1) {
            System.out.print("         " + msg.message + ENTER + ENTER);
        }
        //print game + msg + get input from user
        else if (msg.state  >= 2) {
            if (msg.state == 2)
                display(msg);
            net = msg;
            if (msg.state == 3) {
                display_contract(msg);
                System.out.print(ENTER);
            }
            check_and_send(ctx);

        }
    }
}
