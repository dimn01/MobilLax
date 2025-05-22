/*
 * TransitRealController.java
 * ✅ 목적: 출발지/도착지 지명을 받아 Kakao Local API로 좌표를 조회하고,
 *         Tmap API를 통해 경로 요약 및 상세 데이터를 Thymeleaf View 또는 JSON으로 전달
 */

package MobilLax.Domain.Transit.Controller;

import MobilLax.Domain.Transit.Dto.*;
import MobilLax.Domain.Transit.Service.*;
import MobilLax.Global.Response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "🚍 실시간 경로 API", description = "실제 Tmap API를 호출하여 경로 요약 + 상세 정보를 조회합니다.")
@Controller
@RequestMapping("/transit")
@RequiredArgsConstructor
public class TransitRealController {

    private final TransitSummaryService transitSummaryService;
    private final TransitDetailService transitDetailService;
    private final KakaoMapService kakaoMapService;

    /**
     * ✅ JSON 응답: summary + detail 경로 데이터를 JSON으로 반환
     */
    @Operation(summary = "Tmap 경로 응답(JSON)", description = "출발/도착지 좌표 기반으로 Tmap 요약 및 상세 경로 데이터를 반환합니다.")
    @PostMapping("/route/json")
    @ResponseBody
    public ApiResponse<TransitRouteResponse> getRealRouteJson(@RequestBody TransitRequestDto requestDto) {
        List<TransitSummaryDto> summaries = transitSummaryService.getRouteSummaries(requestDto);
        List<TransitDetailDto> details = transitDetailService.getRouteDetails(requestDto);
        TransitRouteResponse response = new TransitRouteResponse(summaries, details);
        return ApiResponse.ok(response);
    }

    /**
     * ✅ HTML View 응답: 출발지/도착지 지명 기반으로 경로 데이터를 Thymeleaf로 전달
     */
    @Operation(summary = "Tmap 경로 응답(HTML)", description = "Thymeleaf를 통해 요약 및 상세 정보를 렌더링합니다.")
    @PostMapping("/route")
    public String getRealRouteHtml(@RequestParam String fromName,
                                   @RequestParam String toName,
                                   Model model) {
        double[] fromCoord = kakaoMapService.geocode(fromName);
        double[] toCoord = kakaoMapService.geocode(toName);

        if (fromCoord == null || toCoord == null) {
            model.addAttribute("error", "출발지 또는 도착지의 좌표를 찾을 수 없습니다.");
            return "home";
        }

        TransitRequestDto requestDto = new TransitRequestDto(
                fromCoord[1], fromCoord[0],  // startX, startY
                toCoord[1], toCoord[0],      // endX, endY
                3, 0, "json"
        );

        List<TransitSummaryDto> summaries = transitSummaryService.getRouteSummaries(requestDto);
        List<TransitDetailDto> details = transitDetailService.getRouteDetails(requestDto);

        model.addAttribute("summaries", summaries);
        model.addAttribute("details", details);
        return "detail_route";
    }
}
