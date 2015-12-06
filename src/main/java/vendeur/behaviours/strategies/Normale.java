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

    public static double getPrix(double prix, int dureeLiv, double desir){
        double var = 0;
        double price;
        if(desir > 5) {
            var = desir - 5;
            price = prix+(prix*var*5/100);
        }
        else {
            var = 5 - desir;
            price = prix-(prix*var*5/100);
        }
        return price+tableauPrixLiv.get(dureeLiv);
    }

    public static double getNegoce(double prix, int nbNegoce, double desir) {
        double var = 0;
        double price;
        if(desir > 5) {
            var = desir - 5;
            price = prix+(prix*var*5/100);
        }
        else {
            var = 5 - desir;
            price = prix-(prix*var*5/100);
        }
        return price - (nbNegoce * 10 * price / 100);
    }

}
