package MobilLax.Model;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Leg {
    private String mode;
    private String startName;
    private String endName;
    private int sectionTime;
}