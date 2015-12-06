/*
 * Auteur : Aymeric ZANIRATO
 * Email: aymeric@zanirato.fr
 */
package client.outils;

import org.json.simple.JSONObject;

/**
 * Classe permettant de créer des produits
 * @author Aymeric
 */
public class Produit {

    /**
     * La provenance correspond au nom complet de l'agent nous ayant envoyé le produit
     */
    private String provenance;
    
    /**
     * ID du produit défini par une chaine de caractères
     */
    private String id;
    
    /**
     * Le nom du produit
     */
    private String nom;
    
    /**
     * Le prix du produit
     */
    private double prix;
    
    /**
     * La quantité souhaité du produit
     */
    private int quantite;
    
    /**
     * La date de livraison du produit
     */
    private long dateLivraison;
    
    /**
     * La note donné au produit (correspond à son avis)
     */
    private double avis;


    /**
     * Permet de créer un produit en spécifiant c'est caractéristiques une par une
     * @param provenance
     * @param id
     * @param nom
     * @param quantite
     * @param prix
     * @param dateLivraison 
     */
    public Produit(String provenance, String id, String nom, int quantite, double prix, long dateLivraison) {
        this.provenance = provenance;
        this.id = id;
        this.nom = nom;
        this.quantite = quantite;
        this.prix = prix;
        this.dateLivraison = dateLivraison;
        this.avis = 0;
    }
    
    /**
     * Permet de créer un produit à partir d'un message JSON
     * @param jsonObject Le message JSON contenant les informations sur le produit
     * @param provenance La provenance du produit (agent l'ayant envoyé)
     */
    public Produit(JSONObject jsonObject, String provenance){
            this.provenance = provenance;
            this.id = jsonObject.get("idProduit").toString();
            this.nom = jsonObject.get("nomProduit").toString();
            this.quantite = Integer.parseInt(jsonObject.get("quantite").toString());
            this.prix = Double.parseDouble(jsonObject.get("prix").toString());
            this.dateLivraison = Long.parseLong(jsonObject.get("date").toString());
    }

    /**
     * Permet de retourner le produit this sous la forme d'un JSONObject
     * @return Le produit sous la forme d'un JSONObject
     */
    public JSONObject getJSONObject(){
        JSONObject obj = new JSONObject();
        obj.put("idProduit", this.getId());
        obj.put("nomProduit", this.getNom());
        obj.put("quantite", this.getQuantite());
        obj.put("prix", this.getPrix());
        obj.put("date", this.getDateLivraison());
        
        return obj;
    }
    
    /**
     * Méthode equals permettant de comparer si deux produits sont identiques
     * @param p Le produit à comparer
     * @return true si les produits sont identiques, sinon false 
     */
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
    
    // GETTER AND SETTER
    
    /**
     * Getter de la provenance (agent ayant envoyé le produit)
     * @return Le nom complet de l'agent ayant envoyé le produit
     */
    public String getProvenance() {
        return provenance;
    }

    /**
     * Setter de la provenance 
     * @param provenance Nouveau nom complet de l'agent de provenance pour ce produit 
     */
    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    /**
     * Getter de l'ID du produit
     * @return L'ID du produit
     */
    public String getId() {
        return id;
    }

    /**
     * Setter de l'ID du produit
     * @param id Le nouvel ID du produit
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Getter du nom du produit
     * @return Le nom du produit
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Setter du nom du produit
     * @param nom Le nouveau nom du produit
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Getter du prix du produit
     * @return Le prix du produit
     */
    public double getPrix() {
        return prix;
    }

    /**
     * Setter du prix du produit
     * @param prix Le nouveau prix du produit
     */
    public void setPrix(double prix) {
        this.prix = prix;
    }

    /**
     * Getter de la date de livraison du produit
     * @return La date de livraison du produit (en timestand)
     */
    public long getDateLivraison() {
        return this.dateLivraison;
    }

    /**
     * Setter de la date de livraison du produit
     * @param dateLivraison La nouvelle date de livraison du produit en timestand
     */
    public void setDateLivraison(int dateLivraison) {
        this.dateLivraison = dateLivraison;
    }

    /**
     * Getter de la quantité du produit
     * @return La quantité souhaité du produit
     */
    public int getQuantite() {
        return quantite;
    }

    /**
     * Setter de la quantité du produit
     * @param quantite La nouvelle quantité souhaité du produit
     */
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    /**
     * Getter de l'avis du produit (note sur 5)
     * @return L'avis du produit
     */
    public double getAvis() {
        return avis;
    }
    
    /**
     * Setter de l'avis du produit
     * @param avis La nouvelle note de l'avis du produi (note sur 5)
     */
    public void setAvis(double avis) {
        this.avis = avis;
    }
}
