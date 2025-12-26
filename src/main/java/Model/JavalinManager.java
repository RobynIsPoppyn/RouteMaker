package Model;

import Model.Beans.Route;
import Model.Beans.Step;
import Model.Beans.Waypoint;
import Model.Database.DatabaseManager;
import Model.Database.QueryManager;
import Model.Maps.MapAccessor;
import Model.Maps.MapJSONInterpreter;
import Model.Maps.MapboxJSONInterpreter;
import io.javalin.http.Handler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

public class JavalinManager {

    DatabaseManager dbManager;
    Connection connection;
    MapAccessor mapAccessor;
    JavalinManager(DatabaseManager dbManager, Connection connection, MapAccessor mapAccessor){
        this.dbManager = dbManager;
        this.connection = connection;
        this.mapAccessor = mapAccessor;
    }
    public Handler getAllRoutesFromDatabase = ctx -> {
        try {
            ctx.json(DatabaseManager.getAllRoutes(connection));
        }catch(SQLException e){
            e.printStackTrace();
        }
    };

    public Handler getRouteFromMap = ctx -> {
        String waypoints = ctx.queryParam("locations");

        String [] splitLocations = waypoints.replaceAll("%2C", ",").split(";");
        for (String s : splitLocations){
            System.out.println(s);
        }
        ArrayList<Waypoint> processedWaypoints = new ArrayList<>();
        for (String s : splitLocations){
            processedWaypoints.add(new Waypoint(
                    Double.parseDouble(s.substring(0, s.indexOf(","))),
                    Double.parseDouble(s.substring(s.indexOf(",") + 1))));
        }
        ctx.json(MapJSONInterpreter.routesToJSON(
                new MapboxJSONInterpreter().getRoute(
                        mapAccessor.obtainMapRoute(processedWaypoints).body())));
    };

    //Use locations as input, making a call to map API then saves
    public Handler saveRouteFromMapToDatabase = ctx ->{
        String waypoints = ctx.queryParam("locations");


        String [] splitLocations = waypoints.replaceAll("%2C", ",").split(";");

        ArrayList<Waypoint> processedWaypoints = new ArrayList<>();
        for (String s : splitLocations){
            processedWaypoints.add(new Waypoint(
                    Double.parseDouble(s.substring(0, s.indexOf(","))),
                    Double.parseDouble(s.substring(s.indexOf(",") + 1))));
        }
        Route route = new MapboxJSONInterpreter().getRoute(
                mapAccessor.obtainMapRoute(processedWaypoints).body());
        ctx.json(MapJSONInterpreter.routesToJSON(route));

        DatabaseManager.insertRoute(connection, route);
    };

    public Handler saveRouteToDatabase = ctx -> {
        DatabaseManager.insertRoute(connection, routeFromJSON(ctx.body()));
    };

    public Handler updateRouteInDatabase = ctx -> {
        try {
            Integer routeID = Integer.parseInt(ctx.queryParam("id"));
            ctx.json(DatabaseManager.updateRouteWithID(connection, routeID, routeFromJSON(ctx.body())));
        }catch(NullPointerException e){
            throw new NullPointerException("id provided is in improper form or not provided. Use query parameter 'id'"
            + e.getMessage());
        }



    };

    public Handler deleteRouteWithID = ctx -> {
        try {
            DatabaseManager.deleteByID(connection, Integer.parseInt(ctx.queryParam("id")));
        }catch (NullPointerException e){
            throw new NullPointerException("Required query, 'id', is not formatted properly or present\n" + e.getMessage());
        }
    };

    private Route routeFromJSON(String json){
        JSONObject input = new JSONObject(json);
        return new Route(
                input.getInt("id"),
                input.getString("name"),
                stepsFromJSON(input.getJSONArray("steps").toString()),
                input.getInt("distanceMeters"),
                input.getInt("durationSec"));

    }

    private LinkedList<Step> stepsFromJSON(String json){
        LinkedList<Step> output = new LinkedList<>();
        JSONArray input = new JSONArray(json);
        for(int i = 0; i < input.length(); i++){
            JSONObject currStep = input.getJSONObject(i);
            output.add(new Step(
                    new Waypoint(
                            currStep.getDouble("startLat"),
                            currStep.getDouble("startLong")),
                    new Waypoint(
                            currStep.getDouble("startLat"),
                            currStep.getDouble("endLong")),
                    currStep.getInt("durationSec"),
                    currStep.getString("instruction")));

        }
        return output;
    }

}
