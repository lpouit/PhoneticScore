package lu.lpouit.scoring;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.xml.sax.SAXParseException;

import lu.lpouit.scoring.PhoneticScore;


public class Main {
	static LevenshteinScore leven;
	static PhoneticScore phon;
	static String language = "fra";
	
	public static void main(String[] args) {
		init();
		//System.out.println(phon.similarityScoreByWord("le discours, interdit", "interdit, le : discours", true, 2));
		mainMenu();
		Scanner in = new Scanner(System.in);
		while(in.hasNextLine()) {
			switch (in.nextLine()) {
			case "0":
				System.out.println("bye");
				System.exit(0);
				break;
			case "1":
				testPhonetics();
				break;
			case "2":
				testScoring();
				break;
			}
			mainMenu();
		}
		in.close();
	}
	
	private static void mainMenu() {
		System.out.println();
		System.out.println("MENU");
		System.out.println("  0  exit");
		System.out.println("  1  test phonetic transcription");
		System.out.println("  2  test scoring");
	}

	public static void testPhonetics() {
		init();
		menuPhonetics();
		Scanner in = new Scanner(System.in);
		while(in.hasNextLine()) {
			switch (in.nextLine()) {
			case "0":
				return;
			case "1":
				change_language();
				break;
			case "2":
				init();
				break;
			case "3":
				convertTestFile();
				break;
			case "4":
				inputSentence();
				break;
			}
			menuPhonetics();
		}
		in.close();
	}

	private static void menuPhonetics() {
		System.out.println();
		System.out.println("Test Phonetics");
		System.out.println("  0  back");
		System.out.println("  1  change language");
		System.out.println("  2  re-load rules");
		System.out.println("  3  transcript phonetic.tests.txt");
		System.out.println("  4  input sentence");
	}

	public static void testScoring() {
		init();
		menuScoring();
		Scanner in = new Scanner(System.in);
		while(in.hasNextLine()) {
			switch (in.nextLine()) {
			case "0":
				return;
			case "1":
				change_language();
				break;
			case "2":
				init();
				break;
			case "3":
				scoreTestFile();
				break;
			}
			menuScoring();
		}
		in.close();
	}

	private static void menuScoring() {
		System.out.println();
		System.out.println("Test Scoring");
		System.out.println("  0  back");
		System.out.println("  1  change language");
		System.out.println("  2  re-load rules");
		System.out.println("  3  compute scoring.tests.txt");
	}

	public static void change_language() {
		menuLanguage();
		Scanner in = new Scanner(System.in);
		while(in.hasNextLine()) {
			switch (in.nextLine()) {
			case "0":
				System.exit(0);
				return;
			case "1":
				language = "fra";
				init();
				return;
			case "2":
				language = "deu";
				init();
				return;
			case "3":
				language = "nld";
				init();
				return;
			}
			menuLanguage();
		}
		in.close();
	}

	private static void menuLanguage() {
		System.out.println();
		System.out.println("Change language for phonetics");
		System.out.println("  0  back");
		System.out.println("  1  french/français");
		System.out.println("  2  germain/deutsch");
		System.out.println("  3  dutch");
	}

	private static void init() { 
		leven = PhoneticScore.getInstance();
		phon.resetInstances();
		phon = PhoneticScore.getInstance(language);
		System.out.println("rules of phonetic transcription loaded for "+language);
	}

	private static void inputSentence() {
		System.out.println("your sentence:");
		Scanner in = new Scanner(System.in);
		String s=in.nextLine();
		for (int i=1; i<=phon.getLevelNb(); i++)
			System.out.println("Level "+i+": "+phon.getPhonetic(s, i, false));
	}

	private static void convertTestFile() {
		System.out.println("<load words to be tested from phonetic.tests.txt");
		System.out.println(">save phonetic transcription into phonetic.tests.out.txt");

		Scanner tests;
		PrintStream writer;
		try {
			tests = new Scanner(new File("./phonetic.tests.txt"));
			writer = new PrintStream(new File("./phonetic.tests.out.txt"));
			while(tests.hasNextLine()) {
				String s = tests.nextLine();
				writer.println("[Original] "+s);
				for (int i=1; i<=phon.getLevelNb(); i++) {
					String p = phon.getPhonetic(s, i, false);
					writer.println("[Level "+i+"] "+p);
				}
			}
			writer.close();
			tests.close();
			displayFile("./phonetic.tests.out.txt");
		} catch (FileNotFoundException e) {
			System.err.println("phonetic tests failed");
		}
	}

	private static void scoreTestFile() {
		System.out.println("<load words to be scored from scoring.tests.txt");
		System.out.println(">save scoring values into scoring.tests.out.txt");

		Scanner tests;
		PrintStream writer;
		try {
			tests = new Scanner(new File("./scoring.tests.txt"));
			writer = new PrintStream(new File("./scoring.tests.out.txt"));
			while(tests.hasNextLine()) {
				String s1 = tests.nextLine();
				String s2 = tests.nextLine();
				String r1 = "[score using renormalized Levenstein distance]: "+leven.similarityScore(s1, s2);
				String r2 = "[score using renormalized Levenstein distance word by word]: "+leven.similarityScoreByWord(s1, s2);
				writer.println(s1);
				writer.println(s2);
				writer.println(r1);
				writer.println(r2);
				for (int i=1; i<=phon.getLevelNb(); i++) {
					r1 = "[score using renormalized Levenstein distance on phonetic transcription level "+i+"]: "+phon.similarityScore(s1, s2, i);
					r2 = "[score using renormalized Levenstein distance word by word on phonetic transcription level "+i+"]: "+phon.similarityScoreByWord(s1, s2, false, i);
					writer.println(r1);
					writer.println(r2);
				}
			}
			writer.close();
			tests.close();
			displayFile("./scoring.tests.out.txt");
		} catch (FileNotFoundException e) {
			System.err.println("scoring tests failed");
		}
	}

	private static void displayFile(String fileName) {
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			stream.forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
