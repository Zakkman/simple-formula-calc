package simple_formula_calc;

import java.util.ArrayList;
import java.util.Arrays;

public class Equation {
    
    private char[] formula;
    
    private char variableToSolve;
    private String equationLeftSide;

    private ArrayList<Variable> variableList = new ArrayList<>();
    private ArrayList<Parenthesis> parenthesisList = new ArrayList<>();

    private ArrayList<EquationElement> fullEquation = new ArrayList<>();
    private EquationElement answer;

    public Equation(String formula, char variableToSolve) {
        this.formula = formula.toCharArray();
        this.variableToSolve = variableToSolve;
        this.identifyVars();
    }

    public void setFormula(String formula) {
        this.formula = formula.toCharArray();
    }

    public char[] getFormula() {
        return this.formula;
    }

    public char getVariableToSolve() {
        return this.variableToSolve;
    }

    public ArrayList<Variable> getVariableList() {
        return this.variableList;
    }

    private void identifyVars() {
        
        readingFormula:
        for (int i = 0; i < this.formula.length; i++) {
            if (formula[i] == this.variableToSolve || formula[i] == 'x') {
                continue;
            }        
            for (Variable v : this.variableList) {
                if (v.getLetter() == formula[i]) {
                    continue readingFormula;
                }
            }

            try {
                if (formula[i] == 's' && formula[i + 1] == 'q' && formula[i + 2] == 'r' && formula[i + 3] == 't') {
                    i += 3;
                    continue;
                }
            }catch (Exception e) {
            }

            if ('a' <= formula[i] && formula[i] <= 'z' || 'A' <= formula[i] && formula[i] <= 'Z') {
                this.variableList.add(new Variable(formula[i], Double.NaN));
            }
        }   
    }

    public ArrayList<EquationElement> getFullEquation() {
        return this.fullEquation;
    }

    public void setLeftSide(String leftSide) {
        this.equationLeftSide = leftSide;
    }

    public void addTerm(Term term) {
        this.fullEquation.add(term);
    }

    public void addOperator(Operator oper) {
        this.fullEquation.add(oper);
    }

    public void addParenthesis(Parenthesis par) {
        this.parenthesisList.add(par);
        this.fullEquation.add(par);
    }

    public void initializePars() throws Exception{

        giveParPrecedents();
        findPartnerPars();

        int parIndex = 0;

        for (EquationElement eE : fullEquation) {

            if (parIndex < parenthesisList.size() && eE.equals(parenthesisList.get(parIndex)) ) {
                eE = parenthesisList.get(parIndex);
                parIndex++;
            }
        }
        /*
        for (EquationElement eE : fullEquation) {

            if (eE.getClass().equals(Parenthesis.class) && eE.toString().equals("(")) {
                System.out.println(((Parenthesis)(eE)) + " = " + ((Parenthesis)(eE)).getPrecedence() + " Partner: " + ((Parenthesis)(eE)).getPartnerPar());
            }
        }
        */
    }

    private void giveParPrecedents() {

        int parCount = 0;

        for (Parenthesis par : this.parenthesisList) {
            
            if (par.toString().equals("(")) {
                parCount++;
                par.setPrecedence(parCount);
            }

            if (par.toString().equals(")")) {
                par.setPrecedence(parCount);
                parCount--;
            }
        }
    }

    private void findPartnerPars() throws Exception{

        nextPar:
        for (int parListIndex = 0; parListIndex < parenthesisList.size(); parListIndex++) {

            Parenthesis currentPar = parenthesisList.get(parListIndex);

            if (currentPar.toString().equals("(")) {

                for (int findParIndex = parListIndex; findParIndex < parenthesisList.size(); findParIndex++) {

                    if (parenthesisList.get(findParIndex).toString().equals(")") && currentPar.getPrecedence() == parenthesisList.get(findParIndex).getPrecedence()) {
                        parenthesisList.get(parenthesisList.indexOf(currentPar)).setPartnerPar(parenthesisList.get(findParIndex));
                        continue nextPar;
                    }

                    if (findParIndex == parenthesisList.size() - 1) {
                        throw new Exception("Parenthesis Incomplete");
                    }
                }
            }
        }
    }


    public String equationDisplay() {
        String display = this.equationLeftSide + " ";

        for (EquationElement eE : this.fullEquation) { //&& !Arrays.asList("^", "(", ")", "sqrt", "!").contains(eE.toString())
            if (eE.getClass() == Operator.class && !Arrays.asList("^", "(", ")", "sqrt", "!").contains(eE.toString())) {
                display += " " + eE.toString() + " ";
            }else {
                display += eE.toString(); 
            }
        }
        return display;   
    }

    public Operator getHighestPrecedenceOperator(int startIndex, int endIndex) throws Exception{

        Operator highestPrecedent = new Operator("\n");

        if (fullEquation.get(startIndex).getClass().equals(Parenthesis.class) && fullEquation.get(endIndex).getClass().equals(Parenthesis.class) && endIndex - startIndex == 2) {
            replace(null, null, null, fullEquation.get(startIndex + 1));
            return null;
        }
        
        for (int i = startIndex; i < endIndex; i++) {

            if (fullEquation.get(i).getClass().equals(Operator.class)) {
                if (( (Operator)(fullEquation.get(i) )).getPrecedence() > highestPrecedent.getPrecedence()) {
                    highestPrecedent = ((Operator)(fullEquation.get(i)));
                }
            }
        }

        if (highestPrecedent.toString().equals("\n")) {
            throw new Exception("No operators found from: index[" + startIndex + "]" + " = " + fullEquation.get(startIndex) + " -> to: index[" + endIndex  + "]" + " = " + 
            fullEquation.get(endIndex) + " // at: " + equationDisplay());
        }
        
        return highestPrecedent;
    }

    private int getHighestParPrecedence() throws Exception{
        
        int highestParPrecedence = 0;

        for (EquationElement eE : fullEquation) {

            if (eE.toString().equals("(") && ((Parenthesis)(eE)).getPrecedence() > highestParPrecedence) {
                highestParPrecedence = ((Parenthesis)(eE)).getPrecedence();
            }
        }

        if (highestParPrecedence == 0) {
            throw new Exception("No parenthesis to read/Parenthesis has no precedence");
        }

        return highestParPrecedence;
    }

    public Parenthesis getLatestPar() throws Exception {
        
        int latestParPrecedence = getHighestParPrecedence();
        Parenthesis latestPar = null;

        for (EquationElement eE : fullEquation) {
            
            if (eE.toString().equals("(") && ((Parenthesis)(eE)).getPrecedence() == latestParPrecedence) {
                latestParPrecedence = ((Parenthesis)(eE)).getPrecedence();
                latestPar = (Parenthesis)eE;
            }
        }

        if (latestPar == null) {
            throw new Exception("No par found");
        }

        return latestPar;
    }

    public boolean hasPar() {

        for (EquationElement eE : fullEquation) {
            if (eE.getClass().equals(Parenthesis.class)) {
                return true;
            }
        }
        return false;
    }

    public void operate(Operator oper) throws Exception{

        if (fullEquation.indexOf(oper) < 0) {
            throw new Exception("Possible typing of formula error //-> const/var and const/var mushed together without operator in between");
        }

        Term num1;
        Term num2;
        EquationElement answer;
        
        if (fullEquation.indexOf(oper) != 0 && (fullEquation.get(fullEquation.indexOf(oper) - 1)).getClass() != Operator.class 
        && (fullEquation.get(fullEquation.indexOf(oper) - 1)).getClass() != Parenthesis.class) {
            num1 = (Term)(fullEquation.get(fullEquation.indexOf(oper) - 1));
        }else {
            num1 = null;
        }
        
        if (fullEquation.indexOf(oper) != fullEquation.size() - 1 && (fullEquation.get(fullEquation.indexOf(oper) + 1)).getClass() != Operator.class
        && (fullEquation.get(fullEquation.indexOf(oper) + 1)).getClass() != Parenthesis.class) {
            num2 = (Term)(fullEquation.get(fullEquation.indexOf(oper) + 1));
        }else {
            num2 = null;
        }
        
        if (!oper.toString().equals("sqrt") && !oper.toString().equals("!")) {
            answer = Operation.normalOperate(num1, oper, num2);
        }else if (oper.toString().equals("sqrt")) {
            answer = Operation.specialOperate(num2, oper);
        }else if (oper.toString().equals("!")) {
            answer = Operation.specialOperate(num1, oper);
        }else {
            throw new Exception("Something went wrong! Unrecognized Operator- Cannot Proceed");
        }

        replace(num1, oper, num2, answer);
    }
    
    private void replace(Term num1, Operator oper, Term num2, EquationElement answer) {

        if (oper != null && !oper.toString().equals("sqrt") && !oper.toString().equals("!")) {
            fullEquation.removeAll(Arrays.asList(num1, num2));
            fullEquation.set(fullEquation.indexOf(oper), answer);
        }else if (oper != null && oper.toString().equals("sqrt")) {
            fullEquation.remove(num2);
            fullEquation.set(fullEquation.indexOf(oper), answer);
        }else if (oper != null && oper.toString().equals("!")){
            fullEquation.remove(num1);
            fullEquation.set(fullEquation.indexOf(oper), answer);
        }

        EquationElement answerLeftElement = null;
        EquationElement answerRightElement = null;

        if (fullEquation.indexOf(answer) != fullEquation.size() - 1 && fullEquation.indexOf(answer) != 0) {
            answerLeftElement = fullEquation.get(fullEquation.indexOf(answer) - 1);
            answerRightElement = fullEquation.get(fullEquation.indexOf(answer) + 1);
        }

        if (answerLeftElement == null || answerRightElement == null) {
            return;
        }

        if (answerLeftElement.getClass().equals(Parenthesis.class) && answerRightElement.getClass().equals(Parenthesis.class)) {
            fullEquation.removeAll(Arrays.asList(answerLeftElement, answerRightElement));
        }
   
    }

    public void setAnswer(EquationElement answer) {
        this.answer = answer;
    }

    public EquationElement getAnswer() throws Exception{
        if (this.answer == null) {
            throw new Exception("Answer is empty");
        }
        return this.answer;
    } 
}
