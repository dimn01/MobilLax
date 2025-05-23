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

        const tTime = timeCal(data.metaData.plan.itineraries[index].totalTime);
        const tFare = data.metaData.plan.itineraries[index].fare.regular.totalFare.toLocaleString() + "원";
        const tTrans = data.metaData.plan.itineraries[index].transferCount + "회";

        document.querySelector('.summary-item:nth-child(1) strong').textContent = tTime;
        document.querySelector('.summary-item:nth-child(2) strong').textContent = tFare;
        document.querySelector('.summary-item:nth-child(3) strong').textContent = tTrans;

        const legs = data.metaData.plan.itineraries[index].legs;
        renderSidebarRoute(index, legs);
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

function timeCal(sec) {
    var tTime = ""
    if(Math.floor((sec / 60) / 60)) {
        tTime = Math.floor((sec / 60) / 60).toFixed(0) + "시 "
                           + ((sec / 60) % 60).toFixed(0) + "분";
    }
    else { tTime = (sec / 60).toFixed(0) + "분"; }

    return tTime;
}

// sidebar 상세경로 표시
function renderSidebarRoute(index, legs) {
  const container = document.querySelector('.sidebar-content');
  container.innerHTML = ""; // 기존 내용 비움

  legs.forEach((leg, i) => {
    const mode = leg.mode;
    const stepNumberSymbol = String.fromCharCode(0x2460 + i);

    let iconClass = "fas fa-question"; // 기본 아이콘
    if (mode === "WALK") iconClass = "fas fa-walking";
    else if (mode === "BUS") iconClass = "fas fa-bus";
    else if (mode === "SUBWAY" || mode === "TRAIN") iconClass = "fas fa-subway";
    // else if (mode === "RAIL") iconClass = "fas fa-train";

    // 출발, 도착 정류장/역 이름
    const startName = leg.start.name || "";
    const endName = leg.end.name || "";
    const routeName = leg.route || "";
    const timeMin = timeCal(leg.sectionTime) ? `약 ${timeCal(leg.sectionTime)}` : "";
    const distance = leg.distance ? `${Math.floor(leg.distance)}m` : "";

    // 정류장 수 or 역 수
    let stepDesc = "";
    if (mode === "BUS" && leg.passStopList?.stations?.length) {
      const stationCount = leg.passStopList.stationList.length ? leg.passStopList.stationList.length : "";
      stepDesc = `${timeMin}, 약 ${stationCount}개 정류장`;
    } else if (mode === "SUBWAY" || mode === "RAIL") {
      const stationCount = leg.passStopList.stationList.length ? leg.passStopList.stationList.length : "";
      stepDesc = `${timeMin}, ${stationCount}개 역 이동`;
    } else if (mode === "WALK") {
      stepDesc = `${timeMin}, ${distance}`;
    }

    // HTML 조립
    container.innerHTML += `
      <div class="route-step">
        <i class="${iconClass}"></i>
        <div class="step-content">
          <h4>${stepNumberSymbol} ${routeName ? routeName + " " : ""}${startName}${endName ? ` → ${endName}` : ""}</h4>
          <p>${stepDesc}</p>
        </div>
      </div>
    `;

    // 화살표 추가 (마지막 제외)
    if (i < legs.length - 1) {
      container.innerHTML += `<div class="step-arrow">↓</div>`;
    }
  });
}