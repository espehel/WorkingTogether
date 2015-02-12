package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Constants;

/**
 * Created by espen on 10/02/15.
 */
public abstract class SolverAgent extends Agent {

    protected String name;
    protected String descriptionType;
    protected String descriptionName;

    @Override
    protected void setup(){

        name = "Math-Solver-agent";
        descriptionType = Constants.GENERAL_SOLVING_DESCRIPTION_TYPE;
        descriptionName = "JADE-Math-Solver";

        // Printout a welcome message
        System.out.println("Hello! "+ name + getAID().getName() + " is ready.");

        initiate();

    }

    protected abstract double calculate(double firstOperand, double secondOperand);

    protected void addBehaviours(){
        //behaviour for bidding in auctions
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                ACLMessage msg = myAgent.receive(mt);
                if (msg != null) {
                    // CFP Message received. Process it
                    //String operator = msg.getContent();
                    ACLMessage reply = msg.createReply();


                    //if (operator.charAt(0) == '+') {
                        // The requested book is available for sale. Reply with the price
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContent(String.valueOf(1));
                   // }
                    /*else {
                        // The requested book is NOT available for sale.
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent("cant-calculate");
                    }*/
                    myAgent.send(reply);
                }
                else {
                    block();
                }
            }
        });

        //behaviour for calculating expressions
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
                ACLMessage msg = myAgent.receive(mt);
                if (msg != null) {
                    // ACCEPT_PROPOSAL Message received. Process it
                    String expression = msg.getContent();
                    ACLMessage reply = msg.createReply();

                    String[] elements = expression.split(" ");
                    double firstOperand = Double.parseDouble(elements[1]);
                    double secondOperand = Double.parseDouble(elements[2]);

                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(String.valueOf(calculate(firstOperand,secondOperand)));

                    myAgent.send(reply);
                }
                else {
                    block();
                }
            }
        });
    }

    protected void initiate(){
        // Register the solving service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(descriptionType);
        sd.setName(descriptionName);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        addBehaviours();
    }

    @Override
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Close the GUI
        //myGui.dispose();
        // Printout a dismissal message
        System.out.println(name+getAID().getName()+" terminating.");
    }
}
