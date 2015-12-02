package ereputation.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Team EReputation
 */
public class ReputationCalculator {
    
    private static final long MAX_VALEUR_REPUTATION = 10;
    private static final long MAX_JOURS_REPUTATION  = 63;
    
    public static double execute(String dateSortie) {
        long jours = daysFromNow(dateSortie);
        
        if(jours > MAX_JOURS_REPUTATION) {
            return MAX_VALEUR_REPUTATION;
        } else {
            return round(Math.abs(Math.sin((double)jours/20) * MAX_VALEUR_REPUTATION), 2);   
        }
    }
    
    private static long daysFromNow(String dateText) {
        long days = -1;
        
        try {
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-mm-dd");
            
            Date date = myFormat.parse(dateText);
            Date now = new Date();
            
            long difference = now.getTime() - date.getTime();
            days = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
        } catch (ParseException ex) {
            Logger.getLogger(ReputationCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return days;
    }
    
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
