/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package erepresentation.controler;

import erepresentation.EReputationAgent;
import jade.core.AID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author greg
 */
public class RequeteAgent {
    //demande avis d'un fournisseur
    public static String demandeAvisFourniseur(JSONObject demandeAvis, AID agent){
        String nomFournisseur = demandeAvis.get("nom").toString();
        
        //recuperation de l'avie du fournisseur
        int avis = 3;
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO,  AID.AGENT_CLASSNAME+"demande avis fournisseur");
        //*********
        JSONObject retour = new JSONObject();
        retour.put("avis", avis);
        retour.put("type", "fournisseur");
        retour.put("nom",nomFournisseur);
        return retour.toJSONString();
    }
    //demande avis d'un vendeur
    public static String demandeAvisVendeur(JSONObject demandeAvis, AID agent){
        String nomFournisseur = demandeAvis.get("nom").toString();
        
        //recuperation de l'avie du fournisseur
        int avis = 3;
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO,  AID.AGENT_CLASSNAME+"demande avis vendeur");
        //*********
        
        JSONObject retour = new JSONObject();
        retour.put("avis", avis);
        retour.put("type", "vendeur");
        retour.put("nom",nomFournisseur);
        return retour.toJSONString();
    }
    //demande avis sur un produit
    public static String demandeAvisProduit(JSONObject demandeAvis, AID agent){
        String idProduit = demandeAvis.get("id").toString();
        
        //recuperation de l'avie du fournisseur
        //getavis(nomVendeur)
        int reputation = 3;
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO,  AID.AGENT_CLASSNAME+"demande avis produit");
        //*********
        
        JSONObject retour = new JSONObject();
        retour.put("reputation", reputation);
        retour.put("type", "vendeur");
        retour.put("id",idProduit);
        return retour.toJSONString();
    }
    //donne avis sur un vendeur
    public static void donneAvisVendeur(JSONObject donneAvis, AID agent){
        String nomVendeur = donneAvis.get("nom").toString();
        //recuperation de l'avie du fournisseur
        //Insert avis fournisseur 
        String type = donneAvis.get("type").toString();
        String id = donneAvis.get("id").toString();
        //insert(type, name, avis);
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO,  AID.AGENT_CLASSNAME+"donne avis vendeur");
        //*********
    }
    //donne avis sur un fournisseur
    public static void donneAvisFournisseur(JSONObject donneAvis, AID agent){
        String nomFournisseur = donneAvis.get("nom").toString();
        
        //recuperation de l'avie du fournisseur
        //Insert avis fournisseur 
        String type = donneAvis.get("type").toString();
        String id = donneAvis.get("avis").toString();
        //insert(type, name, avis);
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO, AID.AGENT_CLASSNAME+"donne avis fournisseur");
        //*********
    }
    //donne avis sur un produit
    public static void donneAvisProduit(JSONObject donneAvis, AID agent){
        String idProduit = donneAvis.get("id").toString();
        
        //recuperation de l'avie du fournisseur
        //Insert avis fournisseur 
        String type = donneAvis.get("type").toString();
        String id = donneAvis.get("avis").toString();
        //insert(type, name, avis);
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO,  AID.AGENT_CLASSNAME+"donne avis produit");
        //*********
    }
}
