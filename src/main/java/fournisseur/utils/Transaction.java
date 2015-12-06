package fournisseur.utils;

/**
 *
 * @author Tom
 */
public class Transaction {

    private int idProduit;
    private long dateLivraison;
    private String client;
    private int nbNego;
    private int qte;
    private int delai;
    private double prixPropose;
    private boolean abouti;

    public Transaction(int idProduit, Long dateLivraison, String client, int qte, int delai, double prixPropose) {
        this.idProduit = idProduit;
        this.dateLivraison = dateLivraison;
        this.client = client;
        this.nbNego = 0;
        this.qte = qte;
        this.delai = delai;
        this.prixPropose = prixPropose;
        this.abouti= false;
    }

    public double getPrixPropose() {
        return prixPropose;
    }

    public void setPrixPropose(double prixPropose) {
        this.prixPropose = prixPropose;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public long getDateLivraison() {
        return dateLivraison;
    }

    public String getClient() {
        return client;
    }

    public int getNbNego() {
        return nbNego;
    }

    public int getQte() {
        return qte;
    }

    public int getDelai() {
        return delai;
    }

    public void incNbNego() {
        this.nbNego++;
    }

    public boolean isAbouti() {
        return abouti;
    }

    public void setAbouti(boolean abouti) {
        this.abouti = abouti;
    }
    
    
}
