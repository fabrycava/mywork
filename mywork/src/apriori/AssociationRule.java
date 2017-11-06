package apriori;

import java.util.Comparator;

public class AssociationRule implements Comparable<AssociationRule> {
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
		sb.append(i + "\t==>\t" + s + "\t\t" + confidence + "\n");

		return sb.toString();

	}

	@Override
	public int hashCode() {
		return i.hashCode() * s.hashCode() * 57;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (o == null)
			return false;
		if (o instanceof AssociationRule) {
			AssociationRule ar = (AssociationRule) o;
			return this.i.equals(ar.i) && this.s.equals(ar.s);
		}
		return false;
	}

	public void setConfidence(double d) {
		confidence = d;
	}

	public double getConfidence() {
		return confidence;
	}

	public boolean contains(AssociationRule ar1) {
		if (this.i == null || this.s == null || ar1.s == null || ar1.s == null)
			return false;

		if (this.i.cardinality() > ar1.i.cardinality() && this.s.cardinality() >= ar1.s.cardinality())
			return this.i.containsAll(ar1.i) && this.s.containsAll(ar1.s);
		return false;
	}

	@Override
	public int compareTo(AssociationRule o) {
		if (this.i.cardinality() == o.i.cardinality())

			if (this.s.cardinality() == o.s.cardinality())
				return 0;
			else if (this.s.cardinality() > o.s.cardinality())
				return 1;
			else
				return -1;
		else
			return this.i.cardinality() > o.i.cardinality() ? 1 : -1;

	}

	public static Comparator<AssociationRule> getComparator(Order order) {
		return order == Order.ASCENDING ? new AscendingAssociationRuleComparator()
				: new DescendingAssociationRuleComparator();
	}

	private static class AscendingAssociationRuleComparator implements Comparator<AssociationRule> {

		@Override
		public int compare(AssociationRule o1, AssociationRule o2) {
			return o1.compareTo(o2);
		};

	}

	private static class DescendingAssociationRuleComparator implements Comparator<AssociationRule> {

		@Override
		public int compare(AssociationRule o1, AssociationRule o2) {
			return o1.compareTo(o2) * (-1);
		};

	}
}