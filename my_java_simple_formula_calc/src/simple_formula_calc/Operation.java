package simple_formula_calc;

public final class Operation {

    static EquationElement specialOperate(Term num, Operator oper) throws Exception{
        
        if (oper.toString().equals("sqrt")) {
            return new Term(Math.sqrt(num.getValue()));

        }else if (oper.toString().equals("!")) {
            return new Term(factorial(num.getValue()));
        }
        throw new Exception("Something went wrong! Unknown Special Operation");
    }

    static EquationElement normalOperate(Term num1, Operator oper, Term num2) throws Exception {
        
        if (oper.toString().equals("+")) {
            return new Term(num1.getValue() + num2.getValue());

        }else if (oper.toString().equals("-")) {
            return new Term(num1.getValue() - num2.getValue());

        }else if (oper.toString().equals("x") || oper.toString().equals("*")) {
            return new Term(num1.getValue() * num2.getValue());

        }else if (oper.toString().equals("/")) {
            if (num2.getValue() == 0) {
                return new Term(Double.NaN);
            }else {
                return new Term(num1.getValue() / num2.getValue());
            }

        }else if (oper.toString().equals("^")){
            return new Term(Math.pow(num1.getValue(), num2.getValue()));
        }
        throw new Exception("Something went wrong! Unknown Non-Special Operation");
    }
    
    private static double factorial(double num) throws Exception{

        if (num % 1 == 0) {
        
        double fact = 1;

        for (int i = 1; i <= num; i++) {
            fact *= i;
        }

        return fact;

        }else {
            throw new Exception("Can only factorial integers");
        }
    }
}

