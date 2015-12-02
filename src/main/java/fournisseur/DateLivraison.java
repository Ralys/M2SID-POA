package fournisseur;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author tom
 */
public class DateLivraison {

    public static HashMap<Integer, Date> getListeDateLivraison() {
        Date dateJour = new Date();
        HashMap<Integer, Date> listDate = new HashMap<>();

        //Livraison express : 1 jour
        listDate.put(1, DateLivraison.addDays(dateJour, 1));

        //Livraison normale : 3 jours
        listDate.put(3, DateLivraison.addDays(dateJour, 3));

        //Livraison la poste : 10 jours
        listDate.put(10, DateLivraison.addDays(dateJour, 10));

        return listDate;
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
    
    
    //TODO recalcul du délai :/
}
