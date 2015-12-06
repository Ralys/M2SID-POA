/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vendeur.tools;

import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author jonathan
 */
public class Dates {
        public static Long dateJour = new Date().getTime() / 1000;
    public static HashMap<Integer, Long> getListeDateLivraison() {
        HashMap<Integer, Long> listDate = new HashMap<>();
        //livraison rapide : 1 
        listDate.put(1, new Long(1449254195));

        //Livraison normale : 3 jours
        listDate.put(3, new Long(1449254185));

        //Livraison la poste : 10 jours
        listDate.put(10, new Long(1449254105));
        return listDate;
    }

    public static Long addDays(Long date, int days) {
        return date + 86400 * days;//1 jour = 86400 secondes
    }
    
    public static Long addDays(int days){
        return dateJour + 86400 * days;
    }
}
