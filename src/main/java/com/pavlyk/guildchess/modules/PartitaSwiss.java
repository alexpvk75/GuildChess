package com.pavlyk.guildchess.modules;

public class PartitaSwiss {
    private GiocatoreSwiss bianco;
    private GiocatoreSwiss nero;
    private double risultato; // 1 - vinto bianco, 0.5 - pareggio, 0 - vinto nero
    public PartitaSwiss(GiocatoreSwiss bianco, GiocatoreSwiss nero){
        this.bianco = bianco;
        this.nero = nero;
    }
    public double getRisultato(){
        return risultato;
    }
    public void setRisultato(double nuovo){
        this.risultato = nuovo;
    }
    public GiocatoreSwiss getBianco(){
        return bianco;
    }
    public void setBianco(GiocatoreSwiss b){
        this.bianco = b;
    }
    public GiocatoreSwiss getNero(){
        return nero;
    }
    public void setNero(GiocatoreSwiss n){
        this.nero = n;
    }
}
