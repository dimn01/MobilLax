async function publicRoute() {
    const headers = {
        accept: 'application/json',
        "Content-Type": "application/json",
        "appKey": "gyeuBycm5d6tSeRpNvNyq1dm6YctkXF29812OT8D"
    };

    const {s_lat, s_lon, e_lat, e_lon} = JSON.parse(localStorage.getItem('info'));
    const params ={
        "startX" : Number(s_lon),
        "startY" : Number(s_lat),
        "endX" :  Number(e_lon),
        "endY" :  Number(e_lat),
        "lang" : 0,
        "format": 'json',
        "count": 10,
        "searchDttm": '202505221000'
    };

    try {
        const response = await fetch("https://apis.openapi.sk.com/transit/routes/", {
            method: "POST",
            headers: headers,
            body: JSON.stringify(params)
        });
        const data = await response.json();
        console.log(data);
        // mock data download
//
//        const text = JSON.stringify(data, null, 2);
//
//        // 텍스트 파일로 만들기
//        const blob = new Blob([text], { type: "text/plain" });
//        const url = URL.createObjectURL(blob);
//
//        const a = document.createElement("a");
//        a.href = url;
//        a.download = "mock.txt";
//        document.body.appendChild(a);
//        a.click();
//        document.body.removeChild(a);
//        URL.revokeObjectURL(url);

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

    } catch (error) {
        console.error("car API 호출 중 오류 발생:", error);
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