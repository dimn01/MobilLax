/*
 * TransitRequestDto.java
 */
package MobilLax.Domain.Transit.Dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransitRequestDto {
    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private int count = 3;
    private int lang = 0;
    private String format = "json";
}