package pattern.abstractFactory;

public class TapeRecorder implements Recorder {

	Tape tapeInside;

	public void accept(Media med) {
		tapeInside = (Tape) med;
	}

	public void record(String sound) {
		if (tapeInside == null)
			System.out.println("error: insert tape");
		else
			System.out.println(tapeInside.readTape());
	}
}
