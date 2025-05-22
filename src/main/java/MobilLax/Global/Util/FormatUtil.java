/**
 * FormatUtil.java
 *
 * ✅ 파일 목적: 시간 및 거리 데이터를 사람이 읽기 쉬운 형식으로 변환하는 유틸리티 클래스
 * ✅ 사용 위치: 모든 컨트롤러 또는 서비스 클래스에서 거리/시간 출력 시 활용 가능
 *
 * 작성자: 김영빈
 * 마지막 수정일: 2025-05-13
 */

package MobilLax.Global.Util;

public class FormatUtil {

    /**
     * ✅ 시간 포맷 도우미
     * - 초 단위 값을 시/분/초로 보기 좋은 문자열로 변환합니다.
     * - 예: 3720 → "1시간 2분 0초"
     *
     * @param seconds 초 단위 시간
     * @return 포맷 문자열
     */
    public static String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append("시간 ");
        if (minutes > 0 || hours > 0) sb.append(minutes).append("분 ");
        sb.append(secs).append("초");

        return sb.toString();
    }

    /**
     * ✅ 거리 포맷 도우미
     * - 1000m 이상이면 km 단위로, 그 외에는 m로 표시합니다.
     * - 예: 2349 → "2.3km", 850 → "850m"
     *
     * @param meters 미터 단위 거리
     * @return 포맷 문자열
     */
    public static String formatDistance(int meters) {
        if (meters >= 1000) {
            return String.format("%.1fkm", meters / 1000.0);
        } else {
            return meters + "m";
        }
    }

    // 객체 생성 방지
    private FormatUtil() {}
}