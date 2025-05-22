package MobilLax.Domain.Transit.Dto;

import lombok.*;

/**
 * ✅ 목적: Tmap 요약 응답 데이터를 View에 맞게 전달하기 위한 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransitSummaryDto {

    private int totalTime;         // 전체 이동 시간 (초)
    private int totalDistance;     // 총 이동 거리 (m)
    private int totalFare;         // 총 요금 (원)
    private int totalWalkDistance; // 총 도보 거리 (m)
    private int transferCount;     // 환승 횟수
    private int pathType;          // 경로 유형 (예: 고속+기차=4)

    // ⏱️ 포맷된 문자열 (View 출력용)
    private String formattedTotalTime;
    private String formattedTotalDistance;
    private String formattedWalkDistance;

    /**
     * ✅ 모든 수치를 사람이 읽기 쉬운 포맷으로 변환
     */
    public void formatAll() {
        this.formattedTotalTime = formatTime(totalTime);
        this.formattedTotalDistance = formatDistance(totalDistance);
        this.formattedWalkDistance = formatDistance(totalWalkDistance);
    }

    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int sec = seconds % 60;

        if (hours > 0) {
            return String.format("%d시간 %d분 %d초", hours, minutes, sec);
        } else if (minutes > 0) {
            return String.format("%d분 %d초", minutes, sec);
        } else {
            return String.format("%d초", sec);
        }
    }

    private String formatDistance(int meters) {
        if (meters >= 1000) {
            return String.format("%.1fkm", meters / 1000.0);
        } else {
            return meters + "m";
        }
    }
}