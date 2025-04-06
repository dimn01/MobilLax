var map, lat, lng, now, marker, s_marker, e_marker;
var n_lat, n_lng;
var markers = [];

function initTmap() {
        map = new Tmapv2.Map("map_div", {
        center: new Tmapv2.LatLng(37.56520450, 126.98702028), // 지도 초기 좌표
        width: "100%",
        height: "100%",
        zoom: 16
    });

    // 현위치 마커
    if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
        function(position) {
            n_lat = position.coords.latitude;
            n_lng = position.coords.longitude;

            // 팝업 생성
            // let content = "<div style=' position: relative; border-bottom: 1px solid #dcdcdc; line-height: 18px; padding: 0 3px 2px 0;'>"
            // 		+ "<div style='font-size: 12px; line-height: 15px;'>"
            // 		+ "<span style='display: inline-block; width: 4px; height: 5px; background-image: url(/resources/images/common/icon_blet.png); vertical-align: middle; margin-right: 5px;'></span>현재위치"
            // 		+ "</div>" + "</div>";

            // 현위치 마커 생성
            // let marker = new Tmapv2.Marker({
            // 	position : new Tmapv2.LatLng(n_lat,n_lng),
            // 	map : map
            // });

            // 마커 설명
            // InfoWindow = new Tmapv2.InfoWindow({
            // 	position : new Tmapv2.LatLng(n_lat,n_lng),
            // 	content : content,
            // 	type : 2,
            // 	map : map
            // });

            // 현위치
            now = new Tmapv2.LatLng(n_lat,n_lng);
            map.setCenter(now);
            map.setZoom(15);
        });
    }

    map.addListener("contextmenu", onContextmenu);
    map.addListener("click", onClick);
}

// 현위치 이동
function Move(){
    map.setCenter(now); // 지도의 중심 좌표를 설정합니다.
}

// 모든 마커 제거거
function removeMarkers() {
    for (let i = 0; i < markers.length; i++) {
        markers[i].setMap(null);
    }
    markers = [];
}

// 좌클릭
function onClick(e) {
    hideContextMenu();
    if(marker != null) { marker.setMap(null); marker = null;}
}

// 우클릭
function onContextmenu(e) {
    hideContextMenu();
    removeMarkers();
    lat = e.latLng.lat();
    lng = e.latLng.lng();
    marker = new Tmapv2.Marker({
        position: new Tmapv2.LatLng(lat,lng),
        icon: `https://tmapapi.tmapmobility.com/upload/tmap/marker/pin_r_s_simple.png`,
        map: map
    });
    markers.push(marker);

    // var lngLat = e.lngLat;
    // console.log(lngLat);

    const menu = document.getElementById('contextMenu');
    menu.style.left = e.pageX + "px";
    menu.style.top = e.pageY + "px";
    menu.style.display = "block";
}

function markerClick(e) {
    alert('wow');
}

$(document).ready(function() {
    $("#setStart").click(function() {
        alert("출발지로 설정되었습니다.");
        if (s_marker != null) {
            s_marker.setMap(null);
        }
        s_marker = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(lat,lng),
            icon: `https://tmapapi.tmapmobility.com/upload/tmap/marker/pin_r_m_s.png`,
            title: 'start',
            map: map
        });
        console.log(s_marker['_marker_data']['vsmMarker']['_lngLat'][0]);
        hideContextMenu();
    });

    $("#setWaypoint").click(function() {
        alert("경유지로 설정되었습니다.");
        hideContextMenu();
    });

    $("#setEnd").click(function() {
        alert("목적지로 설정되었습니다.");
        if (e_marker != null) {
            e_marker.setMap(null);
        }
        e_marker = new Tmapv2.Marker({
            position: new Tmapv2.LatLng(lat,lng),
            icon: `https://tmapapi.tmapmobility.com/upload/tmap/marker/pin_r_m_e.png`,
            title: 'end',
            map: map
        });
        hideContextMenu();
    });
})

function hideContextMenu() {
    const contextMenu = document.getElementById("contextMenu");
    contextMenu.style.display = "none";
    // $("#contextMenu").hide(); // 메뉴 숨기기
}