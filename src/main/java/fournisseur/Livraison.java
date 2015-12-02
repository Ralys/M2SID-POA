package fournisseur;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author tom
 */
public class Livraison {
    
    private static HashMap<Integer,Double> prixLivraison;
    
    static{
        prixLivraison = new HashMap<>();
        prixLivraison.put(10, 0.0);
        prixLivraison.put(11, 10.0);
        prixLivraison.put(3, 5.0);
    }

    public static HashMap<Integer, Date> getListeDateLivraison() {
        Date dateJour = new Date();
        HashMap<Integer, Date> listDate = new HashMap<>();

        //Livraison express : 1 jour
        listDate.put(1, Livraison.addDays(dateJour, 1));

        //Livraison normale : 3 jours
        listDate.put(3, Livraison.addDays(dateJour, 3));

        //Livraison la poste : 10 jours
        listDate.put(10, Livraison.addDays(dateJour, 10));

        return listDate;
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
    
    public static double prixLivraisonByDelai(int delai){
        return prixLivraison.get(delai);
    }
    
    
    //TODO recalcul du délai :/
}
