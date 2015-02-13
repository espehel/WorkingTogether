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

        int mid = (elements.length-1)/2;

        operator = elements[0].charAt(0);
        firstOperand = "";
        secondOperand = "";
        for (int i = 1; i < elements.length; i++) {
            if(i<= mid)
                firstOperand += elements[i]+" ";
            else if (i>mid)
                secondOperand += elements[i]+" ";
        }
    }

    public boolean isLeafExpression(){
        return firstOperand.length()==1 && secondOperand.length()==1;
    }

    @Override
    public String toString() {
        return operator + " " + firstOperand + " " + secondOperand;
    }
}
