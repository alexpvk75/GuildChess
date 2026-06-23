package com.pavlyk.guildchess.modules;

import java.util.ArrayList;

public class GiocatoreSwiss {
    private String nome;
    private int Elo;
    private int numeroAbbinamento;

    private ArrayList<PartitaSwiss> partite = new ArrayList<>();
    private int bilancioColori = 0;
    private int serieColori;
    private int lastColore;
    private boolean Bye = false;

    private double punteggio = 0;
    private int vittorie= 0;
    private double Buchholz = 0;
    private double Berger = 0; //Sonneborn-Berger
    private double TPR = 0; //Tournament Performance Rating

    public GiocatoreSwiss(String nome, int Elo){
        this.nome = nome;
        this.Elo = Elo;
    }
    public String getNome(){
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public int getElo() {
        return Elo;
    }
    public void setElo(int elo) {
        this.Elo = elo;
    }
    public int getNumeroAbbinamento() {
        return numeroAbbinamento;
    }
    public void setNumeroAbbinamento(int numeroAbbinamento) {
        this.numeroAbbinamento = numeroAbbinamento;
    }
    public int aggiornareElo(GiocatoreSwiss Opp, double puntoMio){
        int R_A = this.Elo;
        int R_B = Opp.getElo();
        double E_A = 1/(1+Math.pow(10, (R_B - R_A)/400));
        int K;
        //assegnazione di K secondo USCF, NON secondo FIDE
        if(R_A >= 2100 && R_A <= 2400){
            K = 24;
        } else {
            K = (R_A > 2400)?16:32;
        }
        int R_nuovo = (int)Math.round(R_A+K*(puntoMio - E_A));
        return (R_nuovo>=100)?R_nuovo:100;
    }
    public boolean getBye(){
        return Bye;
    }
    public void dareBye(){
        this.Bye = true;
        this.punteggio+=1;
    }
    public double getPunteggio() {
        return punteggio;
    }
    public void setPunteggio(double punteggio) {
        this.punteggio = punteggio;
    }
    public int getVittorie() {
        return vittorie;
    }
    public void setVittorie(int vittorie) {
        this.vittorie = vittorie;
    }
    public int getBilancioColori() {
        return bilancioColori;
    }
    public void setBilancioColori(int bilancioColori) {
        this.bilancioColori = bilancioColori;
    }
    public double getBuchholz() {
        return Buchholz;
    }
    public void setBuchholz(double buchholz) {
        this.Buchholz = buchholz;
    }
    public double getBerger() {
        return Berger;
    }
    public void setBerger(double berger) {
        this.Berger = berger;
    }
    public ArrayList<GiocatoreSwiss> getOpponenti() {
        ArrayList<GiocatoreSwiss> opponenti = new ArrayList<>();
        for(PartitaSwiss p : partite){
            if(p.getBianco().equals(this)){
                opponenti.add(p.getNero());
            } else if (p.getNero().equals(this)){
                opponenti.add(p.getBianco());
            }
        }
        return opponenti;
    }
    public ArrayList<PartitaSwiss> getPartite(){
        return partite;
    }
    public void aggiungiPartita(PartitaSwiss partita){
        boolean sonoBianco = this.equals(partita.getBianco());
        int colore = (sonoBianco)?0:1;
        this.bilancioColori += (sonoBianco)?1:-1;
        if (partite.isEmpty() || lastColore != colore) {
            serieColori = 1;
        } else {
            serieColori++;
        }
        lastColore = colore;
        GiocatoreSwiss opponente = (sonoBianco)?partita.getNero():partita.getBianco();
        double mioRisultato = Math.abs(colore - partita.getRisultato());
        this.punteggio += mioRisultato;
        this.aggiornareElo(opponente, mioRisultato);
        this.partite.add(partita);
    }
    public double getTPR() {
        return TPR;
    }
    public void setTPR(double TPR) {
        this.TPR = TPR;
    }

    public int getSerieColori() {
        return serieColori;
    }

    public void setSerieColori(int serieColori) {
        this.serieColori = serieColori;
    }

    public int getLastColore() {
        return lastColore;
    }

    public void setLastColore(int lastColore) {
        this.lastColore = lastColore;
    }
}
