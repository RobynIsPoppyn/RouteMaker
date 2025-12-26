package Model.Beans;


import java.util.LinkedList;

public record Route(int id, String name, LinkedList<Step> steps, float durationSec, float distanceMeters) {

    public LinkedList<Step> getSteps(){
        return steps;
    }

    public Route(){
        this(-1,"Route", new LinkedList<Step>(), 0, 0);
    }

    public Route(LinkedList<Step> steps, String durationStr, String distanceStr){
        this("Route", steps, durationStr, distanceStr);
    }

    public Route(String name, LinkedList<Step> steps, String durationStr, String distanceStr){
        this(name, new LinkedList<Step>(),
                Float.parseFloat(durationStr.substring(0,durationStr.length() - 1)), Float.parseFloat(distanceStr));
    }

    public Route(String name, LinkedList<Step> steps, float durationSec, float distanceMeters){
        this(-1, name, steps, durationSec, distanceMeters);
    }

}
