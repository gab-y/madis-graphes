import java.io.File;
import java.io.IOException;


public class mainActivity {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//FormatGraphe graph = new FormatGraphe(new File("./graph.dot"));
		//int[][] matrice = graph.getMatrice();
		int[][] matrice ={{0, 0, 1, 1, 0},{1,1,1,0,0},{0,1,0,0,0},{1,1,0,0,0},{1,0,0,1,0}};
		FormatGraphe graph = new FormatGraphe(matrice);
		System.out.print("    ");
		for(int i=1 ; i<=5 ; i++){
			System.out.print(i+ " ");
		}
		System.out.println();
		for(int i=0 ; i<5 ; i++){
			System.out.print(i+1 + " : ");
			for(int j : matrice[i]){
				System.out.print(matrice[i][j]+ " ");
			}
			System.out.println();
		}
		
		
		//graph.ecrireGraphe("./newGraph.dot");
	}

}
