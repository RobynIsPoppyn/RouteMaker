package Model.Maps;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import Model.Beans.Waypoint;

public interface MapAccessor {
    public abstract HttpResponse<String> obtainMapRoute(ArrayList<Waypoint> Intermediates);
}
