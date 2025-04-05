package simple_formula_calc;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class Calculator {

	static String[][] extractedOptions = new String[1][6];
	static String[] formulaPicked;
	static String solutionVarValues;

	public static void main(String[] args) throws Exception {

		Equation equation;

		try (Scanner formulaOptionsScan = new Scanner(
				Paths.get("C:\\Users\\Zakk\\eclipse-workspace\\my_java_simple_formula_calc\\FormulaOptions.txt"))) {

			while (formulaOptionsScan.hasNextLine()) {
				String currentLine = formulaOptionsScan.nextLine();
				String[] splittedLines;

				if (currentLine.isBlank() || currentLine.isEmpty()) {
					continue;
				}
				splittedLines = currentLine.split("[|]+");

				if (splittedLines[0].equals("Format:")) {
					continue;
				}

				for (int column = 0; column < 6; column++) {
					extractedOptions[extractedOptions.length - 1][column] = splittedLines[column];
				}

				if (formulaOptionsScan.hasNextLine()) {
					String[][] helperList = new String[extractedOptions.length + 1][6];

					for (int row = 0; row < extractedOptions.length; row++) {
						helperList[row] = extractedOptions[row];
					}

					extractedOptions = helperList;
				}
			}

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Format Error", "Error!", JOptionPane.ERROR_MESSAGE);
		}

		programLoop: while (true) {

			formulaPickToVarValueInputLoop: while (true) {

				String formulaOptionsDisplay = "";

				for (int row = 0; row < extractedOptions.length; row++) {
					formulaOptionsDisplay += extractedOptions[row][0] + " ";
					formulaOptionsDisplay += extractedOptions[row][1].split("Formula\\sname/description:\\s")[1]
							+ " >>  ";
					formulaOptionsDisplay += extractedOptions[row][2].split("Formula:\\s")[1] + "\n";
				}

				// WILL ASK THE USER WHICH FORMULA TO USE-
				String optionPicked = JOptionPane.showInputDialog(null, formulaOptionsDisplay, "Choose a Formula: ",
						JOptionPane.QUESTION_MESSAGE);

				if (optionPicked == null) {
					break programLoop;
				}

				String infoDisplay = "";
				boolean optionMatches = false;

				for (String[] row : extractedOptions) {

					if (row[0].equals(optionPicked + ".")) {
						optionMatches = true;

						String[] splittedInfo = row[4].split("\\s/");

						infoDisplay += row[2] + "\n";
						for (String str : splittedInfo) {
							infoDisplay += str + "\n";
						}

						int confirmOption = JOptionPane.showConfirmDialog(null, infoDisplay, "Confirm Formula?",
								JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

						if (confirmOption == JOptionPane.YES_OPTION) {

							formulaPicked = row;

						} else if (confirmOption == JOptionPane.NO_OPTION) {
							continue formulaPickToVarValueInputLoop;
						} else {
							break programLoop;
						}

					}
				}

				if (!optionMatches) {
					int retryFormulaPick = JOptionPane.showConfirmDialog(null,
							"Option not found! Would you like to retry?", "Error!", JOptionPane.YES_NO_OPTION,
							JOptionPane.ERROR_MESSAGE);

					if (retryFormulaPick == JOptionPane.YES_OPTION) {
						continue;
					} else if (retryFormulaPick == JOptionPane.NO_OPTION) {
						break programLoop;
					}

				}

				equation = new Equation(formulaPicked[2].split("Formula:\\s")[1],
						formulaPicked[3].split("Variable\\sto\\ssolve:\\s")[1].charAt(0));

				varValueInputLoop: while (true) {

					String formulaAndVarToSolveDisplay = "Formula: " + String.valueOf(equation.getFormula()) + "  |  "
							+ "Solve for: " + equation.getVariableToSolve() + "\nInputs:";
					String varValuesDisplay = "";

					for (Variable v : equation.getVariableList()) {

						String valueInputStatus = "";

						inputtingCurrentValue: while (true) {
							try {
								String valueInput = (String) JOptionPane.showInputDialog(null,
										formulaAndVarToSolveDisplay + varValuesDisplay + "\n\n" + v.getLetter() + " = ",
										"Input Values", JOptionPane.INFORMATION_MESSAGE, null, null, valueInputStatus);

								if (valueInput == null) {
									continue formulaPickToVarValueInputLoop;
								}

								v.setValue(Double.parseDouble(valueInput));

								varValuesDisplay += "\n" + v.getLetter() + " = " + v.toString();
								valueInputStatus = "";

								break;

							} catch (Exception e) {
								valueInputStatus = "INVALID INPUT!";
								continue inputtingCurrentValue;
							}
						}
					}

					// CONFIRM VARIABLE
					int confirmVarValues = JOptionPane.showConfirmDialog(null,
							formulaAndVarToSolveDisplay + varValuesDisplay, "Confirm Values?",
							JOptionPane.YES_NO_CANCEL_OPTION);

					if (confirmVarValues == JOptionPane.YES_OPTION) {
						solutionVarValues = varValuesDisplay;
						break formulaPickToVarValueInputLoop;

					} else if (confirmVarValues == JOptionPane.NO_OPTION) {
						continue varValueInputLoop;

					} else {
						continue formulaPickToVarValueInputLoop;
					}
				}
			}

			boolean solutionContinued = false;
			solving: while (true) {

				boolean rightSideFound = false;

				readingFormula: for (int formulaIndex = 0; formulaIndex < equation
						.getFormula().length; formulaIndex++) {

					char currentChar = equation.getFormula()[formulaIndex];

					if (currentChar == '=') {
						rightSideFound = true;
						equation.setLeftSide(String.valueOf(equation.getFormula()).substring(0, formulaIndex + 1));
					}

					if (!rightSideFound) {
						continue readingFormula;
					}

					if (currentChar == ' ') {
						continue readingFormula;
					}

					if ('0' <= currentChar && currentChar <= '9') {

						String number = String.valueOf(currentChar);

						for (int numberIndex = formulaIndex; numberIndex < equation
								.getFormula().length; numberIndex++) {

							try {
								Double.parseDouble(number + String.valueOf(equation.getFormula()[numberIndex + 1]));
							} catch (Exception e) {
								equation.addTerm(new Term(Double.parseDouble(number)));
								;
								formulaIndex = numberIndex;

								continue readingFormula;
							}

							if (numberIndex != equation.getFormula().length - 1) {
								number += equation.getFormula()[numberIndex + 1];
							}
						}
					}

					if (currentChar == 's') {
						if (equation.getFormula()[formulaIndex + 1] == 'q'
								&& equation.getFormula()[formulaIndex + 2] == 'r'
								&& equation.getFormula()[formulaIndex + 3] == 't') {
							equation.addOperator(new Operator("sqrt"));
							formulaIndex += 3;
						}
					}

					if (Arrays.asList('+', '-', '*', 'x', '/', '^', '!').contains(currentChar)) {
						equation.addOperator(new Operator(String.valueOf(currentChar)));

					}

					if (Arrays.asList('(', ')').contains(currentChar)) {
						equation.addParenthesis(new Parenthesis(String.valueOf(currentChar)));
					}

					if (currentChar == 'a') {
						if (equation.getFormula()[formulaIndex + 1] == 'n'
								&& equation.getFormula()[formulaIndex + 2] == 's'
								&& equation.getFormula()[formulaIndex + 3] == 'w'
								&& equation.getFormula()[formulaIndex + 4] == 'e'
								&& equation.getFormula()[formulaIndex + 5] == 'r') {
							equation.addTerm((Term) equation.getAnswer());
							formulaIndex += 5;
						}
					}

					if (solutionContinued) {
						continue readingFormula;
					}

					for (Variable v : equation.getVariableList()) {
						if (v.getLetter() == currentChar) {
							equation.addTerm(new Term(v.getValue()));
							;
						}
					}

				}

				equation.initializePars();

				String solutionDisplay = "Formula: " + String.valueOf(equation.getFormula()) + "  |  " + "Solve for: "
						+ equation.getVariableToSolve() + "\nInputs:" + solutionVarValues + "\n\nSolve:\n"
						+ equation.equationDisplay();

				for (int equationIndex = 0; equationIndex < equation.getFullEquation().size(); equationIndex++) {

					if (equation.hasPar()) {
						Parenthesis latestPar = equation.getLatestPar();
						Operator highestPrecedenceOperator = equation.getHighestPrecedenceOperator(
								equation.getFullEquation().indexOf(latestPar),
								equation.getFullEquation().indexOf(latestPar.getPartnerPar()));

						if (highestPrecedenceOperator != null) {
							equation.operate(highestPrecedenceOperator);
						}
						equationIndex = 0;
					} else {
						equation.operate(
								equation.getHighestPrecedenceOperator(0, equation.getFullEquation().size() - 1));
						equationIndex = 0;
					}

					solutionDisplay += "\n" + equation.equationDisplay();
				}

				equation.setAnswer(equation.getFullEquation().get(0));

				JOptionPane.showMessageDialog(null, solutionDisplay, "Result:", JOptionPane.INFORMATION_MESSAGE);

				String continuedSolution = formulaPicked[5].split("Continued\\ssolution/none:\\s")[1];

				if (continuedSolution.equals("none") && !solutionContinued) {
					break solving;
				} else if (!solutionContinued
						&& JOptionPane.showConfirmDialog(null, continuedSolution + "\nAnswer: " + equation.getAnswer(),
								"Continue Solution?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					equation.setFormula(continuedSolution);
					solutionVarValues = "\nAnswer: " + equation.getAnswer();
					equation.getFullEquation().clear();
					solutionContinued = true;
				} else {
					break solving;
				}

			}
		}

	}// MAIN

}// CLASS
