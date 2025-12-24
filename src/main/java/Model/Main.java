package Model;

import Model.Beans.Waypoint;
import Model.Database.DatabaseManager;
import Model.Maps.GoogleMapsJSONInterpreter;
import Model.Maps.GoogleMapsAccessor;
import Model.Maps.MapboxAccessor;
import Model.Maps.MapboxJSONInterpreter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import io.javalin.Javalin;

public class Main {

    static Waypoint test1 = new Waypoint("Test 1",38.49488155289983, -122.70128744361865);
    static Waypoint test2 = new Waypoint("Test 2", 38.4809585581821, -122.6899908275392);

    static Waypoint test3 = new Waypoint("Test 3", 38.50180040725877, -122.7140623161063);
    static Waypoint test4 = new Waypoint("Test 4", 38.49837464984697, -122.71195946427656);
    static Waypoint test5 = new Waypoint("Test 5", 38.50603200044317, -122.70256100409276);
    static Waypoint test6 = new Waypoint("Test 6", 38.530006331442166, -122.72831021042747);



    static final String JDBC_URL = "jdbc:postgresql://localhost:5432/student?" +
            "currentScheme=public&user=postgres&password=password";
    public static void main(String[] args) throws SQLException {



//        System.out.println();
       // String s = MapsAccessor.postMapsRoute(test1, test2).body();
       // System.out.println(s);
        ArrayList<Waypoint> testList = new ArrayList<>();

        testList.add(test1);
        testList.add(test2);
        //testList.add(test3);
       // testList.add(test4);
        //testList.add(test5);
        //testList.add(test6);

        MapboxAccessor mapbox = new MapboxAccessor();
        String s = mapbox.obtainMapRoute(testList).body();
        System.out.println(s);
        Connection c = DriverManager.getConnection(JDBC_URL);
        try {
            //DatabaseManager.dropRouteTable(c);
            //DatabaseManager.createTable(c, JDBC_URL);
            MapboxJSONInterpreter mapboxJSON = new MapboxJSONInterpreter();
            DatabaseManager.createRoute(c, mapboxJSON.getRoute("Long test", s));

            //JSONInterpreter.routesToJSON(DatabaseManager.getRoutesOfName(c,"NewRoute"));

         //  Route alternateTest = JSONInterpreter.getRoute("NewRoute", MapsAccessor.postMapsRoute(test2, test1).body());
         //   System.out.println(alternateTest);
            //DatabaseManager.updateRouteWithID(c, 1, alternateTest);

        }catch (Exception e){
            e.printStackTrace();
        }
        c.close();


        var app = Javalin.create()
                .get("/", ctx -> ctx.result("Hello World"))
                .start(8080);

    }
}