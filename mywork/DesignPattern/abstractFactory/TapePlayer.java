package pattern.abstractFactory;

public class TapePlayer implements Player {
	Tape tapeInside;

	public void accept(Media med) {
		tapeInside = (Tape) med;
	}

	public void play() {
		if (tapeInside == null)
			System.out.println("Error: Insert a tape.");
		else
			System.out.println(tapeInside.readTape());
	}
}
