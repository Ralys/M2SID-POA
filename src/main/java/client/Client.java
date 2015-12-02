package client;

/*
 * Auteur : Aymeric ZANIRATO
 * Email: aymeric@zanirato.fr
 */
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import common.*;

/**
 *
 * @author Aymeric
 */
public class Client extends SuperAgent {

    private final String monService = TypeAgent.Client;
    private ArrayList<Produit> lproposition;
    private ArrayList<String> lAgentsRepond;
    private int nbRechercheEnvoye = 0;
    private final SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
    private String typeAgentClient;
    private String typeAgentCible;

    protected void setup() {

        // initailisation des attributs
        Object[] arguments = getArguments();
        typeAgentClient = arguments[0].toString();
        typeAgentCible = arguments[1].toString();
        String typeProduit ="";
        if(arguments[2] != null){
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

        // écoute des messages
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    traiterMessage(msg);
                    block();
                }
            }
        });

        if (typeRecherche.equalsIgnoreCase("true")) {
            // on lance la recherche
            this.jeCherche(typeAgentCible, typeProduit, recherche, quantite);
        }else{
            this.jeChercheReference(typeAgentCible, reference , quantite);
        }

    }

    public void traiterMessage(ACLMessage message) {

        try {
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(message.getContent());

            if (object.containsKey("jePropose")) {
                JSONArray array = (JSONArray) object.get("jePropose");
                ajouterProposition(array, message);
                effectuerChoix();
            }

            if (object.containsKey("commandeOk")) {
                JSONObject obj = (JSONObject) object.get("commandeOk");
                afficherAchat(obj, message);
                // laisser avis erep
            }

            if (object.containsKey("commandePasOK")) {
                JSONObject obj = (JSONObject) object.get("commandePasOK");
                afficherRaison(obj, message);
            }
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
            Jade.envoyerMessage(this, f, message);
            nbRechercheEnvoye++;
            Jade.loggerEnvoi(message);
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
            Jade.envoyerMessage(this, f, message);
            nbRechercheEnvoye++;
            Jade.loggerEnvoi(message);
        }
    }

    public void jeChoisis(Produit p) {

        AID aid = new AID(p.getProvenance());

        // construction de l'objet JSON à envoyé
        JSONObject jeChoisi = new JSONObject();
        jeChoisi.put("jeChoisis", p.getJSONObject());

        // envoi du message + afficahge dans les logs
        Jade.envoyerMessage(this, aid, jeChoisi.toString());
        Jade.loggerEnvoi(jeChoisi.toString());
    }

    public void ajouterProposition(JSONArray array, ACLMessage message) {

        String provenance = message.getSender().getName();

        for (Object obj : array.toArray()) {
            JSONObject jsonObject = (JSONObject) obj;
            String idProduit = jsonObject.get("idProduit").toString();
            String nomProduit = jsonObject.get("nomProduit").toString();
            int quantite = Integer.parseInt(jsonObject.get("quantite").toString());
            double prix = Double.parseDouble(jsonObject.get("prix").toString());
            // supression des backslashes ajoutés autmatiquement au passe en json
            String date = jsonObject.get("date").toString().replace("\\", "");
            Date dateLivraison;

            try {
                dateLivraison = formater.parse(date);
                Produit p = new Produit(provenance, idProduit, nomProduit, quantite, prix, dateLivraison);
                lproposition.add(p);
            } catch (ParseException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        lAgentsRepond.add(provenance);
        Jade.loggerReception(message.getContent());

    }

    public void effectuerChoix() {
        // verification que toutes les propositions sont reçu
        if (nbRechercheEnvoye == lAgentsRepond.size()) {

            switch (typeAgentClient) {
                case TypeAgentClient.Econome:
                    jeChoisis(moinsCher());
                    break;

                case TypeAgentClient.Presse:
                    jeChoisis(plusTot());
                    break;

                default:
                    System.err.println("Type d'agent client inconnu !");
            }
        }

    }

    public void afficherAchat(JSONObject jsonObj, ACLMessage message) {

        String date = jsonObj.get("date").toString().replace("\\", "");
        StringBuilder sb = new StringBuilder("Achat effectué chez : ");
        sb.append(message.getSender().getName());
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

        lproposition.clear();
        lAgentsRepond.clear();

        // arrêt de l'agent 
        doDelete();
    }

    public void afficherRaison(JSONObject obj, ACLMessage message) {
        StringBuilder sb = new StringBuilder("Commande impossible chez : ");
        sb.append("message.getSender().getName()\n");
        sb.append("Raison : ");
        sb.append(obj.get("raison").toString());
        Jade.loggerCommandeAnnulee(sb.toString());

        // retirer la proposition
        // choisir la meilleur proposition suivante si il y en a
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
            if (produit.getDateLivraison().before(produitChoisi.getDateLivraison())) {
                produitChoisi = produit;
            }
        }
        return produitChoisi;
    }

    protected void takeDown() {
        // on se retire du registre de service afin q'un autre
        // agent du même nom puisse se lancer
        Jade.deRegisterService(this);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // deverrouillage du bouton de validation
                // FXMLController.btnValider.setDisable(false);
            }
        });

    }

}
