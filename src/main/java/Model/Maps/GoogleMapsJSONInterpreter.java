package Model.Maps;

import Model.Beans.Route;
import Model.Beans.Step;
import Model.Beans.Waypoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;


public class GoogleMapsJSONInterpreter extends MapJSONInterpreter {
    //Creates a Route Java Object from Google Map's API Output, giving it default name "Route"
    public Route getRoute(String JSONInput){

        return getRoute("Route", JSONInput);
    }

    @Override
    //Creates a Route Java Object from Google Map's API Output and names it
    public Route getRoute(String name, String JSONInput){

        // JSONArray o = new JSONObject(JSONInput).getJSONArray("routes");

        JSONObject relevantRoute = (JSONObject) (new JSONObject(JSONInput).getJSONArray("routes").get(0));
        if (relevantRoute == null) {
            throw new IllegalArgumentException("JSON Argument is not formatted correctly. " +
                    "\nRequires a JSONArray with key \"routes\"");
        }

        Route ret = new Route(
                name,
                new LinkedList<Step>(),
                (String) relevantRoute.get("duration").toString(),
                relevantRoute.get("distanceMeters").toString());



        JSONArray legs = (((JSONObject) relevantRoute)
                .getJSONArray("legs"));


        JSONArray stepsList = new JSONArray();

        for (int i = 0; i < legs.length(); i++) {
            //iterate through the array of steps given by the json, then remove the first entry
            //We read the start and end locations, after casting them to json objects
            stepsList = ((JSONObject)legs.get(i)).getJSONArray("steps");
            while(stepsList.iterator().hasNext()){
                ret.getSteps().add(new Step(
                        new Waypoint((JSONObject) ( (JSONObject) (stepsList.get(0))).get("startLocation")),
                        new Waypoint((JSONObject) ( (JSONObject) (stepsList.get(0))).get("endLocation")),
                        stepsList.getJSONObject(0).getInt("durationSec"),
                        ((JSONObject)stepsList.get(0)).get("navigationInstruction").toString()));
                stepsList.remove(0);
            }


        }

        return ret;
    }




}
