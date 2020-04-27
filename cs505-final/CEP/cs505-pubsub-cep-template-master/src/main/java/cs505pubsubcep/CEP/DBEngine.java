package cs505pubsubcep.CEP;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.lang.ClassNotFoundException;

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

    /*finally
    {
      try
      {
        if(connection != null)
          connection.close();
      }
      catch(SQLException e)
      {
        // connection close failed.
        System.err.println(e);
      }
    }*/
    initDB(connection);
  }
  public static void initDB(Connection connection) {
      try {
	      
      	int status = 0;
      	Statement statement = connection.createStatement();
      	statement.setQueryTimeout(30);  // set timeout to 30 sec.

      	//statement.executeUpdate("drop table if exists person");
      	statement.executeUpdate("create table patient (first_name string, last_name string, mrn string, zipcode integer, patient_status_code integer)");
      	statement.executeUpdate("create table hospital (id integer, name string, address string, city string, state string, zip string, type string, beds integer, county string, countyfips integer, country string, latitude float, longitude float, naics_code integer, website string, owner string, trauma string, helipad varchar(1)");
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
     catch(SQLException e)
    {
      // if the error message is "out of memory", 
      // it probably means no database file is found
      System.err.println(e.getMessage());
    }
  }   

}
