package data;

import java.io.IOException;
import java.util.List;

public class WORDSIM {

	public static void main(String argb[]) throws IOException {
		if (argb.length==0)
			System.out.println("Usage: java -cp this.jar classname vectorPath testNPairsPath");

		VectorDictionary w2v = w2vFactory.getWordVector(argb[0]);

		List<NominalPair> pairs = DataFactory.getNominalPairs(argb[1]);

		fillPredictions(pairs, w2v);
		double d = calculateSpearmanCorr(pairs);
		System.out.println(d);
	}

	/**
	 * Spearman's correlation as rho = 1- 6sum(d^2)/(n^3-n)
	 */
	private static double calculateSpearmanCorr(List<NominalPair> pairs) {
		double rho = 0.0d;
		double dsum = 0.0d;
		double n = pairs.size();
		for (int i = 0; i < pairs.size(); i++) {
			double diff = pairs.get(i).averageRating - pairs.get(i).prediction;
			dsum+=diff*diff;
		}
		rho = 1.0 - 6.0*dsum / (n*n*n-n);
		return rho;
	}

	private static void fillPredictions(List<NominalPair> pairs, VectorDictionary w2v) {
		for (NominalPair pair: pairs){
			String wa=pair.wa, wb = pair.wb;
			double []wav = w2v.getVector(wa);
			double [] wbv = w2v.getVector(wb);
//			System.out.println(wa +" "+ wb);
//			System.out.println(wav + " "+ wbv);
			pair.prediction = cosineSim(wav,wbv);
		}
	}

	private static double cosineSim(double[] wav, double[] wbv) {
		double sum=0.0d;
		double lena = 0.0d, lenb = 0.0d;
		for (int i = 0 ; i < wav.length; i++){
			sum+= wav[i]*wbv[i];
			lena += wav[i]*wav[i];
			lenb+= wbv[i]*wbv[i];
		}
		if (lena==0.0 || lenb==0.0) return 0;
		return sum/(Math.sqrt(lena)*Math.sqrt(lenb));
	}

}
