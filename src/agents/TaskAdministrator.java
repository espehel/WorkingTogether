package agents;

import calculation.Expression;
import communication.RequestPerformer;
import gui.TaskAdministratorGUI;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by espen on 10/02/15.
 */
public class TaskAdministrator extends Agent {

    // The type of math problem to solve
    private Expression targetMathProblem;
    // The list of known math solver agents
    private AID[] solverAgents;
    //map of expressionCatalogue
    private Map<Integer,Expression> expressionCatalogue = new HashMap<Integer, Expression>();
    //queue for the expressionCatalogue
    private Queue<Expression> expressionQueue = new ArrayDeque<Expression>();

    private String name;

    private static TaskAdministrator instance;

    // Put agent initializations here
    protected void setup() {

        name = "Task-Administrator-agent";

        // Printout a welcome message
        System.out.println("Hello! "+ name + getAID().getName() + " is ready.");


        //this class acts as a controller for the gui. This makes sre the gui gets the controller instance that the jade.Boot creates
        instance = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                TaskAdministratorGUI.run((String[]) getArguments());
            }
        }).start();
    }

    public void runAuction(final Expression expression){


        // Sends a description of tasks to all solvers and wait for their proposal
        addBehaviour(new OneShotBehaviour(this) {
            @Override
            public void action() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType(expression.getDescription());
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    solverAgents = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        solverAgents[i] = result[i].getName();
                    }

                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }
                //perform the request with this expression
                myAgent.addBehaviour(new RequestPerformer(myAgent, solverAgents, expression));

            }
        });
    }

    public Expression getNextExpression(){
        if(expressionQueue.isEmpty()){
            setResult(String.valueOf(expressionCatalogue.get(0).result));
            reset();
            return null;
        }

        Expression next = expressionQueue.poll();

        if(!next.isLeafExpression){
            next.firstOperand = String.valueOf(expressionCatalogue.get(Integer.parseInt(next.firstOperand)).result);
            next.secondOperand = String.valueOf(expressionCatalogue.get(Integer.parseInt(next.secondOperand)).result);
        }

        return next;
    }

    private void reset() {
        Expression.resetIdGenerator();
        expressionCatalogue.clear();
        expressionQueue.clear();
    }

    private Expression buildTree(Expression expression) {
        // Leafnodes are placed in the hashmap
        if (expression.isLeafExpression) {
            expressionCatalogue.put(expression.getId(), expression);
            expressionQueue.add(expression);
        } else {

            // normal nodes have the id to their childs as the operands
            expression.firstOperand = String.valueOf(buildTree(new Expression(expression.firstOperand)).getId());
            expression.secondOperand = String.valueOf(buildTree(new Expression(expression.secondOperand)).getId());
            expressionCatalogue.put(expression.getId(), expression);
            expressionQueue.add(expression);
        }
        System.out.println(expression);
        return expression;

    }

    //-----------------------------------USER INTERFACE CONTROLS--------------------------------------------------------------

    public static TaskAdministrator getInstance(){
        if(instance==null)
            throw new IllegalStateException();
        else
            return instance;
    }

    @FXML
    TextField input;

    @FXML
    Button calculate;

    @FXML
    Label result;


    @FXML
    private void initialize(){


    }
    @FXML
    private void handleButtonAction() {
        buildTree(new Expression(input.getText()));
        runAuction(expressionQueue.poll());
    }
    public void setResult(final String result){

        final Label label = this.result;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                label.setText(result);
            }
        });
    }

}
