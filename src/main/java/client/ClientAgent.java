package client;

/*
 * Auteur : Aymeric ZANIRATO
 * Email: aymeric@zanirato.fr
 */
import client.UI.FXMLController;
import client.outils.TypeAgentClient;
import client.outils.Log;
import client.outils.Produit;
import client.behaviours.Econome;
import client.behaviours.Mefiant;
import client.behaviours.Presse;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import common.*;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Classe permettant de créer des agents client et de leur affecter un comportement
 * @author Aymeric
 */
public class ClientAgent extends SuperAgent {

    /**
     * 
     */
    private final String monService = TypeAgent.Client;
    
    /**
     * La liste des propositions reçu par les vendeurs ou fournisseurs pour une recherche effectué par le client
     */
    private ArrayList<Produit> lproposition;
    
    /**
     * La liste des noms complet des agents ayant répondus
     */
    private ArrayList<String> lAgentsRepond;
    
    /**
     * Le nombre de recherche envoyé
     */
    private int nbRechercheEnvoye = 0;
    
    /**
     * Le nombre de recherche reçu
     */
    private int nbReponseReçu = 0;
    
    /**
     * Le nombre de demande d'avis sur le produit envoyé
     */
    private int nbDemandeAvisProduitEnvoye = 0;
    
    /**
     * Le nombre de demande d'avis sur le produit reçu
     */
    private int nbDemandeAvisProduitRecu = 0;
    
    /**
     * Le nombre de demande d'avis sur le revendeur envoyé
     */
    private int nbDemandeAvisRevendeurEnvoye = 0;
    
    /**
     * Le nombre de demande d'avis sur le revendeur reçu
     */
    private int nbDemandeAvisRevendeurRecu = 0;
    
    /**
     * Le type (comportement) de l'agent client
     */
    private String typeAgentClient;
    
    /**
     * Le type de l'agent cible (erep, fournisseur, vendeur)
     */
    private String typeAgentCible;
    
    /**
     * La date limite accepté par le client pour faire son achat
     */
    private long limiteDate =0;
    
    /**
     * La prix maximum accepté par le client pour faire son achat
     */
    private double limitePrix = 0;
    
    /**
     * La quantité souhaité du produit par le client
     */
    private int quantite = 0;

    // **************************************************************** //
    //
    //  Méthodes d'exécution de l'agent
    //
    // **************************************************************** //
   /**
    * Méthode exécuté lors de la création d'un agent client, permet de lui affecter
    * les paramètres voulu en fonction de la saisi dans l'interface ainsi que de 
    * lui affecter son comportement
    */
    protected void setup() {

        // initailisation des attributs
        Object[] arguments = getArguments();
        typeAgentClient = arguments[0].toString();
        typeAgentCible = arguments[1].toString();
        String typeProduit = "";
        if (arguments[2] != null) {
            typeProduit = arguments[2].toString();
        }
        String recherche = arguments[3].toString();
        String reference = arguments[4].toString();
        quantite = Integer.parseInt(arguments[5].toString());
        String typeRecherche = arguments[6].toString();
        
        if(typeAgentClient.equalsIgnoreCase(TypeAgentClient.Econome)){
            limitePrix = Double.parseDouble(arguments[7].toString());
        }
        
        if(typeAgentClient.equalsIgnoreCase(TypeAgentClient.Presse)){
            limitePrix = Long.parseLong(arguments[7].toString());
        }
        this.lproposition = new ArrayList<Produit>();
        this.lAgentsRepond = new ArrayList<String>();

        // enregistrement du service
        registerService(monService);

        // écoute
        if (typeAgentClient.equals(TypeAgentClient.Econome)) {
            addBehaviour(new Econome(this));
        }

        // écoute
        if (typeAgentClient.equals(TypeAgentClient.Presse)) {
            addBehaviour(new Presse(this));
        }

        // écoute
        if (typeAgentClient.equals(TypeAgentClient.Mefiant)) {
            addBehaviour(new Mefiant(this));
        }

        if (typeRecherche.equalsIgnoreCase("true")) {
            // on lance la recherche
            this.jeCherche(typeAgentCible, typeProduit, recherche, quantite);
        } else {
            this.jeChercheReference(typeAgentCible, reference, quantite);
        }

    }

    /**
     * Méthode permettant de tuer un agent client, une fois que celui-ci a fini son achat
     */
    public void takeDown() {
        try {
            // on se retire du registre de service afin q'un autre
            // agent du même nom puisse se lancer
            DFService.deregister(this);
            Logger.getLogger(this.getLocalName()).log(Level.INFO, "Fin de l'agent !");
            doDelete();
        } catch (FIPAException ex) {
            Logger.getLogger(ClientAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Méthode permettant de tuer un agent client, une fois que celui-ci a fini son achat
     */
    public void takeDown2() {
        try {
            // on se retire du registre de service afin q'un autre
            // agent du même nom puisse se lancer
            DFService.deregister(this);
            Logger.getLogger(this.getLocalName()).log(Level.INFO, "Fin de l'agent !");
            doDelete();
        } catch (FIPAException ex) {
            Logger.getLogger(ClientAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // **************************************************************** //
    //
    //  Méthodes liées à l'envoi de message
    //
    // **************************************************************** //
    /**
     * Permet d'envoyer un message de recherche de produit par nom de produit aux destinataires
     * @param typeAgent Le type du destinataire
     * @param typeProduit Le type de produit
     * @param recherche La recherche effectuée
     * @param quantite La quantité souhaitée
     */
    public void jeCherche(String typeAgent, String typeProduit, String recherche, int quantite) {

        // construction de l'objet JSON à envoyé
        JSONObject jeCherche = new JSONObject();
        JSONObject elementRecherche = new JSONObject();
        elementRecherche.put("quantite", quantite);
        elementRecherche.put("recherche", recherche);
        elementRecherche.put("typeProduit", typeProduit);
        jeCherche.put("jeCherche", elementRecherche);

        // envoi du message de recherche à tous les agents
        // du type choisi
        AID[] agent = findAgentsFromService(typeAgentCible);
        for (AID f : agent) {
            String message = jeCherche.toString();
            envoyerMessage(this, ACLMessage.REQUEST, f, message);
            nbRechercheEnvoye++;
            Log.envoi(nomAgent(f.getName()), message);
        }
    }

    /**
     * Permet d'envoyer un message de recherche de produit par référence aux destinataires
     * @param typeAgent Le type de destinataire
     * @param reference La référence du produit
     * @param quantite La quantité souhaité
     */
    public void jeChercheReference(String typeAgent, String reference, int quantite) {

        // construction de l'objet JSON à envoyé
        JSONObject jeChercheReference = new JSONObject();
        JSONObject elementRecherche = new JSONObject();
        elementRecherche.put("quantite", quantite);
        elementRecherche.put("reference", reference);
        jeChercheReference.put("jeChercheRef", elementRecherche);

        // envoi du message de recherche à tous les agents du type choisi
        AID[] agent = findAgentsFromService(typeAgentCible);
        for (AID f : agent) {
            String message = jeChercheReference.toString();
            envoyerMessage(this, ACLMessage.REQUEST, f, message);
            nbRechercheEnvoye++;
            Log.envoi(nomAgent(f.getName()), message);
        }
    }

    /**
     * Permet de choisi le produit passé en paramètre (lui même sélectionné dans la liste des propositions
     * faites par les vendeurs ou fournisseurs) et de prévenir son destinataire
     * @param p Le produit qui a été choisi
     */
    public void jeChoisis(Produit p) {

        AID aid = new AID(p.getProvenance());

        // construction de l'objet JSON à envoyé
        JSONObject jeChoisi = new JSONObject();
        jeChoisi.put("jeChoisis", p.getJSONObject());

        // envoi du message + afficahge dans les logs
        envoyerMessage(this, ACLMessage.ACCEPT_PROPOSAL, aid, jeChoisi.toString());
        Log.envoi(nomAgent(p.getProvenance()), jeChoisi.toString());
    }

    /**
     * Méthode d'envoi d'avis sur un agent fournisseur ou vendeur
     *
     * @param nomAgent nom de l'agent sur lequel on donne notre l'avis
     * @param typeAgent type de l'agent sur lequel on donne notre l'avis
     */
    public void donneAvis(String typeAgent, String nomAgent) {
        AID[] agent = findAgentsFromService(TypeAgent.EReputation);

        int avis = 0;

        // construction de l'objet JSON à envoyé
        JSONObject donneAvis = new JSONObject();
        JSONObject contenu = new JSONObject();
        contenu.put("type", typeAgent);
        contenu.put("nom", nomAgent);
        contenu.put("avis", avis);
        donneAvis.put("donneAvis", contenu);

        // envoi du message + afficahge dans les logs
        envoyerMessage(this, ACLMessage.INFORM, agent[0], donneAvis.toString());
        Log.envoi(TypeAgent.EReputation, donneAvis.toString());
    }

    /**
     * Permet de donner un avis sur un produit et d'envoyer un message à l'erep
     * @param idProduit L'ID du produit
     */
    public void donneAvisProduit(String idProduit) {
        AID[] agent = findAgentsFromService(TypeAgent.EReputation);

        // avis aléatoire entre 0 et 5
        long avis = (long) (Math.random() * (5));

        // construction de l'objet JSON à envoyé
        JSONObject donneAvis = new JSONObject();
        JSONObject contenu = new JSONObject();
        contenu.put("type", "Produit");
        contenu.put("id", idProduit);
        contenu.put("avis", avis);
        donneAvis.put("donneAvis", contenu);

        // envoi du message + afficahge dans les logs
        envoyerMessage(this, ACLMessage.INFORM, agent[0], donneAvis.toString());
        Log.envoi(TypeAgent.EReputation, donneAvis.toString());
    }

    /**
     * Méthode de demande d'avis sur un vendeur ou un fournisseur
     *
     * @param typeAgent type de l'agent auquel on veux l'avis
     * @param nomAgent nom de l'agent auquel on veux l'avis
     */
    public void demandeAvis(String typeAgent, String nomAgent) {
        AID[] agent = findAgentsFromService(TypeAgent.EReputation);

        // construction de l'objet JSON à envoyé
        JSONObject demandeAvis = new JSONObject();
        JSONObject contenu = new JSONObject();
        contenu.put("type", typeAgent);
        contenu.put("nom", nomAgent);
        demandeAvis.put("demandeAvis", contenu);

        // envoi du message + afficahge dans les logs
        envoyerMessage(this, ACLMessage.REQUEST, agent[0], demandeAvis.toString());
        Log.envoi(TypeAgent.EReputation, demandeAvis.toString());
    }

    /**
     * Permet de demander un avis à l'erep sur un produit
     * @param idProduit L'ID du produit
     */
    public void demandeAvisProduit(String idProduit) {
        AID[] agent = findAgentsFromService(TypeAgent.EReputation);

        // construction de l'objet JSON à envoyé
        JSONObject demandeAvis = new JSONObject();
        JSONObject contenu = new JSONObject();
        contenu.put("type", "Produit");
        contenu.put("id", idProduit);
        demandeAvis.put("demandeAvis", contenu);

        nbDemandeAvisProduitEnvoye++;

        // envoi du message + afficahge dans les logs
        envoyerMessage(this, ACLMessage.REQUEST, agent[0], demandeAvis.toString());
        Log.envoi(TypeAgent.EReputation, demandeAvis.toString());
    }

    /**
     * Permet de demander la désirabilité d'un produit à l'agent erep
     * @param adresseAgentErep L'agetn erep à qui demander
     * @param idProduit L'ID du produit recherché
     */
    public void demandeDesirabilite(String adresseAgentErep, String idProduit) {
        AID aid = new AID(adresseAgentErep);

        // construction de l'objet JSON à envoyé
        JSONObject demandeDesirabilite = new JSONObject();
        JSONObject contenu = new JSONObject();
        contenu.put("type", "Produit");
        contenu.put("id", idProduit);
        demandeDesirabilite.put("demandeDesirabilite", contenu);

        // Envoi du message + affichage dans les logs
        envoyerMessage(this, ACLMessage.REQUEST, aid, demandeDesirabilite.toString());
        Log.envoi(nomAgent(adresseAgentErep), demandeDesirabilite.toString());
    }
    
    /**
     * Permet d'ajouter un log sur l'achat(réussi ou non) et d'envoyer un message à l'erep sur le résultat de l'achat
     * @param adresseAgentErep nom complet de l'agent erep
     * @param reussi true si la négociation a réussi sinon false
     * @param NbNegociation Le nombre de négociation effectué
     */
    public void achatEffectue(String adresseAgentErep, Boolean reussi, int NbNegociation){
        AID aid = new AID(adresseAgentErep);
        
        // construction de l'objet JSON à envoyé
        JSONObject achatEffectue = new JSONObject();
        JSONObject contenu = new JSONObject();
        contenu.put("success",reussi );
        contenu.put("comportement",typeAgentClient);
        contenu.put("nbNegociations",NbNegociation);
        achatEffectue.put("achatEffectue", contenu);
        
        // Envoi du message + affichage dans les logs
        envoyerMessage(this, ACLMessage.INFORM, aid, achatEffectue.toString());
        Log.envoi(nomAgent(adresseAgentErep), achatEffectue.toString());
        
    }

    // **************************************************************** //
    //
    //  Méthodes de traitement
    //
    // **************************************************************** //
    /**
     * Permet d'ajouter une proposition reçu dans la liste des propositions et d'ajouter un log du message reçu
     * @param array Le tableau contenant les informations sur le produit
     * @param message Le message reçu
     */
    public void ajouterProposition(JSONArray array, ACLMessage message) {

        for (Object obj : array.toArray()) {
            JSONObject jsonObject = (JSONObject) obj;

            Produit p = new Produit(jsonObject, message.getSender().getName());
            lproposition.add(p);
        }

        lAgentsRepond.add(nomAgent(message));
        Log.reception(nomAgent(message), message.getContent());

    }

    // **************************************************************** //
    //
    //  Méthodes d'affichage
    //
    // **************************************************************** //
    /**
     * Permet d'afficher le message final lors d'un achat réussi
     * @param jsonObj Le tableau JSON contenant les informations sur le produit
     * @param message Le message reçu
     */
    public void afficherAchat(JSONObject jsonObj, ACLMessage message) {

        Log.reception(nomAgent(message), message.getContent());

        StringBuilder sb = new StringBuilder("Achat effectué chez : ");
        sb.append(nomAgent(message));
        sb.append("\n");
        sb.append("Nom produit : ");
        sb.append(jsonObj.get("nomProduit").toString());
        sb.append("\n");
        sb.append("Quantité : ");
        sb.append(jsonObj.get("quantite").toString());
        sb.append("\n");
        sb.append("Prix : ");
        sb.append(jsonObj.get("prix").toString());
        sb.append("\n");
        sb.append("Date Livraison : ");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateStr = simpleDateFormat.format(new Date((Long.parseLong(jsonObj.get("date").toString()))*1000));
        sb.append(dateStr);

        Log.achat(sb.toString());
    }

    /**
     * Afficher la raison de l'annulation d'une commande
     * @param obj Le tableau JSON contenant les informations sur le produit
     * @param message Le message reçu
     */
    public void afficherRaison(JSONObject obj, ACLMessage message) {
        StringBuilder sb = new StringBuilder("Commande impossible chez : ");
        sb.append(nomAgent(message));
        sb.append("\n");
        sb.append("Raison : ");
        sb.append(obj.get("raison").toString());
        Log.commandeAnnulee(sb.toString());
    }

    /**
     * Affiche la raison d'une commande impossible
     * @param obj Le tableau JSON contenant les informations sur le produit
     * @param message Le message reçu
     */
    public void afficherRaisonInvalide(JSONObject obj, ACLMessage message) {
        StringBuilder sb = new StringBuilder("Commande impossible chez : ");
        sb.append(nomAgent(message));
        sb.append("\n");
        sb.append("Raison : ");
        sb.append(obj.get("raison").toString());
        Log.affiche(sb.toString());
    }

    // **************************************************************** //
    //
    //  Méthodes outils
    //
    // **************************************************************** //
    /**
     * Permet de retourner le nom complet de l'agent
     * @param message Le message envoyé par l'agent dont on souhaite connaitre le nom complet
     * @return Le nom complet de l'agent
     */
    public String nomAgent(ACLMessage message) {
        return message.getSender().getLocalName();
    }

    /**
     * Permet de retourner le nom simple de l'agent
     * @param adresseAgent Le nom complet de l'agent
     * @return Le nom simple de l'agent sans son adresse IP
     */
    public String nomAgent(String adresseAgent) {
        String split[] = adresseAgent.split("@");
        return split[0];
    }

    /**
     * Permet de retirer une proposition qui est dans la liste des propositions
     * @param produitARetirer Le produit à retirer
     */
    public void retirerProposition(Produit produitARetirer) {
        Produit produitASupprimer = null;
        for (Produit prod : lproposition) {
            if (prod.equals(produitARetirer)) {
                produitASupprimer = prod;
            }
        }
        if (produitASupprimer != null) {
            lproposition.remove(produitASupprimer);
        }
    }

    /**
     * Méthode retournant le produit le moins cher parmi la liste des
     * propositions
     *
     * @return le produit le moins cher
     */
    public Produit moinsCher() {
        Produit produitChoisi = lproposition.get(0);
        for (Produit produit : lproposition) {
            if (produit.getPrix() < produitChoisi.getPrix()) {
                produitChoisi = produit;
            }
        }
        return produitChoisi;
    }

    /**
     * Méthode retournant le produit livré au plut tot parmi la liste des
     * propositions (vérifie si la quantité est bonne)
     *
     * @return le produit livrable en premier
     */
    public Produit plusTot() {
        Produit produitChoisi = null;
        // On instancie produitChoisi si une proposition possède la quantité suffisante
        for (Produit produit : lproposition) {
            if (produit.getQuantite() >= this.quantite) {
                produitChoisi = produit;
                break;
            }
        }
        // Si il existe au moins un produit dont la quantité est suffisante
        if (produitChoisi != null) {
            for (Produit produit : lproposition) {
                // On choisi en fonction de la date au plus tôt et de la quantité
                if (produit.getDateLivraison() < produitChoisi.getDateLivraison() && produit.getQuantite() >= this.quantite) {
                    produitChoisi = produit;
                }
            }
        } else {
            produitChoisi = lproposition.get(0);
            for (Produit produit : lproposition) {
                // On choisi en fonction de la date au plus tôt
                if (produit.getDateLivraison() < produitChoisi.getDateLivraison() ) {
                    produitChoisi = produit;
                }
            }
        }
        return produitChoisi;
    }

    /**
     * Méthode permettant de trouver le produit ayant le meilleur avis parmi les propositions qui ont été effectués
     * @return Le produit avec le meilleur avis
     */
    public Produit meilleurAvisProduit() {
        Produit produitChoisi = lproposition.get(0);
        for (Produit produit : lproposition) {
            if (produit.getAvis() > produitChoisi.getAvis()) {
                produitChoisi = produit;
            }
        }
        return produitChoisi;
    }

    /**
     * Permet de retirer toutes les propositions de la liste dont le prix dépasse le prix maximum accepté par le client
     * @param prixMaximum Le prix maximum accepté par le client pour l'achat du produit
     */
    public void nettoyerPropositionPrix(Double prixMaximum) {
        ArrayList<Produit> lisProduitASupprimer = new ArrayList<Produit>();
        for (Produit produit : lproposition) {
            if (produit.getPrix() > prixMaximum) {
                lisProduitASupprimer.add(produit);
            }
        }
        // Suppression des proposition ne correspondant pas aux critères
        for (Produit produit : lisProduitASupprimer) {
            lproposition.remove(produit);
        }
    }
    
    /**
     * Permet de retirer toutes les propositions de la liste dont la date dépasse le date limite acceptée par le client
     * @param dateMaximum La date limite accepté par le client pour l'achat du produit
     */
    public void nettoyerPropositionDate(long dateMaximum){
        ArrayList<Produit> lisProduitASupprimer = new ArrayList<Produit>();
        for (Produit produit : lproposition) {
            if (produit.getDateLivraison() > dateMaximum) {
                lisProduitASupprimer.add(produit);
            }
        }
        // suppression des proposition ne correspondant pas aux critères
        for (Produit produit : lisProduitASupprimer) {
            lproposition.remove(produit);
        }
    }
    
        /**
     * Méthode qui permet de choisir une proposition d'un vendeur ou fournisseur pour un client fidèle
     *
     * @return le produit choisi
     */
    public Produit choixFidelite() {
//        System.out.println("La fidelite c'est le client");
        Produit produitChoisi = lproposition.get(0);
        String provenanceAncienAchat = "";
        
        boolean existAncienAchat = false;
        // On parcours la liste des achats effectués
        if (FXMLController.lAchatsEffectues.size() > 0) {
            for (Map.Entry<String, Produit> entrySet : FXMLController.lAchatsEffectues.entrySet()) {
                String key = entrySet.getKey();
                Produit value = entrySet.getValue();
//                System.out.println("Dans 1er boucle clé = "+key);
                // Si l'un d'entre eux a été effectué par le client en question on enregistre la provenance
                if (key.compareTo(this.getName()) == 0) {
                    provenanceAncienAchat = value.getProvenance();
                    existAncienAchat = true;
                    System.out.println("Ancien achat client "+this.getName() +" provenance "+ value.getProvenance());
                    break;
                }
            }
        }
        
        // On choisit le produit ayant la même provenance
        for (Produit produit : lproposition) {
            if(existAncienAchat){
//                System.out.println("test provenance : "+ produit.getProvenance() + " =? " +provenanceAncienAchat);
                if (produit.getProvenance().compareTo(provenanceAncienAchat) == 0) {
//                    System.out.println("Dans provenance : "+ produit.getProvenance());
                    // Si plusieurs produit de même provenance on choisit le moins cher
                    if(produit.getPrix() < produitChoisi.getPrix()){
                        produitChoisi = produit;
                    }
                }
            }else {
//                System.out.println("Else pas d'ancien achat");
                if(produit.getPrix() < produitChoisi.getPrix()){
                    produitChoisi = produit;
                }
            }
        }
        return produitChoisi;
    }

    /**
     * Permet d'envoyer un message
     * @param client Le client
     * @param typeMessage Le type de messahe
     * @param receiver Le receveur du message
     * @param message Le message à envoyer
     */
    public void envoyerMessage(Agent client, int typeMessage, AID receiver, String message) {
        ACLMessage msg = new ACLMessage(typeMessage);
        msg.setContent(message);
        msg.addReceiver(receiver);
        client.send(msg);
    }

    // **************************************************************** //
    //
    //  Getter & Setter
    //
    // **************************************************************** //
    
    /**
     * Getter permettant de retourner la liste des propositions faite par les vendeurs et fournisseurs
     * @return La liste des propositions
     */
    public ArrayList<Produit> getLproposition() {
        return lproposition;
    }

    /**
     * Setter de la liste des propositions
     * @param lproposition La nouvelle liste de proposition
     */
    public void setLproposition(ArrayList<Produit> lproposition) {
        this.lproposition = lproposition;
    }

    /**
     * Getter de la liste des agents ayant répondus
     * @return La liste des agents ayant répondu
     */
    public ArrayList<String> getlAgentsRepond() {
        return lAgentsRepond;
    }

    /**
     * Setter de la liste des agents ayants répondu
     * @param lAgentsRepond La nouvelle liste des agents ayant répondu
     */
    public void setlAgentsRepond(ArrayList<String> lAgentsRepond) {
        this.lAgentsRepond = lAgentsRepond;
    }

    /**
     * Getter du nombre de recherche envoyé
     * @return Le nombre de recherche envoyé
     */
    public int getNbRechercheEnvoye() {
        return nbRechercheEnvoye;
    }

    /**
     * Setter du nombre de recherche envoyé
     * @param nbRechercheEnvoye Le nouveau nombre de recherche envoyé
     */
    public void setNbRechercheEnvoye(int nbRechercheEnvoye) {
        this.nbRechercheEnvoye = nbRechercheEnvoye;
    }

    /**
     * Getter du type d'agent client
     * @return Le type (comportement) de l'agent client
     */
    public String getTypeAgentClient() {
        return typeAgentClient;
    }

    /**
     * Setter de du type d'agent client
     * @param typeAgentClient Le nouveau type (comportement) du client
     */
    public void setTypeAgentClient(String typeAgentClient) {
        this.typeAgentClient = typeAgentClient;
    }

    /**
     * Getter du type d'agent cible
     * @return Le type de l'agent cyible
     */
    public String getTypeAgentCible() {
        return typeAgentCible;
    }
    
    /**
     * Setter de l'agent cicle
     * @param typeAgentCible Le nouveau type de l'agent cible
     */
    public void setTypeAgentCible(String typeAgentCible) {
        this.typeAgentCible = typeAgentCible;
    }

    /**
     * La date limite d'achat pour un client
     * @return La date limite d'achat
     */
    public long getLimiteDate() {
        return limiteDate;
    }
    
    /**
     * Setter de la date limite d'achat du client
     * @param limiteDate La nouvelle date d'achat limite du client
     */
    public void setLimiteDate(long limiteDate) {
        this.limiteDate = limiteDate;
    }

    /**
     * Getter du maximum auquel le client accepte de faire un achat
     * @return La prix maximum accepté par le client pour faire un achat
     */
    public double getLimitePrix() {
        return limitePrix;
    }

    /**
     * Setter du prix maximum d'achat du client
     * @param limitePrix Le prix limite d'achet
     */
    public void setLimitePrix(double limitePrix) {
        this.limitePrix = limitePrix;
    }

    /**
     * Getter du nombre de réponse reçu
     * @return Le nombre de réponse reçu
     */
    public int getNbReponseReçu() {
        return nbReponseReçu;
    }

    /**
     * Setter du nombre de réponse reçu
     * @param nbReponseReçu Le nouveau nombre de réponse reçu
     */
    public void setNbReponseReçu(int nbReponseReçu) {
        this.nbReponseReçu = nbReponseReçu;
    }

    /**
     * Getter du nombre de produit voulu par le client
     * @return La quantité voulu
     */
    public int getQuantite() {
        return quantite;
    }

    /**
     * Setter de la quantité voulu par le client
     * @param quantite La nouvelle quantité voulu par le client
     */
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    /**
     * Getter du nombre de demande d'avis sur le produit envoyé
     * @return Le nombre de demande d'avis envoyé sur le produit
     */
    public int getNbDemandeAvisProduitEnvoye() {
        return nbDemandeAvisProduitEnvoye;
    }

    /**
     * Setter du nombre de demande d'avis envoyé sur le produit
     * @param nbDemandeAvisProduitEnvoye Le nouveau nombre de demande d'avis
     */
    public void setNbDemandeAvisProduitEnvoye(int nbDemandeAvisProduitEnvoye) {
        this.nbDemandeAvisProduitEnvoye = nbDemandeAvisProduitEnvoye;
    }
    
    /**
     * Getter du nombre de demande d'avis reçu sur le produit
     * @return Le nombre de demande d'avis reçu sur le produit
     */
    public int getNbDemandeAvisProduitRecu() {
        return nbDemandeAvisProduitRecu;
    }

    /**
     * Setter du nombre de demande d'avis sur le produit reçu
     * @param nbDemandeAvisProduitRecu Le nouveua nombre de demande d'avis reçu sur le produit
     */
    public void setNbDemandeAvisProduitRecu(int nbDemandeAvisProduitRecu) {
        this.nbDemandeAvisProduitRecu = nbDemandeAvisProduitRecu;
    }

    /**
     * Getter du nombre de demande d'avis envoyé par le revendeur
     * @return Le nombre de demande d'avis envoyé par le revendeur
     */
    public int getNbDemandeAvisRevendeurEnvoye() {
        return nbDemandeAvisRevendeurEnvoye;
    }

    /**
     * Setter du nombre de demande d'avis envoyé par le revendeur
     * @param nbDemandeAvisRevendeurEnvoye Le nouveau nombre de demande d'avis envoyé par le revendeur
     */
    public void setNbDemandeAvisRevendeurEnvoye(int nbDemandeAvisRevendeurEnvoye) {
        this.nbDemandeAvisRevendeurEnvoye = nbDemandeAvisRevendeurEnvoye;
    }

    /**
     * Getter du nombre de demande d'avis reçu par le revendeur
     * @return Le nombre de demande d'avis reçu par le revendeur
     */
    public int getNbDemandeAvisRevendeurRecu() {
        return nbDemandeAvisRevendeurRecu;
    }

    /**
     * Setter du nombre de demande d'avis reçu par le revendeur
     * @param nbDemandeAvisRevendeurRecu  Le nouveau nombre de demande d'avis reçu par le revendeur
     */
    public void setNbDemandeAvisRevendeurRecu(int nbDemandeAvisRevendeurRecu) {
        this.nbDemandeAvisRevendeurRecu = nbDemandeAvisRevendeurRecu;
    }
}
