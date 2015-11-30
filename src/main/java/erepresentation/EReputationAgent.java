package erepresentation;

import common.SuperAgent;
import common.TypeAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Team EReputation
 */
public class EReputationAgent extends SuperAgent {
    
    /**
     * Méthode de mise en place de l'agent
     */
    @Override
    protected void setup() {
        this.registerService(TypeAgent.EReputation);
        
        this.addBehaviour(new CyclicBehaviour(this) {
            
            @Override
            public void action() {
                ACLMessage message = EReputationAgent.this.receive();
                if(message == null) return;
                
                String receptionMessage = "(" + myAgent.getLocalName() + ") Message reçu : " + message.getContent().replace("\n", "").replace("\t", "") + " de " + message.getSender().getName();
                Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO, receptionMessage);
                EReputationAgent.this.traiterMessage(message);
                block();
            }
        });
    }

    @Override
    protected void traiterMessage(ACLMessage message) {
        String content = message.getContent();
        
        try {
            
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(content);
            
            if(object.containsKey("demandeAvis")) 
                this.traiterDemandeAvis((JSONObject)object.get("demandeAvis"), message.getSender());
            
            if(object.containsKey("donnerAvis"))
                this.traiterDonnerAvis((JSONObject)object.get("donnerAvis"), message.getSender());
            
        } catch (ParseException ex) {
            Logger.getLogger(EReputationAgent.class.getName()).log(Level.WARNING, "Format de message invalide");
        }
    }
    
    private void traiterDemandeAvis(JSONObject demandeAvis, AID agent) {
        // Récupération du type (Fournisseur, Vendeur, Produit)
        String type = demandeAvis.get("type").toString();
        
        switch(type) {
            case TypeAgent.Fournisseur:
            case TypeAgent.Vendeur:
                String nom = demandeAvis.get("nom").toString();
                break;
            
            case "Produit":
                Long idProduit = (Long) demandeAvis.get("id");
                break;
        }
        
        // TODO recherche en base de données
        
        // ajout de la propriété avis
        demandeAvis.put("avis", 3);
        
        // création de la réponse
        JSONObject reponse = new JSONObject();
        reponse.put("retourAvis", demandeAvis);
        
        // transformation de la réponse en JSON
        String reponseJSON = reponse.toString();
        
        // envoi de la réponse
        this.sendMessage(ACLMessage.INFORM, reponseJSON, agent);
        
        String envoiMessage = "(" + this.getLocalName() + ") Message envoyé : " + reponseJSON;
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO, envoiMessage);
    }
    
    private void traiterDonnerAvis(JSONObject donnerAvis, AID agent) {
        // TODO
    }
    
    private void sendMessage(int typeMessage, String contenu, AID destinataire) {
        ACLMessage messsage = new ACLMessage(typeMessage);
        messsage.setContent(contenu);
        messsage.addReceiver(destinataire);
        this.send(messsage);
    }
    
}
