package tdb;
import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;


// pour la fac
public class Ex_1
{
	public static final String rdf_file = "departement.rdf";
    public static void main(String[] args)
    {
        // Direct way: Make a TDB-back Jena model in the named directory.
        String directory = "My_Db_dept" ;
        Dataset ds = TDBFactory.createDataset(directory) ;
        Model model = ds.getDefaultModel() ;
        // file manager pour lire et charger le fichier dans le modele
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
}