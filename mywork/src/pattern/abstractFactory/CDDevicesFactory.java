package pattern.abstractFactory;

public class CDDevicesFactory implements DevicesFactory {
	public Player createPlayer() {
		return new CDPlayer();
	}

	public Recorder createRecorder() {
		return new CDRecorder();
	}

	public Media createMedia() {
		return new CD();
	}
}
