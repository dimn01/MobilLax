/**
 * TransitDetailDto.java
 *
 * ✅ 파일 목적: 대중교통 상세 경로 정보를 담는 DTO 클래스입니다.
 *              Tmap API 응답 중 'legs' 및 경유지, 정류장 등 상세 내용을 표현합니다.
 *
 * 작성자: 김영빈
 * 마지막 수정일: 2025-05-12
 */

package MobilLax.Model.Dto;

import lombok.Data;
import java.util.List;

/**
 * ✅ 클래스 설명:
 * Tmap 경로 탐색 API의 상세 경로 응답(JSON) 정보를 표현하는 DTO입니다.
 * 각 구간(leg)의 교통 수단, 거리, 시간, 출도착지, 정류장 리스트 등 복합 구조로 구성됩니다.
 */
@Data
public class TransitDetailDto {
    private int totalTime;                     // 총 소요 시간 (초)
    private int totalFare;                     // 총 요금 (원)
    private int transferCount;                 // 환승 횟수
    private List<TransitLeg> legs;             // 경로 구간 리스트

    /**
     * ✅ 개별 경로 구간 정보 클래스
     * 버스/지하철/도보 등 각 구간의 속성을 표현합니다.
     */
    @Data
    public static class TransitLeg {
        private String mode;                   // 교통 수단 종류: WALK, BUS, SUBWAY 등
        private int sectionTime;               // 구간 소요 시간 (초)
        private int distance;                  // 구간 거리 (미터)
        private String route;                  // 노선명
        private String routeId;                // 노선 ID
        private String routeColor;             // 노선 색상 (예: 지하철 노선 색)
        private int type;                      // 노선 유형
        private int service;                   // 서비스 타입
        private String passShapeLineString;    // 구간 도로 선형 정보 (LineString)
        private StopInfo start;                // 출발 정류장/지점 정보
        private StopInfo end;                  // 도착 정류장/지점 정보
        private List<Lane> lanes;              // 복수 노선 정보 (일부 API에 제공됨)
        private List<Step> steps;              // 도보 세부 단계
        private List<Station> stations;        // 통과 정류장 리스트
    }

    /**
     * ✅ 정류장/지점 정보 클래스
     * 도착/출발 지점 정보로 활용됩니다.
     */
    @Data
    public static class StopInfo {
        private String name;                   // 정류장 또는 지점 이름
        private double lat;                    // 위도
        private double lon;                    // 경도
    }

    /**
     * ✅ 노선 정보 클래스
     * 일부 복수 노선이 있을 경우 사용됩니다.
     */
    @Data
    public static class Lane {
        private String route;                  // 노선명
        private String routeColor;             // 노선 색상
        private String routeId;                // 노선 ID
        private int type;                      // 노선 유형
        private int service;                   // 서비스 타입
    }

    /**
     * ✅ 도보 상세 경로 클래스
     * 도보 이동 구간의 단계별 설명을 포함합니다.
     */
    @Data
    public static class Step {
        private String streetName;             // 도보 거리 내 도로명
        private double distance;               // 거리 (미터)
        private String description;            // 경로 설명
        private String linestring;             // LineString 형식의 도보 경로
    }

    /**
     * ✅ 통과 정류장 정보 클래스
     * 경유하는 모든 정류장 정보를 담습니다.
     */
    @Data
    public static class Station {
        private String stationID;              // 정류장 ID
        private String stationName;            // 정류장 이름
        private double lat;                    // 위도
        private double lon;                    // 경도
    }
}
