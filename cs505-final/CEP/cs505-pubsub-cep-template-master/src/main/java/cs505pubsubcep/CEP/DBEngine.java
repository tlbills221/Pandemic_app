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
