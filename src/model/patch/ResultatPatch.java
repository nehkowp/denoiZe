package model.patch;
import java.util.ArrayList;
import java.util.List;
import model.base.Position;

public class ResultatPatch {
	private List<Patch> patches;
	private List<Position> positions;
	
	public ResultatPatch() {
		this.patches = new ArrayList<>();
		this.positions = new ArrayList<>();
	}
	
	public void ajouterPatch(Patch patch, Position position) {
		this.getPatches().add(patch);
		this.getPositions().add(position);
	}

	public List<Patch> getPatches() {
		return patches;
	}

	public List<Position> getPositions() {
		return positions;
	}
	
	public int taille() {
		return patches.size();
	}
}
