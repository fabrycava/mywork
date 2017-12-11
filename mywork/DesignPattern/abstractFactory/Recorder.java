package pattern.abstractFactory;

public interface Recorder {
	void accept(Media med);

	void record(String sound);
}
