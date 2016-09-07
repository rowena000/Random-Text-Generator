import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * GenText class
 * This class will have a main that's responsible for processing the command-line arguments, 
 * opening and closing the files, and handling any errors related to the above tasks. 
 * 
 * Every k-word prefix we generate is a sequence found in the source text. 
 * We generate the next word by looking at the frequency of all of the successors of this prefix in the source text.
 * How to call it from the command line:
 * 		java GenText [-d] prefixLength numWords sourceFile outFile
 * 		
 */
public class GenText {
	private static final int LINE_SIZE = 80;
	private static ArrayList<Object> arguments;
	
	public static void main(String[] args) {
		arguments = new ArrayList<Object>();
		try {
			validateArgs(args);
		} catch (InvalidArgsException e) {
			// Print error message with command instruction
			System.out.println(e.getMessage());
			printError(e.getMessage());
		}
		
		try {
			boolean debug = (boolean)arguments.get(0);
			int prefixLength = (int)arguments.get(1);
			int numWords = (int)arguments.get(2);
			String fileName = (String)arguments.get(3);
			String outFileName = (String)arguments.get(4);
		
			File newFile = new File(fileName);//can throw FileNotFoundException("ERROR: input file does not exist.")
			ArrayList<String> wordsList = readFromFile(newFile);
			if (prefixLength >= wordsList.size()) {
				throw new InvalidArgsException("ERROR: prefixLength >= number of words in sourceFile. ");
			}
			File outFile = getOutputFile(outFileName);//can throw invalidArgsException("ERROR: can't write to output file. ")
			
			RandomTextGenerator gen = new RandomTextGenerator(prefixLength, numWords, wordsList, LINE_SIZE, debug);
			String text = gen.generate();

			// write to file
			writeToFile(outFile, text);

		} catch (Exception e) {
			// Print out error message without command instruction
			System.out.println(e.getMessage());
			printError(null);	
		}
	
	}
	
	/**
	 * Validate user command arguments. If valid, put into arguments.
	 * @param args arguments from main method
	 * @throws InvalidArgsException 5 kinds of InvalidArgsException which should be printed out following by 
	 * 		instruction of how to use commands.
	 * (4 of them are required in the assignment and the other one is to check too many arguments) 
	 */
	private static void validateArgs(String[] args) throws InvalidArgsException {
		if (args.length < 4) {
			throw new InvalidArgsException("ERROR: missing command-line arguments. ");
		}	
		if (args.length > 5) {		
			throw new InvalidArgsException("ERROR: too many command-line arguments. ");
		} 
		
		if (args.length == 5 && !args[0].equals("-d")) {
			throw new InvalidArgsException("ERROR: you are using 5 arguments and first argument should be -d. ");
		} 
		
		int offset = 0;
		boolean debug = false;
		if (args.length == 5) {
			debug = true;
			offset++;
		} 
		arguments.add(debug);
		
		try {
			int prefixLen = Integer.parseInt(args[offset]);
			int numWords = Integer.parseInt(args[offset + 1]);
			if (numWords < 0) {
				throw new InvalidArgsException("ERROR: numWords < 0. ");
			}
			if (prefixLen < 1) {
				throw new InvalidArgsException("ERROR: prefixLength < 1. ");
			}
			arguments.add(prefixLen);
			arguments.add(numWords);
		} catch (NumberFormatException e) {
			throw new InvalidArgsException("ERROR: prefixLength or numWords arguments are not integers. ");
		}

		arguments.add(args[offset + 2]);
		arguments.add(args[offset + 3]);
	}
	
	/**
	 * Read from source text
	 * @param file source text
	 * @return an ArrayList<String> with each word in the file as an element. 
	 * @throws FileNotFoundException input file does not exist.
	 */
	private static ArrayList<String> readFromFile(File file) throws FileNotFoundException{
		if (!file.exists()) {
			throw new FileNotFoundException("ERROR: input file does not exist. ");
		}
		Scanner in = new Scanner(file);
		ArrayList<String> source = new ArrayList<String>();
		while (in.hasNext()) {
			source.add(in.next());
		}
		in.close();
		return source;
	}
	
	/**
	 * Get output file.
	 * @param outFileName output file name
	 * @return output file
	 * @throws Exception can't write to output file
	 */
	private static File getOutputFile(String outFileName) throws Exception {
		File outFile = new File(outFileName);
		//If no such file with the given output file name, create a new one.
		if (!outFile.exists()) {
			outFile.createNewFile();
		} else if (!outFile.canWrite()) {
			//Or throw an exception if the existing file can't write in.
			throw new InvalidArgsException("ERROR: can't write to output file. ");
		}
		return outFile;
	}
	
	/**
	 * Write to output file
	 * @param file output file
	 * @param text text to be write in
	 * @throws Exception 
	 */
	private static void writeToFile(File file, String text) throws Exception {
		FileWriter writer = new FileWriter(file);
		writer.write(text);
		writer.close();
	}
	
	/**
	 * Differentiate two kinds of error conditions, first print out command instruction, second doesn't. Then exit the program.
	 * @param msg error message
	 */
	private static void printError(String msg) {
		if (msg == null) {
			System.out.println("Please correct the command and run again.");
		} else {
			System.out.println("Please run the program using the following command: ");
			System.out.println("\tjava GenText [-d] prefixLength numWords sourceFile outFile");
			System.out.println("[-d] is optional, it means enable debug mode or not"
					+ "prefixLength and numWords should be integer. \n" 
					+ "sourceFile is the name of input source file.\n" 
					+ "outFile is the name of output file.");
		
		}
		System.exit(0);
	}
}

/**
 * InvalidArgsException class
 * @author wang
 *
 */
class InvalidArgsException extends Exception {
	public InvalidArgsException(String msg) {
		super(msg);
	}
}
