function showSuggestions(value, type) {
  const suggestions = type === 'from' ? document.getElementById("fromSuggestions") : document.getElementById("toSuggestions");

  if (!value.trim()) {
    suggestions.innerHTML = '';
    return;
  }

  const ps = new kakao.maps.services.Places();
  ps.keywordSearch(value, function (data, status) {
    if (status !== kakao.maps.services.Status.OK) {
      suggestions.innerHTML = '<div>검색 결과가 없습니다.</div>';
      return;
    }

    const html = data.slice(0, 5).map(place => `
      <div onclick="selectSuggestion('${place.place_name}', '${type}')">
        <i class='fas fa-map-marker-alt'></i>
        <span>${place.place_name}</span>
        <small style='margin-left:auto;'>${place.address_name}</small>
      </div>
    `).join('');

    suggestions.innerHTML = html;
  });
}

function selectSuggestion(place, type) {
  document.getElementById(type + "Input").value = place;
  document.getElementById(type + "Suggestions").innerHTML = '';
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