document.write('<script scr="/init.js"></script>');

async function showSuggestions(value, type) {
  const suggestions = type === 'from' ? document.getElementById("fromSuggestions") : document.getElementById("toSuggestions");

  if (!value.trim()) {
    suggestions.innerHTML = '';
    return;
  }

  const headers = {
    "appKey": "gyeuBycm5d6tSeRpNvNyq1dm6YctkXF29812OT8D"
  };

  const params = new URLSearchParams({
    "version": 2,
    "searchKeyword": value,
    "resCoordType": "EPSG3857",
    "reqCoordType": "WGS84GEO",
    "searchtypCd": "A",
//    "centerLon": n_lng,
//    "centerLat": n_lat,
//    "radius": "0",
    "count": 5
  });

  try {
    const response = await fetch(`https://apis.openapi.sk.com/tmap/pois?version=1&format=json&callback=result&${params.toString()}`, {
    method: "GET",
    headers: headers
  });

    if(response.status != 200) {
        suggestions.innerHTML = '<div>검색 결과가 없습니다.</div>';
        return;
    }
    const data = await response.json();
    const resultpoisData = data.searchPoiInfo.pois.poi;

    let suggest = "";

    for (let k in resultpoisData) {
        const poi = resultpoisData[k];
        const name = poi.name;
        const addrName = poi.upperAddrName + " " + poi.middleAddrName
            + " " + poi.lowerAddrName;
        const a_lat = poi.noorLat;
        const a_lon = poi.noorLon;

        suggest += `<div onclick='selectSuggestion("${name}", "${type}", "${a_lat}", "${a_lon}")'>
            <i class='fas fa-map-marker-alt'></i>
            <span>${name}</span>
            <small style='margin-left:auto;'>${addrName}</small>
        </div>`;
    }
    suggestions.innerHTML = suggest;
  } catch (error) {
    console.error("API 호출 중 오류 발생:", error);
  }
}

function selectSuggestion(place, type, lat, lon) {
  document.getElementById(type + "Input").value = place;
  document.getElementById(type + "Suggestions").innerHTML = '';

  if (type == 'from') { setStart(lat, lon); }
  if (type == 'to') { setEnd(lat, lon); }
}

function swapInputs() {
  const fromInput = document.getElementById("fromInput");
  const toInput = document.getElementById("toInput");
  const temp = fromInput.value;
  fromInput.value = toInput.value;
  toInput.value = temp;
}

function showRouteResults() {
    const from = document.getElementById("fromInput").value;
    const to = document.getElementById("toInput").value;
    if (!from || !to) {
      alert("출발지와 도착지를 모두 입력해주세요.");
      return;
    }
    document.getElementById("routeResults").style.display = "block";
}