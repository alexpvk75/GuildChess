package com.pavlyk.guildchess.modules.abbinamento;

import java.util.*;
import java.lang.Math;

public class TorneoSwiss {
    private ArrayList<GiocatoreSwiss> giocatori = new ArrayList<>();
    private ArrayList<RoundSwiss> rounds = new ArrayList<>();
    private int roundsTot;
    private int coloreIniziale; // 0 = bianco, 1 = nero (estratto a sorte prima del round 1)
    private int minElo;

    public TorneoSwiss(int N_giocatori){
        this.roundsTot = (int)(Math.ceil(Math.log(N_giocatori)/Math.log(2)));
    }

    public void inizializzare() {
        giocatori.sort((a, b) -> {
            int c = Integer.compare(b.getElo(), a.getElo());
            if (c != 0) return c;
            return a.getNome().compareTo(b.getNome());
        });
        for (int i = 0; i < giocatori.size(); i++) {
            giocatori.get(i).setNumeroAbbinamento(i + 1);
        }
        coloreIniziale = new Random().nextBoolean() ? 0 : 1;
        this.setMinElo(giocatori.get(0).getElo());
        for (GiocatoreSwiss g : giocatori){
            if(g.getElo() < this.getMinElo()) this.setMinElo(g.getElo());
            for(GiocatoreSwiss o : giocatori){
                if(!o.equals(g)){
                    g.setTPR(g.getTPR()+o.getElo());
                }
            }
            g.setTPR(g.getTPR()/(giocatori.size()-1));
        }
    }
    public int getRoundsTot(){
        return roundsTot;
    }
    public ArrayList<GiocatoreSwiss> getGiocatori() {
        return giocatori;
    }
    public void setGiocatori(ArrayList<GiocatoreSwiss> giocatori) {
        this.giocatori = giocatori;
    }
    public ArrayList<RoundSwiss> getRounds() {
        return rounds;
    }
    public void setRounds(ArrayList<RoundSwiss> rounds) {
        this.rounds = rounds;
    }

    public void abbinare(){
        ArrayList<GiocatoreSwiss> daAbbinare = new ArrayList<>(this.getGiocatori());
        //dare bye se numero di giocatori e' dispari
        GiocatoreSwiss giocatoreBye = null;
        if(this.getGiocatori().size() % 2 != 0){
            int gx = daAbbinare.size() - 1;
            while(gx >=0){
                if(!daAbbinare.get(gx).getBye()) {
                    giocatoreBye = daAbbinare.get(gx);
                    daAbbinare.remove(gx);
                    break;
                }
                gx--;
            }
        }
        RoundSwiss taleRound = new RoundSwiss();
        boolean ordineCasuale = this.rounds.isEmpty();
        boolean sucesso = abbinare(daAbbinare, taleRound, ordineCasuale);
        if (sucesso) {
            if(giocatoreBye!=null){
                giocatoreBye.dareBye();
            }
        }
        this.getRounds().add(taleRound);
    }

    private boolean abbinare(ArrayList<GiocatoreSwiss> daAbbinare, RoundSwiss round, boolean ordineCasuale) {
        if (daAbbinare.isEmpty()) return true;

        GiocatoreSwiss A = daAbbinare.get(0);
        ArrayList<Integer> candidati = new ArrayList<>();
        for (int i = 1; i < daAbbinare.size(); i++) {
            if (!A.getOpponenti().contains(daAbbinare.get(i))) {
                candidati.add(i);
            }
        }
        if (candidati.isEmpty()) return false;
        if (ordineCasuale) {
            Collections.shuffle(candidati, new Random());
        }
        for (int candidato : candidati) {
            GiocatoreSwiss B = daAbbinare.get(candidato);
            ArrayList<GiocatoreSwiss> resto = new ArrayList<>(daAbbinare);
            resto.remove(candidato);
            resto.remove(0);
            boolean decisioneColore = decidereColore(A, B);
            PartitaSwiss coppia = decisioneColore ? new PartitaSwiss(A, B) : new PartitaSwiss(B, A);
            round.getPartite().add(coppia);
            if (abbinare(resto, round, ordineCasuale)) return true;
            round.getPartite().remove(round.getPartite().size() - 1);
        }
        return false;
    }

    public void aggiornare(boolean finito){
        for (GiocatoreSwiss g : giocatori){
            g.setVittorie(0);
            g.setPareggi(0);
            g.setBuchholz(0);
            g.setBerger(0);
            for (GiocatoreSwiss o : giocatori){
                if (!o.equals(g) && g.getOpponenti().contains(o)){
                    g.setBuchholz(g.getBuchholz() + o.getPunteggio());
                    for(RoundSwiss r : rounds){
                        for(PartitaSwiss p : r.getPartite()){
                            if((g.equals(p.getBianco()) && o.equals(p.getNero())) || (o.equals(p.getBianco()) && g.equals(p.getNero()))){ //controllo di presenza
                                int colore = (g.equals(p.getBianco())) ? 0 : 1;
                                g.setBerger(g.getBerger() + (Math.abs(colore-p.getRisultato()) * o.getPunteggio()));
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
            //Buchholz - somma dei punteggi dei opponenti
            c = Double.compare(b.getBuchholz(), a.getBuchholz());
            if (c != 0) return c;
            //Sonneborn-Berger - somma pesata dei punteggi dei opponenti in base a risultati head-to-head
            c = Double.compare(b.getBerger(), a.getBerger());
            if (c != 0) return c;
            // numero di abbinamento (ranking iniziale per Elo)
            c = Integer.compare(a.getNumeroAbbinamento(), b.getNumeroAbbinamento());
            return c;
        });
    }

    public boolean decidereColore(GiocatoreSwiss A, GiocatoreSwiss B) { //true = A bianco, false = A nero
        int bilA = A.getBilancioColori();
        int bilB = B.getBilancioColori();
        int serA = A.getSerieColori();
        int serB = B.getSerieColori();
        int ultA = A.getLastColore();
        int ultB = B.getLastColore();
        if (bilA >= 2 && bilB <= -2)
            return false;
        if (bilA <= -2 && bilB >= 2)
            return true;
        if (bilA >= 2)
            return false;
        if (bilA <= -2)
            return true;
        if (bilB >= 2)
            return true;
        if (bilB <= -2)
            return false;
        if (serA >= 2 && ultA == 0)
            return false;
        if (serA >= 2 && ultA == 1)
            return true;
        if (serB >= 2 && ultB == 0)
            return true;
        if (serB >= 2 && ultB == 1)
            return false;
        if (bilA > bilB)
            return false;
        if (bilA < bilB)
            return true;
        GiocatoreSwiss piuAlto = A.getNumeroAbbinamento() < B.getNumeroAbbinamento() ? A : B;
        boolean piuAltoBianco = (piuAlto.getNumeroAbbinamento() % 2 == 1) == (coloreIniziale == 0);
        return A.equals(piuAlto) ? piuAltoBianco : !piuAltoBianco;
    }

    public void stampareClassifica(){
        int gx = 1;
        if(this.getRounds().size() == this.getRoundsTot()){
            System.out.println("\nPOS | Nome(#) | PTS | W | D | TB1 | TB2 | TPR | Bye");
        } else {
            System.out.println("\nPOS | Nome(#) | PTS | W | D | TB1 | TB2 | Bye");
        }
        for (GiocatoreSwiss g : giocatori){
            if(this.getRounds().size() == this.getRoundsTot()){
                System.out.println(gx + " | " + g.getNome() +" (" + g.getNumeroAbbinamento() + ") | " + g.getPunteggio() + " | " + g.getVittorie() 
                + " | " + g.getPareggi() + " | " + g.getBuchholz() + " | " + g.getBerger() + " | " + (int)g.getTPR() + " | " + ((g.getBye())?1:0));
            } else {
                System.out.println(gx + " | " + g.getNome() + " | " + g.getPunteggio() + " | " + g.getVittorie() 
                + " | " + g.getPareggi() + " | " + g.getBuchholz() + " | " + g.getBerger() + " | " + ((g.getBye())?1:0));
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
