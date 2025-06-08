let resultdrawArr = [];
document.addEventListener("DOMContentLoaded", async function () {
  const type = new URLSearchParams(window.location.search).get("type") || "shortestTime";
  const titleMap = {
    "shortestTime": "최단시간 상세경로",
    "shortestDistance": "최단거리 상세경로",
    "leastTransfer": "최소환승 상세경로"
  };

  // h2 타이틀 변경
  const h2 = document.querySelector("#routeTitle");
  if (h2 && titleMap[type]) {
    h2.textContent = titleMap[type];
    document.title = `MobilLax - ${titleMap[type]}`;
  }

  // 지도 초기화
  map = new Tmapv2.Map("map_div", {
      center: new Tmapv2.LatLng(37.56520450, 126.98702028), // 지도 초기 좌표
      width: "100%",
      height: "100%",
      zoom: 16
  });

  const headers = {
      accept: 'application/json',
      "Content-Type": "application/json",
      "appKey": "gyeuBycm5d6tSeRpNvNyq1dm6YctkXF29812OT8D"
  };

  console.log(JSON.parse(localStorage.getItem("selectedTo")))
  const selectedFrom = JSON.parse(localStorage.getItem("selectedFrom")) || {};
  const selectedTo = JSON.parse(localStorage.getItem("selectedTo"));
  const params ={
      "startX" : Number(selectedFrom.x),
      "startY" : Number(selectedFrom.y),
      "endX" :  Number(selectedTo.x),
      "endY" :  Number(selectedTo.y),
      "lang" : 0,
      "format": 'json',
      "count": 10,
      "searchDttm": '202505221000'
  };

  // 경로 API 호출
  try {
      const response = await fetch("https://apis.openapi.sk.com/transit/routes/", {
          method: "POST",
          headers: headers,
          body: JSON.stringify(params)
      });
      const data = await response.json();
      console.log(data);
      const index = getBestItineraryIndex(type || "shortestTime", data);

      const legs = data.metaData.plan.itineraries[index].legs;
      const routeBounds = new Tmapv2.LatLngBounds();

      for (var leg of legs) {
          var marker_s = new Tmapv2.LatLng(Number(leg.start.lat), Number(leg.start.lon));
          var marke1r_e = new Tmapv2.LatLng(Number(leg.end.lat), Number(leg.end.lon));
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
    console.warn("🚨 API 실패, 더미 데이터 fallback:", error.message);

    try {
      const response = await fetch('https://3173cb8e-d014-4dcb-8da9-2e53ad672e15.mock.pstmn.io', {
                       method: "GET",
      });
      // 텍스트를 JSON으로 파싱
      const text = await response.text();
      const data = JSON.parse(text);
      console.log(data);
      const index = getBestItineraryIndex(type || "shortestTime", data);

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

    } catch (fallbackError) {
      console.error("❌ 더미 JSON도 불러오기 실패:", fallbackError);
      document.querySelector(".sidebar-content").innerHTML = "<p>경로 정보를 불러올 수 없습니다.</p>";
    }
  }
});

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

  if (type === "shortestDistance") {
    let minDistance = Infinity;
    itineraries.forEach((it, i) => {
      if (it.totalDistance < minDistance) {
        minDistance = it.totalDistance;
        bestIndex = i;
      }
    });
  } else if (type === "shortestTime") {
    let minTime = Infinity;
    itineraries.forEach((it, i) => {
      if (it.totalTime < minTime) {
        minTime = it.totalTime;
        bestIndex = i;
      }
    });
  } else if (type === "leastTransfer") {
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

document.addEventListener("DOMContentLoaded", () => {
  const payBtn = document.querySelector(".btn-pay");
  payBtn?.addEventListener("click", handleDirectPayment);
});

// 요약 정보 렌더링
function renderSummary(data) {
  const itinerary = data.metaData.plan.itineraries[0];
  const box = document.querySelector(".summary-box");

  const timeMin = Math.round(itinerary.totalTime / 60);
  const distKm = (itinerary.totalDistance / 1000).toFixed(1);
  const transfers = itinerary.transferCount;

  box.innerHTML = `
    <div class="summary-item">
      <i class="fas fa-clock"></i>
      <span>총 소요 시간</span>
      <strong>${Math.floor(timeMin / 60)}시간 ${timeMin % 60}분</strong>
    </div>
    <div class="summary-item">
      <i class="fas fa-won-sign"></i>
      <span>총 거리</span>
      <strong>${distKm}km</strong>
    </div>
    <div class="summary-item">
      <i class="fas fa-random"></i>
      <span>환승 횟수</span>
      <strong>${transfers}회</strong>
    </div>
  `;
}

// 세부 경로 렌더링
function renderSteps(itinerary) {
  const container = document.querySelector(".sidebar-content");
  container.innerHTML = "";

  itinerary.legs.forEach((leg, idx) => {
    const mode = leg.mode || "UNKNOWN";
    const iconClass = getIcon(mode);
    const rawRoute = leg.route || "노선 정보 없음";
    const routeText = mode === "WALK" ? "" : ` (${rawRoute})`;
    const time = leg.sectionTime ? Math.round(leg.sectionTime / 60) : "정보 없음";
    const start = leg.start?.name || "출발지 없음";
    const end = leg.end?.name || "도착지 없음";

    const stepEl = document.createElement("div");
    stepEl.classList.add("route-step");
    stepEl.dataset.index = idx;
    stepEl.legData = leg; // ✅ leg 객체 저장

    stepEl.innerHTML = `
      <i class="fas fa-${iconClass}"></i>
      <div class="step-content">
        <h4>${idx + 1}. ${start} → ${end}</h4>
        <p>${mode}${routeText} · ${time}분</p>
      </div>
    `;

    stepEl.addEventListener("click", () => {
      stepEl.classList.toggle("selected");
    });

    container.appendChild(stepEl);

    if (idx < itinerary.legs.length - 1) {
      const arrow = document.createElement("div");
      arrow.classList.add("step-arrow");
      arrow.innerHTML = `<i class="fas fa-arrow-down"></i>`;
      container.appendChild(arrow);
    }
  });
}

// 이동수단별 아이콘 매핑
function getIcon(mode) {
  const icons = {
    BUS: "bus",
    SUBWAY: "subway",
    WALK: "walking",
    TAXI: "car"
  };
  return icons[mode] || "map-marker-alt";
}

function showCartPopup() {
  document.getElementById("cartPopup").classList.remove("hidden");
}
function closeCartPopup() {
  document.getElementById("cartPopup").classList.add("hidden");
}
function goToCart() {
  location.href = "/cart";
}

function showNoRoutePopup() {
  document.getElementById("noRoutePopup").classList.remove("hidden");
}
function closeNoRoutePopup() {
  document.getElementById("noRoutePopup").classList.add("hidden");
}

document.addEventListener("DOMContentLoaded", () => {
  const cartBtn = document.querySelector(".btn-cart");
  cartBtn?.addEventListener("click", handleAddToCart);
});
async function handleAddToCart() {
  const selectedEls = [...document.querySelectorAll(".route-step.selected")];
  if (selectedEls.length === 0) {
    showNoRoutePopup();
    return;
  }

  // ✅ routePayment > 0 이고 출발지/도착지가 있는 경우만 추출
  const selectedLegs = selectedEls
    .map(el => el.legData)
    .filter(leg =>
      leg.routePayment > 0 &&
      leg.start?.name &&
      leg.end?.name
    )
    .map(leg => ({
      mode: leg.mode,
      route: leg.route,
      routeId: leg.routeId,
      routePayment: leg.routePayment,
      startName: leg.start.name,
      endName: leg.end.name
    }));

  if (selectedLegs.length === 0) {
    alert("요금이 있는 경로만 장바구니에 담을 수 있습니다.");
    return;
  }

  try {
    const res = await fetch("/api/cart/add", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ selectedLegs })
    });

    if (!res.ok) throw new Error("서버 응답 실패");

    const totalFare = await res.json();
    console.log("🛒 총 결제 금액:", totalFare);
    showCartPopup();

  } catch (error) {
    alert("장바구니 담기 실패: " + error.message);
  }
}
// 새로 추가된 바로 결제 함수
async function handleDirectPayment() {
  const selectedEls = [...document.querySelectorAll(".route-step.selected")];
  if (selectedEls.length === 0) {
    showNoRoutePopup();
    return;
  }

  const selectedLegs = selectedEls
    .map(el => el.legData)
    .filter(leg =>
      leg.routePayment > 0 &&
      leg.start?.name &&
      leg.end?.name
    )
    .map(leg => ({
      mode: leg.mode,
      route: leg.route,
      routeId: leg.routeId,
      routePayment: leg.routePayment,
      startName: leg.start.name,
      endName: leg.end.name
    }));

  if (selectedLegs.length === 0) {
    alert("요금이 있는 경로만 결제할 수 있습니다.");
    return;
  }

  try {
    const res = await fetch("/payment/direct-sdk-ready", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ selectedLegs })
    });

    if (!res.ok) throw new Error("결제 준비 실패");

    const payments = await res.json();

    for (const [transportType, data] of Object.entries(payments)) {
      const { storeId, channelKey, paymentId, orderName, amount, groupId } = data;

      const response = await PortOne.requestPayment({
        storeId,
        channelKey,
        paymentId,
        orderName,
        totalAmount: amount,
        currency: "CURRENCY_KRW",
        payMethod: "CARD"
      });

      if (response.code !== undefined) {
        alert(`${transportType} 결제가 실패했습니다: ${response.message}`);
        continue;
      }

      await fetch("/payment/complete", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          paymentId,
          groupId,
          amount,
          transportType
        })
      });

      alert(`${transportType} 결제가 완료되었습니다.`);
    }
  } catch (error) {
    alert("결제 중 오류가 발생했습니다: " + error.message);
  }
}
