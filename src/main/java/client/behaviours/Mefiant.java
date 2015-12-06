/*
 * Auteur : Aymeric ZANIRATO
 * Email: aymeric@zanirato.fr
 */
package client.behaviours;

import client.ClientAgent;
import client.outils.Log;
import client.outils.Produit;
import common.TypeAgent;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Classe qui permet de définir le comportement méfiant d'un client.
 *
 * @author Aymeric
 */
public class Mefiant extends CyclicBehaviour {

    /**
     * Le client en question
     */
    private final ClientAgent mefiant;
    private final HashMap<String, Double> hmAvisProduit = new HashMap<String, Double>();
    private final HashMap<String, Double> hmAvisRevendeur = new HashMap<String, Double>();
    private static final double avisRevendeurMin = 3;

    /**
     * Constructeur permettant d'affecter le comportement méfiant à un client
     *
     * @param agent Agent qui possèdera le comportement méfiant
     */
    public Mefiant(Agent agent) {
        this.mefiant = (ClientAgent) agent;
    }

    /**
     * Action effectuée par le type comportement méfiant.
     */
    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            traiterMessage(msg);
            block();
        }
    }

    /**
     * Comportement effectué pour traiter un message en fonction de son type
     *
     * @param message Le message à traiter.
     */
    public void traiterMessage(ACLMessage message) {

        try {
            Logger.getLogger(Econome.class.getName()).log(Level.INFO, message.getContent());
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(message.getContent());

            if (object.containsKey("jePropose")) {
                JSONArray array = (JSONArray) object.get("jePropose");
                mefiant.ajouterProposition(array, message);
                mefiant.setNbReponseReçu(mefiant.getNbReponseReçu() + 1);

                if (mefiant.getNbReponseReçu() == mefiant.getNbRechercheEnvoye()) {
                    // récupation des différents id des produits
                    for (Produit produit : mefiant.getLproposition()) {
                        if (!hmAvisProduit.containsKey(produit.getId())) {
                            hmAvisProduit.put(produit.getId(), 0.0);
                            mefiant.demandeAvisProduit(produit.getId());
                        }
                        if (!hmAvisRevendeur.containsKey(mefiant.nomAgent(produit.getProvenance()))) {
                            hmAvisRevendeur.put(mefiant.nomAgent(produit.getProvenance()), 0.0);
                            mefiant.demandeAvis(mefiant.getTypeAgentCible(), mefiant.nomAgent(produit.getProvenance()));
                        }
                    }
                }
            }

            if (object.containsKey("quantiteInsuffisante")) {
                mefiant.setNbReponseReçu(mefiant.getNbReponseReçu() + 1);
                JSONArray array = (JSONArray) object.get("quantiteInsuffisante");
                mefiant.ajouterProposition(array, message);
                Log.reception(mefiant.nomAgent(message), message.getContent());

                if (mefiant.getNbReponseReçu() == mefiant.getNbRechercheEnvoye()) {
                    // récupation des différents id des produits
                    for (Produit produit : mefiant.getLproposition()) {
                        if (!hmAvisProduit.containsKey(produit.getId())) {
                            hmAvisProduit.put(produit.getId(), 0.0);
                            mefiant.demandeAvisProduit(produit.getId());
                        }

                        if (!hmAvisRevendeur.containsKey(mefiant.nomAgent(produit.getProvenance()))) {
                            hmAvisRevendeur.put(mefiant.nomAgent(produit.getProvenance()), 0.0);
                            mefiant.demandeAvis(mefiant.getTypeAgentCible(), mefiant.nomAgent(produit.getProvenance()));
                        }
                    }
                }

                if (mefiant.getNbReponseReçu() == mefiant.getNbRechercheEnvoye()
                        && mefiant.getNbDemandeAvisProduitEnvoye() == mefiant.getNbDemandeAvisProduitRecu()
                        && mefiant.getNbDemandeAvisRevendeurEnvoye() == mefiant.getNbDemandeAvisRevendeurRecu()) {

                    // on retire les produits des reveudeur peu fiable
                    nettoyerPropositionRevendeur();
                    // on retire les produits qui n'on pas le meilleur avis
                    if (mefiant.getLproposition().size() > 0) {
                        supprimerAutresProduits(mefiant.meilleurAvisProduit().getId());
                    }
                    if (mefiant.getLproposition().size() > 0) {
                        mefiant.jeChoisis(mefiant.moinsCher());
                    } else {
                        Log.arretRecherche();
                        mefiant.arretAgent();
                    }
                }
            }

            if (object.containsKey("requeteInvalide")) {
                JSONObject jsonObject = (JSONObject) object.get("requeteInvalide");
                // aucune proposition correspond à la recherche pour cet agent
                mefiant.setNbReponseReçu(mefiant.getNbReponseReçu() + 1);
                Log.reception(mefiant.nomAgent(message), message.getContent());
                mefiant.afficherRaisonInvalide(jsonObject, message);

                if (mefiant.getNbReponseReçu() == mefiant.getNbRechercheEnvoye()) {
                    // récupation des différents id des produits
                    for (Produit produit : mefiant.getLproposition()) {
                        if (!hmAvisProduit.containsKey(produit.getId())) {
                            hmAvisProduit.put(produit.getId(), 0.0);
                            mefiant.demandeAvisProduit(produit.getId());
                        }
                        if (!hmAvisRevendeur.containsKey(mefiant.nomAgent(produit.getProvenance()))) {
                            hmAvisRevendeur.put(mefiant.nomAgent(produit.getProvenance()), 0.0);
                            mefiant.demandeAvis(mefiant.getTypeAgentCible(), mefiant.nomAgent(produit.getProvenance()));
                        }
                    }
                }

                if ((mefiant.getNbReponseReçu() == mefiant.getNbRechercheEnvoye()
                        && mefiant.getNbDemandeAvisProduitEnvoye() == mefiant.getNbDemandeAvisProduitRecu()
                        && mefiant.getNbDemandeAvisRevendeurEnvoye() == mefiant.getNbDemandeAvisRevendeurRecu())) {

                    // on retire les produits des reveudeur peu fiable
                    nettoyerPropositionRevendeur();
                    // on retire les produits qui n'on pas le meilleur avis
                    if (mefiant.getLproposition().size() > 0) {
                        supprimerAutresProduits(mefiant.meilleurAvisProduit().getId());
                    }
                    if (mefiant.getLproposition().size() > 0) {
                        mefiant.jeChoisis(mefiant.moinsCher());
                    } else {
                        Log.arretRecherche();
                        mefiant.arretAgent();
                    }
                }
            }

            if (object.containsKey("commandeOk")) {
                JSONObject obj = (JSONObject) object.get("commandeOk");
                mefiant.afficherAchat(obj, message);

                // laisser avis erep sur vendeur/fournisseur + produit
                mefiant.donneAvis(mefiant.getTypeAgentCible(), mefiant.nomAgent(message));
                mefiant.donneAvisProduit(obj.get("idProduit").toString());

                // arreter agent
                mefiant.arretAgent();
            }

            if (object.containsKey("commandePasOK")) {
                JSONObject obj = (JSONObject) object.get("commandePasOK");
                mefiant.afficherRaison(obj, message);

                Produit produitAnnule = new Produit(obj, message.getSender().getName());

                // retirer la proposition
                mefiant.retirerProposition(produitAnnule);

                // choisir la meilleur proposition suivante si il y en a
                if (mefiant.getLproposition().size() > 0) {
                    mefiant.jeChoisis(mefiant.moinsCher());
                } else {
                    Log.arretRecherche();
                    mefiant.arretAgent();
                }
            }

            if (object.containsKey("retourAvis")) {
                Log.reception(mefiant.nomAgent(message), message.getContent());
                JSONObject obj = (JSONObject) object.get("retourAvis");
                if (obj.get("type").toString().equalsIgnoreCase("Produit")) {
                    mefiant.setNbDemandeAvisProduitRecu(mefiant.getNbDemandeAvisProduitRecu() + 1);
                    String idProduit = obj.get("id").toString();
                    double avis = 0;
                    // vérification que la valeur est pas null
                    if (obj.get("avis") != null) {
                        avis = Double.parseDouble(obj.get("avis").toString());
                    }

                    // mise à jour de la valeur de l'avis
                    hmAvisProduit.replace(idProduit, avis);
                }

                if (obj.get("type").toString().equalsIgnoreCase(TypeAgent.Fournisseur)
                        || obj.get("type").toString().equalsIgnoreCase(TypeAgent.Vendeur)) {
                    mefiant.setNbDemandeAvisRevendeurRecu(mefiant.getNbDemandeAvisRevendeurRecu() + 1);
                    String nomRevendeur = obj.get("nom").toString();
                    double avis = 0;
                    // vérification que la valeur est pas null
                    if (obj.get("avis") != null) {
                        avis = Double.parseDouble(obj.get("avis").toString());
                    }
                    if (avis >= avisRevendeurMin) {
                        // mise à jour de la valeur de l'avis
                        hmAvisRevendeur.replace(nomRevendeur, avis);
                    } else {
                        // suppression du produit
                        hmAvisRevendeur.remove(nomRevendeur);
                    }

                }

                if (mefiant.getNbReponseReçu() == mefiant.getNbRechercheEnvoye()
                        && mefiant.getNbDemandeAvisProduitEnvoye() == mefiant.getNbDemandeAvisProduitRecu()
                        && mefiant.getNbDemandeAvisRevendeurEnvoye() == mefiant.getNbDemandeAvisRevendeurRecu()) {

                    // on retire les produits des reveudeur peu fiable
                    nettoyerPropositionRevendeur();
                    // on retire les produits qui n'on pas le meilleur avis
                    if (mefiant.getLproposition().size() > 0) {
                        supprimerAutresProduits(mefiant.meilleurAvisProduit().getId());
                    }
                    if (mefiant.getLproposition().size() > 0) {
                        mefiant.jeChoisis(mefiant.moinsCher());
                    } else {
                        Log.arretRecherche();
                        mefiant.arretAgent();
                    }
                }
            }

        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(ClientAgent.class.getName()).log(Level.SEVERE, "Parse impossible, format JSON invalide");
        }
    }

    /**
     * Méthode qui supprime de la liste des proposition les produits venant de
     * revendeur peut fiable
     */
    public void nettoyerPropositionRevendeur() {

        ArrayList<Produit> lisProduitASupprimer = new ArrayList<Produit>();
        for (Produit produit : mefiant.getLproposition()) {
            if (!hmAvisRevendeur.containsKey(mefiant.nomAgent(produit.getProvenance()))) {
                lisProduitASupprimer.add(produit);
            }
        }
        // suppression des produits de revendeur non fiable
        for (Produit produit : lisProduitASupprimer) {
            mefiant.getLproposition().remove(produit);
            Logger.getLogger(Presse.class.getName()).log(Level.INFO, "Suppression d'un produit : Revendeur peu fiable");
        }

        // mise a jour des notes des produits restants
        miseAJourPropositionProduit();
    }

    /**
     * Méthode qui met à jour les produit avec leur avis récupéré auprès de
     * l'ereputation
     */
    public void miseAJourPropositionProduit() {
        for (Produit produit : mefiant.getLproposition()) {
            produit.setAvis(hmAvisProduit.get(produit.getId()));
        }
    }

    /**
     * Méthode de suppression de produits ne correspondant pas à l'id en
     * paramètre
     *
     * @param idProduit
     */
    public void supprimerAutresProduits(String idProduit) {
        ArrayList<Produit> lisProduitASupprimer = new ArrayList<Produit>();
        for (Produit produit : mefiant.getLproposition()) {
            if (!produit.getId().equalsIgnoreCase(idProduit)) {
                lisProduitASupprimer.add(produit);
            }
        }

        for (Produit produit : lisProduitASupprimer) {
            mefiant.getLproposition().remove(produit);
            Logger.getLogger(Presse.class.getName()).log(Level.INFO, "Suppression d'un produit : pas le meilleur avis");
        }
    }

}
