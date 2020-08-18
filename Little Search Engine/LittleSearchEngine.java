package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		HashMap<String, Occurrence> keywrd = new HashMap<String, Occurrence>();
		
		Scanner scr = null;
		scr = new Scanner(new File(docFile));
		
		while (scr.hasNext()) {
			String wrd = getKeyword(scr.next());
			
			if(wrd != null) {
				if(!keywrd.containsKey(wrd)) {
					keywrd.put(wrd, new Occurrence(docFile, 1));
				} else {
					keywrd.get(wrd).frequency++;
				}
			}
		}
		scr.close();
		return keywrd;
	}
	
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		
		for(String key : kws.keySet()) {
			
			if(keywordsIndex.keySet().contains(key)) {
				keywordsIndex.get(key).add(kws.get(key));
				insertLastOccurrence(keywordsIndex.get(key));
			}
			
			else {
				ArrayList<Occurrence> array = new ArrayList<Occurrence>();
				array.add(kws.get(key));
				keywordsIndex.put(key, array);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		
		String alphabetUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String punctuation = ".,?:;!";
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		boolean normal = false;
		
		for(int i = word.length()-1; i >= 0; i--) {
			if(alphabetUpperCase.indexOf(word.substring(i, i+1)) > -1) {
				char lowerCase = alphabetUpperCase.charAt(alphabetUpperCase.indexOf(word.substring(i, i+1)));
				word = word.substring(0, i) + lowerCase + word.substring(i+1);
			}
			
			if(!normal && punctuation.indexOf(word.substring(i, i+1)) >= 0)
				word = word.substring(0, i);
			else {
				normal = true;
				
				if(alphabet.indexOf(word.substring(i, i+1)) < 0 || noiseWords.contains(word))
					return null;
			}	
		}
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		
		if(occs.size() < 2) {
			return null;
		}
		int minimum = 0, maximum = occs.size()-2, middle = 1;
		ArrayList<Integer> midPoint = new ArrayList<Integer>();
		
		while(minimum != maximum) {
			middle = (minimum + maximum)/2;
			midPoint.add(middle);
			
			if(occs.get(occs.size()-1).frequency < occs.get(middle).frequency) {
				minimum = middle + 1;
			}
			else if(occs.get(occs.size()-1).frequency > occs.get(middle).frequency) {
				maximum = middle;
			} 
			else {
				break;
			}
		}
		if(occs.get(occs.size()-1).frequency > occs.get(minimum).frequency) {
			occs.add(minimum, occs.remove(occs.size()-1));
		} 
		else {
			occs.add(minimum +1, occs.remove(occs.size()-1));
		}
		return midPoint;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<String> res = new ArrayList<String>();
		
		ArrayList<Occurrence> occurs1 = new ArrayList<Occurrence>();
		ArrayList<Occurrence> occurs2 = new ArrayList<Occurrence>();
		for(Occurrence occ : keywordsIndex.get(kw1)) {
			occurs1.add(occ);
		}for(Occurrence occ : keywordsIndex.get(kw2)) {
			occurs2.add(occ);
		}
		
		while(res.size() < 5 && occurs1.size()+occurs2.size() > 0) {
			String text = null;
			
			if(occurs1.size() == 0) {
				text = occurs2.remove(0).document;
			}
			else if(occurs2.size() == 0) {
				text = occurs1.remove(0).document;
			}
			else {
				if(occurs2.get(0).frequency > occurs1.get(0).frequency)
					text = occurs2.remove(0).document;
				else
					text = occurs1.remove(0).document;
			}
			if(text != null && !res.contains(text)) {
				res.add(text);
			}
		}
		
		if(res.size() > 0)
			return res;
		return null;
	
	}
}
