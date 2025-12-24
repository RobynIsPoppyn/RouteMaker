package Model.Maps;
import Model.Beans.Waypoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class MapboxAccessor implements MapAccessor {

    static String apiKey = "sk.eyJ1IjoicmRlY2FzdHJvNDcwIiwiYSI6ImNtaml5YWt6MjF0eTAzZXB5cW15NWg5bnYifQ.06voI91DC3JjaaMnVD7u1g";

    //Temporary test values

    //Intended for Http requests: obtain a route from Google Maps using prespecified fields
    public HttpResponse<String> obtainMapRoute(ArrayList<Double> latitudes, ArrayList<Double> longitudes){

        ArrayList<Waypoint> temp = new ArrayList<>();
        for (int i = 0; i < latitudes.size(); i++){
            temp.add(new Waypoint(latitudes.get(0), longitudes.get(0)));
        }

        return obtainMapRoute(temp);

    }

    @Override
    //obtain a route from Google Maps using prespecified fields (base implementation)
    public HttpResponse<String> obtainMapRoute(ArrayList<Waypoint> Intermediates){
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.mapbox.com/directions/v5/mapbox/driving/" +
                                    buildCoordinates(Intermediates) +
                                    "?annotations=distance%2Cduration" +
                                    "&steps=true" +
                                    "&overview=full&language=en&access_token="
                                    + apiKey)
                            )
                    .GET()
                    .build();

            System.out.println("Request sent to MapboxAPI: " + request);
            return HttpClient
                    .newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());

        }catch (IOException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return null;

    }

    //obtain a route from Google Maps using prespecified fields
    public HttpResponse<String> obtainMapRoute(Waypoint origin, Waypoint destination){
        ArrayList<Waypoint> temp = new ArrayList<>();
        temp.add(origin);
        temp.add(destination);
        return obtainMapRoute(temp);

    }




    //Builds the JSON body
    public static String buildCoordinates(ArrayList<Waypoint> waypoints){

      String ret = "";

      for (int i = 0; i < waypoints.size(); i++){
          ret += waypoints.get(i).longitude() + "%2C" + waypoints.get(i).latitude();
          if (i != waypoints.size() - 1){
              ret += "%3B";
          }
          System.out.println(ret);
      }



        return ret;
    }

}

