/*
 * Auteur : Aymeric ZANIRATO
 * Email: aymeric@zanirato.fr
 */
package client;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author Aymeric
 */
public class Produit {

    private String provenance;
    private String id;
    private String nom;
    private double prix;
    private int quantite;
    private int dateLivraison;


    public Produit(String provenance, String id, String nom, int quantite, double prix, int dateLivraison) {
        this.provenance = provenance;
        this.id = id;
        this.nom = nom;
        this.quantite = quantite;
        this.prix = prix;
        this.dateLivraison = dateLivraison;
    }
    
    public Produit(JSONObject jsonObject, String provenance){
            this.provenance = provenance;
            this.id = jsonObject.get("idProduit").toString();
            this.nom = jsonObject.get("nomProduit").toString();
            this.quantite = Integer.parseInt(jsonObject.get("quantite").toString());
            this.prix = Double.parseDouble(jsonObject.get("prix").toString());
            this.dateLivraison = Integer.parseInt(jsonObject.get("date").toString());
    }

    public JSONObject getJSONObject(){
        JSONObject obj = new JSONObject();
        obj.put("idProduit", this.getId());
        obj.put("nomProduit", this.getNom());
        obj.put("quantite", this.getQuantite());
        obj.put("prix", this.getPrix());
        obj.put("date", this.getDateLivraison());
        
        return obj;
    }
    
    public boolean equals(Produit p){
        boolean res = false;
        
        if(this.id.equalsIgnoreCase(p.getId())
           && this.provenance.equalsIgnoreCase(p.getProvenance())
           && this.nom.equalsIgnoreCase(p.getNom())
           && this.prix == p.getPrix()
           && this.dateLivraison == p.getDateLivraison()
           && this.quantite == p.getQuantite()){
           res = true;
        }
        
        
        return res;
    }
    
    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getDateLivraison() {
        return this.dateLivraison;
    }

    public void setDateLivraison(int dateLivraison) {
        this.dateLivraison = dateLivraison;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

}
