package MobilLax.Domain.Route.Repository;

import MobilLax.Domain.Route.Dto.RouteResponse;

public interface RouteService {
    RouteResponse getRoute(String type);
}