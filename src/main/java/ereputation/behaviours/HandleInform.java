package ereputation.behaviours;

import common.TypeAgent;
import common.TypeLog;
import ereputation.EReputationAgent;
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
 * @author Team E-réputation
 */
public class HandleInform extends CyclicBehaviour {
    private JSONParser parser;
    
    public HandleInform(Agent agent) {
        super(agent);
        this.parser = new JSONParser();
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage message = myAgent.receive(mt);
        if(message == null) return;
        
        String receptionMessage = "(" + myAgent.getLocalName() + ") Message reçu : " + message.getContent().replace("\n", "").replace("\t", "") + " de " + message.getSender().getName();
        Logger.getLogger(myAgent.getLocalName()).log(Level.INFO, receptionMessage);
        TypeLog.logEreputation.Info(myAgent.getLocalName()+":"+receptionMessage);
        
        traiterInformation(message);
        block();
    }
    
    /**
     * Méthode principale qui parse le contenu du message 
     * pour déterminer quelle méthode doit être appelée
     * @param message 
     */
    private void traiterInformation(ACLMessage message) {
        String content = message.getContent();
        
        try {
            JSONObject object = (JSONObject) this.parser.parse(content);
            
            if(object.containsKey("donneAvis")) 
                this.donneAvis((JSONObject)object.get("donneAvis"), message.getSender());
            
            if(object.containsKey("achatEffectue"))
                this.achatEffectue((JSONObject)object.get("achatEffectue"), message.getSender());
            
            if(object.containsKey("venteEffectuee")) 
                this.venteEffectuee((JSONObject)object.get("venteEffectuee"), message.getSender());
            
        } catch (ParseException ex) {
            Logger.getLogger(myAgent.getLocalName()).log(Level.WARNING, "Format de message invalide " + ex);
            TypeLog.logEreputation.Erreur(HandleInform.class+":"+ex.getMessage());
        }
    }
    
    /**
     * Gestion lorsqu'un agent donne un avis
     * @param donneAvis contenu du message
     * @param agent l'émetteur du message
     * @throws ParseException 
     */
    private void donneAvis(JSONObject donneAvis, AID agent) throws ParseException {
        String nom = "";
        String type = donneAvis.get("type").toString();
        
        Long avis = (Long) donneAvis.get("avis");
        
        switch(type) {
            case TypeAgent.Fournisseur:
            case TypeAgent.Vendeur:
                nom = donneAvis.get("nom").toString();
                break;
            
            case EReputationAgent.Produit:
                nom = donneAvis.get("id").toString();
                break;
        }
        
        EReputationAgent erep = (EReputationAgent)myAgent;
        
        // ajout en base de données
        erep.sendMessage(ACLMessage.INFORM, QueryBuilder.insertAvis(agent.getLocalName(), type+"_"+nom, avis), erep.getBDDAgent());
        
        Logger.getLogger(myAgent.getLocalName()).log(Level.INFO, myAgent.getLocalName()+": Enregistrement en base de données :"+donneAvis.toString());
        TypeLog.logEreputation.Info(myAgent.getLocalName()+": Enregistrement en base de données :"+donneAvis.toJSONString());
    }
    
    /**
     * Gestion lorsqu'un client nous informe d'un achat (dans le cadre d'un négociation)
     * @param achatEffectue contenu du message
     * @param agent l'émetteur du message
     * @throws ParseException 
     */
    private void achatEffectue(JSONObject achatEffectue, AID agent) throws ParseException {
        boolean success = (boolean) achatEffectue.get("success");
        
        String comportement = achatEffectue.get("comportement").toString();
        long nb_negociations = (Long) achatEffectue.get("nbNegociations");
        
        EReputationAgent erep = (EReputationAgent)myAgent;
        
        // ajout en base de données
        erep.sendMessage(ACLMessage.INFORM, QueryBuilder.insertNegociation(comportement, success, nb_negociations), erep.getBDDAgent());
        
        Logger.getLogger(myAgent.getLocalName()).log(Level.INFO, myAgent.getLocalName()+": Enregistrement en base de données :"+achatEffectue.toString());
        TypeLog.logEreputation.Info(myAgent.getLocalName()+": Enregistrement en base de données :"+achatEffectue.toJSONString());
    }
    
    /**
     * Gestion lorsqu'un vendeur nous informe d'une vente
     * @param demandeSolde contenu du message
     * @param agent l'émetteur du message
     * @throws ParseException 
     */
    private void venteEffectuee(JSONObject demandeSolde, AID agent) throws ParseException{
        
         String idVente = demandeSolde.get("id").toString();
         
         EReputationAgent erep = (EReputationAgent)myAgent;
         
        // recherche en base de données
        ACLMessage messageBDD = erep.sendMessage(ACLMessage.REQUEST, QueryBuilder.verifierVente(idVente), erep.getBDDAgent(), true);
        JSONArray resultatsBDD = (JSONArray) this.parser.parse(messageBDD.getContent());
        JSONObject resultat = (JSONObject) resultatsBDD.get(0);
        
        
        //TO DO deux cas, verfification OK / KO
        if(resultat.get("statusVente").toString().equals("1")){
            //TO DO definir le cas ok
        }else{
            //TO DO definir le cas ko
        }
        
        Logger.getLogger(myAgent.getLocalName()).log(Level.INFO, myAgent.getLocalName()+": verification de la vente :"+resultat);
        TypeLog.logEreputation.Info(myAgent.getLocalName()+": verification de la vente :"+resultat);
        
    }
}
