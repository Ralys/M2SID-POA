package ereputation.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Team EReputation
 */
public class DesirabiliteCalculator {
    
    private static final long MAX_VALEUR_DESIRABILITE = 10;
    private static final long MAX_JOURS_DESIRABILITE  = 63;
    
    public static double execute(String dateSortie) {
        long jours = daysFromNow(dateSortie);
        
        if(jours > MAX_JOURS_DESIRABILITE) {
            return MAX_VALEUR_DESIRABILITE;
        } else {
            return round(Math.abs(Math.sin((double)jours/20) * MAX_VALEUR_DESIRABILITE), 2);   
        }
    }
    
    private static long daysFromNow(String dateText) {
        long days, timestamp, difference;
        
        timestamp = Long.parseLong(dateText);
        Date date = new Date(timestamp);
        Date now = new Date();
        
        difference = now.getTime() - date.getTime();
        days = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
        
        return days;
    }
    
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
