package ereputation.behaviours;

import common.TypeAgent;
import ereputation.EReputationAgent;
import static ereputation.EReputationAgent.logEreputation;
import ereputation.tools.ReputationCalculator;
import ereputation.tools.QueryBuilder;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Team EReputation
 */
public class HandleRequest extends CyclicBehaviour {
    
    private JSONParser parser;
    
    public HandleRequest(Agent agent) {
        super(agent);
        this.parser = new JSONParser();
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        ACLMessage message = myAgent.receive(mt);
        if(message == null) return;
        
        String receptionMessage = "(" + myAgent.getLocalName() + ") Message reçu : " + message.getContent().replace("\n", "").replace("\t", "") + " de " + message.getSender().getName();
        //Logger.getLogger(myAgent.getLocalName()).log(Level.INFO, receptionMessage);
        logEreputation.Info(myAgent.getLocalName()+":"+receptionMessage);
        
        traiterRequete(message);
        block();
    }
    
    private void traiterRequete(ACLMessage message) {
        String content = message.getContent();
        
        try {
            JSONObject object = (JSONObject) this.parser.parse(content);
            
            if(object.containsKey("demandeAvis")) 
                this.demandeAvis((JSONObject)object.get("demandeAvis"), message.getSender());
            
            if(object.containsKey("demandeReputation"))
                this.demandeReputation((JSONObject)object.get("demandeReputation"), message.getSender());
        } catch (ParseException ex) {
            Logger.getLogger(myAgent.getLocalName()).log(Level.WARNING, "Format de message invalide");
            logEreputation.Erreur(HandleRequest.class+":"+ex.getMessage());
        }
    }
    
    private void demandeAvis(JSONObject demandeAvis, AID agent) throws ParseException {
        String nom = "";
        String type = demandeAvis.get("type").toString();
        
        switch(type) {
            case TypeAgent.Fournisseur:
            case TypeAgent.Vendeur:
                nom = demandeAvis.get("nom").toString();
                break;
            
            case EReputationAgent.Produit:
                nom = demandeAvis.get("id").toString();
                break;
        }
        
        EReputationAgent erep = (EReputationAgent)myAgent;
        
        // recherche en base de données
        ACLMessage messageBDD = erep.sendMessage(ACLMessage.REQUEST, QueryBuilder.selectAvis(type, nom), erep.getBDDAgent(), true);
        JSONArray resultatsBDD = (JSONArray) this.parser.parse(messageBDD.getContent());
        JSONObject resultat = (JSONObject) resultatsBDD.get(0);
        
        JSONObject retourAvis = demandeAvis;
        retourAvis.put("avis", resultat.get("AVIS"));
        
        JSONObject reponse = new JSONObject();
        reponse.put("retourAvis", retourAvis);
        
        String reponseJSON = reponse.toJSONString();
        
        // envoi de la réponse
        erep.sendMessage(ACLMessage.INFORM, reponseJSON, agent);
        
        String envoiMessage = "(" + myAgent.getLocalName() + ") Message envoyé : " + reponseJSON;
        //Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO, envoiMessage);
        logEreputation.Info(myAgent.getLocalName()+":"+envoiMessage);
    }
    
    private void demandeReputation(JSONObject demandeReputation, AID agent) throws ParseException {
        String type = demandeReputation.get("type").toString(),
               ref = demandeReputation.get("id").toString();
        
        EReputationAgent erep = (EReputationAgent)myAgent;
        
        // recherche en base de données
        ACLMessage messageBDD = erep.sendMessage(ACLMessage.REQUEST, QueryBuilder.selectDateSortie(ref), erep.getBDDAgent(), true);
        JSONArray resultatsBDD = (JSONArray) this.parser.parse(messageBDD.getContent());
        JSONObject resultat = (JSONObject) resultatsBDD.get(0);
        
        JSONObject retourReputation = demandeReputation;
        double reputation = ReputationCalculator.execute(resultat.get("DATE_SORTIE").toString());
        retourReputation.put("reputation", reputation);
        
        JSONObject reponse = new JSONObject();
        reponse.put("retourReputation", retourReputation);
        
        String reponseJSON = reponse.toJSONString();
        
        // envoi de la réponse
        erep.sendMessage(ACLMessage.INFORM, reponseJSON, agent);
        
        String envoiMessage = "(" + myAgent.getLocalName() + ") Message envoyé : " + reponseJSON;
        //Logger.getLogger(myAgent.getLocalName()).log(Level.INFO, envoiMessage);
        logEreputation.Info(myAgent.getLocalName()+":"+envoiMessage);
    }

}
