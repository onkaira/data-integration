package jenaengine;

import hbase.HbaseProvider;
import hbase.SanteDep;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import neo4j.Neo4jProvider;
import neo4j.NodePath;

import org.openjena.atlas.io.IndentedWriter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.query.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import odbc.DataProvider;
import tdb.PopulationDep;
import tdb.TdbProvider;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;

import d2rq.D2rqProvider;



@SuppressWarnings("unused")
public class Jena {
	public static final String NL = System.getProperty("line.separator") ;
	public static void main(String[] args) throws IOException, NumberFormatException
	{
		int choix =0;
		//creer le modele global

		String ns = "http://www.projet.fr/";
		String ns_ville = "http://www.ville-neo4j.com/";
		String ns_sdep = "http://www.sante-dep-hbase.com/";
		String ns_pop = "http://www.tdbPopulation.com/";
		String reg = "PREFIX reg: <"+ns+"region#"+">" ;
		String cog = "PREFIX cog: <"+ns+"commune#"+">" ;
		String loc = "PREFIX loc: <"+ns+"localite#"+">" ;
		String dep = "PREFIX dep: <"+ns+"departement#"+">" ;
		String isf = "PREFIX isf: <"+ns+"impot#"+">" ;
		String arm = "PREFIX arm: <"+ns+"arrondissement_municipal#"+">" ;
		String rdf = "PREFIX rdf: <"+RDF.getURI()+">" ;
		String ville = "PREFIX neo: <"+ns_ville+">" ;
		String sdep = "PREFIX sdep: <"+ns_sdep+">" ;
		String pop = "PREFIX pop: <"+ns_pop+">" ;

		//****************PROVIDERS******************
		//
		//D2RQ
		D2rqProvider d2rq = new D2rqProvider();
		Model d2rqModel = d2rq.getModel();
		Model model =ModelFactory.createDefaultModel();

		//NEO4J
		ArrayList<NodePath> villePaths  = DataProvider.provideDataForNeo4j();
		Neo4jProvider neoProvider = new Neo4jProvider(villePaths, ns_ville,"neo");
		neoProvider.createNeo4jGraphDb();
		FileManager.get().readModel( model, neoProvider.generateRdf() ); 

		//HBASE
		ArrayList<SanteDep> s = DataProvider.provideDataForHbase();
		HbaseProvider hbProvider = new HbaseProvider();
		//Model mHbase;
		try {
			hbProvider.createData(s);
			FileManager.get().readModel( model, hbProvider.generateRDF(ns_sdep) ); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//TDB
		ArrayList<PopulationDep> popList  = DataProvider.provideDataForTdb();
		TdbProvider tdb = new TdbProvider();
		try {
			FileManager.get().readModel(model, tdb.generateRDF(popList, ns_pop));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String rdf_file ="global.rdf";
		FileOutputStream outStream = new FileOutputStream(rdf_file) ;
		try {


			//m.write(System.out, "N-TRIPLE");
			model.write(outStream, "RDF/XML");


		}
		finally
		{
			outStream.close();
		}



		try {
			choix = createMenu();
			if(choix>0 && choix<10){
				do{
					System.out.println("Resultats :");
					switch (choix) {
					case 1: //Les noms de départements, le nombre d'hopitaux et de pharmacies par département, la population et la natalité moyenne par département  et la region. (tdb - hbase - d2rq)
						String queryStringConst1 = cog + NL + dep + NL +rdf + NL +reg+NL+
						"CONSTRUCT WHERE { "  
						+"?d dep:nomDepartement ?departement ."
						+"?r reg:nomRegion  ?region ."
						+"?d dep:depInReg ?r }";
						System.out.println(queryStringConst1);
						model.add(QueryExecutionFactory.create(queryStringConst1, d2rqModel).execConstruct());
						String queryString1 = reg + NL + dep + NL +rdf + NL +sdep+NL+pop+NL+
						"SELECT   ?departement  ?nbHopitaux ?nbPharmacies ?popMoyenne ?natalite ?region "  
						+ "WHERE { "
						+"?d dep:nomDepartement ?departement ."
						+"?r reg:nomRegion  ?region ."
						+"?d dep:depInReg ?r ."
						+"?p pop:depName ?departement ."
						+"?p pop:popMoyenne ?popMoyenne ."
						+"?p pop:natalite ?natalite ."
						+"?s sdep:depName ?departement ."
						+"?s sdep:nbHopitaux ?nbHopitaux ."
						+"?s sdep:nbPharmacies ?nbPharmacies }";
						
						execQuery( queryString1, model, choix);
						break;
					case 2: //2. Les noms des grande villes et leurs regions. (neo4j - d2rq)
						String queryStringConst2 = cog + NL + reg + NL +rdf + NL +
						"CONSTRUCT WHERE { "
						+"?c cog:nomCommune ?commune ."
						+"?r reg:nomRegion ?n ."
						+"?c cog:communeInReg  ?r }";
						System.out.println(queryStringConst2);
						model.add(QueryExecutionFactory.create(queryStringConst2, d2rqModel).execConstruct());
						String queryString2 = cog + NL + reg + NL +rdf + NL +ville+NL+
						"SELECT  DISTINCT ?commune ?region "  
						+ "WHERE {?c cog:nomCommune ?commune ."
						+"?r reg:nomRegion ?region ."
						+"?c cog:communeInReg  ?r ."
						+"?v neo:name  ?commune ."
						+"?b neo:NEAR_TO_BIG_CITY  ?v }";
						//+"FILTER (?n=\"Languedoc-Roussillon\") }";
						execQuery( queryString2, model, choix);
						break;
					case 3: //3. Le nombre de communes proches d'une grande ville par departement. (neo4j - d2rq)
						String queryStringConst3 = cog + NL + dep + NL +rdf + NL +
						"CONSTRUCT WHERE { "
						+"?c cog:nomCommune ?commune ."
						+"?d dep:nomDepartement ?departement ."
						+"?c cog:communeInDep  ?d }";
						
						System.out.println(queryStringConst3);
						model.add(QueryExecutionFactory.create(queryStringConst3, d2rqModel).execConstruct());
						String queryString3 = cog + NL + dep + NL +rdf + NL +ville+NL+
						"SELECT  ?departement (COUNT(?commune) as ?nbrCommune) "  
						+ "WHERE {?c cog:nomCommune ?commune ."
						+"?d dep:nomDepartement ?departement ."
						+"?c cog:communeInDep  ?d ."
						+"?v neo:name  ?commune ."
						+"?v neo:NEAR_TO_BIG_CITY  ?b }"
						+"GROUP BY ?departement";
						execQuery( queryString3, model, choix);
						break;				
					case 4: //4. Les communes qui correspondent a des chef lieux de region. (d2rq)
						String queryString4 = cog + NL + reg + NL +rdf + NL +
						"SELECT   ?commune "  
						+ "WHERE {?c cog:nomCommune ?commune ."
						+"?c cog:aChefLieu ?cl ."
						+"?c cog:communeInReg  ?r ."
						+"FILTER (?cl=\"4\") }";
						execQuery( queryString4, d2rqModel, choix);
						break;
					case 5: //5. Les localites soumises a l’ISF ainsi que le nom de leur departement et de leur region d’appartenance. (d2rq)
						String queryString5 = cog + NL + reg + NL +rdf + NL +isf + NL +loc+NL+dep+NL+arm+NL+
						"SELECT   ?localite ?departement ?region "  
						+ "WHERE { "
						+"{?a arm:nomARM ?localite ."
						+"?l loc:codeInseeARM ?a  ."
						+"?i isf:codeInseeLocalite ?l ."
						+"?r reg:nomRegion ?region ."
						+"?c cog:communeInReg  ?r ."
						+"?d dep:nomDepartement ?departement ."
						+"?c cog:communeInDep  ?d ."
						+"?a arm:armInCommune  ?c }"
						+"UNION{?c cog:nomCommune ?localite ."
						+"?l loc:codeInseeCommune ?c ."
						+"?i isf:codeInseeLocalite ?l ."
						+"?r reg:nomRegion ?region ."
						+"?c cog:communeInReg  ?r ."
						+"?d dep:nomDepartement ?departement ."
						+"?c cog:communeInDep  ?d }"
						+ "}";
						execQuery( queryString5, d2rqModel, choix);
						break;

					case 6:
						//les communes découpée en fraction cantonale sont les plus grandes d'où cdc=1*/ (d2rq).
						String queryString6 = cog + NL + reg + NL +rdf + NL +isf + NL +loc+NL+dep+NL+
						"SELECT   ?commune "  
						+ "WHERE {?c cog:nomCommune ?commune ."
						+"?r reg:nomRegion ?region ."
						+"?c cog:communeInReg  ?r ."
						+"?d dep:nomDepartement ?departement ."
						+"?c cog:communeInDep  ?d ." 
						+"?l loc:codeInseeCommune ?c ."
						+"?i isf:codeInseeLocalite  ?l ."
						+"?c cog:CDCCommune  ?cdc ."
						+"?i isf:nbrRedevable  ?nbr ."
						+"FILTER (?cdc=\"1\" && ?nbr<100) }";
						execQuery( queryString6, d2rqModel, choix);
						break;
					case 7: //7. Donner l'impot moyen, la population moyenne et la natalité moyenne par region. (d2rq - tdb)
						String queryStringConst7 = cog + NL + reg + NL +rdf + NL +isf + NL +loc+NL+dep+NL+
						"CONSTRUCT WHERE { "
						+"?c cog:nomCommune ?commune ."
						+"?r reg:nomRegion ?region ."
						+"?c cog:communeInReg  ?r ."
						+"?d dep:nomDepartement ?departement ."
						+"?c cog:communeInDep  ?d ." 
						+"?l loc:codeInseeCommune ?c ."
						+"?i isf:codeInseeLocalite  ?l ."
						+"?i isf:impotMoyen  ?impMoyen }";
						
						System.out.println(queryStringConst7);
						model.add(QueryExecutionFactory.create(queryStringConst7, d2rqModel).execConstruct());
						String queryString7 = cog + NL + reg + NL +rdf + NL +isf + NL +loc+NL+dep+NL+pop+NL+
						"SELECT   ?region (AVG(?impMoyen) as ?impMoyenReg) (AVG(?popMoyenne) as ?popMoyenneReg) (AVG(?natalite) as ?nataliteMoyenneReg) "  
						+ "WHERE {?c cog:nomCommune ?commune ."
						+"?r reg:nomRegion ?region ."
						+"?c cog:communeInReg  ?r ."
						+"?d dep:nomDepartement ?departement ."
						+"?c cog:communeInDep  ?d ." 
						+"?l loc:codeInseeCommune ?c ."
						+"?i isf:codeInseeLocalite  ?l ."
						+"?p pop:depName ?departement ."
						+"?p pop:popMoyenne ?popMoyenne ."
						+"?p pop:natalite ?natalite ."
						+"?i isf:impotMoyen  ?impMoyen }"
						+"GROUP BY ?region ";
						execQuery( queryString7, model, choix);
						break;
					case 8: //8. Donner le nombre d'hopitaux et la natalité moyenne des département dont les communes dépassent la moyenne d'impot de la region, les noms des département et les noms de régions . (tdb - hbase - d2rq)
						
						String queryStringConst8 = cog + NL + reg + NL +rdf + NL +isf + NL +loc+NL+dep+NL+
						"CONSTRUCT WHERE { "
						+"?c cog:nomCommune ?commune ."
						+"?r reg:nomRegion ?region ."
						+"?c cog:communeInReg  ?r ."
						+"?d dep:nomDepartement ?departement ."
						+"?c cog:communeInDep  ?d ." 
						+"?l loc:codeInseeCommune ?c ."
						+"?i isf:codeInseeLocalite  ?l ."
						+"?i isf:patrimoineM  ?patrtmoineMoyen ."
						+"?i isf:impotMoyen  ?impMoyen }";
						
						System.out.println(queryStringConst8);
						model.add(QueryExecutionFactory.create(queryStringConst8, d2rqModel).execConstruct());
						String queryString8 = cog + NL + reg + NL +rdf + NL +isf + NL +loc+NL+dep+NL+pop+NL+sdep+
						"SELECT  ?natalite ?nbHopitaux ?impMoyen ?impMoyenReg  ?departement ?region "  
						+ "WHERE {?c cog:nomCommune ?commune ."
						+"?r reg:nomRegion ?region ."
						+"?c cog:communeInReg  ?r ."
						+"?d dep:nomDepartement ?departement ."
						+"?c cog:communeInDep  ?d ." 
						+"?l loc:codeInseeCommune ?c ."
						+"?i isf:codeInseeLocalite  ?l ."
						+"?p pop:depName ?departement ."
						+"?p pop:natalite ?natalite ."
						+"?s sdep:depName ?departement ."
						+"?s sdep:nbHopitaux ?nbHopitaux ."
						+"?i isf:impotMoyen  ?impMoyen ."
						+"{SELECT   ?region (AVG(?impMoyen) as ?impMoyenReg) "  
						+ "WHERE {?c cog:nomCommune ?commune ."
						+"?r reg:nomRegion ?region ."
						+"?c cog:communeInReg  ?r ."
						+"?l loc:codeInseeCommune ?c ."
						+"?i isf:codeInseeLocalite  ?l ."
						+"?p pop:depName ?departement ."
						+"?p pop:natalite ?natalite ."
						+"?s sdep:depName ?departement ."
						+"?s sdep:nbHopitaux ?nbHopitaux ."
						+"?i isf:impotMoyen  ?impMoyen }"
						+"GROUP BY ?region}"
						+"FILTER(?impMoyen > ?impMoyenReg)}";
						execQuery( queryString8, model, choix);
						break;
					case 9: //9. Donner le nom des 10 départements ayant le meilleur taux de population moyenne, le nom de leurs regions et leur partimoine moyen. (tdb - d2rq)
						String queryStringConst9 = cog + NL + reg + NL +rdf + NL +isf + NL +loc+NL+dep+NL+
						"CONSTRUCT WHERE { "
						+"?c cog:nomCommune ?commune ."
						+"?r reg:nomRegion ?region ."
						+"?c cog:communeInReg  ?r ."
						+"?d dep:nomDepartement ?departement ."
						+"?c cog:communeInDep  ?d ." 
						+"?l loc:codeInseeCommune ?c ."
						+"?i isf:codeInseeLocalite  ?l ."
						+"?i isf:patrimoineM  ?patrtmoineMoyen ."
						+"?i isf:impotMoyen  ?impMoyen }";
						
						System.out.println(queryStringConst9);
						model.add(QueryExecutionFactory.create(queryStringConst9, d2rqModel).execConstruct());
						String queryString9 = cog + NL + reg + NL +rdf + NL +isf + NL +loc+NL+dep+NL+pop+NL+
						"SELECT ?departement ?popMoyenne ?natalite ?region  (AVG(?patrtmoineMoyen) as ?patrtmoineMoyenDep) "  
						+ "WHERE {?c cog:nomCommune ?commune ."
						+"?r reg:nomRegion ?region ."
						+"?c cog:communeInReg  ?r ."
						+"?d dep:nomDepartement ?departement ."
						+"?c cog:communeInDep  ?d ." 
						+"?l loc:codeInseeCommune ?c ."
						+"?i isf:codeInseeLocalite  ?l ."
						+"?p pop:depName ?departement ."
						+"?p pop:popMoyenne ?popMoyenne ."
						+"?p pop:natalite ?natalite ."
						+"?i isf:patrimoineM  ?patrtmoineMoyen }"
						+"GROUP BY ?departement ?departement ?popMoyenne ?natalite ?region "
						+"ORDER BY DESC(?popMoyenne) "
						+"LIMIT 10";
						execQuery( queryString9, model, choix);
						break;
					default:
						System.out.println("Choix incorrect !");
						break;
					}

					choix = createMenu();
				}while(choix!=10);

			} 
		}catch (NumberFormatException e) {
			System.out.println("Erreur de saisie, valeur probablement non numerique : "+NL+e.getMessage());
		}

	}
	public static void execQuery( String strQuery, Model m, int choix) throws IOException{

		Query query = QueryFactory.create(strQuery) ;
		query.serialize(new IndentedWriter(System.out,true )) ;
		QueryExecution qexec = QueryExecutionFactory.create(query, m) ;
		QueryExecution qexec2 = QueryExecutionFactory.create(query, m) ;


		System.out.println("Les elements du modele : ") ;
		FileOutputStream outStream = new FileOutputStream("R"+choix+".txt") ;
		try {


			ResultSet rs = qexec.execSelect() ;
			ResultSet rs2 = qexec2.execSelect();
			//Le résultat de la requête est stoqué dans un fichier .txt dans la racine du projet
			ResultSetFormatter.out(outStream, rs, query);
			ResultSetFormatter.out(System.out, rs2, query);


		}
		finally
		{
			qexec.close() ;
			outStream.close();
		}
	}
	public static int createMenu( ) throws IOException{
		InputStreamReader ipsr=new InputStreamReader(System.in);
		BufferedReader br=new BufferedReader(ipsr);

		String menu = NL+
				"1. Les noms de départements, le nombre d'hopitaux et de pharmacies par département, la population et la natalité moyenne par département  et la region. (tdb - hbase - d2rq)"+NL  
				+"2. Les noms des grande villes et leurs regions. (neo4j - d2rq)"+NL
				+"3. Le nombre de communes proche d'une grande ville par departement. (neo4j - d2rq)"+NL
				+"4. Les communes qui correspondent a des chef lieux de region. (d2rq)"+NL
				+"5. Les localites soumises a l’ISF ainsi que le nom de leur departement et de leur region d’appartenance. (d2rq)"+NL
				+"6. Les communes de plus de 20000 habitants qui ont moins de 100 habitants soumis a l'ISF. (d2rq)"+NL 
				+"7. Donner l'impot moyen, la population moyenne et la natalité moyenne par region. (d2rq - tdb)"+NL
				+"8. Donner le nombre d'hopitaux et la natalité moyenne des département dont les communes dépassent la moyenne d'impot de la region, les noms de départements et les nom de régions . (tdb - hbase - d2rq)"+NL
				+"9. Donner le nom des 10 départements ayant le meilleur taux de population moyenne, le nom de leurs regions et leur partimoine moyen. (tdb - d2rq)"+NL
				+"10. Quitter"+NL;
		System.out.println(menu);
		System.out.println("Entrer le numero d'une requête / Qitter: ");
		return Integer.parseInt(br.readLine());

	}

}
