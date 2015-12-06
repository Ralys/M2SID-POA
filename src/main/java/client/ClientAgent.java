package client;

/*
 * Auteur : Aymeric ZANIRATO
 * Email: aymeric@zanirato.fr
 */
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

/**
 *
 * @author Aymeric
 */
public class ClientAgent extends SuperAgent {

    private final String monService = TypeAgent.Client;
    private ArrayList<Produit> lproposition;
    private ArrayList<String> lAgentsRepond;
    private int nbRechercheEnvoye = 0;
    private int nbReponseReçu = 0;
    private int nbDemandeAvisProduitEnvoye = 0;
    private int nbDemandeAvisProduitRecu = 0;
    private int nbDemandeAvisRevendeurEnvoye = 0;
    private int nbDemandeAvisRevendeurRecu = 0;
    private String typeAgentClient;
    private String typeAgentCible;
    private long limiteDate =0;
    private double limitePrix = 0;
    private int quantite = 0;

    // **************************************************************** //
    //
    //  Méthodes d'exécution de l'agent
    //
    // **************************************************************** //
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

    // **************************************************************** //
    //
    //  Méthodes liées à l'envoi de message
    //
    // **************************************************************** //
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

    public void jeChercheReference(String typeAgent, String reference, int quantite) {

        // construction de l'objet JSON à envoyé
        JSONObject jeChercheReference = new JSONObject();
        JSONObject elementRecherche = new JSONObject();
        elementRecherche.put("quantite", quantite);
        elementRecherche.put("reference", reference);
        jeChercheReference.put("jeChercheRef", elementRecherche);

        // envoi du message de recherche à tous les agents
        // du type choisi
        AID[] agent = findAgentsFromService(typeAgentCible);
        for (AID f : agent) {
            String message = jeChercheReference.toString();
            envoyerMessage(this, ACLMessage.REQUEST, f, message);
            nbRechercheEnvoye++;
            Log.envoi(nomAgent(f.getName()), message);
        }
    }

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

    public void donneAvisProduit(String idProduit) {
        AID[] agent = findAgentsFromService(TypeAgent.EReputation);

        // avis aléatoire entre 0 et 5
        double avis = (Math.random() * (5));

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

    public void demandeDesirabilite(String adresseAgentErep, String idProduit) {
        AID aid = new AID(adresseAgentErep);

        // construction de l'objet JSON à envoyé
        JSONObject demandeDesirabilite = new JSONObject();
        JSONObject contenu = new JSONObject();
        contenu.put("type", "Produit");
        contenu.put("id", idProduit);
        demandeDesirabilite.put("demandeDesirabilite", contenu);

        // envoi du message + afficahge dans les logs
        envoyerMessage(this, ACLMessage.REQUEST, aid, demandeDesirabilite.toString());
        Log.envoi(nomAgent(adresseAgentErep), demandeDesirabilite.toString());
    }
    
    public void achatEffectue(String adresseAgentErep,Boolean reussi, int NbNegociation){
        AID aid = new AID(adresseAgentErep);
        
        // construction de l'objet JSON à envoyé
        JSONObject achatEffectue = new JSONObject();
        JSONObject contenu = new JSONObject();
        contenu.put("success",reussi );
        contenu.put("comportement",typeAgentClient);
        contenu.put("nbNegociations",NbNegociation);
        achatEffectue.put("achatEffectue", contenu);
        
        // envoi du message + afficahge dans les logs
        envoyerMessage(this, ACLMessage.INFORM, aid, achatEffectue.toString());
        Log.envoi(nomAgent(adresseAgentErep), achatEffectue.toString());
        
    }

    // **************************************************************** //
    //
    //  Méthodes de traitement
    //
    // **************************************************************** //
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

    public void afficherRaison(JSONObject obj, ACLMessage message) {
        StringBuilder sb = new StringBuilder("Commande impossible chez : ");
        sb.append(nomAgent(message));
        sb.append("\n");
        sb.append("Raison : ");
        sb.append(obj.get("raison").toString());
        Log.commandeAnnulee(sb.toString());
    }

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
    public String nomAgent(ACLMessage message) {
        return message.getSender().getLocalName();
    }

    public String nomAgent(String adresseAgent) {
        String split[] = adresseAgent.split("@");
        return split[0];
    }

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
     * propositions
     *
     * @return le produit livrable en premier
     */
    public Produit plusTot() {

        Produit produitChoisi = lproposition.get(0);
        for (Produit produit : lproposition) {
            if (produit.getDateLivraison() < produitChoisi.getDateLivraison()) {
                produitChoisi = produit;
            }
        }
        return produitChoisi;
    }

    public Produit meilleurAvisProduit() {
        Produit produitChoisi = lproposition.get(0);
        for (Produit produit : lproposition) {
            if (produit.getAvis() > produitChoisi.getAvis()) {
                produitChoisi = produit;
            }
        }
        return produitChoisi;
    }

    
    public void nettoyerPropositionPrix(Double prixMaximum) {
        ArrayList<Produit> lisProduitASupprimer = new ArrayList<Produit>();
        for (Produit produit : lproposition) {
            if (produit.getPrix() > prixMaximum) {
                lisProduitASupprimer.add(produit);
            }
        }
        // suppression des proposition ne correspondant pas aux critères
        for (Produit produit : lisProduitASupprimer) {
            lproposition.remove(produit);
        }
    }
    
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
    public ArrayList<Produit> getLproposition() {
        return lproposition;
    }

    public void setLproposition(ArrayList<Produit> lproposition) {
        this.lproposition = lproposition;
    }

    public ArrayList<String> getlAgentsRepond() {
        return lAgentsRepond;
    }

    public void setlAgentsRepond(ArrayList<String> lAgentsRepond) {
        this.lAgentsRepond = lAgentsRepond;
    }

    public int getNbRechercheEnvoye() {
        return nbRechercheEnvoye;
    }

    public void setNbRechercheEnvoye(int nbRechercheEnvoye) {
        this.nbRechercheEnvoye = nbRechercheEnvoye;
    }

    public String getTypeAgentClient() {
        return typeAgentClient;
    }

    public void setTypeAgentClient(String typeAgentClient) {
        this.typeAgentClient = typeAgentClient;
    }

    public String getTypeAgentCible() {
        return typeAgentCible;
    }

    public void setTypeAgentCible(String typeAgentCible) {
        this.typeAgentCible = typeAgentCible;
    }

    public long getLimiteDate() {
        return limiteDate;
    }

    public void setLimiteDate(long limiteDate) {
        this.limiteDate = limiteDate;
    }

    public double getLimitePrix() {
        return limitePrix;
    }

    public void setLimitePrix(double limitePrix) {
        this.limitePrix = limitePrix;
    }

    public int getNbReponseReçu() {
        return nbReponseReçu;
    }

    public void setNbReponseReçu(int nbReponseReçu) {
        this.nbReponseReçu = nbReponseReçu;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getNbDemandeAvisProduitEnvoye() {
        return nbDemandeAvisProduitEnvoye;
    }

    public void setNbDemandeAvisProduitEnvoye(int nbDemandeAvisProduitEnvoye) {
        this.nbDemandeAvisProduitEnvoye = nbDemandeAvisProduitEnvoye;
    }

    public int getNbDemandeAvisProduitRecu() {
        return nbDemandeAvisProduitRecu;
    }

    public void setNbDemandeAvisProduitRecu(int nbDemandeAvisProduitRecu) {
        this.nbDemandeAvisProduitRecu = nbDemandeAvisProduitRecu;
    }

    public int getNbDemandeAvisRevendeurEnvoye() {
        return nbDemandeAvisRevendeurEnvoye;
    }

    public void setNbDemandeAvisRevendeurEnvoye(int nbDemandeAvisRevendeurEnvoye) {
        this.nbDemandeAvisRevendeurEnvoye = nbDemandeAvisRevendeurEnvoye;
    }

    public int getNbDemandeAvisRevendeurRecu() {
        return nbDemandeAvisRevendeurRecu;
    }

    public void setNbDemandeAvisRevendeurRecu(int nbDemandeAvisRevendeurRecu) {
        this.nbDemandeAvisRevendeurRecu = nbDemandeAvisRevendeurRecu;
    }

}
