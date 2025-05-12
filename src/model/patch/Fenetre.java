package model.patch;
import model.base.Img;
import model.base.Position;

public class Fenetre {
	private Img image;
	private Position position;
	
	public Fenetre(Img image, Position position) {
		this.image = image;
		this.position = position;
	}

	public Img getImage() {
		return image;
	}

	public Position getPosition() {
		return position;
	}
	
}
