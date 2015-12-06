/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vendeur.behaviours.strategies;

import java.util.HashMap;

/**
 *
 * @author jonathan
 */
public class Soldes {

    private final static HashMap<Integer, Integer> tableauPrixLiv = new HashMap() {{
        put(1, 4);
        put(3, 2);
        put(10, 0);
    }};

    public static double getPrix(double prixU, double prixL, int dureeLiv, double desir){
        double var = 0;
        double price;
        if(desir > 5) {
            var = desir - 5;
            price = prixU+(prixU*var*5/100);
        }
        else {
            var = 5 - desir;
            price = prixU-(prixU*var*5/100);
        }
        return (price-prixL)/2+prixL+tableauPrixLiv.get(dureeLiv);
    }

}
