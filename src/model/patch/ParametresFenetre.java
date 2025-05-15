package model.patch;

public class ParametresFenetre {
	
	private int chevauchementCombineX;
	private int chevauchementCombineY;
    private int nombreFenetresX;      
    private int nombreFenetresY;      
    private int nombreFenetresTotal;  
    private int tailleFenetreCalculee;
    
    public int getChevauchementCombineX() { return chevauchementCombineX; }
    public int getChevauchementCombineY() { return chevauchementCombineY; }
    public int getNombreFenetresX() { return nombreFenetresX; }
    public int getNombreFenetresY() { return nombreFenetresY; }
    public int getNombreFenetresTotal() { return nombreFenetresTotal; }
    public int getTailleFenetreCalculee() { return tailleFenetreCalculee; }
    
    
    public static ParametresFenetre calculerParametresFenetre(int largeurImage, int hauteurImage, int tailleFenetre){ 
    	
        ParametresFenetre params = new ParametresFenetre();
       
        
    	params.nombreFenetresX = (int) Math.ceil(largeurImage/tailleFenetre)+1;
    	params.nombreFenetresY = (int) Math.ceil(hauteurImage/tailleFenetre)+1;
    	
    	int restePixelsLargeur = largeurImage % tailleFenetre;
    	int restePixelsHauteur = hauteurImage % tailleFenetre;
    	
    	params.chevauchementCombineX = tailleFenetre-restePixelsLargeur;
    	params.chevauchementCombineY = tailleFenetre-restePixelsHauteur;
    	params.tailleFenetreCalculee = tailleFenetre;
    	params.nombreFenetresTotal = params.nombreFenetresX * params.nombreFenetresY;
        
        	
        
     
        
        
		return params;
    }

    
	
    
}
