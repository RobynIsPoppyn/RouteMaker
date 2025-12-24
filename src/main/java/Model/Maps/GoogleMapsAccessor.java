package Model.Maps;
import Model.Beans.Waypoint;
import Utilities.Logging;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class GoogleMapsAccessor implements MapAccessor{

    static String apiKey = "AIzaSyDuAjCLvW7pgVxTKFIvlOlVT9EoMLBG1xY";

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
                    .uri(URI.create("https://routes.googleapis.com/directions/v2:computeRoutes"))
                    .header("Content-Type", "application/json")
                    .header("X-Goog-FieldMask",
                            "routes.distanceMeters," +
                                    "routes.duration," +
                                    "routes.legs.steps.startLocation," +
                                    "routes.legs.steps.endLocation," +
                                    "routes.legs.steps.navigationInstruction")
                    .header("X-Goog-Api-Key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(buildPolylineRequestBody(Intermediates).toString()))
                    .build();

            System.out.println("Post to Google API successful" );
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

    public static HttpResponse<String> sendGetHttpRequest(String url){
        try{
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            return HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
        }catch (InterruptedException e){
            Logging.log(e);
        }catch (IOException e){
            Logging.log(e);
        }
        return null;
    }

    public static HttpResponse<String> getMapsDirections(Waypoint origin, Waypoint destination){
        return sendGetHttpRequest(
                "https://maps.googleapis.com/maps/api/directions/" +
                        "json?origin=" + origin +
                        "&destination=" + destination +
                        "&key=" + apiKey);

    }

    public static JSONObject buildPolylineRequestBody(Waypoint origin, Waypoint destination){
        ArrayList<Waypoint> result = new ArrayList<Waypoint>();
        result.add(origin);
        result.add(destination);
        return buildPolylineRequestBody(result);
    }


    public static JSONObject buildPolylineRequestBody(ArrayList<Waypoint> waypoints){

        //Create objects and insert their fields in a reverse tree fashion
        JSONObject request = new JSONObject();

        JSONObject JSONorigin = new JSONObject();
        JSONObject JSONdestination = new JSONObject();

        JSONObject location1 = new JSONObject();
        JSONObject location2 = new JSONObject();

        JSONObject latLng1 = new JSONObject();
        JSONObject latLng2 = new JSONObject();
        latLng1.put("latitude", waypoints.get(0).latitude());
        latLng1.put("longitude", waypoints.get(0).longitude());
        latLng2.put("latitude", waypoints.get(waypoints.size() - 1).latitude());
        latLng2.put("longitude", waypoints.get(waypoints.size() - 1).longitude());

        //Create an array of intermediates using the middle waypoints
        JSONArray JSONIntermediates = new JSONArray();
        for(int i = 1; i < waypoints.size() - 1; i++){
            JSONObject latLang = new JSONObject();
            latLang.put("latitude", waypoints.get(i).latitude());
            latLang.put("longitude", waypoints.get(i).longitude());

            JSONObject location = new JSONObject();
            location.put("latLng", latLang);
            JSONObject waypoint = new JSONObject();
            waypoint.put("location", location);
            JSONIntermediates.put(waypoint);
        }

        location1.put("latLng", latLng1);
        location2.put("latLng", latLng2);
        JSONorigin.put("location", location1);
        JSONdestination.put("location", location2);


        //Insert each of the fields to the top level object
        request.put("origin", JSONorigin);
        request.put("destination", JSONdestination);
        request.put("intermediates", JSONIntermediates);

        request.put("travelMode", "DRIVE");
        request.put("computeAlternativeRoutes", false);
        request.put("languageCode", "en-US");
        request.put("units", "METRIC");




        return request;
    }

}

