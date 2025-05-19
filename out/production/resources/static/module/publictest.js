var resultdrawArr = [];

async function publicTest() {
    try {
        // 로컬의 data.txt 파일을 fetch로 불러옴
        const response = await fetch('https://3173cb8e-d014-4dcb-8da9-2e53ad672e15.mock.pstmn.io', {
                         method: "GET",
                     });
        // 텍스트를 JSON으로 파싱
        const text = await response.text();
        const data = JSON.parse(text);

        const legs = data.metaData.plan.itineraries[0].legs;
        for (var leg of legs) {
            if (leg.mode === "WALK" && leg.steps) {
              for (var step of leg.steps) {
                var points = parseLineString(step.linestring);
                drawLine(points, "#888888"); // 도보 경로: 회색
              }
            } else if (leg.mode === "BUS" && leg.passShape) {
              var color = `#${leg.routeColor || "0068B7"}`;
              var points = parseLineString(leg.passShape.linestring);
              drawLine(points, color); // 버스 경로
            }
         }

        // 사용 예시
        console.log("불러온 목업 데이터:", data);
    } catch (err) {
        console.error("파일을 불러오는 중 에러 발생:", err);
    }
}

// 라인 그리기
function drawLine(latlngs, color) {
  var polyline = new Tmapv2.Polyline({
    path: latlngs,
    strokeColor: color,
    strokeWeight: 6,
    map: map
  });
  resultdrawArr.push(polyline);
}

function clearRoutes() {
    if(resultdrawArr.length > 0) {
        resultdrawArr.forEach(polyline => polyline.setMap(null));
        resultdrawArr = [];
    }
}

// 좌표 문자열 → Tmap LatLng 배열
function parseLineString(line) {
  return line.trim().split(" ").map(pair => {
    const [lon, lat] = pair.split(",").map(Number);
    return new Tmapv2.LatLng(lat, lon);
  });
}