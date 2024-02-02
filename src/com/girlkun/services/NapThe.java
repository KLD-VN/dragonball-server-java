package com.girlkun.services;

import com.girlkun.models.player.Player;


public class NapThe {

    private static NapThe I;
    
    public static NapThe gI(){
        if(NapThe.I == null){
            NapThe.I = new NapThe();
        }
        return NapThe.I;
    }
    
    public void napThe(Player pl, String maThe, String seri){
        System.out.println(maThe);
        System.out.println(seri);
    }
    
}





















