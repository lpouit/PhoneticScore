package lu.lpouit.scoring;

/**
 * This class extends and uses {@link org.apache.commons.text.similarity.LevenshteinDistance}
 * to compute a similarity-score between 2 {@link String}s
 * using the Levenshtein's algorithm.<br>
 * <br>
 * You can either compare a hole text and the order of the words counts, either
 * compare word by word and the order doesn't count. 
 * 
 * @see #getInstance()
 * 
 * @author lpouit
 * @since 0.1
 */
public class LevenshteinScore extends org.apache.commons.text.similarity.LevenshteinDistance {
	
	private static LevenshteinScore INSTANCE;
	static {
		INSTANCE = new LevenshteinScore();
	}
	
	/**
	 * Describes if any debug informations should be printed to {@link System}.out
	 */
	public static boolean DEBUG = false;
	
	private static final String char2remove = ",;:=?./+%£$*&'(§!)-_|@#{}[]<>\"\\";
	private static final String char2replace = "àâäéèêëîïôöùûüçÀÂÄÉÈÊËÎÏÔÖÙÛÜÇ";
	private static final String remplacement= "aaaeeeeiioouuucAAAEEEEIIOOUUUC";
	
    /**
     * Removes unwanted characters {@value #char2remove} and replaces {@value #char2replace}.<br>
	 * Prepares and normalizes text/words before comparison.
	 * 
	 * @param input {@link String} to normalise
	 * @return Normalised {@link String}
     */
	public static String normalisation(String s1) {
		String s2 = s1;
		for (char c: char2remove.toCharArray())
			s2=s2.replace(c, ' ');
		for (int i=0; i<char2replace.length(); i++)
			s2=s2.replace(char2replace.charAt(i), remplacement.charAt(i));
		while (s2.indexOf("  ")>0)
			s2=s2.replace("  ", " ");
		return s2.toUpperCase().trim();
	}

	/**
	 * Compares word independently from order using renormalized Levenshtein distance
	 * and builds the best score between all word-combinations using the Hungarian algorithm.<br>
	 * The result is a similarity-score between <i>0.0</i> and <i>1.0</i>.<br>
	 * <b>The result doesn't depend on the word order.</b>
	 * 
	 * @see HungarianAlgorithm
	 * 
	 * @param str1 {@link String} to compare
	 * @param str2 {@link String} to compare
	 * @return the total score between 0 and 1
	 */
	public double similarityScoreByWord(String str1, String str2) {
		String[] v1=LevenshteinScore.normalisation(str1).split(" ");
		String[] v2=LevenshteinScore.normalisation(str2).split(" ");
		int r = Math.max(v1.length, v2.length);
		int[][] distance = new int[r][r];
		double[][] score = new double[r][r];
		int shiftSpace = 0;
		
		if (DEBUG) {
			System.out.println("comparison word by word of \""+str1+"\" and \""+str2+"\" ; matrix of scores:");
			for (int i2=0; i2<v2.length; i2++) 
				shiftSpace=Math.max(shiftSpace, v2[i2].length());
			shiftSpace++;
			System.out.print(displayIndentedValue(" ", shiftSpace));
			for (int i2=0; i2<v2.length; i2++) 
				System.out.print(displayIndentedValue(v2[i2], shiftSpace));
			System.out.println();
		}
		for (int i1=0; i1<r; i1++) {
			if (DEBUG && i1<v1.length) 
				System.out.print(displayIndentedValue(v1[i1], shiftSpace));
			else
				System.out.print(displayIndentedValue(" ", shiftSpace));
			for (int i2=0; i2<r; i2++) {
				if (i1<v1.length && i2<v2.length) {
					distance[i1][i2] = super.apply(v1[i1], v2[i2]);
					score[i1][i2] = computeScore(distance[i1][i2], Math.max(v1[i1].length(), v2[i2].length()));
				} else {
					distance[i1][i2] = Integer.MAX_VALUE;
					score[i1][i2] = -1;
				}
				if (DEBUG) System.out.print(displayIndentedValue(String.format("%.2f", score[i1][i2]),shiftSpace));
			}
			if (DEBUG) System.out.println();
		}
		
		HungarianAlgorithm ha = new HungarianAlgorithm(distance);
		int[][] optimal = ha.findOptimalAssignment();

		if (DEBUG) {
			System.out.println("best combination");
			for (int i1=0; i1<optimal.length; i1++) {
				for (int i2=0; i2<optimal[i1].length; i2++) {
					System.out.print("  "+optimal[i1][i2]);
				}
				System.out.println();
			}
		}

		double result=0;
		int compteur=0;
		//if (DEBUG) System.out.println("calculus");
		for (int i=0; i<optimal.length; i++) {
			double lscore=score[optimal[i][1]][optimal[i][0]];
			if (lscore>=0) {
				//if (DEBUG) System.out.println(" "+optimal[i][0]+" "+optimal[i][1]+" "+lscore);
				result += lscore;
				compteur++;
			}
		}

		//if (DEBUG) System.out.println("score="+(result / compteur)+" ("+result+"/"+compteur+")");
		return result / compteur;
	}
	
	/**
	 * Compares the {@link String}s using renormalized Levenshtein distance.<br>
	 * The result is a similarity-score between <i>0.0</i> and <i>1.0</i>.<br>
	 * <b>The result depends on the word order.</b>
	 * 
	 * @param str1 {@link String} to compare
	 * @param str2 {@link String} to compare
	 * @return the total score between 0 and 1
	 */
	public double similarityScore(String str1, String str2) {
		return computeScore(super.apply(LevenshteinScore.normalisation(str1), LevenshteinScore.normalisation(str2)), Math.max(str1.length(), str2.length()));
	}

	/**
	 * Function of re-normalization of the Levenshtein distance (1->infinity to 0->1) 
	 * 
	 * @param distance
	 * @param length
	 * @return the score
	 */
	private double computeScore(int distance, int length) {
		return (double)(length-distance)/(double)length;
	}
	
	private String displayIndentedValue(String input, int indentation) {
		String result=input;
		while (result.length()<indentation)
			result+=" ";
		return result;
	}
	
	/**
	 * @return the static instance of {@link LevenshteinScore}.
	 */
	public static LevenshteinScore getInstance() {return INSTANCE;}
	
}
