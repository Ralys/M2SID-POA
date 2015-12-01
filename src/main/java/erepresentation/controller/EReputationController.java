/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    //demande avis d'un fournisseur
    public String demandeAvisFourniseur(JSONObject demandeAvis, AID agent){
        String nomFournisseur = demandeAvis.get("nom").toString();
        
        // TODO recherche en base de données
        //recuperation de l'avie du fournisseur
        int avis = 3;
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO,  AID.AGENT_CLASSNAME+"demande avis fournisseur");
        //*********
        JSONObject retourAvis = new JSONObject();
        JSONObject retour = new JSONObject();
        retour.put("avis", avis);
        retour.put("type", "Fournisseur");
        retour.put("nom",nomFournisseur);
        retourAvis.put("retourAvis", retour);
        return retourAvis.toJSONString();
    }
    //demande avis d'un vendeur
    public String demandeAvisVendeur(JSONObject demandeAvis, AID agent){
        String nomFournisseur = demandeAvis.get("nom").toString();
        
        // TODO recherche en base de données
        //recuperation de l'avie du fournisseur
        int avis = 3;
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO,  AID.AGENT_CLASSNAME+"demande avis vendeur");
        //*********
        
        JSONObject retourAvis = new JSONObject();
        JSONObject retour = new JSONObject();
        retour.put("avis", avis);
        retour.put("type", "Vendeur");
        retour.put("nom",nomFournisseur);
        retourAvis.put("retourAvis", retour);
        return retourAvis.toJSONString();
    }
    //demande avis sur un produit
    public String demandeAvisProduit(JSONObject demandeAvis, AID agent){
        String idProduit = demandeAvis.get("id").toString();
        
        // TODO recherche en base de données
        //recuperation de l'avie du fournisseur
        //getavis(nomVendeur)
        int reputation = 3;
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO,  AID.AGENT_CLASSNAME+"demande avis produit");
        //*********
        
        JSONObject retourAvis = new JSONObject();
        JSONObject retour = new JSONObject();
        retour.put("reputation", reputation);
        retour.put("type", "Produit");
        retour.put("id",idProduit);
        retourAvis.put("retourAvis", retour);
        return retourAvis.toJSONString();
    }
    //donne avis sur un vendeur
    public String donneAvisVendeur(JSONObject donneAvis, AID agent){
        String nomVendeur = donneAvis.get("nom").toString();
        
        // TODO insertion en base de données
        //recuperation de l'avie du fournisseur
        //Insert avis fournisseur 
        String type = donneAvis.get("type").toString();
        String id = donneAvis.get("id").toString();
        //insert(type, name, avis);
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO,  AID.AGENT_CLASSNAME+"donne avis vendeur");
        //*********
        
        // retourne JSON contenant SQL
        return null;
    }
    //donne avis sur un fournisseur
    public String donneAvisFournisseur(JSONObject donneAvis, AID agent){
        String nomFournisseur = donneAvis.get("nom").toString();
        
        // TODO insertion en base de données
        //recuperation de l'avie du fournisseur
        //Insert avis fournisseur 
        String type = donneAvis.get("type").toString();
        String id = donneAvis.get("avis").toString();
        //insert(type, name, avis);
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO, AID.AGENT_CLASSNAME+"donne avis fournisseur");
        //*********
        
        // retourne JSON contenant SQL
        return null;
    }
    //donne avis sur un produit
    public String donneAvisProduit(JSONObject donneAvis, AID agent){
        String idProduit = donneAvis.get("id").toString();
        
        // TODO insertion en base de données
        //recuperation de l'avie du fournisseur
        //Insert avis fournisseur 
        String type = donneAvis.get("type").toString();
        String id = donneAvis.get("avis").toString();
        //insert(type, name, avis);
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO,  AID.AGENT_CLASSNAME+"donne avis produit");
        //*********
        
        // retourne JSON contenant SQL
        return null;
    }
}
