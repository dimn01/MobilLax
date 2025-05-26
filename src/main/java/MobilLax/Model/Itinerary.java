package MobilLax.Model;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Itinerary {
    private int totalTime;
    private int totalDistance;
    private int transferCount;
    private List<Leg> legs;
}