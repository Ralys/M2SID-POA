package erepresentation.controller;

import erepresentation.EReputationAgent;
import jade.core.AID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author greg
 */
public class EReputationController {
    
    /**
     * Demande avis sur un fournisseur
     * @param demandeAvis, la demande d'avis
     * @param agent, l'agent ayant demandé
     * @return contenu JSON du message
     */
    public String demandeAvisFourniseur(JSONObject demandeAvis, AID agent){
        String type = demandeAvis.get("type").toString(),
               nomFournisseur = demandeAvis.get("nom").toString();
        
        // TODO recherche en base de données
        //recuperation de l'avis du fournisseur
        
        int avis = 3;
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO,  AID.AGENT_CLASSNAME+"demande avis fournisseur");
        
        
        JSONObject retour = demandeAvis;
        retour.put("avis", avis);
        
        JSONObject retourAvis = new JSONObject();
        retourAvis.put("retourAvis", retour);
        
        return retourAvis.toJSONString();
    }
    
    /**
     * Demande avis sur un vendeur
     * @param demandeAvis, la demande d'avis
     * @param agent, l'agent ayant demandé
     * @return contenu JSON du message
     */
    public String demandeAvisVendeur(JSONObject demandeAvis, AID agent){
        String type = demandeAvis.get("type").toString(),
               nomVendeur = demandeAvis.get("nom").toString();
        
        // TODO recherche en base de données
        //recuperation de l'avie du fournisseur
        //getavis(nomVendeur)
        
        int avis = 3;
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO,  AID.AGENT_CLASSNAME+"demande avis vendeur");
        
        JSONObject retour = demandeAvis;
        retour.put("avis", avis);
        
        JSONObject retourAvis = new JSONObject();
        retourAvis.put("retourAvis", retour);
        
        return retourAvis.toJSONString();
    }
    
    /**
     * Demande avis sur un produit
     * @param demandeAvis, la demande d'avis
     * @param agent, l'agent ayant demandé
     * @return contenu JSON du message
     */
    public String demandeAvisProduit(JSONObject demandeAvis, AID agent){
        String  type = demandeAvis.get("type").toString(),
                refProduit = demandeAvis.get("ref").toString();
        
        // TODO recherche en base de données
        //recuperation de l'avis du produit
        
        int avis = 3;
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO,  AID.AGENT_CLASSNAME+"demande avis produit");
        
        JSONObject retour = demandeAvis;
        retour.put("avis", avis);
        
        JSONObject retourAvis = new JSONObject();
        retourAvis.put("retourAvis", retour);
        
        return retourAvis.toJSONString();
    }
    
    /**
     * Demande réputation sun produit
     * @param demandeReputation, la demande de réputation
     * @param agent, l'agent ayant demandé
     * @return contenut JSON du message
     */
    public String demandeReputationProduit(JSONObject demandeReputation, AID agent) {
        String type = demandeReputation.get("type").toString(),
               refProduit = demandeReputation.get("ref").toString();
        
        // TODO chercher en BDD la date de sortie du produit
        
        int reputation = 6;
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO,  AID.AGENT_CLASSNAME+"demande reputation produit");
        
        JSONObject retour = demandeReputation;
        retour.put("reputation", reputation);
        
        JSONObject retourReputation = new JSONObject();
        retourReputation.put("retourReputation", retour);
        
        return retourReputation.toJSONString();
    }
    
    /**
     * Donner avis sur un vendeur
     * @param donneAvis
     * @param agent
     * @return 
     */
    public String donneAvisVendeur(JSONObject donneAvis, AID agent){
        String  type = donneAvis.get("type").toString(),
                nomVendeur = donneAvis.get("nom").toString();
        
        Long avis = (Long) donneAvis.get("avis");
        
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO,  AID.AGENT_CLASSNAME+"donne avis vendeur");
        
        return this.insertSQL(agent.getName(), type+"_"+nomVendeur, avis);
    }
    
    /**
     * Donner avis sur un Fournisseur
     * @param donneAvis
     * @param agent
     * @return 
     */
    public String donneAvisFournisseur(JSONObject donneAvis, AID agent){
        String type = donneAvis.get("type").toString(),
               nomFournisseur = donneAvis.get("nom").toString();
        
        Long avis = (Long) donneAvis.get("avis");
        
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO, AID.AGENT_CLASSNAME+"donne avis fournisseur");
        
        return this.insertSQL(agent.getName(), type+"_"+nomFournisseur, avis);
    }
   
    /**
     * Donner avis sur un Produit
     * @param donneAvis
     * @param agent
     * @return 
     */
    public String donneAvisProduit(JSONObject donneAvis, AID agent){        
        String type = donneAvis.get("type").toString(),
               refProduit = donneAvis.get("ref").toString();
        
        Long avis = (Long) donneAvis.get("avis");
        
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO,  AID.AGENT_CLASSNAME+"donne avis produit");
        
        return this.insertSQL(agent.getName(), type+"_"+refProduit, avis);
    }
    
    private String insertSQL(String nomEmetteur, String nomDestinataire, Long avis) {
        String sql = "INSERT INTO AVIS(NOM_EMETTEUR, NOM_DESTINATAIRE, AVIS) VALUES(" + nomEmetteur + "," + nomDestinataire + "," +  avis + ")";
        
        JSONObject request = new JSONObject();
        request.put("type", "insert");
        request.put("sql", sql);
        
        // retourne JSON contenant SQL
        return request.toJSONString();
    }
}
