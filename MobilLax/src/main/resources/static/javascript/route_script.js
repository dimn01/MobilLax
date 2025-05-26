document.addEventListener("DOMContentLoaded", async function () {
  const params = new URLSearchParams(window.location.search);
  const type = params.get("type") || "shortestTime";  // 기본값 설정

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

  // 지도 초기화 (카카오맵 사용 시)
  if (window.kakao && window.kakao.maps) {
    kakao.maps.load(() => {
      const container = document.getElementById('map');
      if (container) {
        new kakao.maps.Map(container, {
          center: new kakao.maps.LatLng(37.5665, 126.9780),
          level: 4
        });
      }
    });
  }

  // 경로 API 호출
  try {
    const res = await fetch(`/api/route/detail?type=${type}`);
    if (!res.ok) throw new Error("API 호출 실패");

    const data = await res.json();
    renderSummary(data);
    renderSteps(data.metaData.plan.itineraries[0]);  // 첫 번째 경로만 사용

  } catch (error) {
    console.warn("🚨 API 실패, 더미 데이터 fallback:", error.message);

    try {
      const dummyRes = await fetch("/javascript/dummy/route_dummy_data.json");
      const dummyData = await dummyRes.json();
      renderSummary(dummyData);
      renderSteps(dummyData.metaData.plan.itineraries[0]);
    } catch (fallbackError) {
      console.error("❌ 더미 JSON도 불러오기 실패:", fallbackError);
      document.querySelector(".sidebar-content").innerHTML = "<p>경로 정보를 불러올 수 없습니다.</p>";
    }
  }
});


// 요약 정보 렌더링
function renderSummary(data) {
  const itinerary = data.metaData.plan.itineraries[0];
  const box = document.querySelector(".summary-box");

  const timeMin = Math.round(itinerary.totalTime / 60);
  const distKm = (itinerary.totalDistance / 1000).toFixed(1);
  const transfers = itinerary.transferCount;

  box.innerHTML = `
    <p>총 소요 시간: ${Math.floor(timeMin / 60)}시간 ${timeMin % 60}분</p>
    <p>총 거리: ${distKm}km</p>
    <p>환승 횟수: ${transfers}회</p>
  `;
}


// 세부 경로 렌더링
function renderSteps(itinerary) {
  const container = document.querySelector(".sidebar-content");
  container.innerHTML = "";

  itinerary.legs.forEach((leg, idx) => {
    const mode = leg.mode || "UNKNOWN";
    const iconClass = getIcon(mode);
    const rawRoute = leg.route || "노선 정보 없음"; // 변수 이름 변경! ✅

    const routeText = mode === "WALK" ? "" : ` (${rawRoute})`;
    const time = leg.sectionTime ? Math.round(leg.sectionTime / 60) : "정보 없음";

    const start = leg.startName || leg.start?.name || "출발지 없음";
    const end = leg.endName || leg.end?.name || "도착지 없음";

    const stepEl = document.createElement("div");
    stepEl.classList.add("route-step");

    stepEl.innerHTML = `
      <i class="fas fa-${iconClass}"></i>
      <div class="step-content">
        <h4>${idx + 1}. ${start} → ${end}</h4>
        <p>${mode}${routeText} · ${time}분</p>
      </div>
    `;

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