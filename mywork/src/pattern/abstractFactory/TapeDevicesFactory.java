package pattern.abstractFactory;

public class TapeDevicesFactory implements DevicesFactory {
	@Override
	public Player createPlayer() {
		return new TapePlayer();
	}

	public Recorder createRecorder() {
		return new TapeRecorder();
	}

	public Media createMedia() {
		return new Tape();
	}
}
