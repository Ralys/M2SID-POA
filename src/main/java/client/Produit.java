/*
 * Auteur : Aymeric ZANIRATO
 * Email: aymeric@zanirato.fr
 */
package client;

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
    private Date dateLivraison;

    private SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");

    public Produit(String provenance, String id, String nom, int quantite, double prix, Date dateLivraison) {
        this.provenance = provenance;
        this.id = id;
        this.nom = nom;
        this.quantite = quantite;
        this.prix = prix;
        this.dateLivraison = dateLivraison;
    }
    
    public Produit(JSONObject jsonObject, String provenance){
        try {
            this.provenance = provenance;
            this.id = jsonObject.get("idProduit").toString();
            this.nom = jsonObject.get("nomProduit").toString();
            this.quantite = Integer.parseInt(jsonObject.get("quantite").toString());
            this.prix = Double.parseDouble(jsonObject.get("prix").toString());
            // supression des backslashes ajoutés autmatiquement au passe en json
            String date = jsonObject.get("date").toString().replace("\\", "");
            this.dateLivraison = formater.parse(date);
        } catch (ParseException ex) {
            Logger.getLogger(Produit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JSONObject getJSONObject(){
        JSONObject obj = new JSONObject();
        obj.put("idProduit", this.getId());
        obj.put("nomProduit", this.getNom());
        obj.put("quantite", this.getQuantite());
        obj.put("prix", this.getPrix());
        obj.put("date", this.getDateLivraisonToString());
        
        return obj;
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

    public Date getDateLivraison() {
        return this.dateLivraison;
    }

    public String getDateLivraisonToString() {
        return formater.format(dateLivraison);
    }

    public void setDateLivraison(Date dateLivraison) {
        this.dateLivraison = dateLivraison;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

}
