package Model.Maps;

import Model.Beans.Route;
import Model.Beans.Step;
import Model.Beans.Waypoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;


public class GoogleMapsJSONInterpreter implements MapJSONInterpreter {
    //Creates a Route Java Object from Google Map's API Output, giving it default name "Route"
    public Route getRoute(String JSONInput){

        return getRoute("Route", JSONInput);
    }

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
                        ((JSONObject)stepsList.get(0)).get("navigationInstruction").toString()));
                stepsList.remove(0);
            }


        }

        return ret;
    }

    //Turns an array list of routes into a JSON formatted string
    public String  routesToJSON(ArrayList<Route> routes){

        try{
            JSONObject result = new JSONObject("{\"routes\":[]}");
            int i = 0;


            for (Route r : routes) {
                //Create a route then insert it into routes
                JSONObject currRoute = new JSONObject();
                currRoute.put("name", r.name());
                currRoute.put("distanceMeters", r.distanceMeters());
                currRoute.put("durationSec", r.durationSec());
                result.append("routes", currRoute);

                //Iterate through a route's steps, creating a JSON object to insert into the step array
                System.out.println(result);
                for (Step s : r.getSteps()) {
                    JSONObject currStep = new JSONObject();
                    currStep.put("startLat", s.startpoint().latitude());
                    currStep.put("startLong", s.startpoint().longitude());
                    currStep.put("endLat", s.startpoint().latitude());
                    currStep.put("endLat", s.startpoint().longitude());
                    currStep.put("instruction", s.instruction());
                    ((JSONObject) ((JSONArray)result.get("routes")).get(i)).append("steps", currStep);
                }
                i++;
            }
            System.out.println((result));
            return result.toString();
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }



}
