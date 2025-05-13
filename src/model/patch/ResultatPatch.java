package model.patch;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import model.base.Position;

public class ResultatPatch implements Iterable<model.patch.ResultatPatch.PairePatchPosition>{
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

	@Override
	public Iterator<PairePatchPosition> iterator() {
        return new PatchPositionIterator();
    }
	
	
	public static class PairePatchPosition{
		private Position position;
		private Patch patch;
		
		public PairePatchPosition(Patch patch,Position position) {
			this.patch = patch;
			this.position = position;
		}
		
		  public Patch getPatch() {
	            return patch;
	        }
	        
        public Position getPosition() {
            return position;
        }
	        
	}
	
	
	
    private class PatchPositionIterator implements Iterator<PairePatchPosition> {
        private int currentIndex = 0;
        
        @Override
        public boolean hasNext() {
            return currentIndex < patches.size() && currentIndex < positions.size();
        }
        
        @Override
        public PairePatchPosition next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            PairePatchPosition pair = new PairePatchPosition(
                patches.get(currentIndex),
                positions.get(currentIndex)
            );
            
            currentIndex++;
            
            return pair;
        }
        
    }
	
}




