document.write('<script scr="/init.js"></script>');

// POI 통합 검색 API 요청
async function locationSearch () {
    const searchKeyword = document.getElementById("searchKeyword").value;
    // console.log(searchKeyword);
    const headers = {
            "appKey": "gyeuBycm5d6tSeRpNvNyq1dm6YctkXF29812OT8D"
        };
    const params = new URLSearchParams({
            "version": 2,
            "searchKeyword": searchKeyword,
            "resCoordType": "EPSG3857",
            "reqCoordType": "WGS84GEO",
            "searchtypCd": "A",
            "centerLon": n_lng,
            "centerLat": n_lat,
            "radius": "0",
            "count": 6
        });

    try {
            const response = await fetch(`https://apis.openapi.sk.com/tmap/pois?version=1&format=json&callback=result&${params.toString()}`, {
                method: "GET",
                headers: headers
            });

            const data = await response.json();
            console.log(data);
            const resultpoisData = data.searchPoiInfo.pois.poi;
            console.log(resultpoisData);

            removeMarkers()

            let innerHtml = "";
            const positionBounds = new Tmapv2.LatLngBounds();

            for (let k in resultpoisData) {
                const poi = resultpoisData[k];
                const noorLat = Number(poi.noorLat);
                const noorLon = Number(poi.noorLon);
                const name = poi.name;

                const pointCng = new Tmapv2.Point(noorLon, noorLat);
                const projectionCng = new Tmapv2.Projection.convertEPSG3857ToWGS84GEO(pointCng);
                const search_lat = projectionCng._lat;
                const search_lon = projectionCng._lng;

                const markerPosition = new Tmapv2.LatLng(search_lat, search_lon);
                const marker = new Tmapv2.Marker({
                    position: markerPosition,
                    icon: `https://tmapapi.tmapmobility.com/upload/tmap/marker/pin_b_m_${k}.png`,
                    iconSize: new Tmapv2.Size(24, 38),
                    title: name,
                    map: map
                });

                innerHtml += `<li><img src='https://tmapapi.tmapmobility.com/upload/tmap/marker/pin_b_m_${k}.png' style='vertical-align:middle;'/><span>${name}</span></li>`;
                markers.push(marker);
                positionBounds.extend(markerPosition);
            }

            document.getElementById("searchResult").innerHTML = innerHtml;
            map.panToBounds(positionBounds);
            console.log(markers[0].getPosition()+positionBounds);
            // map.setCenter(markers[0].getPosition());
            map.setZoom(3);

        } catch (error) {
            console.error("API 호출 중 오류 발생:", error);
        }
};

//function showSuggestions(value, type) {
//
//    const searchKeyword = document.getElementById("searchKeyword").value;
//    // console.log(searchKeyword);
//    const headers = {
//        "appKey": "gyeuBycm5d6tSeRpNvNyq1dm6YctkXF29812OT8D"
//    };
//    const params = new URLSearchParams({
//        "version": 2,
//        "searchKeyword": searchKeyword,
//        "resCoordType": "EPSG3857",
//        "reqCoordType": "WGS84GEO",
//        "searchtypCd": "A",
//        "centerLon": n_lng,
//        "centerLat": n_lat,
//        "radius": "0",
//        "count": 5
//    });
//
//try {
//        const response = await fetch(`https://apis.openapi.sk.com/tmap/pois?version=1&format=json&callback=result&${params.toString()}`, {
//            method: "GET",
//            headers: headers
//        });
//
//        const data = await response.json();
//        console.log(data);
//        const resultpoisData = data.searchPoiInfo.pois.poi;
//        console.log(resultpoisData);
//
//        removeMarkers()
//
//        let innerHtml = "";
//        const positionBounds = new Tmapv2.LatLngBounds();
//
//        for (let k in resultpoisData) {
//            const poi = resultpoisData[k];
//            const noorLat = Number(poi.noorLat);
//            const noorLon = Number(poi.noorLon);
//            const name = poi.name;
//
//            const pointCng = new Tmapv2.Point(noorLon, noorLat);
//            const projectionCng = new Tmapv2.Projection.convertEPSG3857ToWGS84GEO(pointCng);
//            const search_lat = projectionCng._lat;
//            const search_lon = projectionCng._lng;
//
//            const markerPosition = new Tmapv2.LatLng(search_lat, search_lon);
//            const marker = new Tmapv2.Marker({
//                position: markerPosition,
//                icon: `https://tmapapi.tmapmobility.com/upload/tmap/marker/pin_b_m_${k}.png`,
//                iconSize: new Tmapv2.Size(24, 38),
//                title: name,
//                map: map
//            });
//
//            innerHtml += `<li><img src='https://tmapapi.tmapmobility.com/upload/tmap/marker/pin_b_m_${k}.png' style='vertical-align:middle;'/><span>${name}</span></li>`;
//            markers.push(marker);
//            positionBounds.extend(markerPosition);
//        }
//
//        document.getElementById("searchResult").innerHTML = innerHtml;
//        map.panToBounds(positionBounds);
//        console.log(markers[0].getPosition()+positionBounds);
//        // map.setCenter(markers[0].getPosition());
//        map.setZoom(3);
//
//    } catch (error) {
//        console.error("API 호출 중 오류 발생:", error);
//    }

//-----------------------------------------------------------------------------
//  const suggestions = type === 'from' ? document.getElementById("fromSuggestions") : document.getElementById("toSuggestions");
//
//  if (!value.trim()) {
//    suggestions.innerHTML = '';
//    return;
//  }
//
//  const ps = new kakao.maps.services.Places();
//  ps.keywordSearch(value, function (data, status) {
//    if (status !== kakao.maps.services.Status.OK) {
//      suggestions.innerHTML = '<div>검색 결과가 없습니다.</div>';
//      return;
//    }
//
//    const html = data.slice(0, 5).map(place => `<div onclick="selectSuggestion('${place.place_name}', '${type}')">
//        <i class='fas fa-map-marker-alt'></i>
//        <span>${place.place_name}</span>
//        <small style='margin-left:auto;'>${place.address_name}</small>
//      </div>`).join('');
//
//    suggestions.innerHTML = html;
//  });
//}
//
//function selectSuggestion(place, type) {
//  document.getElementById(type + "Input").value = place;
//  document.getElementById(type + "Suggestions").innerHTML = '';
//}
//
//function swapInputs() {
//  const fromInput = document.getElementById("fromInput");
//  const toInput = document.getElementById("toInput");
//  const temp = fromInput.value;
//  fromInput.value = toInput.value;
//  toInput.value = temp;
//}