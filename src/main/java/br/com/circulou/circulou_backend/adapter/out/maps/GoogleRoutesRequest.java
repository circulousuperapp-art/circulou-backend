package br.com.circulou.circulou_backend.adapter.out.maps;

import java.util.List;

public record GoogleRoutesRequest(
    Waypoint origin,
    Waypoint destination,
    String travelMode,
    String routingPreference
) {
    public record Waypoint(Location location) {}
    public record Location(LatLng latLng) {}
    public record LatLng(Double latitude, Double longitude) {}
}

record GoogleRoutesResponse(List<Route> routes) {
    public record Route(Integer distanceMeters, String duration) {}
}
