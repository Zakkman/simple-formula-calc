package simple_formula_calc;

import java.util.Arrays;
import java.util.List;

public class EquationElement { //RETHINK THIS

    private String form;

    public EquationElement (String form) {
        this.form = form;
    }

    public String toString() {
        return this.form;
    }

    public void setForm(String form) {
        this.form = form;
    }

}

class Term extends EquationElement{

    private double value;

    public Term (double value) {
        super(formatValue(value));      
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        super.setForm(formatValue(value));
        this.value = value;
    }

    private static String formatValue(double value) {

        if (value % 1 == 0) { 
            return String.valueOf((int) value);
        } else {
            return String.valueOf(value);
        }
    }

}


class Variable extends Term {

    private char letter;
    
    public Variable(char c, double value) {
        super(value);
        this.letter = c;
    }

    public char getLetter() {
        return this.letter;
    }
}

class Operator extends EquationElement {

    private int precedence;

    public Operator (String symbol) throws Exception{
        super(symbol);

        List<String> pemdas =  Arrays.asList("\n", "+-", "x*/", "^sqrt", "!");

        for (String operIdentifier : pemdas) {

            if (operIdentifier.contains(symbol)) {
                precedence = pemdas.indexOf(operIdentifier) + 1;
            }
        }
    }

    public int getPrecedence() {
        return precedence;
    }

    public void addPrecedence(int amount) {
        this.precedence += amount;
    }
}

class Parenthesis extends EquationElement {

    private EquationElement partnerPar;
    private int parPrecedent;
 
    public Parenthesis (String symbol) throws Exception{
        super(symbol);
    }

    public void setPartnerPar(EquationElement partnerPar) {
        this.partnerPar = partnerPar;
    }

    public EquationElement getPartnerPar() throws Exception{
        if (this.partnerPar == null) {
            throw new Exception("This Parenthesis has no partner");
        }
        return this.partnerPar;
    }

    public void setPrecedence(int parPrecedent) {
        this.parPrecedent = parPrecedent;
    }

    public int getPrecedence() {
        return this.parPrecedent;
    }
}
