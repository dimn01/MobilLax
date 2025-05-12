/**
 * TransitController.java
 * ✅ 파일 목적: 대중교통 API 요청을 처리하고, 요약 및 상세 경로 데이터를 JSON 또는 HTML로 응답하는 Controller입니다.
 * 작성자: 김영빈
 * 마지막 수정일: 2025-05-12
 */

package MobilLax.Controller;

import MobilLax.Model.Dto.TransitRequestDto;
import MobilLax.Model.Dto.TransitSummaryDto;
import MobilLax.Model.Dto.TransitDetailDto;
import MobilLax.Service.TransitService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * ✅ 클래스 설명:
 * 클라이언트로부터 대중교통 출발/도착 좌표를 받아 최적 경로 요약 및 상세 정보를 응답합니다.
 * - JSON 응답 (POST/GET)
 * - HTML 페이지 렌더링
 */
@Controller
@RequestMapping("/api/transit")
@RequiredArgsConstructor
public class TransitController {

    private final TransitService transitService;

    /**
     * ✅ POST 방식으로 최적 상세 경로 응답
     * @param request TransitRequestDto (startX, startY, endX, endY 등 포함)
     * @return TransitDetailDto 상세 경로 정보
     */
    @PostMapping("/route/detail")
    @ResponseBody
    public TransitDetailDto getBestTransitRoute(@RequestBody TransitRequestDto request) {
        return transitService.getRouteDetail(request);
    }

    /**
     * ✅ GET 방식으로 최적 경로 요약 + 상세 경로 응답
     * @param request TransitRequestDto (쿼리 파라미터 자동 바인딩)
     * @return TransitRouteResponse (요약 + 상세 DTO 포함)
     */
    @GetMapping("/route/json")
    @ResponseBody
    public TransitRouteResponse viewRoute(@ModelAttribute TransitRequestDto request) {
        TransitSummaryDto summary = transitService.getBestRouteSummary(request);
        TransitDetailDto detail = transitService.getRouteDetail(request);
        return new TransitRouteResponse(summary, detail);
    }

    /**
     * ✅ GET 방식으로 HTML 페이지 렌더링
     * @param request TransitRequestDto
     * @param model Thymeleaf 모델 객체
     * @return 렌더링할 Thymeleaf 템플릿 이름 (templates/transit_route_view.html)
     */
    @GetMapping("/route/html")
    public String viewRouteHtml(@ModelAttribute TransitRequestDto request, Model model) {
        model.addAttribute("summary", transitService.getBestRouteSummary(request));
        model.addAttribute("detail", transitService.getRouteDetail(request));
        return "transit_route_view";
    }

    /**
     * ✅ JSON 응답을 위한 내부 DTO 클래스
     * TransitSummaryDto와 TransitDetailDto를 묶어서 반환
     */
    @Getter
    @AllArgsConstructor
    public static class TransitRouteResponse {
        private TransitSummaryDto summary;
        private TransitDetailDto detail;
    }
}
