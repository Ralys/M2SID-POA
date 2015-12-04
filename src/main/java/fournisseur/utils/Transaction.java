package fournisseur.utils;

/**
 *
 * @author Tom
 */
public class Transaction {

    private int idProduit;
    private Long dateLivraison;
    private String client;
    private int nbNego;
    private int qte;
    private int delai;

    public Transaction(int idProduit, Long dateLivraison, String client, int qte, int delai) {
        this.idProduit = idProduit;
        this.dateLivraison = dateLivraison;
        this.client = client;
        this.nbNego = 0;
        this.qte = qte;
        this.delai = delai;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public Long getDateLivraison() {
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
}
