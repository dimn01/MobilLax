/*
 * TransitRouteResponse.java
 * ✅ 목적: 요약 + 상세 정보 DTO 응답 객체 (공용)
 */
package MobilLax.Domain.Transit.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class TransitRouteResponse {
    private List<TransitSummaryDto> summaries;
    private List<TransitDetailDto> details;
}