package apriori;

public class AssociationRule {
	private ItemSet i, s;

	private double confidence = 0.0;

	public AssociationRule(ItemSet i, ItemSet s) {
		this.i = i;
		this.s = s;
	}

	public ItemSet getI() {
		return i;
	}

	public ItemSet getS() {
		return s;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(i + "\t==>\t" + s + "\t:\t" + confidence+"\n");

		return sb.toString();

	}

	
	 @Override
	public int hashCode() {
		return i.hashCode()*s.hashCode()*57;
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (o == null)
			return false;
		if (o instanceof AssociationRule) {
			AssociationRule ar = (AssociationRule) o;
			return this.i.equals(ar.i) && this.s .equals(ar.s);
		}
		return false;
	}

	public void setConfidence(double d) {
		confidence = d;
	}

	public double getConfidence() {
		return confidence;
	}
}