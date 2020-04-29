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
	File file = new File("mydb.db");
        System.out.println(file.delete());
	connection = DriverManager.getConnection("jdbc:sqlite:mydb.db");	
	      
      	status = 0;
      	Statement statement = connection.createStatement();
      	statement.setQueryTimeout(30);  // set timeout to 30 sec.

      	//statement.executeUpdate("drop table if exists person");
      	statement.executeUpdate("create table patient (first_name string, last_name string, mrn string, zipcode integer, patient_status_code integer)");
      	statement.executeUpdate("create table hospital (id integer, name string, address string, city string, state string, zip string, type string, beds integer, county string, countyfips integer, country string, latitude float, longitude float, naics_code integer, website string, owner string, trauma string, helipad varchar(1))");
<<<<<<< HEAD
      	statement.executeUpdate("create table zipdistance (zip_from integer, zip_to integer, distance float)");
=======
      	statement.executeUpdate("create table alerts (zipcode integer)");
>>>>>>> 7abfed395e8857dd838745b62049b8348c955389
	BufferedReader csvReader = new BufferedReader( new FileReader("src/main/java/cs505pubsubcep/CEP/hospitals.csv"));
	csvReader.readLine(); //skip 1st row
	String row;
	while ((row = csvReader.readLine()) != null) {
		row = row.replace("'", "");
		String[] data = row.split(",");
		System.out.println(data.length);
		if (data.length > 18) { //if comma in address
			data[2] = data[2] + " " +  data[3];
			data[2] = data[2].replace("\"", "");
			System.out.println(data[2]);
			System.out.println(data[3]);
			data = ArrayUtils.remove(data, 3);
		}
		data[2] = data[2].replace(",", " "); //Remove commas from address
		String queryString = "insert into hospital values(" + data[0] + ",'" + data[1] + "','" + data[2] + "','" + data[3] + "','" + data[4] + "'," + data[5] + ",'" + data[6] + "'," + data[7] + ",'" + data[8] + "'," + data[9] + ",'" + data[10] + "'," + data[11] + "," + data[12] + "," + data[13] + ",'" + data[14] + "','" + data[15] + "','" + data[16] + "','" + data[17] + "')";
		System.out.println(queryString);
		statement.executeUpdate(queryString);
	}
<<<<<<< HEAD

	//reads the kyzipdistance into zipdistance table
	BufferedReader zipReader = new BufferedReader(new FileReader("src/main/java/cs505pubsubcep/CEP/kyzipdistance.csv"));
	zipReader.readLine(); //skip 1st row
	String zipRow;
	while((zipRow = zipReader.readLine()) != null){
		String[] zipData = zipRow.split(",");
		String queryString = "insert into zipdistance values(" + zipData[0] + "," + zipData[1] + "," + zipData[2] + ")";
		statement.executeUpdate(queryString);
	}

=======
>>>>>>> 7abfed395e8857dd838745b62049b8348c955389
	//statement.executeUpdate("insert into person values(1, 'leo')");
      	//statement.executeUpdate("insert into person values(2, 'yui')");
      	/*ResultSet rs = statement.executeQuery("select * from person");
      	while(rs.next())
      	{
           // read the result set
           System.out.println("name = " + rs.getString("name"));
           System.out.println("id = " + rs.getInt("id"));
        }
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
<<<<<<< HEAD


  //FINDNEARESTHOSPITAL FUNCTION FOR OF1 AND OF2, TAKES A ZIPCODE AND A BOOLEAN STATING WHETHER
  //OR NOT THE PATIENT REQURES A LEVEL IV OR BETTER TREATMENT FACILITY, RETURNS ID OF  NEAREST
  //QUALIFIED HOSPITAL
  public int findNearestHospital(String zipcode, boolean needsIV){
	try{
	   int hospital_id = -1000;
	   boolean found = false;
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
           String status_code = "-1";
	   String zipcode = "-1";
	   int location_code = -1;
	   Statement statement = connection.createStatement();
	   statement.setQueryTimeout(30);

	   //search for patient's status code and zipcode with given mrn
	   ResultSet rs = statement.executeQuery("select patient_status_code,zipcode from patient where mrn='" + mrn + "'");
	   while(rs.next()){
	   	status_code = rs.getString("patient_status_code");
	   	zipcode = rs.getString("zipcode");
	   }

	   //convert status code to a location code - home=0, no assignment=-1, else call function to get id of nearest qualified hospital
	  if(status_code.equals("0") || status_code.equals("1") || status_code.equals("2") || status_code.equals("4")){
	        location_code = 0;
	  }
	  else if(status_code.equals("3") || status_code.equals("5")){
		location_code = findNearestHospital(zipcode, false); //no need for level IV
	  }
	  else if(status_code.equals("6")){
		location_code = findNearestHospital(zipcode, true); //search only level IV
	  }
	  else{
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

=======
  public String[] getHospital(int id) {
   String[] data = new String[3];
   try {
      	Statement statement = connection.createStatement();
      	statement.setQueryTimeout(30);  // set timeout to 30 sec.

      	ResultSet rs = statement.executeQuery("select beds, zip from hospital where hospital.id = " + Integer.toString(id));
	while (rs.next()) {
	data[0] = Integer.toString(rs.getInt("beds"));
	data[1] = Integer.toString(-1); //placeholder until other APIs are done
	data[2] = Integer.toString(rs.getInt("zip"));
	}
       }

   catch(Exception e)
       {
	System.err.println(e.getMessage());
       }
   return data;
  }


>>>>>>> 7abfed395e8857dd838745b62049b8348c955389
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
