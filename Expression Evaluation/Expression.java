package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	ArrayList<String> variablesVisited = new ArrayList<String>();
		ArrayList<String> arraysVisited = new ArrayList<String>();
		for (int i = 0; i < expr.length(); i++){
			if (Character.isLetter(expr.charAt(i))){
				boolean nameIsVariable = true;
				String name = "";
				while (i < expr.length()){
					if (Character.isLetter(expr.charAt(i))){
						name = name + expr.charAt(i);
					} else if (expr.charAt(i) == '['){
						//the name must be the name of an array
						nameIsVariable = false;
						break;
					} else {
						//If it touches any character that is not a letter the name has ended and we can exit the loop
						break;
					}
					i++;
				}
				//Check if variable name was already checked and added before
				boolean nameCheckedAlready = false;
				if (nameIsVariable){
					for (int k = 0; k < variablesVisited.size(); k++){
						if (variablesVisited.get(k).equals(name)){
							nameCheckedAlready = true;
						}
					}
					if (!nameCheckedAlready){
						vars.add(new Variable(name));
						variablesVisited.add(name);
					}
				} else {
					for (int k = 0; k < arraysVisited.size(); k++){
						if (arraysVisited.get(k).equals(name)){
							nameCheckedAlready = true;
						}
					}
					if (!nameCheckedAlready){
						arrays.add(new Array(name));
						arraysVisited.add(name);
					}
				}
			}
		}
    }
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	Stack<Float> numbers = new Stack<Float>();
		Stack<Character> operators = new Stack<Character>();
		Stack<String> arrayNames = new Stack<String>();
		char[] arr = expr.toCharArray();
		for (int i = 0; i < arr.length; i++) {
			//If character is a number
			if (Character.isDigit(arr[i])) {
				//We need to check if the number has multiple digits
				String num = "";
				while (i < arr.length) {
					if (Character.isDigit(arr[i])) {
						num += arr[i];
					}
					else {
						i--; // We decrement i because before this point i was pointing to a character after the number, if we dont decrement i, the for loop will double increment i
						break;
					}
					i++;
				}
				numbers.push(new Float(num));
			} else if (Character.isLetter(arr[i])){
				//The name could be a variable name or an array name
				//We have to do separate things for each situation
				//We assume it is a variable
				boolean isVariable = true;
				String name = "";
				while (i < arr.length){
					if (Character.isLetter(arr[i])) {
						name += arr[i];
					}
					else if (arr[i] == '['){
						//If this happens we know it's an array, we can add the name to the arrayname stack for future reference and push an [ and break
						arrayNames.push(name);
						operators.push('[');
						isVariable = false;
						break;
					}
					else {
						//If we hit a character not a letter nor a [ we know the variable name ended and we can break out
						i--; // If we don't do this the for loop will double increment i
						break;
					}
					i++;
				}
				//Now we need to find the value of the variable and add it to the numbers Stack. We only do this is the name is a variable name
				if (isVariable) {
					int value = 0;
					for (int k = 0; k < vars.size(); k++){
						if (vars.get(k).name.equals(name)){
							value = vars.get(k).value;
						}
					}
					numbers.push(new Float(value));
				}
				//If it was an array we do nothing
			} else if (arr[i] == '(') {
				operators.push('(');
			} else if (arr[i] == ')') {
				while (operators.peek() != '('){
					//We need to apply the operator to the two most recent values put in numbers and push it back in
					//MAJOR Note: it should be value2 (op) value 1
					Float value1 = numbers.pop();
					Float value2 = numbers.pop();
					Float result = calculate(operators.pop(), value1, value2); // I can simplify this later
					numbers.push(result);
				}
				operators.pop();
			} else if (arr[i] == '+' || arr[i] == '-' || arr[i] == '*' || arr[i] == '/') {
				char op = arr[i];

				while (!operators.isEmpty() && hasPriority(operators.peek(), op)){
					//We need to pop the recent operator in the Stack, apply it to the most recent numbers, and push it back in
					Float value1 = numbers.pop();
					Float value2 = numbers.pop();
					Float result = calculate(operators.pop(), value1, value2);
					numbers.push(result);
				}
				operators.push(op);
			} else if (arr[i] == ']'){
				//Same thing with () we keep popping operators and applying them to numbers until we find the closest square bracket
				while (operators.peek() != '['){
					Float value1 = numbers.pop();
					Float value2 = numbers.pop();
					Float result = calculate(operators.pop(), value1, value2);
					numbers.push(result);
				}
				//Now we can finally pop the closing ] in the Operator Stack and replace the recent number on the Stack with the value of the array at that index
				operators.pop();
				float topNumber = numbers.pop();
				int index = (int)topNumber;
				String nameOfArray = arrayNames.pop();
				for (int k = 0; k < arrays.size(); k++){
					if (arrays.get(k).name.equals(nameOfArray)){
						int valueToPush = arrays.get(k).values[index];
						numbers.push(new Float(valueToPush));
						break;
					}
				}
			}
		}

		//Now if there is still stuff inside the operators Stack, apply them and return the final value
		while (!operators.isEmpty()){
			Float value1 = numbers.pop();
			Float value2 = numbers.pop();
			Float result = calculate(operators.pop(), value1, value2);
			numbers.push(result);
		}
		return numbers.pop();
    }

	private static boolean hasPriority(char a, char b){
		if (a == '(' || a == ')' || a == '[' || a == ']'){
			return false;
		}
		if ((b == '*' || b == '/') && (a == '+' || a == '-')){
			return false;
		} else {
			return true;
		}
	}

	private static Float calculate(char op, Float a, Float b){
		if (op == '*'){
			return b * a;
		} else if (op == '/'){
			return b / a;
		} else if (op == '+'){
			return b + a;
		} else if (op == '-'){
			return b - a;
		}
		return null;
	}

}
