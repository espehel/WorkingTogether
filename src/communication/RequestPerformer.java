package communication;

import agents.TaskAdministrator;
import calculation.Expression;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RequestPerformer extends Behaviour {
    private final AID[] solverAgents;
    private final Expression targetMathProblem;
    private AID bestSolver; // The agent who provides the best offer
    private int bestUtility;  // The best offered utility
    private int repliesCnt = 0; // The counter of replies from seller agents
    private MessageTemplate mt; // The template to receive replies
    private int step = 0;

    public RequestPerformer(Agent myAgent, AID[] solverAgents, Expression targetMathProblem) {
        this.solverAgents = solverAgents;
        this.targetMathProblem = targetMathProblem;
        this.myAgent = myAgent;

    }


    @Override
    public void action() {
        System.out.println("step: " + step);
        switch (step) {
            case 0:
                // Send the cfp to all solvers
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                for (int i = 0; i < solverAgents.length; ++i) {
                    cfp.addReceiver(solverAgents[i]);
                }
                //cfp.setContent(targetMathProblem.toString());
                cfp.setConversationId("math-solving");
                cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                myAgent.send(cfp);
                // Prepare the template to get proposals
                mt = MessageTemplate.and(MessageTemplate.MatchConversationId("math-solving"),
                        MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                step = 1;
                break;
            case 1:
                // Receive all proposals/refusals from seller agents
                ACLMessage reply = myAgent.receive(mt);
                if (reply != null) {
                    // Reply received
                    if (reply.getPerformative() == ACLMessage.PROPOSE) {
                        // This is an offer
                        int utility = Integer.parseInt(reply.getContent());
                        if (bestSolver == null || utility < bestUtility) {
                            // This is the best offer at present
                            bestUtility = utility;
                            bestSolver = reply.getSender();
                        }
                    }
                    repliesCnt++;
                    if (repliesCnt >= solverAgents.length) {
                        // We received all replies
                        step = 2; }
                }
                else {
                    block(); }
                break;
            case 2:
                // Send the expression to the best solver
                ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                order.addReceiver(bestSolver);
                order.setContent(targetMathProblem.toString());
                order.setConversationId("math-solver");
                order.setReplyWith("order" + System.currentTimeMillis());
                myAgent.send(order);
                // Prepare the template to get the purchase order reply
                mt = MessageTemplate.and(MessageTemplate.MatchConversationId("math-solver"),
                        MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                step = 3;
                break;
            case 3:
                // Receive the result
                reply = myAgent.receive(mt);
                if (reply != null) {
                    System.out.println("received reply");
                    // Result reply received
                    if (reply.getPerformative() == ACLMessage.INFORM) {
                        // Result Succesfull
                        System.out.println(targetMathProblem + " = " + reply.getContent());
                        targetMathProblem.result = Double.parseDouble(reply.getContent());
                        //((TaskAdministrator)myAgent).setResult(reply.getContent());
                    }
                    step = 4; }
                else {
                    block();
                }
                break; }
    }
    public boolean done() {
        return ((step == 2 && bestSolver == null) || step == 4);
    }
}