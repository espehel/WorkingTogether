package agents;

import utils.Constants;

/**
 * Created by espen on 10/02/15.
 */
public class DivisionSolver extends SolverAgent {

    @Override
    protected void setup(){
        name = "Division-Solver-agent";
        descriptionType = Constants.DIVISION_SOLVING_DESCRIPTION_TYPE;
        descriptionName = "JADE-Division-Solver";

        // Printout a welcome message
        System.out.println("Hello! " + name + getAID().getName() + " is ready.");

        initiate();
    }

    @Override
    protected double calculate(double firstOperand, double secondOperand) {
        return (secondOperand == 0) ? 0 : firstOperand/secondOperand;
    }
}
