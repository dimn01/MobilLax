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

        const type = getQueryParam("type"); // "distance" 또는 "time"
        console.log(type)
        const index = getBestItineraryIndex(type || "time", data);
        console.log(index);

        var tTime = "";
        if(Math.floor((data.metaData.plan.itineraries[index].totalTime / 60) / 60)) {
            tTime = Math.floor((data.metaData.plan.itineraries[index].totalTime / 60) / 60).toFixed(0) + "시 "
                               + ((data.metaData.plan.itineraries[index].totalTime / 60) % 60).toFixed(0) + "분";
        }
        else { tTime = (data.metaData.plan.itineraries[index].totalTime / 60).toFixed(0) + "분"; }
        const tFare = data.metaData.plan.itineraries[index].fare.regular.totalFare.toLocaleString() + "원";
        const tTrans = data.metaData.plan.itineraries[index].transferCount + "회";

        document.querySelector('.summary-item:nth-child(1) strong').textContent = tTime;
        document.querySelector('.summary-item:nth-child(2) strong').textContent = tFare;
        document.querySelector('.summary-item:nth-child(3) strong').textContent = tTrans;

        const legs = data.metaData.plan.itineraries[index].legs;
        const routeBounds = new Tmapv2.LatLngBounds();

        for (var leg of legs) {
            var marker_s = new Tmapv2.LatLng(Number(leg.start.lat), Number(leg.start.lon));
            var marker_e = new Tmapv2.LatLng(Number(leg.end.lat), Number(leg.end.lon));
            routeBounds.extend(marker_s);
            routeBounds.extend(marker_e);
//            console.log(leg.start.lon, leg.start.lat);
//            console.log(leg.end.lon, leg.end.lat);
            if (leg.mode === "WALK" && leg.steps) {
              for (var step of leg.steps) {
                var points = parseLineString(step.linestring);
                drawLine(points, "#888888"); // 도보 경로: 회색
              }
            } else if (leg.passShape && leg.passShape.linestring) {
              var color = `#${leg.routeColor || "0068B7"}`;
              var points = parseLineString(leg.passShape.linestring);
              drawLine(points, color); // 버스 경로
            }
         }
        map.panToBounds(routeBounds);
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

// 최소 거리, 최소 시간, 최소 환승 경로의 index 반환
function getBestItineraryIndex(type, data) {
  const itineraries = data.metaData.plan.itineraries;
  var bestIndex = 0;

  if (type === "distance") {
    let minDistance = Infinity;
    itineraries.forEach((it, i) => {
      if (it.totalDistance < minDistance) {
        minDistance = it.totalDistance;
        bestIndex = i;
      }
    });
  } else if (type === "time") {
    let minTime = Infinity;
    itineraries.forEach((it, i) => {
      if (it.totalTime < minTime) {
        minTime = it.totalTime;
        bestIndex = i;
      }
    });
  } else if (type === "transfer") {
    let minTransfer = Infinity;
    itineraries.forEach((it, i) => {
      if (it.transferCount < minTransfer) {
        minTransfer = it.transferCount;
        bestIndex = i;
      }
    });
  }
  return bestIndex;
}

// URL 쿼리에서 type 추출 (예: distance, time)
function getQueryParam(key) {
  return new URLSearchParams(window.location.search).get(key);
}