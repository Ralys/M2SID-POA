/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fournisseur.behaviors;

/**
 *
 * @author tom
 */
public class WaitNegociationStrategie1 extends WaitNegociation{

    @Override
    public double définirNouveauPrix(int idProduit, int delai, String sender) {
        return 0;
    }
    
}
