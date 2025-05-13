package model.patch;
import java.util.ArrayList;
import java.util.List;
import model.base.Position;
import model.base.Vecteur;

public class ResultatVecteur {
	private List<Vecteur> vecteurs;
	private List<Position> positions;
	
	public ResultatVecteur() {
		this.positions = new ArrayList<>();
		this.vecteurs = new ArrayList<>();
	}
	
	public void ajouterVecteur(Vecteur vecteur, Position position) {
		this.getPositions().add(position);
		this.getVecteurs().add(vecteur);
	}

	public List<Vecteur> getVecteurs() {
		return vecteurs;
	}

	public List<Position> getPositions() {
		return positions;
	}
	
	public int taille() {
		return this.getVecteurs().size();
	}
	
	public double[][] versMatrice() { //a faire
		return new double[1][1];
	}
	

}
