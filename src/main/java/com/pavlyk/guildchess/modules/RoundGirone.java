package com.pavlyk.guildchess.modules;

import java.util.ArrayList;

public class RoundGirone {
    private ArrayList<PartitaGirone> partite = new ArrayList<>();
    public RoundGirone(){
        // ciao
    }

    public ArrayList<PartitaGirone> getPartite(){
        return partite;
    }
    public void setPartite(ArrayList<PartitaGirone> partite){
        this.partite = partite;
    }
}
