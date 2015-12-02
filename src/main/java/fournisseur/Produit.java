package fournisseur;



/**
 *
 * @author Tom
 */
public class Produit {
    private int idProduit;
    private double prixDeBase;
    private String nomProduit;
    private String typeProduit;

    public Produit( int id, String nom,  double prix, String type) {
        this.idProduit = id;
        this.nomProduit = nom;
        this.typeProduit = type;
        this.prixDeBase = prix;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public double getPrixdeBase() {
        return prixDeBase;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public String getTypeProduit() {
        return typeProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public void setPrixdeBase(double prixdeBase) {
        this.prixDeBase = prixdeBase;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public void setTypeProduit(String typeProduit) {
        this.typeProduit = typeProduit;
    }

    
}
