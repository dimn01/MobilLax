package MobilLax.Domain.Transit.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * ✅ 목적: Tmap 상세 응답 데이터를 View에 맞게 전달하기 위한 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransitDetailDto {

    private int totalTime;       // 전체 소요 시간 (초)
    private int totalFare;       // 총 요금 (원)
    private int transferCount;   // 환승 횟수
    private List<TransitLeg> legs; // 이동 구간 리스트

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransitLeg {
        private String mode;
        private int sectionTime;
        private int distance;
        private String route;
        private String routeId;
        private String routeColor;
        private int type;
        private int service;
        private String passShapeLineString;
        private StopInfo start;
        private StopInfo end;

        @JsonProperty("Lane")
        private List<Lane> lanes;

        private List<Step> steps;
        private List<Station> stations;

        private String formattedTime;
        private String formattedDistance;

        private String role;

        // ✅ 구간 요금 정보 (필요 시만 설정됨)
        private Integer routePayment;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StopInfo {
        private String name;
        private double lat;
        private double lon;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Lane {
        private String route;
        private String routeColor;
        private String routeId;
        private int type;
        private int service;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Step {
        private String streetName;
        private double distance;
        private String description;
        private String linestring;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Station {
        private String stationID;
        private String stationName;
        private double lat;
        private double lon;
    }
}
