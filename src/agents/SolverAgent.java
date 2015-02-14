package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.Constants;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by espen on 10/02/15.
 */
public abstract class SolverAgent extends Agent {

    protected String name;
    protected String descriptionType;
    protected String descriptionName;
    public Date finishedAt = new Date();
    public int solvingTime = 0;

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
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                ACLMessage msg = myAgent.receive(mt);
                if(msg != null) {
                    System.out.println(getAID().getName() + ": CFP message");
                    System.out.println("\t"+msg.getContent());

                    // CFP Message received. Process it
                    //String operator = msg.getContent();
                    ACLMessage reply = msg.createReply();

                    //the agent is busy with an other task
                    if (new Date().before(((SolverAgent)myAgent).finishedAt)) {
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent("busy");
                    }
                    //the agent estimates how long it will take and responds with that
                    else {
                        reply.setPerformative(ACLMessage.PROPOSE);
                        int solvingTime = new Random().nextInt(4) + 1;
                        reply.setContent(String.valueOf(solvingTime));
                        ((SolverAgent) myAgent).solvingTime = solvingTime;
                    }
                    myAgent.send(reply);
                }
                else {
                    block();
                }
            }
        });

        //behaviour for calculating expressions
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
                ACLMessage msg = myAgent.receive(mt);
                if (msg != null) {
                    // ACCEPT_PROPOSAL Message received. Process it
                    System.out.println(getAID().getName() + ": Received accept");

                    //sets time when finished
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, ((SolverAgent) myAgent).solvingTime);
                    ((SolverAgent) myAgent).finishedAt = cal.getTime();

                    //removes extra whitespaces
                    String expression = msg.getContent();
                    expression = expression.replaceAll("[ ]{2,}", " ");
                    final ACLMessage reply = msg.createReply();

                    //extracts operands
                    String[] elements = expression.split(" ");
                    System.out.println("\t" + expression);
                    double firstOperand = Double.parseDouble(elements[1]);
                    double secondOperand = Double.parseDouble(elements[2]);

                    //replies with calculated result
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(String.valueOf(calculate(firstOperand, secondOperand)));
                    System.out.println("\tReply content: " + reply.getContent());
                    myAgent.addBehaviour(new WakerBehaviour(myAgent,(long)((SolverAgent) myAgent).solvingTime*1000) {
                        @Override
                        protected void onWake() {
                            super.onWake();
                            myAgent.send(reply);
                        }
                    });
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
        // Printout a dismissal message
        System.out.println(name+getAID().getName()+" terminating.");
    }
}
