package apriori;

import java.util.Comparator;

public class AssociationRule implements Comparable<AssociationRule> {
	private ItemSetIF X, Y;

	private double confidence = 0.0;

	public AssociationRule(ItemSet i, ItemSet s) {
		this.X = i;
		this.Y = s;
	}

	public ItemSetIF getX() {
		return X;
	}

	public ItemSetIF getY() {
		return Y;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(X + "\t==>\t" + Y + "\t\t" + confidence + "\n");

		return sb.toString();

	}

	@Override
	public int hashCode() {
		return X.hashCode() * Y.hashCode() * 57;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (o == null)
			return false;
		if (o instanceof AssociationRule) {
			AssociationRule ar = (AssociationRule) o;
			return this.X.equals(ar.X) && this.Y.equals(ar.Y);
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
		if (X == null || Y == null || ar1.Y == null || ar1.Y == null)
			return false;

		if (X.size() > ar1.X.size() && Y.size() >= ar1.Y.size())
			return X.containsAll(ar1.X) && Y.containsAll(ar1.Y);
		return false;
	}

	@Override
	public int compareTo(AssociationRule o) {
		if (X.size() == o.X.size())

			if (Y.size() == o.Y.size())
				return 0;
			else if (Y.size() > o.Y.size())
				return 1;
			else
				return -1;
		else
			return X.size() > o.X.size() ? 1 : -1;

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