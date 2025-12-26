package Model.Database;

import Model.Beans.Route;
import Model.Beans.Step;
import Model.Beans.Waypoint;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class DatabaseManager {

    public static void createTable(Connection connection, String JDBC_URL){

        try{

            var statement = connection.createStatement();
            var createRoutesStatement = QueryManager.getQuery("createRoutes");
            statement.execute(createRoutesStatement);
            var createStepsStatement = QueryManager.getQuery("createSteps");
            statement.execute(createStepsStatement);


//            Class.forName("org.postgresql.Driver");
//            c = DriverManager
//                    .getConnection("jdbc:postgresql://localhost:5432/student",
//                            "postgres", "password");

        }catch (Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName() + " " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened Database");
    }


    public static void dropRouteTable(Connection connection){
        try {
            var statement = connection.createStatement();
            var dropRoutesStatement = "DROP TABLE IF EXISTS ROUTES";
            statement.execute(dropRoutesStatement);
            var dropStepsStatement = "DROP TABLE IF EXISTS STEPS";
            statement.execute(dropStepsStatement);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    //Create entry into Route table using Route java bean, also inserts corresponding steps in table
    public static void insertRoute(Connection connection, Route route) throws Exception{

        var insertSql = "insert into ROUTES (name, distanceMeters, durationSec) values (?,?,?)";
        var preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setString(1, route.name());
        preparedStatement.setFloat(2, route.distanceMeters());
        preparedStatement.setFloat(3, route.durationSec());
        preparedStatement.execute();

        //Get the route ID to pass to the steps
        ResultSet keys = preparedStatement.getGeneratedKeys();
        if (!keys.next())
            return;

        insertStepsFromRoute(connection, keys.getInt(1), route);

        System.out.println("Record Created");
    }

    private static void insertStepsFromRoute(Connection connection, int routeID, Route route) throws SQLException {
        var insertStepSql = QueryManager.getQuery("insertStep");
        for (Step s : route.getSteps()){
            PreparedStatement statement = connection.prepareStatement(insertStepSql);
            statement.setInt(1, routeID);
            statement.setString(2, s.instruction());
            statement.setInt(3, s.durationSec());
            statement.setDouble(4, s.startpoint().longitude());
            statement.setDouble(5, s.startpoint().latitude());
            statement.setDouble(6, s.endpoint().longitude());
            statement.setDouble(7, s.endpoint().latitude());

            statement.execute();
        }
    }

    public static void readFromTable(Connection connection, String table) throws SQLException {
        var selectSql = "select * from" + table;
        var statement = connection.createStatement();
        var resultSet = statement.executeQuery(selectSql);

        while(resultSet.next()){
            System.out.println(resultSet.getString("Name"));
        }
    }

    //Create an array list of routes by searching for routes in the database
    public static ArrayList<Route> getRoutesOfName(Connection connection, String name) throws SQLException{
        var selectSql = "SELECT id, name, distanceMeters, durationSec FROM ROUTES WHERE name = '" + name + "'";
        var selectSteps = QueryManager.getQuery("selectSteps");

        var statement = connection.createStatement();
        var resultSet = statement.executeQuery(selectSql);

        ArrayList<Route> ret = new ArrayList<Route>();

        while(resultSet.next()){
            var stepStatement = connection.createStatement();
            ret.add(new Route(resultSet.getInt("id"),
                    resultSet.getString("name"),
                    new LinkedList<Step>(),
                    resultSet.getFloat("distanceMeters"),
                    resultSet.getFloat("durationSec")));
            var stepResults = stepStatement.executeQuery(selectSteps.concat(String.valueOf(resultSet.getInt("id"))));
           ret.get(ret.size() - 1).steps().addAll(parseSteps(stepResults));
        }
        return ret;
    }

    public static ArrayList<Route> getAllRoutes(Connection connection) throws SQLException{
        //First select every route from database
        var selectSql = "SELECT id, name, distanceMeters, durationSec FROM ROUTES";
        var selectSteps = QueryManager.getQuery("selectSteps");

        var statement = connection.createStatement();
        var resultSet = statement.executeQuery(selectSql);

        ArrayList<Route> ret = new ArrayList<Route>();

        while(resultSet.next()){
            var stepStatement = connection.createStatement();
            ret.add(new Route(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    new LinkedList<Step>(),
                    resultSet.getFloat("distanceMeters"),
                    resultSet.getFloat("durationSec")));
            var stepResults = stepStatement.executeQuery(selectSteps.concat(String.valueOf(resultSet.getInt("id"))));
            ret.get(ret.size() - 1).steps().addAll(parseSteps(stepResults));
        }
        return ret;
    }
    //Create a list of Java Steps from a resultSet acquired from Steps Table, used internally
    private static LinkedList<Step> parseSteps(ResultSet results) throws SQLException{

        LinkedList<Step> ret = new LinkedList<>();
        while(results.next()){
            ret.add(new Step(
                    new Waypoint(
                            results.getFloat("startLat"),
                            results.getFloat("startLong")),
                    new Waypoint(
                        results.getFloat("endLat"),
                        results.getFloat("endLong")),
                    results.getInt("durationSec"),
                    results.getString("instruction"))
            );
        }
        return ret;
    }





    public static void deleteByName(Connection connection, String name) throws SQLException{
        var deleteSql = "DELETE FROM ROUTE WHERE name = '" + name + "'";
        var statement = connection.createStatement();
        statement.execute(deleteSql, Statement.RETURN_GENERATED_KEYS);

        ResultSet result = statement.getGeneratedKeys();
        var stepStatement = connection.createStatement();
        while(result.next()){
            stepStatement.execute(QueryManager.getQuery("deleteRoute") + result.getInt(1));
        }
       // int rowID = result.getInt(0);
    }

    public static void deleteByID(Connection connection, int id) throws SQLException{
        var deleteSql = QueryManager.getQuery("deleteRoute") + id;
        var statement = connection.createStatement();
        statement.execute(deleteSql);
        deleteSql = QueryManager.getQuery("deleteSteps") + id;
        statement.execute(deleteSql);

    }

    public static int updateRouteWithID(Connection connection, int id, Route updatedRoute) throws SQLException {

        //First update the Route, simple sql execution
        var updateSql = QueryManager.getQuery("updateRoute");
        var preparedStatement = connection.prepareStatement(updateSql + id);
        preparedStatement.setString(1, updatedRoute.name());
        preparedStatement.setFloat(2, updatedRoute.distanceMeters());
        preparedStatement.setFloat(3, updatedRoute.durationSec());

        int rowsAffected = preparedStatement.executeUpdate();

        if(rowsAffected == 0) {
            return 0;
        }
        //Delete each row that was a previous step before so that they can be reset
        //This ensure that if the number of steps changes, no complications arise
        var selectStepsSql = QueryManager.getQuery("deleteSteps");
        preparedStatement = connection.prepareStatement(selectStepsSql + id);
        preparedStatement.execute();

        //For each step in the new Route, add this to the table
        insertStepsFromRoute(connection, id, updatedRoute);
        System.out.println("Record Updated!");
        return rowsAffected;
    }




}
