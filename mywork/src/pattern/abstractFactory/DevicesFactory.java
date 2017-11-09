package pattern.abstractFactory;

public interface DevicesFactory {

	Player createPlayer();
	Recorder createRecorder();
	Media createMedia();
}
