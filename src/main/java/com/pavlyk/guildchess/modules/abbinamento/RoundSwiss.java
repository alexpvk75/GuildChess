package com.pavlyk.guildchess.modules.abbinamento;

import java.util.ArrayList;

public class RoundSwiss {
    private ArrayList<PartitaSwiss> partite = new ArrayList<>();
    public RoundSwiss(){
        // ciao
    }

    public ArrayList<PartitaSwiss> getPartite(){
        return partite;
    }
    public void setPartite(ArrayList<PartitaSwiss> partite){
        this.partite = partite;
    }
}
