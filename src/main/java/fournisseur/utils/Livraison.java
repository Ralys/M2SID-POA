package fournisseur.utils;

import java.util.Date;
import java.util.HashMap;

/**
 *
 *
 * @author tom
 */
public class Livraison {

    private static HashMap<Integer, Double> prixLivraison;

    static {
        prixLivraison = new HashMap<>();
        prixLivraison.put(10, 0.0);
        prixLivraison.put(1, 10.0);
        prixLivraison.put(3, 5.0);
    }

    public static HashMap<Integer, Long> getListeDateLivraison() {
        Long dateJour = new Date().getTime() / 1000;
        HashMap<Integer, Long> listDate = new HashMap<>();

        //Livraison express : 1 jour
        listDate.put(1, Livraison.addDays(dateJour, 1));

        //Livraison normale : 3 jours
        listDate.put(3, Livraison.addDays(dateJour, 3));

        //Livraison la poste : 10 jours
        listDate.put(10, Livraison.addDays(dateJour, 10));

        return listDate;
    }

    public static Long addDays(Long date, int days) {
        return date + 86400 * days;//1 jour = 86400 secondes
    }
    
    public static double prixLivraisonByDelai(int delai) {
        return prixLivraison.get(delai);
    }

}
