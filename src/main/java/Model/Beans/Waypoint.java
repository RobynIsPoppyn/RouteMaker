package Model.Beans;

import org.json.JSONObject;

public record Waypoint (String name, double latitude, double longitude){
    @Override
    public boolean equals(Object o){
        if (o.getClass() != Waypoint.class) {
            throw new IllegalArgumentException("Argument is not a member of Waypoint class");
        }
        return (((Waypoint)o).latitude == this.latitude) && ((Waypoint)o).longitude == this.longitude;
    }

    @Override
    public String toString(){
        return latitude + "," + longitude;
    }

    public Waypoint(JSONObject location){
        this("", ((JSONObject)location.get("latLng")).getDouble("latitude"),
                ((JSONObject)location.get("latLng")).getDouble("longitude"));


    }

    public Waypoint(double latitude, double longitude){
        this("Waypoint", latitude, longitude);
    }

}
