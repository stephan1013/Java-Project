package server;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Deck {
    private List<Integer> deck = new LinkedList<Integer>();
    private int[] noAtout = {19, 0, 0, 0, 10, 2, 3, 4};
    private int[] atout = {6, 0, 0, 9, 5, 14, 1, 3};

    void initDeck() {
        int i = 1;
        while (i != 33) {
            deck.add(i);
            i++;
        }
    }

    void suffleDeck() {
        Collections.shuffle(deck);
    }

    List<Integer> getDeck() {
        return deck;
    }


    public int[] getNoAtout() {
        return noAtout;
    }

    public int[] getAtout() {
        return atout;
    }
}