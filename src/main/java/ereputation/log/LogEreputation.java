/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author greg
 */
public class LogEreputation {
    private static LogEreputation uniqueInstance = new LogEreputation();
    private static FileHandler fileHandler = null;
    
    //pemret de crer une instance de la class LogEreputation
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
    
    //on enregistre une info
    public void Info(String messgae){
        Logger.getLogger("logErep").info(messgae);
    }
    //on enregistre une erreur
    public void Erreur(String messgae){
        Logger.getLogger("logErep").warning(messgae);
    }
    
    //class permettant de definir un format
    static class MonFormateur extends Formatter {

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
