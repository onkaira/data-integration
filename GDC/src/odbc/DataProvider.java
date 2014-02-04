package odbc;
import java.sql.*;
import java.util.ArrayList;

import tdb.PopulationDep;
import hbase.*;
import neo4j.*;
public class DataProvider {
	static String url="jdbc:oracle:thin:@localhost:1521:xe";
	static String username = "onk";
	static String password = "onk";

	public static  ArrayList<NodePath> provideDataForNeo4j(){

		String sqlQuery = "SELECT * from NEAR_TO_BIG_CITY";
		Connection connection;
		ArrayList<NodePath> villePathList = new ArrayList<NodePath>();
		try {
			connection = DriverManager.getConnection(url, username, password);
			Statement statement = connection.createStatement();
			ResultSet res = statement.executeQuery(sqlQuery);

			while (res.next()) {
				System.out.println("--------------VILLE INFOS-------------------");
				System.out.println("com1 : "+res.getString("com1"));
				System.out.println("distance : "+ res.getFloat("distance"));
				System.out.println("com2 : "+ res.getString("com2"));

				NodePath p = new NodePath();
				NodeVille n1 = new NodeVille();
				NearToBigCityRelation rel = new NearToBigCityRelation();
				NodeVille n2 = new NodeVille();

				n1.setName(res.getString("com1"));
				n1.setDataType("string");
				n1.setPropName("name");
				rel.setName("NEAR_TO_BIG_CITY");
				rel.setDistance(res.getFloat("distance"));
				rel.setDatatype("float");
				n2.setName(res.getString("com2"));
				n2.setDataType("string");
				n2.setPropName("name");
				p.setN1(n1);
				p.setN2(n2);
				p.setRel(rel);

				villePathList.add(p);

			}
			connection.close();
		} catch (SQLException e) {
			System.err.println(e);
		}
		return villePathList;
	}



	public static ArrayList<SanteDep> provideDataForHbase(){



		String sqlQuery = "SELECT * from SANTE_DEP";
		Connection connection;
		ArrayList<SanteDep> santeList = new ArrayList<SanteDep>();
		try {
			connection = DriverManager.getConnection(url, username, password);
			Statement statement = connection.createStatement();
			ResultSet res = statement.executeQuery(sqlQuery);

			while (res.next()) {
				System.out.println("--------------SANTE INFOS-------------------");
				System.out.println("idDep : "+ res.getString("idDep"));
				System.out.println("depName : "+ res.getString("depName"));
				System.out.println("nbHopitaux : "+ res.getInt("nbHopitaux"));
				System.out.println("nbPharmacies : "+res.getInt("nbPharmacies"));

				SanteDep s = new SanteDep();
				s.setIdDep(res.getString("idDep"));
				s.setDepName(res.getString("depName"));
				s.setNbHopitaux(res.getInt("nbHopitaux"));
				s.setNbPharmacies(res.getInt("nbPharmacies"));

				santeList.add(s);

			}
			connection.close();
		} catch (SQLException e) {
			System.err.println(e);
		}
		return santeList;
	}

	public static ArrayList<PopulationDep> provideDataForTdb(){



		String sqlQuery = "SELECT * from POP_DEP";
		Connection connection;
		ArrayList<PopulationDep> popList = new ArrayList<PopulationDep>();
		try {
			connection = DriverManager.getConnection(url, username, password);
			Statement statement = connection.createStatement();
			ResultSet res = statement.executeQuery(sqlQuery);
			System.out.println("here");   
			while (res.next()) {
				System.out.println("------------POP INFO---------------------");
				System.out.println("idDep : "+ res.getString("idDep"));
				System.out.println("depName : "+ res.getString("depName"));
				System.out.println("popMoyenne : "+ res.getFloat("popMoyenne"));
				System.out.println("natalite : "+res.getFloat("natalite"));


				PopulationDep p = new PopulationDep();
				p.setIdDep(res.getString("idDep"));
				p.setDepName(res.getString("depName"));
				p.setPopMoyenne(res.getFloat("popMoyenne"));
				p.setNatalite(res.getFloat("natalite"));

				popList.add(p);

			}
			connection.close();
		} catch (SQLException e) {
			System.err.println(e);
		}
		return popList;
	}

	public static void main(String[] args) {

//			ArrayList<SanteDep> s = provideDataForHbase();
		//	ArrayList<NodePath> n = provideDataForNeo4j();
		ArrayList<PopulationDep> p = provideDataForTdb();
	}

}

