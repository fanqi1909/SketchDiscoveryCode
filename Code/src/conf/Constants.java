package conf;


/**
 * stores constants used in the application
 * @author a0048267
 *
 */
public class Constants {
	public static int P = 20; // the number of news kept in the window index
	public static int K = 10; // the number of sketches kept for each object 
	public static int S =4; // cache the top 4 elements for bound estimation
	public static double Alpha = 0.7; // specified in the scoring function for sketch computation
}
