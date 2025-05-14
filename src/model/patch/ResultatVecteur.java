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
	
	public double[][] versMatrice() {
	    if (this.getVecteurs().isEmpty()) {
	        return new double[0][0]; 
	    }

	    int n = this.getVecteurs().get(0).getValeurs().length; 
	    int M = this.getVecteurs().size(); 

	    double[][] matrice = new double[n][M];

	    for (int j = 0; j < M; j++) {
	        for (int i = 0; i < n; i++) {
	            matrice[i][j] = this.vecteurs.get(j).getValeurs()[i];
	        }
	    }

	    return matrice;
	}
}
