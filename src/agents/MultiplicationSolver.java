package agents;

import utils.Constants;

/**
 * Created by espen on 10/02/15.
 */
public class MultiplicationSolver extends SolverAgent {

    @Override
    protected void setup(){
        name = "Multiplication-Solver-agent";
        descriptionType = Constants.MULTIPLICATION_SOLVING_DESCRIPTION_TYPE;
        descriptionName = "JADE-Multiplication-Solver";

        // Printout a welcome message
        System.out.println("Hello! "+ name + getAID().getName() + " is ready.");

        initiate();
    }

    @Override
    protected double calculate(double firstOperand, double secondOperand) {
        return firstOperand*secondOperand;
    }

}
