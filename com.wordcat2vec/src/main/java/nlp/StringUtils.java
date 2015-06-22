package nlp;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	private static HashSet<Character> puncts;
	private static HashSet<Character> notCheckIfContainsPunct;

	static {

		char[] punctArray = "`~!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".toCharArray();
		puncts = new HashSet<Character>(punctArray.length);
		for (char c : punctArray)
			puncts.add(c);

		char[] ifContains = "(){}[]<>".toCharArray();
		notCheckIfContainsPunct = new HashSet<Character>(ifContains.length);
		for (char c : punctArray)
			notCheckIfContainsPunct.add(c);

	}

	public static boolean isAllPunct(String w) {

		for (int i = 0; i < w.length(); i++) {
			if (!puncts.contains(w.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	static Pattern email = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

	public static String normalize(String w) {
		if (w.length() > 20)
			return "[UNK]";
		Matcher emails = email.matcher(w);
		if (emails.matches())
			return ("[URL]");
		else if (isAllPunct(w))
			return "[PUN]";
		else if (isAllNumber(w))
			return "[NUM]";
		else if (w.startsWith("@"))
			return "@MTN";
		else if (w.startsWith("#"))
			return "@HS";

		return w;
	}

	public static void main(String arg[]) {
		Matcher emails = email.matcher("wynnzh@mail.com");
		System.out.println(StringUtils.normalize("Wei@gmail.com"));
	}

	private static boolean isAllNumber(String w) {
		for (int i = 0; i < w.length(); i++) {
			Character c = w.charAt(i);
			if (Character.isAlphabetic(c))
				return false;
		}
		return true;
	}
}
