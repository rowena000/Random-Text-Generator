import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

/**
 * RandomTextGenerator class
 * Generate a random text base on given source text.
 */
public class RandomTextGenerator {

	private int prefixLength;
	private int numWords;
	private int lineSize;
	private ArrayList<String> source;
	private HashMap<Prefix, ArrayList<String>> successors;
	private boolean debug;
	private Random ran;
	
	/**
	 * Create a new RandomTextGenerator
	 * @param prefixLength prefix length
	 * @param numWords number of words to generate
	 * @param source an ArrayList<String> with each word form source text as an element
	 * @param lineSize output file line size
	 * @param debug whether is debug mode
	 */
	public RandomTextGenerator(int prefixLength, int numWords, ArrayList<String> source, int lineSize, boolean debug) {
		this.prefixLength = prefixLength;
		this.numWords = numWords;
		this.source = source;
		successors = new HashMap<Prefix, ArrayList<String>>();
		this.debug = debug;
		if (debug) {
			ran = new Random(1);	
		} else {
			ran = new Random();	
		}
		this.lineSize = lineSize;
		preProcessing();
	}
	
	
	/** 
	 * preprocess, generate successor map
	 * Time complexity: O(source length)
	 */
	private void preProcessing() {	
		//firstPrefixList is an ArrayList storing the first i words in the source file, i=prefixLength.
		ArrayList<String> firstPrefixList = new ArrayList<String>();
		for (int i = 0; i < prefixLength; i++) {
			firstPrefixList.add(source.get(i));
		}
		Prefix prefix = new Prefix(firstPrefixList);
		
		for (int i = prefixLength; i < source.size(); i++) {
			//get the word right after prefix
			String word = source.get(i);
			//if the successor map contains the prefix, add the word to its successor ArrayList
			if (successors.containsKey(prefix)) {
				ArrayList<String> sList = successors.get(prefix);
				sList.add(word);
			} else {
				//else, put the prefix to the map and add the word to its successor ArrayList
				ArrayList<String> sList = new ArrayList<String>();
				sList.add(word);
				successors.put(prefix, sList);
			}
			prefix = prefix.shiftIn(word);
		}
		
	}
	
	/**
	 * generate a new random prefix
	 * @return a random prefix
	 */
	private Prefix randomNewPrefix(ArrayList<Prefix> prefixList){
		int index = ran.nextInt(prefixList.size());	
		Prefix newPrefix = prefixList.get(index);
		debugPrint("chose a new initial prefix: " + newPrefix);
		return newPrefix;
	}
	
	/**
	 * generate a random text
	 * Time complexity, O(numWords)
	 * @return a random text
	 */
	public String generate() {
		StringBuilder text = new StringBuilder();
		//charNum is the number of characters in current line
		int charNum = 0;
		//generate a random prefix as initial prefix
		Set<Prefix> prefixSet = successors.keySet();
		ArrayList<Prefix> prefixList = new ArrayList<Prefix>();
		for (Prefix pre : prefixSet) {
			prefixList.add(pre);
		}
		Prefix prefix = randomNewPrefix(prefixList);

		for (int i = 0; i < numWords;) {
			debugPrint("prefix: " + prefix.toString());

			// if successor map doesn't contain the prefix, means the current
			// prefix is at the end of the file
			// then generate a new prefix
			if (!successors.containsKey(prefix)) {
				debugPrint("successors: <END OF FILE> ");
				prefix = randomNewPrefix(prefixList);
				debugPrint("prefix: " + prefix.toString());
			}
			
			ArrayList<String> succList = successors.get(prefix);
			debugPrint("successors: " + getSuccessorString(succList));

			// generate a random word from the successors of the prefix
			int j = ran.nextInt(succList.size());
			String word = succList.get(j);
			debugPrint("word generated: " + word);

			// if adding a new word cause charNum greater than lineSize, change
			// to a new line and set charNum to 0.
			if (charNum + word.length() > lineSize) {
				text.deleteCharAt(text.length()-1);
				text.append(System.lineSeparator());
				charNum = 0;
			}
			text.append(word);

			if (i != numWords - 1) {
				text.append(" ");
			}
			charNum = charNum + word.length() + 1;
			prefix = prefix.shiftIn(word);
			i++;
		}
		
		return text.toString();
	}
	
	/**
	 * Take a successor list, convert to string for debug print out
	 * @param successors
	 * @param prefix
	 * @return
	 */
	private String getSuccessorString(ArrayList<String> sucList) {
		StringBuilder sucString = new StringBuilder();
		for (int j = 0; j < sucList.size() - 1; j++) {
			sucString.append(sucList.get(j)).append(" ");
		}
		sucString.append(sucList.get(sucList.size() - 1));
		return sucString.toString();
	}
	
	/**
	 * print out debug messages if debug is enabled
	 * @param msg debug message
	 */
	private void debugPrint(Object msg) {
		if (debug) {
			System.out.println("DEBUG: " + msg);
		}
	}
}
