package fournisseur.behaviors.strategie;

import fournisseur.behaviors.WaitNegociation;
import fournisseur.utils.Livraison;
import fournisseur.utils.Produit;
import fournisseur.utils.StocksEtTransaction;
import fournisseur.utils.Transaction;

/**
 *
 * @author tom
 */
public class WaitNegociationStrategie1 extends WaitNegociation {

    @Override
    public double définirNouveauPrix(int idProduit, Long date, String sender, double prixDemande) {
        Transaction t = ((StocksEtTransaction) getDataStore()).getTransaction(idProduit, date, sender);
        t.incNbNego();

        Produit p = ((StocksEtTransaction) getDataStore()).getProduitById(idProduit);
        double prixBase = p.getPrixDeBase();
        double prixDelai = Livraison.prixLivraisonByDelai(t.getDelai());
        double prixNego = Math.max(prixBase, prixDemande - prixDelai);
        
        double prixVente = prixBase + (t.getPrixPropose() - prixNego);
        prixVente = Math.round(prixVente * 100) / 100;
        t.setPrixPropose(prixVente);
        return prixVente + prixDelai;
    }

}
