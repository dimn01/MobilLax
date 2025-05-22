package MobilLax.Model;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Plan {
    private List<Itinerary> itineraries;
}