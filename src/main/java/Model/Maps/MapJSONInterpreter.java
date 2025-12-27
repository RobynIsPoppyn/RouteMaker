package Model.Maps;

import Model.Beans.Route;
import Model.Beans.Step;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public abstract class MapJSONInterpreter {

    public abstract Route getRoute(String name, String JSONInput);

    //public abstract String routesToJSON(ArrayList<Route> routes);

    public static String routesToJSON(ArrayList<Route> routes){

        try{
            JSONObject result = new JSONObject("{\"routes\":[]}");
            int i = 0;


            for (Route r : routes) {
                //Create a route then insert it into routes
                JSONObject currRoute = new JSONObject();
                currRoute.put("id", r.id());
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
                    currStep.put("endLong", s.startpoint().longitude());
                    currStep.put("instruction", s.instruction());
                    currStep.put("durationSec", s.durationSec());
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

    public static String routesToJSON(Route route){
        ArrayList<Route> temp = (new ArrayList<Route>());
        temp.add(route);
        return routesToJSON(temp);
    }
}
