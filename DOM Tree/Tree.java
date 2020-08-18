package structures;

import java.util.*;

import org.w3c.dom.Node;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		/** COMPLETE THIS METHOD **/
		
		Stack<TagNode> tagNodes = new Stack<TagNode>(); //stack for tag nodes
		
		sc.nextLine(); //scans through the line
		
		root = new TagNode("html", null, null); //root node is HTML
		tagNodes.push(root); //pushes <HTML> tag into stack 
		
		while (sc.hasNextLine()) { //traverse through HTML 
			
			String string = sc.nextLine();
			boolean isATag = false; 
			
			if(string.charAt(0) == '<') { // check to see if it is a tag
				if(string.charAt(1) == '/') { // checks to see if its an end tag
					tagNodes.pop();
					continue;
			}
			
			else {
				string = string.replace("<", "");
				string = string.replace(">", "");
				isATag = true; //checks if its a beginning tag 
				}
			}
		
		
		TagNode node = new TagNode(string, null, null);
		
		if (tagNodes.peek().firstChild == null) {
			tagNodes.peek().firstChild = node; 
		}
		else {
			TagNode sib = tagNodes.peek().firstChild;
			while(sib.sibling != null) {
				sib = sib.sibling; 
			}
			sib.sibling = node; 
		}
	
		if (isATag) {
			tagNodes.push(node);
		
				}
			}
		}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		
		this.repTag(root.firstChild, oldTag, newTag);
	}
		
	private void repTag(TagNode root, String oldTag, String newTag) {
		
		for(TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
			if (root.firstChild != null) {
				this.repTag(root.firstChild, oldTag, newTag);
			}
			if (root.sibling != null) {
				this.repTag(root.sibling, oldTag, newTag);
			}
			if (root.tag.equals(oldTag)) {
				root.tag = newTag; 
			}
		}	
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		this.boldRowRec(row, 0, root, root.firstChild);
	}
	
	private void boldRowRec(int row, int rowCount, TagNode prev, TagNode trav) {
		if (trav == null) {
			return;
	}
		else if (trav.tag.equals("tr")) {
			rowCount++;
		}
		if (rowCount == row && trav.firstChild == null) {
			prev.firstChild = new TagNode("b", trav, null);
		}
	boldRowRec(row, rowCount, trav, trav.firstChild);
	boldRowRec(row, rowCount, trav, trav.sibling);
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		if (root == null) {
			return;
		}
		else {
			while (this.containsATag(tag, root)) {
				removeTagN(tag, root, root.firstChild);
				}
			}
		}
	
	private void removeTagN(String tag, TagNode previous, TagNode traverse) {
		if (traverse == null) {
				return;
			}
		if (previous == null ) {
			return;
		}
		
		else if (traverse.tag.equals(tag)) { 
			if (tag.equals("ul")) {
					this.removeListTagHelper(traverse.firstChild);
				}
			if (tag.equals("ol")) {
				this.removeListTagHelper(traverse.firstChild);
			}
		
		if (previous.firstChild == traverse) {
			previous.firstChild = traverse.firstChild;
			this.addLastSibling(traverse.firstChild, traverse.sibling);
		}
		else if (previous.sibling == traverse) {
			this.addLastSibling(traverse.firstChild, traverse.sibling);
			previous.sibling = traverse.firstChild;
		}
		return;
	}
		previous = traverse;
		removeTagN(tag, previous, traverse.firstChild);
		removeTagN(tag, previous, traverse.sibling);
		
}
	private void removeListTagHelper(TagNode traverse) {
		if (traverse == null) {
			return;
		}
		else if (traverse.tag.compareTo("li") == 0) {
			traverse.tag = "p";
		}
		this.removeListTagHelper(traverse.sibling);
	}
	private TagNode getLastSibling (TagNode traverse) {
		while (traverse.sibling == null) {
			return null;
		}
		while (traverse.sibling != null) {
			traverse = traverse.sibling;
		}
		return traverse;
	}

	private void addLastSibling(TagNode traverse, TagNode sibling) {
		traverse =  this.getLastSibling(traverse);
		traverse.sibling = sibling;
	}
	
	private boolean containsATag(String tag, TagNode traverse) {
		if (traverse == null) {
			return false;
		}
		else if (traverse.tag.compareTo(tag) == 0) {
			return true;
		}
		
		return this.containsATag(tag, traverse.firstChild) || 
				containsATag(tag, traverse.sibling);
	}

	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag){
			/** COMPLETE THIS METHOD **/
			
		if (tag.equals("em")) {
			root = addTagN(this.root, word, tag);
			}
		if (tag.equals("b")) {
			root = addTagN(this.root, word, tag);
			}
		}
		
		private TagNode addTagN(TagNode node, String wordNode, String tag) {
			if (node.sibling != null) {
				node.sibling = addTagN(node.sibling, wordNode, tag);
			if (node.sibling == null) { 
				return null;
			}
		}

			if (node.firstChild == null) {
				return null;
			}
			if (node.firstChild!= null) {
				node.firstChild = addTagN(node.firstChild, wordNode, tag);
			}

		
			if (node.tag.contains(wordNode)) {
				TagNode newTagN = new TagNode(tag, node, node.sibling);
				node.sibling = null;
				return newTagN;
			}
		
			return node;
				
		}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}
