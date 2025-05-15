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
       
        
        int pgcdValeur = pgcd(largeurImage,hauteurImage);
        if(pgcdValeur == 1) {
        	
        	params.nombreFenetresX = (int) Math.ceil(largeurImage/tailleFenetre);
        	params.nombreFenetresY = (int) Math.ceil(hauteurImage/tailleFenetre);
        	
        	int restePixelsLargeur = largeurImage % tailleFenetre;
        	int restePixelsHauteur = hauteurImage % tailleFenetre;
        	
        	params.chevauchementCombineX = tailleFenetre-restePixelsLargeur;
        	params.chevauchementCombineY = tailleFenetre-restePixelsHauteur;
        	params.tailleFenetreCalculee = tailleFenetre;
        	params.nombreFenetresTotal = params.nombreFenetresX * params.nombreFenetresY;
        	

        }else {
        	params.chevauchementCombineX = 0;
        	params.chevauchementCombineY = 0;
        	params.tailleFenetreCalculee = pgcdValeur;
        	params.nombreFenetresX = hauteurImage / pgcdValeur;
        	params.nombreFenetresY = largeurImage / pgcdValeur;
            params.nombreFenetresTotal = params.nombreFenetresX * params.nombreFenetresY;
        }
     
        
        
		return params;
    }

    
    public static int pgcd(int a, int b) {
	   if (b==0) return a;
	   return pgcd(b,a%b);
	}
	
	
    
}
