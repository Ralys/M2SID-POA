package ereputation.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Classe s'occupant de calculer
 * la désirabilité d'un produit
 * 
 * @author Team E-réputation
 */
public class DesirabiliteCalculator {
    
    private static final long MAX_VALEUR_DESIRABILITE = 10;
    private static final long MAX_JOURS_DESIRABILITE  = 59;
    
    /**
     * Méthode exécutant le calcul de désirabilité
     * @param dateSortie la date de sortie du produit
     * @return la désirabilité du produit
     */
    public static double execute(String dateSortie) {
        long jours = daysFromNow(dateSortie);
        
        // limitation au nombre de jours max
        jours = (jours > MAX_JOURS_DESIRABILITE) ? MAX_JOURS_DESIRABILITE : jours;
        
        return round(Math.abs(Math.sin((double)jours/20) * MAX_VALEUR_DESIRABILITE), 2);
    }
    
    /**
     * Permet de calculer la différence de jour entre une date et aujourd'hui
     * @param dateText une date
     * @return différence de jours
     */
    private static long daysFromNow(String dateText) {
        long days, timestamp, difference;
        
        timestamp = Long.parseLong(dateText);
        Date date = new Date(timestamp);
        Date now = new Date();
        
        difference = now.getTime() - date.getTime();
        days = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
        
        return days;
    }
    
    /**
     * Permet d'arrondir un nombre x chiffres après la virgule
     * @param value le nombre
     * @param places le nombre de chiffres après la virgule
     * @return un nombre arrondi avec x chiffres après la virgule
     */
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
