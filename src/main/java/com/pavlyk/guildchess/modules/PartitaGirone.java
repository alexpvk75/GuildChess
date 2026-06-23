package com.pavlyk.guildchess.modules;

public class PartitaGirone {
    private GiocatoreGirone bianco;
    private GiocatoreGirone nero;
    private double risultato; // 1 - vinto bianco, 0.5 - pareggio, 0 - vinto nero
    public PartitaGirone(GiocatoreGirone bianco, GiocatoreGirone nero){
        this.bianco = bianco;
        this.nero = nero;
    }
    public double getRisultato(){
        return risultato;
    }
    public void setRisultato(double nuovo){
        this.risultato = nuovo;
    }
    public GiocatoreGirone getBianco(){
        return bianco;
    }
    public void setBianco(GiocatoreGirone b){
        this.bianco = b;
    }
    public GiocatoreGirone getNero(){
        return nero;
    }
    public void setNero(GiocatoreGirone n){
        this.nero = n;
    }
}
