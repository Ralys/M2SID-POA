package ereputation.log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 *
 * @author Team E-réputation
 */
public class LogEreputation {
    private static LogEreputation uniqueInstance = new LogEreputation();
    private static FileHandler fileHandler;
    
    /**
     * Permet de créer une instance unique de la classe
     * @return singleton de LogEreputation
     */
    public static synchronized LogEreputation instance() {
        if (null == uniqueInstance) { // Premier appel
            uniqueInstance = new LogEreputation();
        }
        return uniqueInstance;
    }
    
    private LogEreputation(){
        try {
            //On crée un gestionnaire de fichier "simple"
            fileHandler = new FileHandler("logErep.txt");
            //On assigne une mise en forme simple
            fileHandler.setFormatter(new MonFormateur());
            //On assigne une mise en forme simple
            Logger.getLogger("logErep").addHandler(fileHandler);
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(LogEreputation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Enregistrement d'un information
     * @param message message lié à l'information 
     */
    public void Info(String message){
        Logger.getLogger("logErep").info(message);
    }
    
    /**
     * Enregistrement d'une erreur
     * @param message message lié à l'erreur
     */
    public void Erreur(String message){
        Logger.getLogger("logErep").warning(message);
    }
    
    /**
     * Classe dont le but est de gérer le format des logs
     */
    private static class MonFormateur extends Formatter {

        @Override
        public String format(LogRecord record) {
            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            if(record.getLevel().intValue() == 800){
                return formater.format(new Date(record.getMillis()))+":["+record.getLevel().getName()+"]:"+record.getMessage()+"\n";
            }else{
                return formater.format(new Date(record.getMillis()))+":[ERREUR]:"+record.getMessage()+"\n";
            }
        }

    }
}
