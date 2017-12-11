package pattern.abstractFactory;

public class CDPlayer implements Player {

	CD cDInside;

	public void accept(Media med) {
		cDInside = (CD) med;
	}

	public void play() {
		if (cDInside == null)
			System.out.println("Error: No CD.");
		else
			System.out.println(cDInside.readDisk());
	}

	
}
