package hbase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import odbc.DataProvider;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;


public class HbaseProvider {
	
	private Configuration conf = HBaseConfiguration.create();
	private ArrayList<SanteDep> list;

	public void createData(ArrayList<SanteDep> santeList) throws IOException{
		
		System.out.println( "starting..." );

		System.out.println( "connecting..." );
		
		System.out.println( "getting config..." );
		Configuration hc = HBaseConfiguration.create();

		HTableDescriptor ht = new HTableDescriptor( "santeDep" );
		ht.addFamily( new HColumnDescriptor( "infos" ) );
		HBaseAdmin hba = new HBaseAdmin( hc );
		 
		if(!hba.tableExists(new String("santeDep").getBytes())){
			hba.createTable( ht );
			putData(hc,santeList) ;
		}
		
		
		

		
	}
	
	@SuppressWarnings("deprecation")
	private  List<SanteDep> getData(String tableName, String columnFamily) throws IOException
	{
		ArrayList<SanteDep> list = new ArrayList<SanteDep>();
		SanteDep sante = null;
		Filter famFilter = new FamilyFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(columnFamily)));
	
		HTable htable = new HTable(conf, tableName);
		Scan scan = new Scan();
	    scan.setFilter(famFilter);
	    ResultScanner scanner = htable.getScanner(scan);
	    for (Result result : scanner) {
	    	sante = new SanteDep();
	    	
	    	for (KeyValue entry : result.raw()) {
	    		
	    		switch (Bytes.toString(entry.getQualifier())) {
	    		case "idDep":
	    			sante.setIdDep(Bytes.toString(entry.getValue()));
	    			break;
	    		case "depName":
	    			sante.setDepName(Bytes.toString(entry.getValue()));
	    			break;
	    		case "nbHopitaux":
	    			sante.setNbHopitaux(Integer.parseInt(Bytes.toString(entry.getValue())) );
	    			break;
	    		case "nbPharmacies":
	    			sante.setNbPharmacies(Integer.parseInt(Bytes.toString(entry.getValue())));
	    			break;

				default:
					break;
				}
	    	}
	    	list.add(sante);
	    }
	    
	    scanner.close();
	    htable.close();
	    
		return list;
	}
	
	public String generateRDF(String nameSpace) throws IOException{
		List<SanteDep> list = getData("santeDep", "infos");
		String rdf_file = "Sante.rdf";
		
		Model m = ModelFactory.createDefaultModel();
		m.setNsPrefix("XSD", XSD.getURI());
		m.setNsPrefix("sdep",nameSpace);
		
		Property idDepProp = m.createProperty(nameSpace + "idDep");
		Property depNameProp = m.createProperty(nameSpace + "depName");
		Property nbHopitauxProp = m.createProperty(nameSpace + "nbHopitaux");
		Property nbPharmaciesProp = m.createProperty(nameSpace + "nbPharmacies");
		Resource sante = m.createResource(nameSpace + "Sante");
		
		for(SanteDep s : list) {
			// Resource inseeDepartement = m.getResource(geo + "departement/" + t.getCodeDepartement()); GET RESOURCE FROM MODEL CREATED AND ATTACH TOURISM RESOURCE
			Resource sante_instance = m.createResource(nameSpace+"SanteDep"+s.getIdDep());
			sante_instance.addProperty(RDF.type, sante);
			sante_instance.addProperty(idDepProp, m.createTypedLiteral(s.getIdDep(), XSD.getURI()+"string"));
			sante_instance.addProperty(depNameProp, m.createTypedLiteral(s.getDepName(), XSD.getURI()+"string"));
			sante_instance.addProperty(nbHopitauxProp, m.createTypedLiteral(s.getNbHopitaux(), XSD.getURI()+"int"));
			sante_instance.addProperty(nbPharmaciesProp, m.createTypedLiteral(s.getNbPharmacies(), XSD.getURI()+"int"));
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
	
	private void putData(Configuration hc,  ArrayList<SanteDep> santeList) throws IOException{
		System.out.println("add sante data...");
		for (SanteDep santeDep : santeList) {
			Put SanteInstance = new Put(new String(santeDep.getDepName()).getBytes());
			SanteInstance.add(new String("infos").getBytes(), new String("idDep").getBytes(), new String(santeDep.getIdDep()).getBytes());
			SanteInstance.add(new String("infos").getBytes(), new String("depName").getBytes(), new String(santeDep.getDepName()).getBytes());
			String nbHopitauxToStr = String.valueOf(santeDep.getNbHopitaux());
			String nbPharmaciesToStr = String.valueOf(santeDep.getNbPharmacies());
			SanteInstance.add(new String("infos").getBytes(), new String("nbHopitaux").getBytes(), new String(nbHopitauxToStr).getBytes());
			SanteInstance.add(new String("infos").getBytes(), new String("nbPharmacies").getBytes(), new String(nbPharmaciesToStr).getBytes());
			System.out.println( "creating table...sante " );
			
			// column family
			
			HTable table = new HTable(hc, "santeDep");
			table.put(SanteInstance);
		}
		System.out.println( "done!" );
	}
	public static void main(String[] args) {
		ArrayList<SanteDep> s = DataProvider.provideDataForHbase();
		HbaseProvider hp = new HbaseProvider();
		try {
			hp.createData(s);
			hp.generateRDF("http://www.sante/com#");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
