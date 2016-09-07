import java.util.ArrayList;

/**
 * Prefix class
 * @author wang
 * Class for storing prefix as an ArrayList<String>, each word in the prefix string as an element.
 */
public class Prefix {
	private ArrayList<String> prefix;
	
	/**
	 * Create a prefix with given prefix ArrayList<string>
	 * @param prefixList prefix ArrayList
	 */
	public Prefix(ArrayList<String> prefixList) {
		prefix = (ArrayList<String>)prefixList.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (int i = 0; i < prefix.size(); i++) {
			result = prime * result + prefix.get(i).hashCode();
		}
		
		return result;
	}

	@Override 
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Prefix other = (Prefix) obj;
		ArrayList<String> otherPrefix = other.getPrefix();
		if (otherPrefix.size() != prefix.size()) {
			return false;
		}
		
		for (int i = 0; i < prefix.size(); i++) {
			if (!prefix.get(i).equals(otherPrefix.get(i))) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Update prefix, remove first word in prefix and add a new one at the end.
	 * As Prefix is an immutable object, return a new Prefix instance
	 * @param str new word to put into prefix
	 * @return new prefix
	 */
	public Prefix shiftIn(String str) {
		ArrayList<String> newList = (ArrayList<String>)prefix.clone();
		newList.remove(0);
		newList.add(str);
		Prefix newPrefix = new Prefix(newList);
		return newPrefix;
	}
	
	/**
	 * Get the prefix
	 * @return prefix
	 */
	public ArrayList<String> getPrefix() {
		return (ArrayList<String>) prefix.clone();
	}

	/**
	 * Change a prefix to string with space between each word.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < prefix.size(); i++) {
			sb.append(prefix.get(i));
			if (i != prefix.size() - 1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	
}
