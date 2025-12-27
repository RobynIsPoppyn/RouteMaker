package Model;

import Model.Beans.Waypoint;
import Model.Database.DatabaseManager;
import Model.Maps.MapboxAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.javalin.Javalin;

public class Main {

    static Waypoint test1 = new Waypoint("Test 1",38.49488155289983, -122.70128744361865);
    static Waypoint test2 = new Waypoint("Test 2", 38.4809585581821, -122.6899908275392);

    static Waypoint test3 = new Waypoint("Test 3", 38.50180040725877, -122.7140623161063);
    static Waypoint test4 = new Waypoint("Test 4", 38.49837464984697, -122.71195946427656);
    static Waypoint test5 = new Waypoint("Test 5", 38.50603200044317, -122.70256100409276);
    static Waypoint test6 = new Waypoint("Test 6", 38.530006331442166, -122.72831021042747);




    public static void main(String[] args) throws SQLException {
        final String JDBC_URL = System.getenv("JDBC_URL");
            System.out.println(JDBC_URL);


        MapboxAccessor mapbox = new MapboxAccessor();
       // String s = mapbox.obtainMapRoute(testList).body();
       // System.out.println(s);
        Connection c = DriverManager.getConnection(JDBC_URL);
        //DatabaseManager.dropRouteTable(c);
        DatabaseManager.createTable(c, JDBC_URL);
        var app = Javalin.create()
                .get("/", ctx -> ctx.result("Hello World"))
                .start(8080);
        JavalinManager javalinManager = new JavalinManager(new DatabaseManager(), c, mapbox);

        app.get("/mapRoute", javalinManager.getRouteFromMap);
        app.post("/mapRoute", javalinManager.saveRouteFromMapToDatabase);
        app.put("/mapRoute", javalinManager.updateRouteInDatabase);


        app.get("/savedRoutes", javalinManager.getAllRoutesFromDatabase);
        app.put("/savedRoutes", javalinManager.updateRouteInDatabase);
        app.post("/savedRoutes", javalinManager.saveRouteToDatabase);
        app.delete("/savedRoutes", javalinManager.deleteRouteWithID);

    }
}