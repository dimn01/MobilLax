/*
 * TransitMockController.java
 * ✅ 목적: Mock JSON을 통해 summary + detail 정보를 Service에서 가공 후 View 또는 JSON으로 반환
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

@Tag(name = "🔧 Mock 경로 API", description = "Mock JSON을 기반으로 Tmap 경로 요약/상세 정보를 반환합니다.")
@Controller
@RequestMapping("/api/transit/mock")
@RequiredArgsConstructor
public class TransitMockController {

    private final TransitSummaryService transitSummaryService;
    private final TransitDetailService transitDetailService;

    /**
     * ✅ JSON 응답으로 summary + detail 반환
     */
    @Operation(summary = "Mock 경로 응답(JSON)", description = "Mock 데이터에서 요약 및 상세 경로 정보를 반환합니다.")
    @GetMapping("/route/json")
    @ResponseBody
    public ApiResponse<TransitRouteResponse> getMockRouteJson() {
        List<TransitSummaryDto> summaries = transitSummaryService.getMockRouteSummaries();
        List<TransitDetailDto> details = transitDetailService.getMockRouteDetails();
        TransitRouteResponse response = new TransitRouteResponse(summaries, details);
        return ApiResponse.ok(response);
    }

    /**
     * ✅ HTML View에 summary + detail 전달 (가공 완료된 상태)
     */
    @Operation(summary = "Mock 경로 응답(HTML)", description = "Mock 데이터로부터 HTML 경로 상세 화면 렌더링")
    @GetMapping("/route/html")
    public String getMockRouteHtml(Model model) {
        List<TransitSummaryDto> summaries = transitSummaryService.getMockRouteSummaries();
        List<TransitDetailDto> details = transitDetailService.getMockRouteDetails();

        model.addAttribute("summaries", summaries);
        model.addAttribute("details", details);
        return "detail_route";
    }
}
