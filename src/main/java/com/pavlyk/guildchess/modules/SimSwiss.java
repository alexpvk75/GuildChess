package com.pavlyk.guildchess.modules;

import java.util.*;

public class SimSwiss {
    public static void main(String[] args) {
        int N_giocatori = 64;
        TorneoSwiss torneo1 = new TorneoSwiss(N_giocatori);
        for (int g = 0; g < N_giocatori; g++) {
            torneo1.getGiocatori().add(new GiocatoreSwiss("Giocatore " + (g+1), new Random().nextInt(1000, 1751)));
        }

        torneo1.inizializzare();

        for (int r = 0; r < torneo1.getRoundsTot(); r++){
            if(r==0){
                torneo1.ordinare(false);
            } else {
                torneo1.aggiornare(false);
                torneo1.stampareClassifica();
            }
            torneo1.abbinare();
            System.out.println("Round " + (r+1));
            for (PartitaSwiss p : torneo1.getRounds().get(r).getPartite()){
                System.out.println(p.getBianco().getNome() + "(B) vs "+ p.getNero().getNome()+"(N)");
            }
            for (PartitaSwiss p : torneo1.getRounds().get(r).getPartite()){
                System.out.println();
                System.out.println(p.getBianco().getNome() +"(B) vs "+ p.getNero().getNome()+"(N)");
                double destino = new Random().nextDouble(0, 1);
                int ris = (destino>=0.4 && destino <= 0.6)?5:new Random().nextInt(0, 2);
                p.setRisultato((ris == 5)?0.5:(double)ris);
                if (p.getRisultato() ==0.5) {
                    System.out.println("Pareggio");
                } else {
                    System.out.println("Ha vinto " + ((p.getRisultato() == 1)?p.getBianco().getNome():p.getNero().getNome()));
                }
                p.getBianco().aggiungiPartita(p);
                p.getNero().aggiungiPartita(p);
            }
        }
        torneo1.aggiornare(true);
        torneo1.stampareClassifica();
    }
}