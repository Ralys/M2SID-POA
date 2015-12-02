package vendeur;

import vendeur.behaviours.OfferRequests;
import vendeur.behaviours.PurchaseProduct;
import common.SuperAgent;
import common.TypeAgent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author Aurélien
 */
public class VendeurAgent extends SuperAgent {

    private AID BDDAgent;

    @Override
    protected void setup() {
        this.registerService(TypeAgent.Vendeur);

        this.addBehaviour(new OfferRequests(this));
        this.addBehaviour(new PurchaseProduct(this));
    }

    public AID getBDDAgent() {
        if (this.BDDAgent == null) {
            this.BDDAgent = findBDDAgent();
        }

        return this.BDDAgent;
    }

    private AID findBDDAgent() {
        //Temporaire
        return new AID("BDD", AID.ISLOCALNAME);
    }

    public ACLMessage sendMessage(int typeMessage, String contenu, AID destinataire) {
        return this.sendMessage(typeMessage, contenu, destinataire, false);
    }

    public ACLMessage sendMessage(int typeMessage, String contenu, AID destinataire, boolean withResponse) {
        ACLMessage messsage = new ACLMessage(typeMessage);
        messsage.setContent(contenu);
        messsage.addReceiver(destinataire);
        this.send(messsage);

        if (!withResponse) {
            return null;
        }

        MessageTemplate mt = MessageTemplate.MatchSender(destinataire);
        ACLMessage messageReponse = this.blockingReceive(mt);
        return messageReponse;
    }

}
