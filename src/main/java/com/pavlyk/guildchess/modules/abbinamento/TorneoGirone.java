package com.pavlyk.guildchess.modules.abbinamento;

import java.util.*;
import java.lang.Math;

public class TorneoGirone {
    private ArrayList<GiocatoreGirone> giocatori = new ArrayList<>();
    private ArrayList<RoundGirone> rounds = new ArrayList<>();
    private int roundsTot;
    private int minElo;

    public TorneoGirone(int N_giocatori){
        this.roundsTot = (int)(2*(N_giocatori-(1-N_giocatori%2)));
    }

    public void inizializzare() {
        this.giocatori.sort((a, b) -> {
            int c = Integer.compare(b.getElo(), a.getElo());
            if (c != 0) return c;
            return a.getNome().compareTo(b.getNome());
        });
        for (int i = 0; i < this.giocatori.size(); i++) {
            giocatori.get(i).setNumeroAbbinamento(i + 1);
        }
        this.setMinElo(giocatori.get(0).getElo());
        for (GiocatoreGirone g : this.giocatori){
            if(g.getElo() < this.getMinElo()) this.setMinElo(g.getElo());
            for(GiocatoreGirone o : this.giocatori){
                if(!o.equals(g)){
                    g.setTPR(g.getTPR()+o.getElo());
                }
            }
            g.setTPR(g.getTPR()/(this.giocatori.size()-1));
        }
    }
    public int getRoundsTot(){
        return roundsTot;
    }
    public ArrayList<GiocatoreGirone> getGiocatori() {
        return giocatori;
    }
    public void setGiocatori(ArrayList<GiocatoreGirone> giocatori) {
        this.giocatori = giocatori;
    }
    public ArrayList<RoundGirone> getRounds() {
        return rounds;
    }
    public void setRounds(ArrayList<RoundGirone> rounds) {
        this.rounds = rounds;
    }

    public void abbinare(){
        ArrayList<GiocatoreGirone> daAbbinare = new ArrayList<>(this.getGiocatori());
        daAbbinare.sort((a, b) -> {
            int c = Integer.compare(b.getNumeroAbbinamento(), a.getNumeroAbbinamento());
            return c;
        });
        if (daAbbinare.size() % 2 != 0) daAbbinare.add(null);
        RoundGirone taleRound = new RoundGirone();
        ArrayList<GiocatoreGirone> gruppoA = new ArrayList<>();
        ArrayList<GiocatoreGirone> gruppoB = new ArrayList<>();
        for(int i = 0; i < daAbbinare.size(); i++){
            if (i % 2 == 0) {
                gruppoA.add(daAbbinare.get(i));
            } else {
                gruppoB.add(daAbbinare.get(i));
            }
        }
        int roundsFinora = this.getRounds().size();
        //uso Berger tables
        for(int shift = 0; shift < roundsFinora; shift++){
            Collections.rotate(gruppoA, 1);
            Collections.rotate(gruppoB, -1);
            GiocatoreGirone temp = gruppoB.get(gruppoB.size()-1);
            gruppoB.set(gruppoB.size()-1, gruppoA.get(0));
            gruppoA.set(0, temp);
            Collections.swap(gruppoA, 0, 1);
        }
        boolean decisioneColore = (roundsFinora%2==0);
        int coppie = daAbbinare.size()/2;
        for(int p = 0; p < coppie; p++){
            GiocatoreGirone A = gruppoA.get(p);
            GiocatoreGirone B = gruppoB.get(p);
            if(A == null || B == null) continue;
            PartitaGirone coppia = decisioneColore ? new PartitaGirone(A, B) : new PartitaGirone(B, A);
            taleRound.getPartite().add(coppia);
        }
        this.getRounds().add(taleRound);
    }
    public void aggiornare(boolean finito){
        for (GiocatoreGirone g : giocatori){
            g.setVittorie(0);
            g.setPareggi(0);
            g.setKoya(0);
            g.setBerger(0);
            for (GiocatoreGirone o : giocatori){
                if (!o.equals(g)){
                    for(RoundGirone r : rounds){
                        for(PartitaGirone p : r.getPartite()){
                            if((g.equals(p.getBianco()) && o.equals(p.getNero())) || (o.equals(p.getBianco()) && g.equals(p.getNero()))){
                                int colore = (g.equals(p.getBianco())) ? 0 : 1;
                                g.setBerger(g.getBerger() + (Math.abs(colore-p.getRisultato()) * o.getPunteggio()));
                                if((double)(o.getPunteggio()/o.getPartite().size())>=0.5){
                                    g.setKoya(g.getKoya() + (Math.abs(colore-p.getRisultato())));
                                }
                                g.setVittorie(g.getVittorie() + (int)((p.getRisultato()!=0.5)?Math.abs(colore-p.getRisultato()):0));
                                g.setPareggi(g.getPareggi() + (int)((p.getRisultato()==0.5)?1:0));
                            }
                        }
                    }
                }
            }
            //TPR
            if(finito){
                double P_TPR = (double) (g.getPunteggio() / (double) (g.getPartite().size()));
                double D_TPR;
                if ((P_TPR) == 1.0 || (P_TPR) == 0.0){
                    D_TPR = 1600*P_TPR-800;
                } else {
                    D_TPR = -400 * Math.log10((1.0 - P_TPR) / P_TPR);
                }
                g.setTPR((g.getTPR() + D_TPR)>=this.getMinElo()?(g.getTPR() + D_TPR):this.getMinElo());
            }
        }
        this.spareggiare();
    }

    public void spareggiare() {
        giocatori.sort((a, b) -> {
            //punteggi
            int c = Double.compare(b.getPunteggio(), a.getPunteggio());
            if (c != 0) return c;
            //vittorie
            c = Integer.compare(b.getVittorie(), a.getVittorie());
            if (c != 0) return c;
            //pareggi
            c = Integer.compare(b.getPareggi(), a.getPareggi());
            if (c != 0) return c;
            //Sonneborn-Berger - somma pesata dei punteggi degli opponenti in base a risultati head-to-head
            c = Double.compare(b.getBerger(), a.getBerger());
            if (c != 0) return c;
            //Koya - somme dei punti ottenuti contro gli opponenti che hanno winto piu di 50% delle proprie partite
            c = Double.compare(b.getKoya(), a.getKoya());
            if (c != 0) return c;
            // numero di abbinamento (ranking iniziale per Elo)
            c = Integer.compare(a.getNumeroAbbinamento(), b.getNumeroAbbinamento());
            return c; //fallback definitivo
        });
    }

    public void stampareClassifica(){
        int gx = 1;
        if(this.getRounds().size() == this.getRoundsTot()){
            System.out.println("\nPOS | Nome(#) | PTS | W | D | TB1 | TB2 | TPR ");
        } else {
            System.out.println("\nPOS | Nome(#) | PTS | W | D | TB1 | TB2");
        }
        for (GiocatoreGirone g : giocatori){
            if(this.getRounds().size() == this.getRoundsTot()){
                System.out.println(gx + " | " + g.getNome() + " (" + g.getNumeroAbbinamento() + ") | " + g.getPunteggio() + " | " + g.getVittorie() 
                + " | " + g.getPareggi() + " | " + g.getBerger() + " | " + g.getKoya() + " | " + (int)g.getTPR());
            } else {
                System.out.println(gx + " | " + g.getNome() + " (" + g.getNumeroAbbinamento() + ") | " + g.getPunteggio() + " | " + g.getVittorie() 
                + " | " + g.getPareggi() + " | " + g.getBerger() + " | " + g.getKoya());
            }
            gx++;
        }
    }

    public int getMinElo() {
        return minElo;
    }

    public void setMinElo(int minElo) {
        this.minElo = minElo;
    }
}
