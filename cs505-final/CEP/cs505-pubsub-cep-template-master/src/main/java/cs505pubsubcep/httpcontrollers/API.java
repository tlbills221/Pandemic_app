package cs505pubsubcep.httpcontrollers;

import com.google.gson.Gson;
import cs505pubsubcep.CEP.accessRecord;
import cs505pubsubcep.Launcher;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Path("/api")
public class API {

    @Inject
    private javax.inject.Provider<org.glassfish.grizzly.http.server.Request> request;

    private Gson gson;

    public API() {
        gson = new Gson();
    }

    //check local
    //curl --header "X-Auth-API-key:1234" "http://localhost:8082/api/checkmycep"

    //check remote
    //curl --header "X-Auth-API-key:1234" "http://[linkblueid].cs.uky.edu:8082/api/checkmycep"
    //curl --header "X-Auth-API-key:1234" "http://localhost:8081/api/checkmycep"

    //check remote
    //curl --header "X-Auth-API-key:1234" "http://[linkblueid].cs.uky.edu:8081/api/checkmycep"

    @GET
    @Path("/checkmycep")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkMyEndpoint(@HeaderParam("X-Auth-API-Key") String authKey) {
        String responseString = "{}";
        try {

            //get remote ip address from request
            String remoteIP = request.get().getRemoteAddr();
            //get the timestamp of the request
            long access_ts = System.currentTimeMillis();
            System.out.println("IP: " + remoteIP + " Timestamp: " + access_ts);

            Map<String,String> responseMap = new HashMap<>();
            if(Launcher.cepEngine != null) {

                    responseMap.put("success", Boolean.TRUE.toString());
                    responseMap.put("status_desc","CEP Engine exists");

            } else {
                responseMap.put("success", Boolean.FALSE.toString());
                responseMap.put("status_desc","CEP Engine is null!");
            }

            responseString = gson.toJson(responseMap);


        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/getaccesscount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccessCount(@HeaderParam("X-Auth-API-Key") String authKey) {
        String responseString = "{}";
        try {

            //get remote ip address from request
            String remoteIP = request.get().getRemoteAddr();
            //get the timestamp of the request
            long access_ts = System.currentTimeMillis();
            System.out.println("IP: " + remoteIP + " Timestamp: " + access_ts);

            //generate event based on access
            String inputEvent = gson.toJson(new accessRecord(remoteIP,access_ts));
            System.out.println("inputEvent: " + inputEvent);

            //send input event to CEP
            Launcher.cepEngine.input(Launcher.inputStreamName, inputEvent);

            //generate a response
            Map<String,String> responseMap = new HashMap<>();
            responseMap.put("accesscount",String.valueOf(Launcher.accessCount));
            responseString = gson.toJson(responseMap);

        } catch (Exception ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            ex.printStackTrace();

            return Response.status(500).entity(exceptionAsString).build();
        }
        return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
    }
	
   @GET
   @Path("/getteam")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getTeam(@HeaderParam("X-Auth-API-Key") String authKey) {
      String responseString = "{}";
      try {
         Map<String,String> responseMap = new HashMap<>();
 	 responseMap.put("team_name", "Umbrella Corporation");
	 responseMap.put("team_member_sids", "12140362");
	 responseMap.put("app_status_code", "0");
	 responseString = gson.toJson(responseMap);
	 //return the requested team info
      } catch (Exception ex) {
         StringWriter sw = new StringWriter();
	 ex.printStackTrace(new PrintWriter(sw));
	 String exceptionAsString = sw.toString();
	 ex.printStackTrace();

         return Response.status(500).entity(exceptionAsString).build();
      }
      return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
   }

   @GET
   @Path("/reset")
   @Produces(MediaType.APPLICATION_JSON)
   public Response reset(@HeaderParam("X-Auth-API-Key") String authKey) {
      String responseString = "{}";
      try {
         Map<String,String> responseMap = new HashMap<>();
	 String fileStr = "/mydb.db";
	 File file = new File(fileStr);
 	 responseMap.put("delete SQLite file at path", fileStr);
	 if (file.delete())
	 {
		 responseMap.put("Deleted file", "yes");
	 }
	 else responseMap.put("Deleted file", "no");
	 responseString = gson.toJson(responseMap);
	 //return the deleted file status

      } catch (Exception ex) {
         StringWriter sw = new StringWriter();
	 ex.printStackTrace(new PrintWriter(sw));
	 String exceptionAsString = sw.toString();
	 ex.printStackTrace();

         return Response.status(500).entity(exceptionAsString).build();
      }
      return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
   }


   @GET
   @Path("/zipalertlist")
   @Produces(MediaType.APPLICATION_JSON)
   public Response zipAlert(@HeaderParam("X-Auth-API-Key") String authKey) {
      String responseString = "{}";
      try {
         Map<String,String> responseMap = new HashMap<>();
 	 responseMap.put("zipalertlist", "null");
	 responseString = gson.toJson(responseMap);
	 //return the deleted file status

      } catch (Exception ex) {
         StringWriter sw = new StringWriter();
	 ex.printStackTrace(new PrintWriter(sw));
	 String exceptionAsString = sw.toString();
	 ex.printStackTrace();

         return Response.status(500).entity(exceptionAsString).build();
      }
      return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
   }


   @GET
   @Path("/alertlist")
   @Produces(MediaType.APPLICATION_JSON)
   public Response alertList(@HeaderParam("X-Auth-API-Key") String authKey) {
      String responseString = "{}";
      try {
         Map<String,String> responseMap = new HashMap<>();
 	 responseMap.put("alertlist", "null");
	 responseString = gson.toJson(responseMap);
	 //return the deleted file status
 
      } catch (Exception ex) {
         StringWriter sw = new StringWriter();
	 ex.printStackTrace(new PrintWriter(sw));
	 String exceptionAsString = sw.toString();
	 ex.printStackTrace();

         return Response.status(500).entity(exceptionAsString).build();
      }
      return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
   }

   @GET
   @Path("/getpatient/{mrn}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getPatient(@HeaderParam("X-Auth-API-Key") String authKey, @PathParam("mrn") int mrn) {
      String responseString = "{}";
      try {
         Map<String,String> responseMap = new HashMap<>();
 	 responseMap.put("getpatient", String.valueOf(mrn));
	 responseString = gson.toJson(responseMap);
	 //return the deleted file status

      } catch (Exception ex) {
         StringWriter sw = new StringWriter();
	 ex.printStackTrace(new PrintWriter(sw));
	 String exceptionAsString = sw.toString();
	 ex.printStackTrace();

         return Response.status(500).entity(exceptionAsString).build();
      }
      return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
   }

   @GET
   @Path("/gethospital/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getHospital(@HeaderParam("X-Auth-API-Key") String authKey, @PathParam("id") int id) {
      String responseString = "{}";
      try {
         Map<String,String> responseMap = new HashMap<>();
 	 responseMap.put("gethospital", String.valueOf(id));
	 responseString = gson.toJson(responseMap);
	 //return the deleted file status

      } catch (Exception ex) {
         StringWriter sw = new StringWriter();
	 ex.printStackTrace(new PrintWriter(sw));
	 String exceptionAsString = sw.toString();
	 ex.printStackTrace();

         return Response.status(500).entity(exceptionAsString).build();
      }
      return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
   }

   @GET
   @Path("/testcount")
   @Produces(MediaType.APPLICATION_JSON)
   public Response testCount(@HeaderParam("X-Auth-API-Key") String authKey) {
      String responseString = "{}";
      try {
         Map<String,String> responseMap = new HashMap<>();
 	 responseMap.put("testcount", "null");
	 responseString = gson.toJson(responseMap);
	 //return the deleted file status

      } catch (Exception ex) {
         StringWriter sw = new StringWriter();
	 ex.printStackTrace(new PrintWriter(sw));
	 String exceptionAsString = sw.toString();
	 ex.printStackTrace();

         return Response.status(500).entity(exceptionAsString).build();
      }
      return Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();
   }
}
