/*
 * Auteur : Aymeric ZANIRATO
 * Email: aymeric@zanirato.fr
 */
package client.behaviours;

import client.ClientAgent;
import client.outils.Log;
import client.outils.Produit;
import common.TypeAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Aymeric
 */
public class Negociateur extends CyclicBehaviour {

    private final ClientAgent negociateur;
    private final double facteurDebutNegociation = 0.70;
    private final double facteurFinNegociation = 0.85;
    private Produit produitReference;
    private Produit produitEnNegociation;
    private double prixDepart = 0;
    private double prixFinal = 0;
    private int nbNegociation = 0;

    public Negociateur(Agent agent) {
        this.negociateur = (ClientAgent) agent;
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            traiterMessage(msg);
            block();
        }
    }

    public void traiterMessage(ACLMessage message) {

        try {
            Logger.getLogger(Negociateur.class.getName()).log(Level.INFO, message.getContent());
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(message.getContent());

            if (object.containsKey("jePropose")) {
                JSONArray array = (JSONArray) object.get("jePropose");
                negociateur.ajouterProposition(array, message);
                negociateur.setNbReponseReçu(negociateur.getNbReponseReçu() + 1);
                if (negociateur.getNbReponseReçu() == negociateur.getNbRechercheEnvoye()) {
                    if (negociateur.getLproposition().size() > 0) {
                        jeNegocie(negociateur.moinsCher());
                    } else {
                        Log.arretRecherche();
                        negociateur.finAgent();
                    }
                }
            }

            if (object.containsKey("quantiteInsuffisante")) {
                negociateur.setNbReponseReçu(negociateur.getNbReponseReçu() + 1);
                JSONArray array = (JSONArray) object.get("quantiteInsuffisante");
                negociateur.ajouterProposition(array, message);
                Log.reception(negociateur.nomAgent(message), message.getContent());
                if (negociateur.getNbReponseReçu() == negociateur.getNbRechercheEnvoye()) {
                    if (negociateur.getLproposition().size() > 0) {
                        jeNegocie(negociateur.moinsCher());
                    } else {
                        Log.arretRecherche();
                        negociateur.finAgent();
                    }
                }
            }

            if (object.containsKey("requeteInvalide")) {
                JSONObject jsonObject = (JSONObject) object.get("requeteInvalide");
                // aucune proposition correspond à la recherche pour cet agent
                negociateur.setNbReponseReçu(negociateur.getNbReponseReçu() + 1);
                Log.reception(negociateur.nomAgent(message), message.getContent());

                negociateur.afficherRaisonInvalide(jsonObject, message);

                if ((negociateur.getNbReponseReçu() == negociateur.getNbRechercheEnvoye())) {

                    if (negociateur.getLproposition().size() > 0) {
                        jeNegocie(negociateur.moinsCher());
                    } else {
                        Log.arretRecherche();
                        negociateur.finAgent();
                    }
                }
            }

            if (object.containsKey("commandeOk")) {
                JSONObject obj = (JSONObject) object.get("commandeOk");
                negociateur.afficherAchat(obj, message);

                // laisser avis erep sur vendeur/fournisseur + produit
                negociateur.donneAvis(negociateur.getTypeAgentCible(), negociateur.nomAgent(message));
                negociateur.donneAvisProduit(obj.get("idProduit").toString());

                //laisser les informations à l'ereputation
                negociateur.achatEffectue(Boolean.TRUE, nbNegociation);
                // arreter agent
                negociateur.finAgent();
            }

            if (object.containsKey("commandePasOK")) {
                JSONObject obj = (JSONObject) object.get("commandePasOK");
                negociateur.afficherRaison(obj, message);

                // retirer la proposition
                negociateur.retirerProposition(produitReference);

                // choisir la meilleur proposition suivante si il y en a
                if (negociateur.getLproposition().size() > 0) {
                    jeNegocie(negociateur.moinsCher());
                } else {
                    Log.arretRecherche();
                    negociateur.finAgent();
                }

            }

            if (object.containsKey("jeNegocie")) {
                Log.reception(negociateur.nomAgent(message), message.getContent());
                JSONObject obj = (JSONObject) object.get("jeNegocie");
                Double nouveauPrix = Double.parseDouble(obj.get("prix").toString());

                // on ne negocie pas plus de deux fois
                // sinon perte de temps pour le negociateur
                if (nbNegociation < 2) {

                    // si l'offre est inférieur au prix final désiré
                    // on effectue l'achat
                    if (nouveauPrix <= prixFinal) {
                        // mise à jour du produit avec l'offre
                        produitEnNegociation.setPrix(nouveauPrix);
                        negociateur.jeChoisis(produitEnNegociation);
                    }

                    // une deuxième tentative de négociation
                    if (nouveauPrix > prixFinal && nouveauPrix < prixDepart) {
                        produitEnNegociation.setPrix(prixFinal);
                        jeNegocie(produitEnNegociation);
                    }
                } else {
                    //laisser les informations à l'ereputation
                    negociateur.achatEffectue(Boolean.FALSE, nbNegociation);

                    //on supprime les produits du vendeur/fournisseur en question
                    nettoyerPropositionFournisseur(produitEnNegociation.getProvenance());
                    
                    Log.affiche("Arrêt des négociations avec : "+negociateur.nomAgent(produitEnNegociation.getProvenance()));

                    //remise à zero des variables
                    nbNegociation = 0;
                    prixDepart = 0;
                    prixFinal = 0;

                    if (negociateur.getLproposition().size() > 0) {
                        jeNegocie(negociateur.moinsCher());
                    } else {
                        Log.arretRecherche();
                        negociateur.finAgent();
                    }
                }

            }

        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(ClientAgent.class.getName()).log(Level.SEVERE, "Parse impossible, format JSON invalide");
        }
    }

    public void jeNegocie(Produit p) {
        AID aid = new AID(p.getProvenance());
        produitEnNegociation = p;

        nbNegociation++;

        // première tentative de négotiation
        if (nbNegociation == 1) {
            //Récupération du prix de départ
            prixDepart = p.getPrix();
            prixFinal = prixDepart * facteurFinNegociation;
            produitEnNegociation.setPrix(prixDepart * facteurDebutNegociation);
            produitReference=p;
            Log.affiche("Prix à atteindre : "+prixFinal);
        }

        

        // construction de l'objet JSON à envoyé
        JSONObject jeNegocie = new JSONObject();
        JSONObject contenu = new JSONObject();
        contenu.put("idProduit", produitEnNegociation.getId());
        contenu.put("nomProduit", produitEnNegociation.getNom());
        contenu.put("quantite", produitEnNegociation.getQuantite());
        contenu.put("prix", produitEnNegociation.getPrix());
        contenu.put("date", produitEnNegociation.getDateLivraison());
        jeNegocie.put("jeNegocie", contenu);

        // envoi du message + afficahge dans les logs
        negociateur.envoyerMessage(negociateur, ACLMessage.PROPOSE, aid, jeNegocie.toString());
        Log.envoi(negociateur.nomAgent(p.getProvenance()), jeNegocie.toString());
    }

    public void nettoyerPropositionFournisseur(String provenance) {
        ArrayList<Produit> lisProduitASupprimer = new ArrayList<Produit>();
        for (Produit produit : negociateur.getLproposition()) {
            if (produit.getProvenance().equalsIgnoreCase(provenance)) {
                lisProduitASupprimer.add(produit);
            }
        }
        // suppression des proposition ne correspondant pas aux critères
        for (Produit produit : lisProduitASupprimer) {
            negociateur.getLproposition().remove(produit);
        }
    }

}
