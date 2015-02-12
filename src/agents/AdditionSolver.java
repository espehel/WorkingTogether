package agents;

import utils.Constants;

/**
 * Created by espen on 10/02/15.
 */
public class AdditionSolver extends SolverAgent {

    @Override
    protected void setup(){
        name = "Addition-Solver-agent";
        descriptionType = Constants.ADDITION_SOLVING_DESCRIPTION_TYPE;
        descriptionName = "JADE-Addition-Solver";

        // Printout a welcome message
        System.out.println("Hello! "+ name + getAID().getName() + " is ready.");

        initiate();



    }

    @Override
    protected double calculate(double firstOperand, double secondOperand) {
        return firstOperand+secondOperand;
    }

}
