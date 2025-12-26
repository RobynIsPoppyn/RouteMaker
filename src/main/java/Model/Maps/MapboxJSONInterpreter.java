package Model.Maps;

import Model.Beans.Route;
import Model.Beans.Step;
import Model.Beans.Waypoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;


public class MapboxJSONInterpreter extends MapJSONInterpreter {
    //Creates a Route Java Object from Google Map's API Output, giving it default name "Route"
    public Route getRoute(String JSONInput){

        return getRoute("Route", JSONInput);
    }

    @Override
    //Creates a Route Java Object from Google Map's API Output and names it
    public Route getRoute(String name, String JSONInput) {

        // JSONArray o = new JSONObject(JSONInput).getJSONArray("routes");
        JSONObject input = new JSONObject(JSONInput);
        if (!input.get("code").equals("Ok")){
            System.out.println("Error from mapbox api response: " + input.get("code"));
            return null;
        }
        JSONObject relevantRoute = (JSONObject) (new JSONObject(JSONInput).getJSONArray("routes").get(0));
        if (relevantRoute == null) {
            throw new IllegalArgumentException("JSON Argument is not formatted correctly. " +
                    "\nRequires a JSONArray with key \"routes\"");
        }

        Route ret = new Route(
                name,
                new LinkedList<Step>(),
                (String) relevantRoute.get("duration").toString(),
                relevantRoute.get("distance").toString());



        JSONArray legs = (((JSONObject) relevantRoute)
                .getJSONArray("legs"));


        JSONArray stepsList = new JSONArray();

        //For each leg in the route, which is defined by each successive pair of coordinates entered,
        //we iterate through and add its steps to the Route object
        for (int i = 0; i < legs.length(); i++) {
            //iterate through the array of steps given by the json, then remove the first entry
            //We read the start and end locations, after casting them to json objects
            stepsList = ((JSONObject)legs.get(i)).getJSONArray("steps");
            while(stepsList.iterator().hasNext()){
                JSONObject currManeuver = stepsList.getJSONObject(0).getJSONObject("maneuver");
                JSONObject nextManeuver = null;

                Waypoint start = new Waypoint(
                        currManeuver.getJSONArray("location").getDouble(0),
                        currManeuver.getJSONArray("location").getDouble(1));

                //The final step's type is always arrive, so assign the last step's start and end to be itself
                if (currManeuver.get("type").equals("arrive")){
                    ret.getSteps().add(
                            new Step(start, start,
                            stepsList.getJSONObject(0).getInt("duration"),
                            currManeuver.getString("instruction")));
                }
                //Otherwise, we look at the next step's start to see the current step's end
                else {
                    nextManeuver = stepsList.getJSONObject(1).getJSONObject("maneuver");
                    Waypoint end = new Waypoint(
                            nextManeuver.getJSONArray("location").getDouble(0),
                            nextManeuver.getJSONArray("location").getDouble(1));
                    ret.getSteps().add(new Step(start, end, 0, currManeuver.getString("instruction")));
                }
                stepsList.remove(0);
            }


        }

        return ret;
    }





}
