package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		Node ptr1 = poly1;
		Node ptr2 = poly2;
		Node total = new Node(0, 0, null);
		Node finalResult = total;
		
		
		while (ptr1 != null || ptr2 != null) {

			if (ptr2 != null && ptr1 != null) {
				
				if (ptr1.term.degree > ptr2.term.degree) {
					total.term.degree = ptr2.term.degree;
					total.term.coeff = ptr2.term.coeff;
					ptr2 = ptr2.next;
				} 
				
				else if (ptr2.term.degree > ptr1.term.degree) {
					total.term.degree = ptr1.term.degree;
					total.term.coeff = ptr1.term.coeff;
					ptr1 = ptr1.next;
				} 
				
				else {
					total.term.coeff = ptr1.term.coeff + ptr2.term.coeff;
					total.term.degree = ptr1.term.degree;
					ptr1 = ptr1.next;
					ptr2 = ptr2.next;
				}
				
				if (total.term.coeff != 0) {
					total.next = new Node(0, 0, null);
					total = total.next;
				}
			} 
			
			else if (ptr1 != null && ptr2 == null) {
				total.term.degree = ptr1.term.degree;
				total.term.coeff = ptr1.term.coeff;
				ptr1 = ptr1.next;
			} 
			
			else if (ptr2 != null && ptr1 == null) {
				total.term.degree = ptr2.term.degree;
				total.term.coeff = ptr2.term.coeff;
				ptr2 = ptr2.next;
			}
			
			if (ptr1 == null && ptr2 == null) {
				break;
			}
		}
		
		if (finalResult.next == null) { 
			if (finalResult.term.coeff == 0) {
				finalResult = null;
			}	
		}
		
		return finalResult;
	}
	
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		Node ptr1;
		Node ptr2;
		
		for (ptr1 = poly1; ptr1.next != null; ptr1 = ptr1.next);
		for (ptr2 = poly2; ptr2.next != null; ptr2 = ptr2.next);
		Node result = null;
		
		int highestDegreePossible = ptr1.term.degree + ptr2.term.degree;
		for (int i = highestDegreePossible; i >= 0; i--) {
			float sum = 0;
			
			for (ptr1 = poly1; ptr1 != null; ptr1 = ptr1.next) {
				for (ptr2 = poly2; ptr2 != null; ptr2 = ptr2.next) {
					if (ptr1.term.degree + ptr2.term.degree == i) {
						sum += ptr1.term.coeff * ptr2.term.coeff;
					}
				}
			}
			if (sum != 0) {
				result = new Node(sum, i, result);
			}
		}
		return result;
	}
	
	
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		float total = 0;
		for (Node p = poly; p != null; p = p.next) {
			total += (float) Math.pow(x, p.term.degree) * p.term.coeff;
		}
		return total;
	}

	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}
