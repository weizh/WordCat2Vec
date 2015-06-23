package data;

public class NominalPair {

	String wa,wb;
	String posa,posb;
	double[] ratings;
	double averageRating;
	public String[] ctxta_pw,ctxta_nw,ctxtb_pw,ctxtb_nw;
	
	double prediction;
	
	public String toString(){
		return "wa:wb "+ wa+":" + wb+"\n"
				+ "posa:posb " + posa +":"+posb +"\n"
						+ "averageRating:"+averageRating;
		
	}
}
