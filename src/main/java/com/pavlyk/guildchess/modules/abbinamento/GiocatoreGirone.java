package com.pavlyk.guildchess.modules.abbinamento;

import java.util.ArrayList;

public class GiocatoreGirone {
    private String nome;
    private int Elo;
    private int numeroAbbinamento;

    private ArrayList<PartitaGirone> partite = new ArrayList<>();

    private double punteggio = 0;
    private int vittorie= 0;
    private int pareggi = 0;
    private double Berger = 0; //Sonneborn-Berger
    private double Koya = 0; //Koya System
    private double TPR = 0; //Tournament Performance Rating

    public GiocatoreGirone(String nome, int Elo){
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
    public int aggiornareElo(GiocatoreGirone Opp, double puntoMio){
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
    public int getPareggi() {
        return pareggi;
    }
    public void setPareggi(int pareggi) {
        this.pareggi = pareggi;
    }
    public double getBerger() {
        return Berger;
    }
    public void setBerger(double berger) {
        this.Berger = berger;
    }
    public double getKoya(){
        return Koya;
    }
    public void setKoya(double koya){
        this.Koya = koya;
    }
    public ArrayList<GiocatoreGirone> getOpponenti() {
        ArrayList<GiocatoreGirone> opponenti = new ArrayList<>();
        for(PartitaGirone p : partite){
            if(p.getBianco().equals(this)){
                opponenti.add(p.getNero());
            } else if (p.getNero().equals(this)){
                opponenti.add(p.getBianco());
            }
        }
        return opponenti;
    }
    public ArrayList<PartitaGirone> getPartite(){
        return partite;
    }
    public void aggiungiPartita(PartitaGirone partita){
        boolean sonoBianco = this.equals(partita.getBianco());
        int colore = (sonoBianco)?0:1;
        GiocatoreGirone opponente = (sonoBianco)?partita.getNero():partita.getBianco();
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
}
