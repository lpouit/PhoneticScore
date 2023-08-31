package lu.lpouit.scoring;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import lu.lpouit.scoring.utils.Pair;

/**
 * This class extends {@link lu.lpouit.scoring.LevenshteinScore}
 * to compute a similarity-score between 2 {@link String}s
 * using phonetic transcription for several languages.<br>
 * The class is mono-instance by language so the rules are loaded once.<br>
 * <br>
 * Transcriptions' rules are stored as RegEx into files in the {@link #FOLDER} with this naming pattern: <i>[language].[level].txt</i>.
 * Several levels from 1 to {@link MAX_LEVELS} will automatically be loaded at the creation of a new instance.<br>
 * A lower level represents a less sensitive/precise comparison and result a higher score.<br>
 * 
 * @see #DEBUG
 * @see #MAX_LEVELS
 * @see #FOLDER
 * 
 * @see #getInstance(String)
 * 
 * @author lpouit
 * @version 0.1
 */
public class PhoneticScore extends LevenshteinScore {
	/**
	 * Relative path containing the phonetic files
	 */
	public static String FOLDER = "./phonetics";
	public static int MAX_LEVELS = 10;
	/**
	 * Describes if any debug informations should be printed to {@link System}.out
	 */
	public static boolean DEBUG = false;
	
	private static Map<String, PhoneticScore> INSTANCES;
	static {
		INSTANCES = new HashMap<>();
	}

	private int levelNb;
	protected LinkedList<LinkedList<Pair<String, String>>> regles;

	/**
	 * loads rules from corresponding files as follows:<br>
	 * <b>{@link #FOLDER}/[lang].[1..{@link #MAX_LEVELS}].txt</b>
	 * 
	 * @see {@link #setLanguage(String)}
	 * 
	 * @param lang the iso-code for the language
	 * @throws RuntimeException caused by {@link IOException} If an exception occurs while reading the files.
	 * @throws IllegalArgumentException If the level 1 file does not exist for this language.
	 */
	protected PhoneticScore(String lang) throws IllegalArgumentException {
		try {
			this.levelNb = setLanguage(lang);
		}catch(IOException ioe) {
			throw new RuntimeException("Could not load Phonetic files", ioe);
		}
	}
	
	/**
	 * Removes unwanted characters {@value #char2replace}.<br>
	 * Prepares and normalizes text/words before transcription into the phonetic alphabet
	 * 
	 * @param input {@link String} to normalise
	 * @return Normalised {@link String}
	 */
	public static String normalisation(String input) {
		String s2 = input.toLowerCase();
		
		while (s2.contains("  "))
			s2=s2.replace("  ", " ");
		
		return s2.trim();
	}

	/**
	 * Transcripts into phonetic alphabet.<br>
	 * The input gets normalised before applying the rules.
	 * 
	 * @see {@link #normalisation(String)}
	 * 
	 * @param input {@link String} to normalise
	 * @param maxLevel the maximum depth
	 * @return phonetic {@link String}
	 */
	public String getPhonetic(String input, int maxLevel) {
		String result = normalisation(input);
		if (DEBUG)
			System.out.print(result+" ");
		for(int level = 0; level < Math.min(maxLevel, regles.size()); level++) {
			LinkedList<Pair<String, String>> currentLevel = regles.get(level);
			for(Pair<String, String> regle : currentLevel) {
				String nouveau = result.replaceAll(regle.getKey(), regle.getValue());
				if(!nouveau.equals(result)) {
					nouveau = nouveau.replaceAll(regle.getKey(), regle.getValue());
					if (DEBUG) System.out.print(nouveau+" ");
				}
				result = nouveau;
			}
		}
		if (DEBUG) System.out.println();
		return result;
	}

	/**
	 * Compares word independently from order using phonetics level 2.<br>
	 * The result is a similarity-score between <i>0.0</i> and <i>1.0</i>.<br>
	 * <b>The result doesn't depend on the word order.</b>
	 * 
	 * @see {@link #similarityScoreByWord(String, String, int)}
	 * 
	 * @param str1 {@link String} to compare
	 * @param str2 {@link String} to compare
	 * @return the total score between 0 and 1
	 */
	@Override
	public double similarityScoreByWord(String str1, String str2) {
		return this.similarityScoreByWord(str1, str2, 2);
	}
	
	/**
	 * Compares word independently from order using phonetics.<br>
	 * The result is a similarity-score between <i>0.0</i> and <i>1.0</i>.<br>
	 * <b>The result doesn't depend on the word order.</b>
	 * 
	 * @see {@link #similarityScoreByWord(String, String)}
	 * 
	 * @param str1 {@link String} to compare
	 * @param str2 {@link String} to compare
	 * @param level of transcription (the highest the most phonetic-destructive)
	 * @return the total score between 0 and 1
	 */
	public double similarityScoreByWord(String str1, String str2, int level) {
		String[] v1=str1.split(" ");
		String[] v2=str2.split(" ");

		for (int i=0; i<v1.length; i++)
			v1[i]=getPhonetic(v1[i], level);
		for (int i=0; i<v2.length; i++)
			v2[i]=getPhonetic(v2[i], level);

		return super.similarityScoreByWord(String.join(" ",v1), String.join(" ",v2));
	}

	/**
	 * Compares text using phonetics level 2. Order of words counts.<br>
	 * The result is a similarity-score between <i>0.0</i> and <i>1.0</i>.<br>
	 * <b>The result depends on word order.</b>
	 * 
	 * @see {@link #similarityScore(String, String, int)}
	 * 
	 * @param str1 {@link String} to compare
	 * @param str2 {@link String} to compare
	 * @return the total score between 0 and 1
	 */
	@Override
	public double similarityScore(String str1, String str2) {
		return this.similarityScore(str1, str2, 2);
	}
	
	/**
	 * Compares text using phonetics. Order of words counts.<br>
	 * The result is a similarity-score between <i>0.0</i> and <i>1.0</i>.<br>
	 * <b>The result depends on word order.</b>
	 * 
	 * @see {@link #similarityScore(String, String)}
	 * 
	 * @param str1 {@link String} to compare
	 * @param str2 {@link String} to compare
	 * @param level of transcription (the highest the most phonetic-destructive)
	 * @return the total score between 0 and 1
	 */
	public double similarityScore(String str1, String str2, int level) {
		return super.similarityScore(getPhonetic(str1, level), getPhonetic(str2, level));
	}

	/**
	 * Load or reload rules for this language.<br>
	 * 
	 * @param language {@link String} ISO-639-2 (alpha 3)
	 * @return available number of levels {@link Integer}
	 * @throws IOException If an exception occurs while reading the files.
	 * @throws IllegalArgumentException If the first file doesn't exist.
	 */
	protected int setLanguage(String language) throws IOException {
		if(regles == null)
			regles = new LinkedList<>();
		else
			regles.clear();
		
		int i = 1;
		while(i < MAX_LEVELS) {
			String path = FOLDER+"/"+language+"."+i+".txt";
			try {
				if(!Files.exists(Paths.get(path))) {
					if(i == 1)
						throw new IllegalArgumentException("File 1 for language "+language+", not found. ("+path+")");
					return i-1;
				}
				
				Scanner reglesScanner = new Scanner(new File(path));
				LinkedList<Pair<String, String>> currentLevel = new LinkedList<>();
				while(reglesScanner.hasNext()) {
					String regle = "";
					do {
						regle = reglesScanner.nextLine().trim();
					}while(regle.isEmpty() || regle.startsWith("#"));
					
					String remplacement = reglesScanner.nextLine().replace("-null", "");
					currentLevel.add(new Pair<>(regle, remplacement));
				}
				regles.add(currentLevel);
				reglesScanner.close();
			
				if(DEBUG)
					System.out.println(currentLevel.size()+" phonetic rules loaded from "+path);
				i++;
			} catch (IOException io) {
				if(DEBUG)
					System.err.println("error loading phonetic rules");
				throw io;
			}
		}
		return i;
	}
	
	/**
	 * @return The count of currently loaded levels.
	 */
	public int getLevelNb() {return levelNb;}
	
	/**
	 * Gets the static instance associated to a language and creates it if needed.<br>
	 * 
	 * @see #DEBUG
	 * 
	 * @param language {@link String} ISO-639-2 (alpha 3)
	 * @return the instance
	 */
	public static PhoneticScore getInstance(String language) {
		if(!INSTANCES.containsKey(language))
			INSTANCES.put(language, new PhoneticScore(language));
		return INSTANCES.get(language);
	}
	
	/**
	 * Clear all static instances and free memory.<br>
	 */
	public static void resetInstances() {
		INSTANCES.clear();
	}
}
