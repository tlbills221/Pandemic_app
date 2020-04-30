package cs505pubsubcep;

import cs505pubsubcep.CEP.CEPEngine;
import cs505pubsubcep.CEP.DBEngine;
import cs505pubsubcep.Topics.TopicConnector;
import cs505pubsubcep.httpfilters.AuthenticationFilter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;


public class Launcher {

    public static final String API_SERVICE_KEY = "12140362"; //Change this to your student id
    public static final int WEB_PORT = 8088;
    public static String inputStreamName = null;
    public static long accessCount = -1;
    public static String alerts = "";

    public static TopicConnector topicConnector;

    public static CEPEngine cepEngine = null;

    public static DBEngine dbEngine = null;

    public static void main(String[] args) throws IOException {


        System.out.println("Starting CEP...");
        //Embedded database initialization

        cepEngine = new CEPEngine();


        //START MODIFY
        inputStreamName = "PatientInStream";
        String inputStreamAttributesString = "first_name string, last_name string, mrn string, zip_code string, patient_status_code string";

        /*String outputStreamName = "PatientOutStream";
        String outputStreamAttributesString = "patient_status_code string, count long";


        String queryString = " " +
                "from PatientInStream#window.timeBatch(5 sec) " +
                "select patient_status_code, count() as count " +
                "group by patient_status_code " +
                "insert into PatientOutStream; ";

        //END MODIFY
*/
        //cepEngine.createCEP(inputStreamName, outputStreamName, inputStreamAttributesString, outputStreamAttributesString, queryString);
	   
	String outputStreamName = "countStream";
        String outputStreamAttributesString = "zip_code string, count long";


        String queryString = "from pateintInStream#window.timeBatch(15 sec) as T "+
				"join pateintInStream#window.timeBatch(30 sec) as R "+
				"select T.zipcode as zipcode, T.count() as num1, R.count() as num2 "+
				"group by zipcode "+
				"having num1 >= 2*(num2-num1) "+
				"insert into countStream;";

        //END MODIFY

        cepEngine.createCEP(inputStreamName, outputStreamName, inputStreamAttributesString, outputStreamAttributesString, queryString);
	
        System.out.println("CEP Started...");
 
	
	System.out.println("Starting Database...");
        //Embedded database initialization - added by me
        dbEngine = new DBEngine();
	dbEngine.initDB();
        System.out.println("Database Started...");

        //starting Collector
        topicConnector = new TopicConnector();
        topicConnector.connect();

        //Embedded HTTP initialization
        startServer();


        try {
            while (true) {
                Thread.sleep(5000);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void startServer() throws IOException {

        final ResourceConfig rc = new ResourceConfig()
        .packages("cs505pubsubcep.httpcontrollers")
        .register(AuthenticationFilter.class);

        System.out.println("Starting Web Server...");
        URI BASE_URI = UriBuilder.fromUri("http://0.0.0.0/").port(WEB_PORT).build();
        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);

        try {
            httpServer.start();
            System.out.println("Web Server Started...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
