package calculation;

import utils.Constants;

/**
 * Created by espen on 11/02/15.
 */
public class Expression {

    private static int idGenerator = 0;

    public char operator;
    public String firstOperand;
    public String secondOperand;
    public double result;
    private int id;
    public boolean isLeafExpression;

    public Expression(char operator, String firstOperand, String secondOperand) {
        this.operator = operator;
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        id = idGenerator++;
        isLeafExpression = isLeafExpression();
    }

    public Expression(String text) {
        System.out.println("["+text+"]");
        String[] elements = text.split(" ");

        int mid = (elements.length-1)/2;

        operator = elements[0].charAt(0);
        firstOperand = "";
        secondOperand = "";

        for (int i = 1; i < elements.length; i++) {
            if (i <= mid)
                firstOperand += elements[i] + " ";
            else if (i > mid)
                secondOperand += elements[i] + " ";
        }
        id = idGenerator++;
        isLeafExpression = isLeafExpression();
    }

    private boolean isLeafExpression(){
        return !firstOperand.trim().contains(" ") && !secondOperand.trim().contains(" ");
    }
    public int getId(){
        return id;
    }

    public String getDescription(){
        String description;
        switch (operator){
            case '+' : description = Constants.ADDITION_SOLVING_DESCRIPTION_TYPE;break;
            case '-' : description = Constants.SUBTRACTION_SOLVING_DESCRIPTION_TYPE;break;
            case '/' : description = Constants.DIVISION_SOLVING_DESCRIPTION_TYPE;break;
            case '*' : description = Constants.MULTIPLICATION_SOLVING_DESCRIPTION_TYPE;break;
            default: description = Constants.GENERAL_SOLVING_DESCRIPTION_TYPE;
        }
        return description;
    }

    @Override
    public String toString() {
        return "Expression["+id+"] {"+operator + " " + firstOperand + " " + secondOperand+"} isLeaf: " + isLeafExpression;
    }

    public String stringRepresentation() {
        return operator + " " + firstOperand + " " + secondOperand;
    }

    public static void resetIdGenerator() {
        idGenerator = 0;
    }
}
