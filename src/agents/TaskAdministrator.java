package agents;

import calculation.Expression;
import communication.RequestPerformer;
import gui.TaskAdministratorGUI;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import utils.Constants;

/**
 * Created by espen on 10/02/15.
 */
public class TaskAdministrator extends Agent {

    // The type of math problem to solve
    private Expression targetMathProblem;
    // The list of known math solver agents
    private AID[] solverAgents;

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

        // Get the title of the book to buy as a start-up argument


        /*Object[] args = getArguments();
        if (args != null && args.length > 0) {
            targetMathProblem = (String) args[0];
            System.out.println("Trying to solve " + targetMathProblem);



            // Add a TickerBehaviour that schedules a request to seller agents every minute
            addBehaviour(new TickerBehaviour(this, 60000) {
                protected void onTick() {
                    // Update the list of seller agents
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("math-solving");
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
                    // Perform the request
                    myAgent.addBehaviour(new RequestPerformer(solverAgents, targetMathProblem));
                }
            });
        }*/
    }

    private void runAuction(final String description){


        // Sends a description of tasks to all solvers and wait for their proposal
        addBehaviour(new OneShotBehaviour(this) {
            @Override
            public void action() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType(description);
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
                // Perform the request
                myAgent.addBehaviour(new RequestPerformer(myAgent,solverAgents, targetMathProblem));
            }
        });
    }

    private Expression runCalculation(Expression expression){

        String description;
        switch (expression.operator){
            case '+' : description = Constants.ADDITION_SOLVING_DESCRIPTION_TYPE;break;
            case '-' : description = Constants.SUBTRACTION_SOLVING_DESCRIPTION_TYPE;break;
            case '/' : description = Constants.DIVISION_SOLVING_DESCRIPTION_TYPE;break;
            case '*' : description = Constants.MULTIPLICATION_SOLVING_DESCRIPTION_TYPE;break;
            default: description = Constants.GENERAL_SOLVING_DESCRIPTION_TYPE;
        }

        if(expression.isLeafExpression()) {
            targetMathProblem = expression;
            runAuction(description);
        }else{
            double firstOperand = runCalculation(new Expression(expression.firstOperand)).result;
            double secondOperand = runCalculation(new Expression(expression.secondOperand)).result;
            runCalculation(new Expression(expression.operator, ""+firstOperand,""+secondOperand));
        }

        return expression;
    }

    private Expression parseInput(String text) {
        //retreiving values
        char operator = text.charAt(0);
        int middle = text.length()/2;
        String operand1 = text.substring(0,middle-1);
        String operand2 = text.substring(middle);

        return new Expression(text);
        //return new Expression(operator,operand1,operand2);

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
        Expression base = parseInput(input.getText());

        setResult(""+runCalculation(base).result);
        //result.setText(String.valueOf(runCalculation(base)));

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
