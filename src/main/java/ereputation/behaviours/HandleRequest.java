package ereputation.behaviours;

import common.TypeAgent;
import common.TypeLog;
import ereputation.EReputationAgent;
import ereputation.tools.DesirabiliteCalculator;
import ereputation.tools.QueryBuilder;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.sql.Timestamp;
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
        Logger.getLogger(myAgent.getLocalName()).log(Level.INFO, receptionMessage);
        TypeLog.logEreputation.Info(myAgent.getLocalName()+":"+receptionMessage);
        
        traiterRequete(message);
        block();
    }
    
    /**
     * Méthode principale qui parse le contenu du message 
     * pour déterminer quelle méthode doit être appelée
     * @param message 
     */
    private void traiterRequete(ACLMessage message) {
        String content = message.getContent();
        
        try {
            JSONObject object = (JSONObject) this.parser.parse(content);
            
            if(object.containsKey("demandeAvis")) 
                this.demandeAvis((JSONObject)object.get("demandeAvis"), message.getSender());
            
            if(object.containsKey("demandeDesirabilite"))
                this.demandeDesirabilite((JSONObject)object.get("demandeDesirabilite"), message.getSender());
            
            if(object.containsKey("demandeSolde"))
                this.demandeSolde((JSONObject)object.get("demandeSolde"), message.getSender());
            
            if(object.containsKey("demandeAllSolde"))
                this.demandeAllSolde((JSONObject)object.get("demandeAllSolde"), message.getSender());
            
        } catch (ParseException ex) {
            Logger.getLogger(myAgent.getLocalName()).log(Level.WARNING, "Format de message invalide");
            TypeLog.logEreputation.Erreur(HandleRequest.class+":"+ex.getMessage());
        }
    }
    
    /**
     * Gestion lorsqu'un agent demande un avis
     * @param demandeAvis contenu du message
     * @param agent l'émetteur du message
     * @throws ParseException 
     */
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
        Logger.getLogger(EReputationAgent.class.getName()).log(Level.INFO, envoiMessage);
        TypeLog.logEreputation.Info(myAgent.getLocalName()+":"+envoiMessage);
    }
    
    /**
     * Gestion lorsqu'un agent demande la désirabilité d'un produit
     * @param demandeDesirabilite contenu du message
     * @param agent émetteur du message
     * @throws ParseException 
     */
    private void demandeDesirabilite(JSONObject demandeDesirabilite, AID agent) throws ParseException {
        String type = demandeDesirabilite.get("type").toString(),
               ref = demandeDesirabilite.get("id").toString();
        
        EReputationAgent erep = (EReputationAgent)myAgent;
        
        // recherche en base de données
        ACLMessage messageBDD = erep.sendMessage(ACLMessage.REQUEST, QueryBuilder.selectDateSortie(ref), erep.getBDDAgent(), true);
        JSONArray resultatsBDD = (JSONArray) this.parser.parse(messageBDD.getContent());
        JSONObject resultat = (JSONObject) resultatsBDD.get(0);
        
        JSONObject retourDesirabilite = demandeDesirabilite;
        double desirabilite = DesirabiliteCalculator.execute(resultat.get("DATE_SORTIE").toString());
        retourDesirabilite.put("desirabilite", desirabilite);
        
        JSONObject reponse = new JSONObject();
        reponse.put("retourDesirabilite", retourDesirabilite);
        
        String reponseJSON = reponse.toJSONString();
        
        // envoi de la réponse
        erep.sendMessage(ACLMessage.INFORM, reponseJSON, agent);
        
        String envoiMessage = "(" + myAgent.getLocalName() + ") Message envoyé : " + reponseJSON;
        Logger.getLogger(myAgent.getLocalName()).log(Level.INFO, envoiMessage);
        TypeLog.logEreputation.Info(myAgent.getLocalName()+":"+envoiMessage);
    }

    /**
     * Gestion lorsque le vendeur souhaite savoir s'il est autorisé à effectuer 
     * une période flottante de soldes
     * @param demandeSolde contenu du message
     * @param agent l'émetteur du message
     * @throws ParseException 
     */
    private void demandeSolde(JSONObject demandeSolde, AID agent) throws ParseException{
        
         String dateDebut = demandeSolde.get("date_debut").toString(),
                 dateFin = demandeSolde.get("date_fin").toString();
         int nbJourDemande = Timestamp.valueOf(dateFin).compareTo(Timestamp.valueOf(dateDebut));
         
         EReputationAgent erep = (EReputationAgent)myAgent;
         
          // recherche en base de données
        ACLMessage messageBDD = erep.sendMessage(ACLMessage.REQUEST, QueryBuilder.selectRetourSolde(agent.getName(), dateDebut, dateFin), erep.getBDDAgent(), true);
        JSONArray resultatsBDD = (JSONArray) this.parser.parse(messageBDD.getContent());
        JSONObject resultat = (JSONObject) resultatsBDD.get(0);
        
        
        
        int nbJourConsomme = Integer.parseInt(resultat.get("nbJourSolde").toString());
        int nbJourdemande =  Timestamp.valueOf(dateFin).compareTo(Timestamp.valueOf(dateDebut));
        
        JSONObject retour = new JSONObject();
        retour.put("status", (nbJourConsomme+nbJourdemande)<=10);
        retour.put("joursRestants", Math.abs(nbJourConsomme-10));
        
        JSONObject retourDemandeSolde = new JSONObject();
        retourDemandeSolde.put("resultatSolde", retour);
        
        String reponseJSON = retourDemandeSolde.toJSONString();
        
        // envoi de la réponse
        erep.sendMessage(ACLMessage.INFORM, reponseJSON, agent);
        
        String envoiMessage = "(" + myAgent.getLocalName() + ") Message envoyé : " + reponseJSON;
        Logger.getLogger(myAgent.getLocalName()).log(Level.INFO, envoiMessage);
        TypeLog.logEreputation.Info(myAgent.getLocalName()+":"+envoiMessage);
        
        //ENREGITREMENT DE LA DEMANDE DE SOLE
        if((nbJourConsomme+nbJourdemande)<=10){
            erep.sendMessage(ACLMessage.REQUEST, QueryBuilder.insertSolde(agent.getName(), dateDebut, dateFin), erep.getBDDAgent(), true);
        }
        
    }
    
    /**
     * Gestion lorsqu'un vendeur souhaite connaître les périodes flottantes de
     * soldes de ses concurrents
     * @param demandeSolde contenu du message
     * @param agent l'émetteur du message
     * @throws ParseException 
     */
    private void demandeAllSolde(JSONObject demandeSolde, AID agent) throws ParseException{
        
         String non = demandeSolde.get("vendeur").toString();
         
         EReputationAgent erep = (EReputationAgent)myAgent;
         
          // recherche en base de données
            // recherche en base de données
        ACLMessage messageBDD = erep.sendMessage(ACLMessage.REQUEST, QueryBuilder.selectRetourAllSolde(non), erep.getBDDAgent(), true);
        JSONArray resultatsBDD = (JSONArray) this.parser.parse(messageBDD.getContent());
        
        JSONArray retour = new JSONArray();
        for(Object key : resultatsBDD.toArray()){
            //recupere les resultat un a un
            JSONObject resultat = (JSONObject) key;
            JSONObject solde = (JSONObject) new JSONObject();
            
            solde.put("nom", resultat.get("VENDEUR"));
            solde.put("dateDebut", resultat.get("DATE_START"));
            solde.put("dateFin", resultat.get("DATE_END"));
            
            retour.add(solde);
        }
        
        JSONObject retourDemandeAllSolde = new JSONObject();  
        retourDemandeAllSolde.put("retourPeriodesSoldes", retour);
        
        String reponseJSON = retourDemandeAllSolde.toJSONString();
        
        // envoi de la réponse
        erep.sendMessage(ACLMessage.INFORM, reponseJSON, agent);
        
        String envoiMessage = "(" + myAgent.getLocalName() + ") Message envoyé : " + reponseJSON;
        Logger.getLogger(myAgent.getLocalName()).log(Level.INFO, envoiMessage);
        TypeLog.logEreputation.Info(myAgent.getLocalName()+":"+envoiMessage);
        
    }
    
}
