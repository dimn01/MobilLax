/**
 * TransitSummaryDto.java
 *
 * ✅ 파일 목적: 대중교통 경로 요약 정보를 담는 DTO 클래스입니다.
 *              API 응답 중 전체 시간, 요금, 환승 횟수 등 요약 데이터를 표현합니다.
 *
 * 작성자: 김영빈
 * 마지막 수정일: 2025-05-12
 */

package MobilLax.Model.Dto;

import lombok.Data;

/**
 * ✅ 클래스 설명:
 * 대중교통 경로 요약 정보를 담는 DTO 클래스입니다.
 * Tmap API의 요약 JSON 응답으로부터 전체 소요 시간, 요금, 환승 횟수, 도보 정보 등을 파싱하여 저장합니다.
 */
@Data
public class TransitSummaryDto {
    private int totalTime;             // 총 소요 시간 (초)
    private int totalFare;             // 총 요금 (원)
    private int transferCount;         // 환승 횟수
    private int totalDistance;         // 총 이동 거리 (미터)
    private int totalWalkTime;         // 총 도보 시간 (초)
    private int totalWalkDistance;     // 총 도보 거리 (미터)
    private int pathType;              // 경로 유형 (1: 지하철 중심, 2: 버스 중심 등)
}