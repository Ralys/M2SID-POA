package fournisseur.utils;

/**
 *
 * @author Tom
 */
public class Produit implements Comparable {

    private int idProduit;
    private double prixDeBase;
    private String nomProduit;
    private String typeProduit;
    private long dateSortie;
    private double desirabilite;

    public Produit(int id, String nom, double prix, String type, long date) {
        this.idProduit = id;
        this.nomProduit = nom;
        this.typeProduit = type;
        this.prixDeBase = prix;
        this.dateSortie = date;
        desirabilite = 0;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public double getPrixDeBase() {
        return prixDeBase;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public String getTypeProduit() {
        return typeProduit;
    }

    public long getDateSortie() {
        return dateSortie;
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

    public double getDesirabilite() {
        return desirabilite;
    }

    public void setDesirabilite(double desirabilite) {
        this.desirabilite = desirabilite;
    }

    @Override
    public String toString() {
        return "Produit{" + "idProduit=" + idProduit + ", prixDeBase=" + prixDeBase + ", nomProduit=" + nomProduit + ", typeProduit=" + typeProduit + ", dateSortie=" + dateSortie + ", desirabilite=" + desirabilite + '}';
    }

    @Override
    public int compareTo(Object o) {
        Produit p = (Produit) o;
        if (this.desirabilite < p.getDesirabilite()) {
            return -1;
        } else if (this.desirabilite == p.getDesirabilite()) {
            return 0;
        }
        return 1;
    }

}
