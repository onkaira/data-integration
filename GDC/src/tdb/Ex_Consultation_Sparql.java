package tdb;


import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.query.Query ;
import com.hp.hpl.jena.query.QueryExecution ;
import com.hp.hpl.jena.query.QueryExecutionFactory ;
import com.hp.hpl.jena.query.QueryFactory ;
import com.hp.hpl.jena.query.ResultSet ;
import com.hp.hpl.jena.query.ResultSetFormatter ;
import com.hp.hpl.jena.tdb.TDBFactory ;


public class Ex_Consultation_Sparql
{
    public static void main(String[] args)
    {
        // Direct way: Make a TDB-back Jena model in the named directory.
        String directory = "My_Db_dept" ;
        Dataset dataset = TDBFactory.createDataset(directory) ;
        
        // Potentially expensive query.
        String sparqlQueryString = "SELECT (count(*) AS ?count) { ?s ?p ?o }" ;
        // See http://incubator.apache.org/jena/documentation/query/app_api.html
        
        Query query = QueryFactory.create(sparqlQueryString) ;
        QueryExecution qexec = QueryExecutionFactory.create(query, dataset) ;
        ResultSet results = qexec.execSelect() ;
        ResultSetFormatter.out(results) ;
        qexec.close() ;

        dataset.close();
    }
}