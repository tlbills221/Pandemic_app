package cs505pubsubcep.CEP;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.lang.ClassNotFoundException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.lang.String;
import java.lang.Integer;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
public class DBEngine
{

  
  private Connection connection;
  public int posCount = 0;
  public int negCount = 0; 
  public DBEngine()
  {
    // load the sqlite-JDBC driver using the current class loader

    try
    {
      Class.forName("org.sqlite.JDBC");
      // create a database connection
      connection = DriverManager.getConnection("jdbc:sqlite:mydb.db");
    }
    catch(SQLException | ClassNotFoundException e)
    {
      // if the error message is "out of memory", 
      // it probably means no database file is found
      System.err.println(e.getMessage());
    }


  }
  public int initDB() {
      int status;
      try {
	//File file = new File("mydb.db");
        //System.out.println(file.delete());
	connection = DriverManager.getConnection("jdbc:sqlite:mydb.db");	
	      
      	status = 0;
      	Statement statement = connection.createStatement();
      	statement.setQueryTimeout(30);  // set timeout to 30 sec.

      	posCount = 0;
	negCount = 0;
	statement.executeUpdate("drop table if exists patient");
	statement.executeUpdate("drop table if exists alerts");
	statement.executeUpdate("drop table if exists hospital");
      	statement.executeUpdate("create table patient (first_name string, last_name string, mrn string, zipcode integer, patient_status_code integer)");
      	statement.executeUpdate("create table hospital (id integer, name string, address string, city string, state string, zip string, type string, beds integer, county string, countyfips integer, country string, latitude float, longitude float, naics_code integer, website string, owner string, trauma string, helipad varchar(1))");
      	//statement.executeUpdate("create table zipdistance (zip_from integer, zip_to integer, distance float)");
      	statement.executeUpdate("create table alerts (zipcode integer)");
	BufferedReader csvReader = new BufferedReader( new FileReader("src/main/java/cs505pubsubcep/CEP/hospitals.csv"));
	csvReader.readLine(); //skip 1st row
	String row;
	while ((row = csvReader.readLine()) != null) {
		row = row.replace("'", "");
		String[] data = row.split(",");
		//System.out.println(data.length);
		if (data.length > 18) { //if comma in address
			data[2] = data[2] + " " +  data[3];
			data[2] = data[2].replace("\"", "");
			//System.out.println(data[2]);
			//System.out.println(data[3]);
			data = ArrayUtils.remove(data, 3);
		}
		data[2] = data[2].replace(",", " "); //Remove commas from address
		String queryString = "insert into hospital values(" + data[0] + ",'" + data[1] + "','" + data[2] + "','" + data[3] + "','" + data[4] + "'," + data[5] + ",'" + data[6] + "'," + data[7] + ",'" + data[8] + "'," + data[9] + ",'" + data[10] + "'," + data[11] + "," + data[12] + "," + data[13] + ",'" + data[14] + "','" + data[15] + "','" + data[16] + "','" + data[17] + "')";
		//System.out.println(queryString);
		statement.executeUpdate(queryString);
	}
	statement.executeUpdate("alter table hospital add column available_beds integer");
	statement.executeUpdate("update hospital set available_beds = beds");
	/*
	//reads the kyzipdistance into zipdistance table
	BufferedReader zipReader = new BufferedReader(new FileReader("src/main/java/cs505pubsubcep/CEP/kyzipdistance.csv"));
	zipReader.readLine(); //skip 1st row
	String zipRow;
	while((zipRow = zipReader.readLine()) != null){
		String[] zipData = zipRow.split(",");
		String queryString = "insert into zipdistance values(" + zipData[0] + "," + zipData[1] + "," + zipData[2] + ")";
		System.out.println(queryString);
		statement.executeUpdate(queryString);
	}*/
       status = 1;
        }
     catch(Exception e)
    {
      status = 0;
      // if the error message is "out of memory", 
      // it probably means no database file is found
      System.err.println(e.getMessage());
    }
    return status;
  }


  //FINDNEARESTHOSPITAL FUNCTION FOR OF1 AND OF2, TAKES A ZIPCODE AND A BOOLEAN STATING WHETHER
  //OR NOT THE PATIENT REQURES A LEVEL IV OR BETTER TREATMENT FACILITY, RETURNS ID OF  NEAREST
  //QUALIFIED HOSPITAL
  public int findNearestHospital(String zipcode, boolean needsIV){
	try{
	   int hospital_id = -1000;
	   boolean found = false;
	   connection = DriverManager.getConnection("jdbc:sqlite:mydb.db");
	   Statement statement = connection.createStatement();
	   statement.setQueryTimeout(30);

	   ResultSet rs1 = statement.executeQuery("select zip_to from zipdistance where zip_from=" + zipcode);
	   while(rs1.next() && found == false){
		String zip_to = rs1.getString("zip_to");
		ResultSet rs2 = statement.executeQuery("select id,trauma from hospital where zip=" + zip_to);
		if(rs2.next()){
			String id = rs2.getString("id");
			String trauma = rs2.getString("trauma");
			if(needsIV == true){
				if(trauma.equals("LEVEL IV")){
					hospital_id = Integer.valueOf(id);
					found = true;
				}
			}
			else{
				hospital_id = Integer.valueOf(id);
				found = true;
			}
		}
	   }

	   return hospital_id;
	}
	catch(Exception e){
	   System.err.println(e.getMessage());
	   return -111;
	}
  }


  //GETPATIENTLOCATION FUNCTION FOR OF2, TAKES A PATIENT'S MRN AND RETURNS THE ID OF THEIR LOCATION
  public int getPatientLocation(String mrn){
	try{
	   //initialize variables
           int status_code = -1;
	   String zipcode = "00000";
	   int location_code = -1;
	   connection = DriverManager.getConnection("jdbc:sqlite:mydb.db");
	   Statement statement = connection.createStatement();

	   //search for patient's status code and zipcode with given mrn
	   ResultSet rs = statement.executeQuery("select patient_status_code,zipcode from patient where mrn = \"" + mrn + "\"");
	   while(rs.next()){
	   	status_code = rs.getInt("patient_status_code");
	   	zipcode = Integer.toString(rs.getInt("zipcode"));
	   }

	   //convert status code to a location code - home=0, no assignment=-1, else call function to get id of nearest qualified hospital
	   switch(status_code){
		case 0:
		case 1:
		case 2:
		case 4:
			location_code = 0;
			break;
		case 3:
		case 5:
			location_code = findNearestHospital(zipcode, false); //no need for level IV+
			break;
		case 6:
			location_code = findNearestHospital(zipcode, true); //search for only level IV+
		default:
			location_code = -11;
	   }
	   
	   //return location_code value
	   return location_code;
	}
	catch(Exception e){
	   System.err.println(e.getMessage());
	   return -100;
	}
  }

  public String[] getHospital(int id) {
   String[] data = new String[3];
   try {
      	Statement statement = connection.createStatement();
      	statement.setQueryTimeout(30);  // set timeout to 30 sec.

      	ResultSet rs = statement.executeQuery("select beds, zip, available_beds from hospital where hospital.id = " + Integer.toString(id));
	while (rs.next()) {
	data[0] = Integer.toString(rs.getInt("beds"));
	data[1] = Integer.toString(rs.getInt("available_beds"));
	data[2] = Integer.toString(rs.getInt("zip"));
	}
       }

   catch(Exception e)
       {
	System.err.println(e.getMessage());
       }
   return data;
  }


  public static void closeConnection(Connection connection) {
    try
    {
      if(connection != null) {
          connection.close();
      }
    }
    catch(SQLException e)
    {
        // connection close failed.
        System.err.println(e);
    }
  }

}
