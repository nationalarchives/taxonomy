package gov.tna.discovery.taxonomy.common.service;

public class TaxonomyHelperTools {

    // private static final Logger logger =
    // LoggerFactory.getLogger(TaxonomyHelperTools.class);

    public static long startTimer() {
	long start_time = System.nanoTime();
	return start_time;
    }

    public static long getTimerDifference(long start_time) {
	long end_time = startTimer();
	return Math.round((end_time - start_time) / 1e6);
    }

}
