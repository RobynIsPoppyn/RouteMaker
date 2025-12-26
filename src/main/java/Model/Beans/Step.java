package Model.Beans;

public record Step (Waypoint startpoint, Waypoint endpoint, int durationSec, String instruction) {
}
