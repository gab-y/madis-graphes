import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class mainActivity {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//FormatGraphe graph = new FormatGraphe(new File("./graph.dot"));
		//int[][] matrice = graph.getMatrice();
		int[][] matrice ={{0,0,1,1,0},{1,1,1,0,0},{0,1,0,0,0},{1,1,0,0,0},{1,0,0,1,0}};
		
		affiche(matrice);

		fermetureTransitive(matrice);
		
		affiche(matrice);
		
		FormatGraphe graph = new FormatGraphe(matrice);
		//graph.ecrireGraphe("./newGraph.dot");
		customEcrireGraphe("./newGraph.dot",matrice);
	}
	
	public static void affiche(int[][] matrice){
		int longueur = matrice.length;
		System.out.println();
		System.out.print("    ");
		for(int i=1 ; i<=5 ; i++){
			System.out.print(i+ " ");
		}
		System.out.println();
		for(int i=0 ; i<longueur ; i++){
			System.out.print(i+1 + " : ");
			for(int j=0; j<longueur ; j++){
				System.out.print(matrice[i][j]+ " ");
			}
			System.out.println();
		}
	}
	
	public static void fermetureTransitive (int[][] matrice){
		int longueur = matrice.length;
		boolean tryagain = true;
		while(tryagain){
			tryagain = false;
			for(int i=0 ; i<longueur ; i++){
				for(int j=0; j<longueur ; j++){
					if(matrice[j][i]>0){//on lit par colonne (= origine). Si une destination existe...
						for(int k=0; k<longueur; k++){
							if(matrice[i][k]>0){//...on lit la ligne des noeuds ayant pour destination (= ligne) l'origine ci-dessus
								if(matrice[j][k]<1){
									matrice[j][k]=1;//quand c'est le cas, on met à 1 le croisement de l'origine et de la destination ci-dessus
									tryagain = true;
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static void customEcrireGraphe(String nomFichier, int[][] matrice){
		File fichier = new File(nomFichier);
		int longueur = matrice.length;
		try{
			FileWriter out = new FileWriter(fichier);
			out.write("digraph G {\n");
			for (int i = 0; i < longueur; i++) {
				for (int j = 0; j < longueur; j++) {
					if(matrice[j][i]>0){
						out.write("\t"+i);
						out.write(" -> "+j);
						out.write(";\n");
					}
				}
			}
			out.write("}");
			out.close();
		}catch(Exception e){
			System.err.println("Erreur d'ecriture");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
