package client;

/*
 * Auteur : Aymeric ZANIRATO
 * Email: aymeric@zanirato.fr
 */
import client.behaviours.Econome;
import client.behaviours.Presse;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import common.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import java.util.Date;

/**
 *
 * @author Aymeric
 */
public class Client extends SuperAgent {

    private final String monService = TypeAgent.Client;
    private ArrayList<Produit> lproposition;
    private ArrayList<String> lAgentsRepond;
    private int nbRechercheEnvoye = 0;
    private String typeAgentClient;
    private String typeAgentCible;
    
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
        int quantite = Integer.parseInt(arguments[5].toString());
        String typeRecherche = arguments[6].toString();
        this.lproposition = new ArrayList<Produit>();
        this.lAgentsRepond = new ArrayList<String>();

        // enregistrement du service
        registerService(monService);
        
        // écoute
        if(typeAgentClient.equals(TypeAgentClient.Econome)){
            addBehaviour(new Econome(this));
        }
        
        // écoute
        if(typeAgentClient.equals(TypeAgentClient.Presse)){
            addBehaviour(new Presse(this));
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
        } catch (FIPAException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
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
        AID[] agent = Jade.searchDF(this, typeAgentCible);
        for (AID f : agent) {
            String message = jeCherche.toString();
            Jade.envoyerMessage(this, ACLMessage.REQUEST, f, message);
            nbRechercheEnvoye++;
            Jade.loggerEnvoi(nomAgent(f.getName()), message);
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
        AID[] agent = Jade.searchDF(this, typeAgentCible);
        for (AID f : agent) {
            String message = jeChercheReference.toString();
            Jade.envoyerMessage(this, ACLMessage.REQUEST, f, message);
            nbRechercheEnvoye++;
            Jade.loggerEnvoi(nomAgent(f.getName()), message);
        }
    }

    public void jeChoisis(Produit p) {

        AID aid = new AID(p.getProvenance());

        // construction de l'objet JSON à envoyé
        JSONObject jeChoisi = new JSONObject();
        jeChoisi.put("jeChoisis", p.getJSONObject());

        // envoi du message + afficahge dans les logs
        Jade.envoyerMessage(this, ACLMessage.ACCEPT_PROPOSAL, aid, jeChoisi.toString());
        Jade.loggerEnvoi(nomAgent(p.getProvenance()), jeChoisi.toString());
    }

    /**
     * Méthode d'envoi d'avis sur un agent fournisseur ou vendeur
     *
     * @param adresseAgentErep adresse de l'agent ereputation :
     * Erep@10.10.135.8/JADE
     * @param nomAgent nom de l'agent sur lequel on donne notre l'avis
     * @param typeAgent type de l'agent sur lequel on donne notre l'avis
     */
    public void donneAvis(String adresseAgentErep, String typeAgent, String nomAgent) {
        AID aid = new AID(adresseAgentErep);

        int avis = 0;

        // construction de l'objet JSON à envoyé
        JSONObject donneAvis = new JSONObject();
        JSONObject contenu = new JSONObject();
        contenu.put("type", typeAgent);
        contenu.put("nom", nomAgent);
        contenu.put("avis", avis);
        donneAvis.put("donneAvis", contenu);

        // envoi du message + afficahge dans les logs
        Jade.envoyerMessage(this, ACLMessage.INFORM, aid, donneAvis.toString());
        Jade.loggerEnvoi(nomAgent(adresseAgentErep), donneAvis.toString());
    }

    public void donneAvisProduit(String adresseAgentErep, String idProduit) {
        AID aid = new AID(adresseAgentErep);

        int avis = 0;

        // construction de l'objet JSON à envoyé
        JSONObject donneAvis = new JSONObject();
        JSONObject contenu = new JSONObject();
        contenu.put("type", "Produit");
        contenu.put("id", idProduit);
        contenu.put("avis", avis);
        donneAvis.put("donneAvis", contenu);

        // envoi du message + afficahge dans les logs
        Jade.envoyerMessage(this, ACLMessage.INFORM, aid, donneAvis.toString());
        Jade.loggerEnvoi(nomAgent(adresseAgentErep), donneAvis.toString());
    }

    /**
     * Méthode de demande d'avis sur un vendeur ou un fournisseur
     *
     * @param adresseAgentErep adresse de l'agent ereputation :
     * Erep@10.10.135.8/JADE
     * @param typeAgent type de l'agent auquel on veux l'avis
     * @param nomAgent nom de l'agent auquel on veux l'avis
     */
    public void demandeAvis(String adresseAgentErep, String typeAgent, String nomAgent) {
        AID aid = new AID(adresseAgentErep);

        // construction de l'objet JSON à envoyé
        JSONObject demandeAvis = new JSONObject();
        JSONObject contenu = new JSONObject();
        contenu.put("type", typeAgent);
        contenu.put("nom", nomAgent);
        demandeAvis.put("demandeAvis", contenu);

        // envoi du message + afficahge dans les logs
        Jade.envoyerMessage(this, ACLMessage.REQUEST, aid, demandeAvis.toString());
        Jade.loggerEnvoi(nomAgent(adresseAgentErep), demandeAvis.toString());
    }

    public void demandeAvisProduit(String adresseAgentErep, String idProduit) {
        AID aid = new AID(adresseAgentErep);

        // construction de l'objet JSON à envoyé
        JSONObject demandeAvis = new JSONObject();
        JSONObject contenu = new JSONObject();
        contenu.put("type", "Produit");
        contenu.put("id", idProduit);
        demandeAvis.put("demandeAvis", contenu);

        // envoi du message + afficahge dans les logs
        Jade.envoyerMessage(this, ACLMessage.REQUEST, aid, demandeAvis.toString());
        Jade.loggerEnvoi(nomAgent(adresseAgentErep), demandeAvis.toString());
    }

    public void demandeReputation(String adresseAgentErep, String idProduit) {
        AID aid = new AID(adresseAgentErep);

        // construction de l'objet JSON à envoyé
        JSONObject demandeReputation = new JSONObject();
        JSONObject contenu = new JSONObject();
        contenu.put("type", "Produit");
        contenu.put("id", idProduit);
        demandeReputation.put("demandeReputation", contenu);

        // envoi du message + afficahge dans les logs
        Jade.envoyerMessage(this, ACLMessage.REQUEST, aid, demandeReputation.toString());
        Jade.loggerEnvoi(nomAgent(adresseAgentErep), demandeReputation.toString());
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
        Jade.loggerReception(nomAgent(message), message.getContent());

    }

    // **************************************************************** //
    //
    //  Méthodes d'affichage
    //
    // **************************************************************** //
    public void afficherAchat(JSONObject jsonObj, ACLMessage message) {

        Jade.loggerReception(nomAgent(message), message.getContent());

        String date = jsonObj.get("date").toString().replace("\\", "");
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
        sb.append(date);

        Jade.loggerAchat(sb.toString());

        // laisser avis
        lproposition.clear();
        lAgentsRepond.clear();

        // arrêt de l'agent 
        doDelete();
    }

    public void afficherRaison(JSONObject obj, ACLMessage message) {
        StringBuilder sb = new StringBuilder("Commande impossible chez : ");
        sb.append(nomAgent(message));
        sb.append("\n");
        sb.append("Raison : ");
        sb.append(obj.get("raison").toString());
        Jade.loggerCommandeAnnulee(sb.toString());
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
        for (Produit prod : lproposition) {
            if (prod.equals(produitARetirer)) {
                lproposition.remove(prod);
            }
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

    public boolean offreInteressante(Double prixMaximum) {
        boolean res = false;

        for (Produit produit : lproposition) {
            if (produit.getPrix() < prixMaximum) {
                res = true;
            }
        }
        return res;
    }
    
    
    public boolean offreInteressante(Date dateMaximum) {
        boolean res = false;

        for (Produit produit : lproposition) {
            if (produit.getDateLivraison().before(dateMaximum)) {
                res = true;
            }
        }
        return res;
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
            if (produit.getDateLivraison().before(produitChoisi.getDateLivraison())) {
                produitChoisi = produit;
            }
        }
        return produitChoisi;
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
    
    
    
}
