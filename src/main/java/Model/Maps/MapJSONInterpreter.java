package Model.Maps;

import Model.Beans.Route;

import java.util.ArrayList;

public interface MapJSONInterpreter {

    public abstract Route getRoute(String name, String JSONInput);

    public abstract String routesToJSON(ArrayList<Route> routes);
}
