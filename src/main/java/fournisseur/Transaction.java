package fournisseur;

/**
 *
 * @author Tom
 */
public class Transaction {

    private int idProduit;
    private int delai;
    private String client;
    private int nbNego;
    private int qte;

    public Transaction(int idProduit, int delai, String client,int qte) {
        this.idProduit = idProduit;
        this.delai = delai;
        this.client = client;
        this.nbNego = 0;
        this.qte =qte;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public int getDelai() {
        return delai;
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
    
    public void incNbNego() {
        this.nbNego++;
    }
}
