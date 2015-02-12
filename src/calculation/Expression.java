package calculation;

/**
 * Created by espen on 11/02/15.
 */
public class Expression {
    public char operator;
    public String firstOperand;
    public String secondOperand;

    public Expression(char operator, String firstOperand, String secondOperand) {
        this.operator = operator;
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
    }

    public Expression(String text) {
        String[] elements = text.split(" ");

        operator = elements[0].charAt(0);
        firstOperand = elements[1];
        secondOperand = elements[2];

    }

    public boolean isLeafExpression(){
        return firstOperand.length()==1 && secondOperand.length()==1;
    }
    private Expression getSubExpression(String subExpression){
        return new Expression(subExpression.charAt(0),subExpression.substring(0,((subExpression.length()-1)/2)),subExpression.substring(subExpression.length()/2));
    }
    public Expression getFirstSubExpression(){
        return getSubExpression(firstOperand);
    }
    public Expression getSecondSubExpression(){
        return getSubExpression(secondOperand);
    }

    @Override
    public String toString() {
        return operator + " " + firstOperand + " " + secondOperand;
    }
}
