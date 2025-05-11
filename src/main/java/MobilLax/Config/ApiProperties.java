/**
 * ApiProperties.java
 *
 * ✅ 파일 목적: application.properties 또는 application.yml에 정의된
 *              다양한 대중교통 API 관련 설정값들을 Lombok 기반으로 바인딩하는 설정 클래스입니다.
 *
 * 작성자: 김영빈
 * 마지막 수정일: 2025-05-11
 */

package MobilLax.Config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ✅ 클래스 설명:
 * - `api` 접두어를 가진 설정 항목들을 자동으로 바인딩합니다.
 * - 버스, 시외버스, 고속버스, 지하철, 열차 등 다양한 교통 정보 API의 엔드포인트를 포함합니다.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "api")
public class ApiProperties {

    /** 공공데이터포털에서 발급받은 인증 키 */
    private String key;

    private CitybusApi citybus;
    private SuburbApi suburb;
    private ExpressApi express;
    private StationApi station;
    private BusRouteApi route;
    private SubwayApi subway;
    private TrainApi train;

    // ✅ 시내버스 관련 도착/노선/위치 API URL
    @Getter @Setter
    public static class CitybusApi {
        private String arrivalAll;        // 정류소별 도착예정정보
        private String arrivalSpecific;   // 특정노선 도착예정정보
        private String routeInfo;         // 노선정보 항목 조회
        private String routeNumber;       // 노선번호 목록 조회
        private String routeStops;        // 노선별 경유 정류소 조회
        private String location;          // 노선별 버스 위치 조회
        private String sttnLocation;      // 특정정류소 접근 버스 위치
        private String stationNearby;     // 좌표 기반 근접 정류소 목록
        private String stationNo;         // 정류소번호 목록 조회
        private String stationRoute;      // 정류소별 경유노선 조회
        private String ctycode;           // 도시코드 조회
    }

    // ✅ 시외버스 관련 API URL
    @Getter @Setter
    public static class SuburbApi {
        private String terminal;   // 시외버스 터미널 목록
        private String schedule;   // 출/도착지 기반 시외버스정보
        private String grade;      // 시외버스 등급 목록
        private String ctycode;    // 도시코드 목록
    }

    // ✅ 고속버스 관련 API URL
    @Getter @Setter
    public static class ExpressApi {
        private String arrivalTmn;     // 출발지 기준 도착지 목록
        private String arrivalInfo;    // 고속버스 도착예정정보
        private String terminal;       // 고속버스 터미널 목록
        private String schedule;       // 출/도착지 기반 고속버스 정보
        private String grade;          // 고속버스 등급 목록
        private String ctycode;        // 도시코드 목록
    }

    // ✅ 정류소 정보 API URL
    @Getter @Setter
    public static class StationApi {
        private String nearby;         // 좌표 기반 근접 정류소 목록
        private String byNo;           // 정류소번호 목록 조회
        private String throughRoutes;  // 정류소별 경유노선 목록
        private String ctycode;        // 도시코드 목록
    }

    // ✅ 시내버스 노선정보 API URL
    @Getter @Setter
    public static class BusRouteApi {
        private String info;       // 노선정보 항목 조회
        private String number;     // 노선번호 목록 조회
        private String stop;       // 노선별 경유정류소 목록 조회
        private String ctycode;    // 도시코드 목록 조회
    }

    // ✅ 지하철 관련 API URL
    @Getter @Setter
    public static class SubwayApi {
        private String keyword;        // 키워드 기반 지하철역 목록
        private String exitBus;        // 출구별 버스노선 목록
        private String exitFacility;   // 출구별 주변시설 목록
        private String timetable;      // 지하철역별 시간표
    }

    // ✅ 열차 관련 API URL
    @Getter @Setter
    public static class TrainApi {
        private String info;           // 출/도착지 기반 열차 정보
        private String stationList;    // 시/도별 기차역 목록
        private String type;           // 차량 종류 목록
        private String ctycode;        // 도시코드 목록
    }
}