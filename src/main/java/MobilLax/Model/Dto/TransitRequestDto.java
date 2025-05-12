/**
 * TransitRequestDto.java
 *
 * ✅ 파일 목적: 클라이언트로부터 받은 대중교통 경로 요청 정보를 담는 DTO 클래스입니다.
 *              출발/도착 좌표와 요청 옵션들을 포함합니다.
 *
 * 작성자: 김영빈
 * 마지막 수정일: 2025-05-12
 */

package MobilLax.Model.Dto;

import lombok.Data;

/**
 * ✅ 클래스 설명:
 * Tmap 대중교통 API 요청을 위한 데이터 전송 객체(DTO)입니다.
 * 사용자가 요청한 출발지, 도착지 좌표와 경로 수, 언어, 응답 형식 등을 포함합니다.
 */
@Data
public class TransitRequestDto {

    private String startX;               // 출발지 X좌표 (경도)
    private String startY;               // 출발지 Y좌표 (위도)
    private String endX;                 // 도착지 X좌표 (경도)
    private String endY;                 // 도착지 Y좌표 (위도)

    private int count = 3;               // 경로 최대 개수 (기본값: 3)
    private int lang = 0;                // 언어 설정 (0: 국문, 1: 영문 등)
    private String format = "json";      // 응답 형식 (json/xml 등, 기본: json)
}
