package d2rq;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.openjena.atlas.io.IndentedWriter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDF;

import de.fuberlin.wiwiss.d2rq.jena.ModelD2RQ;


public class D2rqProvider
{
	public Model getModel(){
		return new ModelD2RQ("file:mapping.n3");
	}
}