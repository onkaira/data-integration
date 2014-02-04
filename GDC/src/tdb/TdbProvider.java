package tdb;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import odbc.DataProvider;

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

public class TdbProvider {

	private Dataset ds;
	public String generateRDF(List<PopulationDep> list, String nameSpace) throws IOException{
		
		String rdf_file = "population.rdf";
		Model m = ModelFactory.createDefaultModel();
		m.setNsPrefix("XSD", XSD.getURI());
		m.setNsPrefix("pop",nameSpace);
		
		Property idDepProp = m.createProperty(nameSpace + "idDep");
		Property depNameProp = m.createProperty(nameSpace + "depName");
		Property popMoyenneProp = m.createProperty(nameSpace + "popMoyenne");
		Property nataliteProp = m.createProperty(nameSpace + "natalite");
		Resource pop = m.createResource(nameSpace + "Population");
		
		
		int i=0;
		for(PopulationDep p : list) {
			// Resource inseeDepartement = m.getResource(geo + "departement/" + t.getCodeDepartement()); GET RESOURCE FROM MODEL CREATED AND ATTACH TOURISM RESOURCE
			Resource pop_instance = m.createResource(nameSpace+"Population"+p.getIdDep());
			pop_instance.addProperty(RDF.type, pop);
			pop_instance.addProperty(idDepProp, m.createTypedLiteral(p.getIdDep(), XSD.getURI()+"string"));
			pop_instance.addProperty(depNameProp, m.createTypedLiteral(p.getDepName(), XSD.getURI()+"string"));
			pop_instance.addProperty(popMoyenneProp, m.createTypedLiteral(p.getPopMoyenne(), XSD.getURI()+"float"));
			pop_instance.addProperty(nataliteProp, m.createTypedLiteral(p.getNatalite(), XSD.getURI()+"float"));
		}
		FileOutputStream outStream = new FileOutputStream(rdf_file) ;
		try {


			//m.write(System.out, "N-TRIPLE");
			m.write(outStream, "RDF/XML");
			

		}
		finally
		{
			outStream.close();
		}
		return rdf_file;
	}
	
	public void createData(List<PopulationDep> list, String nameSpace) throws IOException{
		String directory = "My_Db_dept" ;
        ds = TDBFactory.createDataset(directory) ;
        Model model = ds.getDefaultModel();
        String rdf_file = generateRDF( list, nameSpace);
        FileManager.get().readModel( model, rdf_file );
        
        StmtIterator stmt_i = model.listStatements();
    	System.out.println("Objets des triplets du modele ");
    	System.out.println(" ========================== ");
    	while (stmt_i.hasNext())
    	{ 
    		Statement stmt = stmt_i.nextStatement();
    		RDFNode o = stmt.getObject();
    		// test noeud anonyme 
    		// triplet {U ou B x U x U ou B ou L}
    		if (o instanceof Resource)
	    	{ 
    			Resource or = (Resource) o; 
    			if (or.isAnon())
	    	System.out.println(" Noeud anonyme "+or.getId());
	    	else	System.out.println(" Nom objet "+or.getLocalName());}
    		else if (o.isLiteral())
	    	{	Literal ol = (Literal) o;
	    		System.out.println(" Valeur litteral "+ol.getLexicalForm());}
	    	}
       
        ds.close();
        
	}
	public Model getModel(List<PopulationDep> list, String nameSpace) throws IOException{
		createData(list, nameSpace);
		Model m = ds.getDefaultModel() ;
		return m;
	}
	public static void main(String[] args) {
		//TDB
//		ArrayList<PopulationDep> popList  = DataProvider.provideDataForTdb();
//		TdbProvider tdb = new TdbProvider();
//		tdb.createData(popList, "http://www.tdbPopulation.com#");
//		String directory = "My_Db_dept" ;
//		Dataset dataset = TDBFactory.createDataset(directory) ;
		ArrayList<PopulationDep> popList  = DataProvider.provideDataForTdb();
		TdbProvider tdb = new TdbProvider();
		try {
			tdb.createData(popList, "http://www.tdbPopulation.com#");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
