/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vendeur.behaviours.strategies;

import java.util.HashMap;

/**
 * @author jonathan
 */
public class Normale {

    private final static HashMap<Integer, Integer> tableauPrixLiv = new HashMap() {{
        put(1, 8);
        put(3, 4);
        put(10, 0);
    }};

    public static double getPrix(double prix, int dureeLiv){
        return prix+tableauPrixLiv.get(dureeLiv);
    }

}
