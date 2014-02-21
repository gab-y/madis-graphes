import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *  <P>Cette permet de transformer le format de description d'un graphe.
 *  <BR>Elle accepte comme format d'entrée :
 *  <ul>
 *  	<li>Un objet de la classe Graphe</li>
 *  	<li>Un fichier au format Graphviz</li>
 *  	<li>Une matrice d'adjacence entière</li>
 *  </ul>
 *  <BR>Elle permet de récupérer le graphe sous différents formats:
 *  <ul>
 *  	<li>Matrice d'adjacence</li>
 *  	<li>Tableaux d'arcs</li>
 *  	<li>Tableaux de successeurs</li>
 *  	<li>Un fichier Graphviz</li>
 *  	<li>Un objet de la classe Graphe</li>
 * </ul>
 * <BR>Dans cette classe le graphe est enregistré dans la classe Graphe qui contient
 *   la liste des noeuds.
 * <P>A propos du format Grapviz, la plupart des fichiers au format graphviz
 *  peuvent être lus.Toutefois, certains fichiers peuvent ne pas fonctionner,
 *  s'ils emploient des éléments syntaxiques non reconnus par ce programme.
 * <P>Par exemple, la syntaxe :
 * <ul><li>noeud -- noeud -- noeud -- ...</li>
 * <li>ou noeud -> noeud -> noeud -> ...</li></ul>
 * <BR>n'est pas reconnue.
 * <P>Une valuation des arcs est lues à l'aide du champ d'option des noeuds
 * "label". La valeur doit être dans l'une des 2 formes suivantes :
 *  <ul>
 *  	<li>"flux"</li>
 *  	<li>"flux[capacité]"</li>
 *  </ul>
 * les valeurs devant être entières.
 * Si aucune valuation n'est précisée la valeu par défaut du flux est de 1,
 * celle de la capacité est de
 * </P>
 *
 * @author Louis-Marie
 */
public class FormatGraphe {
	/**
	 * Indique si le graphe est orienté (true) ou non (false).
	 */
	protected boolean oriente; // Orienté (true) ou non-orienté (false)
	/**
	 * Décrit le graphe à l'aide d'objets Java.
	 */
	public Graphe graphe;
	
//**********************************************************************
	
	/**
	 * Construit un objet FormatGraphe à partir d'un objet Graphe
	 *
	 * @param G		Le graphe.
	 */
	public FormatGraphe(Graphe G){
		graphe = G;
		oriente = G.isOriente();
	}
	
	/**
	 * Construit un nouvel objet FormatGraphe à l'aide de la matrice
	 *  d'adjacence.
	 *
	 * @param mat	Matrice d'ajacence
	 */
	public FormatGraphe(int[][] mat){
		this.oriente = !(estSymetrique(mat));
		Graphe graphe = new Graphe(oriente);
		int nbSommets = mat.length;
		for (int i = 0; i < nbSommets; i++) {
			Noeud courant = new Noeud(new Integer(i).toString());
			graphe.addNoeud(courant);
			for (int j = 0; j < nbSommets; j++){
				Noeud succCourant = new Noeud(new Integer(j).toString());
				courant.addSucc(succCourant, mat[i][j]);
			}
		}
	}
	
	/**
	 * Construit un nouvel objet FormatGraphe à l'aide d'un fichier au
	 *  format Graphviz (fichier texte).
	 *
	 * @param fichier	nom du fichier
	 * @throws IOException	En cas d'erreur dans la lecteure du fichier.
	 */
	public FormatGraphe(File fichier) throws IOException{
		// Initialisation de la lecture
		FileReader read = new FileReader(fichier);
		BufferedReader in = new BufferedReader(read);
		
		// Recherche d'une ligne commencant par 'g' ou 'd'
		String ligne;
		char c;
		do{
			ligne = in.readLine();
			c = ligne.charAt(0);
			if(ligne == null){
				throw new IOException("Format de fichier erroné.\n" +
						"Pas de balise graph ou digraph");
			}
		}while(c!='d' && c!='g');
		
		// Determination du type de graphe : orienté ou non-orienté
		StringTokenizer st = new StringTokenizer(ligne);
		String champ = st.nextToken();
		if(champ.equals("graph")){
			oriente = false;
		}
		else if(champ.equals("digraph")){
			oriente = true;
		}
		else{
			throw new IOException("Format de fichier erroné.\n" +
					"Pas de balise graph ou digraph");
		}
		
		graphe = new Graphe(oriente);
		
		//Lecture des successeurs des sommets
		if(!oriente){
			// 1er cas : graphe non oriente
			ligne = in.readLine();
			while(!ligne.equals("}")){
				String options = null;
				String nomNoeud = null;
				int flux = 1;
				int capacite = -1;
				String delimiteurs;
				
				//Traitement de la chaine d'options
				int indexOpt = ligne.indexOf('[');
				if(indexOpt!=-1) {
					options = ligne.substring(indexOpt);
					ligne = ligne.substring(0,indexOpt);
					boolean finit = false;
					while(!finit){
						int index = options.indexOf('l');
						options = options.substring(index+1);
						if(options.startsWith("abel")){
							delimiteurs = "\"";
							st = new StringTokenizer(options, delimiteurs);
							st.nextToken();
							options = st.nextToken();
							if(options.contains("[")){
								delimiteurs = "[]";
								st = new StringTokenizer(options, delimiteurs);
								try{
									flux = new Integer(st.nextToken());
								}catch(Exception e){flux = 1;}
								capacite = new Integer(st.nextToken());
							}
							else{
								try{
									flux = new Integer(options);
								}catch(Exception e){flux = 1;}
							}
							finit = true;
						}
						else if(index == -1){
							finit = true;
						}
					}
				}
				
				// Traitement des successeurs
				delimiteurs = " \t{;}=";
				st = new StringTokenizer(ligne, delimiteurs);
				if(st.hasMoreTokens()){
					nomNoeud= st.nextToken();
					if(st.hasMoreTokens()){
						String operande = st.nextToken();
						if(operande.equals("--")){
							Noeud courant;
							if(graphe.getNoeud(nomNoeud)!=null){
								courant = graphe.getNoeud(nomNoeud);
							}
							else{
								courant = new Noeud(nomNoeud);
								graphe.addNoeud(courant);
							}
							while(st.hasMoreTokens()){
								Noeud succ;
								String nomSucc = st.nextToken();
								if(graphe.getNoeud(nomSucc)!=null){
									succ = graphe.getNoeud(nomSucc);
								}
								else{
									succ = new Noeud(nomSucc);
									graphe.addNoeud(succ);
								}
								courant.addSucc(succ,flux,capacite);
								succ.addSucc(courant,flux,capacite);
							}
						}
						//Si l'operande est inconnu ne rien faire
					}
					else if(!nomNoeud.equals("node")){
						Noeud courant;
						if(graphe.getNoeud(nomNoeud)==null){
							courant = new Noeud(nomNoeud);
							graphe.addNoeud(courant);
						}
					}
				}
				ligne = in.readLine();
			}
		}
		else{
			// 2eme cas : graphe oriente
			ligne = in.readLine();
			while(!ligne.equals("}")){
				String options = null;
				String nomNoeud = null;
				int flux = 1;
				int capacite = -1;
				String delimiteurs;
				
				//Traitement de la chaine d'options
				int indexOpt = ligne.indexOf('[');
				if(indexOpt!=-1) {
					options = ligne.substring(indexOpt);
					ligne = ligne.substring(0,indexOpt);
					boolean finit = false;
					while(!finit){
						int index = options.indexOf('l');
						options = options.substring(index+1);
						if(options.startsWith("abel")){
							delimiteurs = "\"";
							st = new StringTokenizer(options, delimiteurs);
							st.nextToken();
							options = st.nextToken();
							if(options.contains("[")){
								delimiteurs = "[]";
								st = new StringTokenizer(options, delimiteurs);
								try{
									flux = new Integer(st.nextToken());
								}catch(Exception e){flux = 1;}
								capacite = new Integer(st.nextToken());
							}
							else{
								try{
									flux = new Integer(options);
								}catch(Exception e){flux = 1;}
							}
							finit = true;
						}
						else if(index == -1){
							finit = true;
						}
					}
				}
				
				// Traitement des successeurs
				delimiteurs = " \t{;}=";
				st = new StringTokenizer(ligne, delimiteurs);
				if(st.hasMoreTokens()){
					nomNoeud= st.nextToken();
				
					if(st.hasMoreTokens()){
						String operande = st.nextToken();
						if(operande.equals("->")){
							Noeud courant;
							if(graphe.getNoeud(nomNoeud)!=null){
								courant = graphe.getNoeud(nomNoeud);
							}
							else{
								courant = new Noeud(nomNoeud);
								graphe.addNoeud(courant);
							}
							while(st.hasMoreTokens()){
								Noeud succ;
								String nomSucc = st.nextToken();
								if(graphe.getNoeud(nomSucc)!=null){
									succ = graphe.getNoeud(nomSucc);
								}
								else{
									succ = new Noeud(nomSucc);
									graphe.addNoeud(succ);
								}
								courant.addSucc(succ,flux,capacite);
							}
						}
						//Si l'operande est inconnu ne rien faire
					}
					else if(!nomNoeud.equals("node")){
						Noeud courant;
						if(graphe.getNoeud(nomNoeud)==null){
							courant = new Noeud(nomNoeud);
							graphe.addNoeud(courant);
						}
					}
				}
				ligne = in.readLine();
			}
		}
		in.close();
		read.close();
	}
	
	/**
	 * Fournit la matrice d'adjacence du graphe.
	 *
	 * @return	Matrice d'adjacence
	 */
	public int[][] getMatrice(){
		int nbSommets = graphe.getNbSommets();
		int[][] matriceAdjacence = new int[nbSommets][nbSommets];
		for (int i = 0; i < nbSommets; i++) {
			Noeud courant = graphe.getNoeud(i);
			System.out.print("noeud n° "+i+ " ");
			for (int j = 0; j < courant.getNbSucc(); j++) {
				Noeud succ = courant.getSucc(j);
				int indexSucc = graphe.indexOf(succ);
				System.out.print(indexSucc);
				matriceAdjacence[i][indexSucc] = 1;//courant.getFlux(j);
			}
			System.out.println();
		}
		return matriceAdjacence;
	}
	
	/**
	 * Fournit un tableau contenant les noms des sommets du graphe.
	 *
	 * @return	Liste des sommets
	 */
	public String[] getListesommets(){
		return graphe.getListesommets();
	}
	
	/**
	 * Fournit une représentation du graphe sous la forme d'un tableau arc.
	 * <P>Il y a en fait 2 tableaux représentés par des vecteurs de
	 *  chaines de caractères.
	 * <P>Le premier contient les noeuds de départ des arcs.<br>
	 * Le second contient les noeuds d'arrivé des arcs.
	 * <P>Les vecteurs doivent être passés vides en paramètres.
	 *
	 * @param depart	Liste des noeuds départ d'arcs	
	 * @param arrivee	Liste des noeuds arrivée des arcs.
	 */
	public void getTableauxArcs(Vector<String> depart, Vector<String> arrivee){
		for (int i = 0; i < graphe.getNbSommets(); i++) {
			Noeud courant = graphe.getNoeud(i);
			for (int j = 0; j < courant.getNbSucc(); j++) {
				depart.add(courant.getNom());
				arrivee.add(courant.getSucc(j).getNom());
			}
		}
	}
	
	/**
	 *  Fournit une représentation du graphe sous la forme d'un tableau
	 *  des successeurs.
	 * <P>Le vecteur passé en paramètre contient la liste des successeurs
	 * de chaque noeud. Il doit être vide au départ.
	 * <P>Le tableau renvoyé contient le nombre de successeurs de chaque noeud.
	 *
	 * @param succ	Liste des successeurs de chaque noeud.
	 * @return	Nombre de successeurs de chaque noeud.
	 */
	public int[] getTableauxSucc(Vector<String> succ){
		int nbSommets = graphe.getNbSommets();
		int[] nbSucc = new int[nbSommets];
		for (int i = 0; i < nbSommets; i++) {
			Noeud courant = graphe.getNoeud(i);
			nbSucc[i] = courant.getNbSucc();
			for (int j = 0; j < courant.getNbSucc(); j++) {
				succ.add(courant.getSucc(j).getNom());
			}
		}
		return nbSucc;
	}
	
	/**
	 * Teste si une matrice d'entier est symétrique.
	 *
	 * @param mat	Matrice à tester
	 * @return	Booleen indiquant le résultat.
	 */
	public boolean estSymetrique(int[][] mat){
		boolean sym = true;
		for (int i = 0; i < mat.length; i++) {
			for (int j = i+1; j < mat.length; j++) {
				if(mat[i][j] != mat[j][i]){
					sym = false;
				}
			}
		}
		return sym;
	}
	
	/**
	 * Transpose une matrice d'entier
	 *
	 * @param mat	Matrice à transposer
	 * @return	Transposée de la matrice
	 */
	public int[][] transpose(int[][] mat){
		int [][]trans = new int[mat.length][mat.length];
		for (int i = 0; i < mat.length; i++) {
			for (int j = i+1; j < mat.length; j++) {
				trans[i][j] = mat[j][i];
			}
		}
		return trans;
	}
	
	/**
	 * Ecrit le graphe dans un fichier texte au format Graphviz
	 *
	 * @param nomFichier
	 */
	public void ecrireGraphe(String nomFichier){
		File fichier = new File(nomFichier);
		try{
			FileWriter out = new FileWriter(fichier);
			if(oriente){
				out.write("digraph G {\n");
				for (int i = 0; i < graphe.getNbSommets(); i++) {
					Noeud courant = graphe.getNoeud(i);
					if(courant.getNbSucc()!=0){
						for (int j = 0; j < courant.getNbSucc(); j++) {
							out.write("\t"+courant.getNom());
							out.write(" -> ");
							out.write(courant.getSucc(j).getNom());
							int flux = courant.getFlux(j);
							int capacite = courant.getCapacite(j);
							if(flux!= 1 || capacite!=-1){
								out.write(" [label = \""+flux);
								if(capacite!=-1){
									out.write("["+capacite+"]");
								}
								out.write("\"]");
							}
							out.write(";\n");
						}
					}
					else{
						out.write("\t"+courant.getNom());
						out.write(";\n");
					}
				}
				out.write("}");
			}
			else{
				int[][] matrice = getMatrice();
				int nbSommets = matrice.length;
				out.write("graph G {\n");
				
				for (int i = 0; i < nbSommets; i++) {
					Noeud courant = graphe.getNoeud(i);
					if(courant.getNbSucc()!=0){
						for (int j = i; j < nbSommets; j++) {
							if(matrice[i][j]!=0){
								out.write("\t"+courant.getNom());
								out.write(" -- ");
								out.write(graphe.getNoeud(j).getNom());
								int flux = matrice[i][j];
								if(flux!= 1){
									out.write(" [label = \""+flux);
									out.write("\"]");
								}
								out.write(";\n");
							}
						}
					}
					else{
						out.write("\t"+courant.getNom());
						out.write(";\n");
					}
				}
				out.write("}");
			}
			out.close();
		}catch(Exception e){
			System.err.println("Erreur d'ecriture");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		File fichier = new File("graph.dot");
		try{
			FormatGraphe b = new FormatGraphe(fichier);
			int[][] mat = b.getMatrice();
			for (int i = 0; i < b.graphe.getNbSommets(); i++) {
				for (int j = 0; j < b.graphe.getNbSommets(); j++) {
					System.out.print(mat[i][j]+" ");
				}
				System.out.println();
			}
			for (int i = 0; i < b.graphe.getNbSommets(); i++) {
				System.out.println(b.graphe.getNoeud(i).getNom());
			}
			b.ecrireGraphe("graph.dot");
		}catch(IOException e){
			System.err.println("erreur");
			System.err.println(e.getMessage());
		}
	}

}
