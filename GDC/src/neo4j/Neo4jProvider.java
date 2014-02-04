package neo4j;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import odbc.DataProvider;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.impl.util.FileUtils;



/**
 * Class Neo4jProvider
 */
public class Neo4jProvider {

	//
	// Fields
	//
	public static final String NL = System.getProperty("line.separator") ;
	private static final String DB_PATH = "/var/lib/neo4j/data/graph.db";
	private HashMap<String,String[]> nodesAndProperties;
	private GraphDatabaseService graphDb;
	private String nodeResult="";
	private HashMap<String, String> rdfMap = new HashMap<String, String>();
	private ExecutionEngine engine;
	private ExecutionResult result;
	private ArrayList<NodePath> villePaths = new ArrayList<NodePath>();
	private Node firstNode;
	private Node secondNode;
	private Object relationship;
	private String nameSpace;
	private String prefixe;
	private Label l;

	public Neo4jProvider(ArrayList<NodePath> villePaths, String nameSpace, String prefixe) {
		super();
		
		this.villePaths = villePaths;
		this.nodesAndProperties = new HashMap<String, String[]>();
		this.nodesAndProperties.put(villePaths.get(0).getN1().toString(), new String[]{villePaths.get(0).getN1().getPropName()+";"+villePaths.get(0).getN1().getDataType()});
		this.nodesAndProperties.put(villePaths.get(0).getN2().toString(), new String[]{villePaths.get(0).getN2().getPropName()+";"+villePaths.get(0).getN2().getDataType()});
		//System.out.println(villePaths.get(0).getN1().toString());
		this.nameSpace = nameSpace;
		this.prefixe = prefixe;
		initNodes();

	}
	private void initNodes(){
		clearDb();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		registerShutdownHook( graphDb );
		try ( Transaction tx = graphDb.beginTx() )
		{
			registerShutdownHook( graphDb );
			 l = new Label() {
				
				@Override
				public String name() {
					// TODO Auto-generated method stub
					return villePaths.get(0).getN1().toString();
				}
			};
			firstNode = graphDb.createNode(l);
	        firstNode.setProperty( villePaths.get(0).getN1().getPropName(), "start");
	        secondNode = graphDb.createNode(l);
	        secondNode.setProperty( villePaths.get(0).getN2().getPropName(), "end");
	        tx.success();
		}
		
	}

	private void setNodesAndProperties ( HashMap<String,String[]> newVar ) {
		nodesAndProperties = newVar;
	}

	private HashMap<String,String[]> getNodesAndProperties ( ) {
		return nodesAndProperties;
	}
	public String generateRdf( ) throws IOException
	{
		String rdf_file = "Ville.rdf";
		engine = new ExecutionEngine( graphDb );

		

		try ( Transaction ignored = graphDb.beginTx() )
		{
			//result = engine.execute( "start n=node:Movie  return n, n.name" );
			result = engine.execute( "match(node1)-[relation]->(node2) return labels(node1) as n1,node1 as id1, type(relation) as r,labels(node2) as n2,node2 as id2" );
			// END SNIPPET: execute
			// START SNIPPET: items
			
			String rdf="<rdf:RDF"+NL;
			
			rdf+="\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""+NL;
			rdf+="\txmlns:"+prefixe+"=\""+nameSpace +"\""+NL;
			rdf+="\txmlns:XSD=\"http://www.w3.org/2001/XMLSchema#\" >"+NL;
			String n1 ="";
			String n2="";
			String r="";
			String id1="";
			String id2="";


			for ( Map<String, Object> row : result )
			{
				
				for ( Entry<String, Object> column : row.entrySet() )
				{
					//nodeResult += column.getKey() + ": " + column.getValue() + "; ";
					//System.out.println(nodeResult);
					switch (column.getKey()) {
					case "r": 
						r=column.getValue().toString();
						break;

					case "n1": 
						n1=removeBraketsHelper(column.getValue().toString());
						break;

					case "n2": 
						n2=removeBraketsHelper(column.getValue().toString());
						break;

					case "id1": 
						id1 =removeBraketsHelper(column.getValue().toString());
						break;

					case "id2": 
						id2=removeBraketsHelper(column.getValue().toString());
						break;

					default:																																																														
						break;
					}
				}
				if(!rdfMap.containsKey(n2+id2)){

					addDescription( n2, id2,nameSpace, prefixe);
				}
				if(!rdfMap.containsKey(n1+id1)){
					addDescription( n1, id1,nameSpace, prefixe);
				}
				rdfMap.put(n1+id1, rdfMap.get(n1+id1)+generateRdfAddRelation(r, n2, id2, nameSpace, prefixe));



				//nodeResult += "\n";
			}
			//System.out.println(nodeResult);
			
			rdf+=unMap();
			
			rdf+="</rdf:RDF>";
			FileOutputStream outStream = new FileOutputStream(rdf_file) ;
			try {

				outStream.write(new String(rdf).getBytes());
				
			}
			finally
			{
				outStream.close();
			}
			return rdf_file;
			
			
		}
	}
	private String unMap(){
		String rdf="";
		Iterator<String> it = rdfMap.values().iterator();
		String strKey = null;
		while (it.hasNext()) {
			String value = it.next();
			for(Map.Entry entry: rdfMap.entrySet()){
				if(value.equals(entry.getValue())){
					strKey = (String) entry.getKey();
					break; //breaking because its one to one map
				}
			}
			rdfMap.put(strKey, value+closeDescription());
			rdf+=rdfMap.get(strKey);


		}
		return rdf;
		
	}
	private void addDescription(String node, String id, String nameSpace, String prefixe){
		rdfMap.put(node+id, generateRdfAddNode(node, id, nameSpace));
		Map<String, Object> params = new HashMap<String, Object>();
		//System.out.println(id);
		params.put( "id", Integer.parseInt(id) );
		//System.out.println("id : "+params.get("id"));
		String[] properties = nodesAndProperties.get(node);
		
		for (int i = 0; i < properties.length; i++) {
			String propName = getPropertyNameHelper(properties[i]);
            String propType = getPropertyTypeHelper(properties[i]);
            String propValue;
			String query = "START n=node({id}) RETURN n, n."+propName;
			 result = engine.execute( query, params);
			//System.out.println(query);
			Iterator<Node> n_column = result.columnAs( "n");
            for ( Node n : IteratorUtil.asIterable( n_column ) )
            {
            	propValue = n.getProperty(propName).toString();
            	rdfMap.put(node+id, rdfMap.get(node+id)+generateRdfAddDataProperty(propName, propValue, propType, nameSpace,prefixe));
            }
		}
	}
	
	private String getPropertyNameHelper(String prop){
		return prop.split(";")[0];
	}
	private String getPropertyTypeHelper(String prop){
		return prop.split(";")[1];
	}

	private String generateRdfAddRelation(String r, String n2, String id2, String nameSpace, String prefixe) {
		// TODO Auto-generated method stub
		String rdf="";
		rdf+="\t\t<"+prefixe+":"+r+" rdf:resource=\""+nameSpace+n2+id2+"\" />"+NL;
		return rdf;
	}

	private String generateRdfAddDataProperty(String propName, String propValue, String propType, String nameSpace, String prefixe) {
		// TODO Auto-generated method stub
		String rdf="";
		rdf+="\t\t<"+prefixe+":"+propName+" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#"+propType+"\">"+ propValue+"</"+prefixe+":"+propName+">"+NL;
		return rdf;
	}

	private String removeBraketsHelper(String str){
		String res = str.substring(str.indexOf('[')+1, str.indexOf(']'));

		return res;

	}

	private String generateRdfAddNode(String node, String id, String nameSpace){
		String rdf ="";
		rdf+="\t<rdf:Description rdf:about=\""+nameSpace+node+id+"\" >"+NL;
		rdf+="\t\t<rdf:type rdf:resource=\""+nameSpace+node+"\" />"+NL;
		return rdf;
	}
	private String closeDescription(){
		String rdf ="";
		rdf+="\t</rdf:Description>"+NL;
		return rdf;
	}
	private static enum RelTypes implements RelationshipType
    {
        NEAR_TO_BIG_CITY
    }
	public void createNeo4jGraphDb()
	{
//		clearDb();
//		
//		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		registerShutdownHook( graphDb );
	
		try ( Transaction tx = graphDb.beginTx() )
		{
			//initialiser avec premier ligne du tableau.

			//for (NodePath path : villePaths) {
				for (NodePath path : villePaths) {
					
				
					//array_type array_element = villePaths[i];
					
					if(!path.getN1().getName().equals(firstNode.getProperty(path.getN1().getPropName()))){
						firstNode = graphDb.createNode(l);
					}
					if(!path.getN2().getName().equals(secondNode.getProperty(path.getN2().getPropName()))){
						secondNode = graphDb.createNode(l);
						
					}
					firstNode.setProperty( path.getN1().getPropName(), path.getN1().getName());
					secondNode.setProperty( path.getN2().getPropName(), path.getN2().getName());
					relationship = firstNode.createRelationshipTo( secondNode, RelTypes.NEAR_TO_BIG_CITY );
					
				}
            
            tx.success();
		}
	}

	private void clearDb()
	{
		try
		{
			FileUtils.deleteRecursively( new File( DB_PATH ) );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}



	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			@Override
			public void run()
			{
				graphDb.shutdown();
			}
		} );
	}

	public static void main(String[] args) {
		//HashMap<String, String[]> nodesAndProperties = new HashMap<String, String[]>();

//		nodesAndProperties.put("Movie", new String[]{"title;string", "year;date"});
//		nodesAndProperties.put("Actor", new String[]{"name;string"});
		ArrayList<NodePath> villePaths  = DataProvider.provideDataForNeo4j();
		Neo4jProvider neo = new Neo4jProvider(villePaths, "http://www.neo4j.com/","neo");
		neo.createNeo4jGraphDb();
		try {
			String s = neo.generateRdf();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}