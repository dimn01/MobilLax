var map, lat, lon, now, s_marker, e_marker;
var markers = [];
// lonlat.lat(); 위도 lonlat.lng(); 경도

function initTmap() {
        map = new Tmapv3.Map("map_div", {
        center: new Tmapv3.LatLng(37.56520450, 126.98702028), // 지도 초기 좌표
        width: "100%",
        height: "100%",
        zoom: 16
    });

    map.on("Click", function(evt) {
        // contextmenu 숨김
        $("#contextMenu").hide();
        removeMarkers()
        lngLat = evt.data.lngLat;
        console.log(lngLat);
        let marker = new Tmapv3.Marker({
            position: new Tmapv3.LatLng(lngLat._lat,lngLat._lng),
            map: map
        });
            markers.push(marker);
    });

    // 현위치 마커
    if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
        function(position) {
            var n_lat = position.coords.latitude;
            var n_lon = position.coords.longitude;

            // 팝업 생성
            // let content = "<div style=' position: relative; border-bottom: 1px solid #dcdcdc; line-height: 18px; padding: 0 3px 2px 0;'>"
            // 		+ "<div style='font-size: 12px; line-height: 15px;'>"
            // 		+ "<span style='display: inline-block; width: 4px; height: 5px; background-image: url(/resources/images/common/icon_blet.png); vertical-align: middle; margin-right: 5px;'></span>현재위치"
            // 		+ "</div>" + "</div>";

            // 현위치 마커 생성
            // let marker = new Tmapv3.Marker({
            // 	position : new Tmapv3.LatLng(n_lat,n_lon),
            // 	map : map
            // });

            // 마커 설명
            // InfoWindow = new Tmapv3.InfoWindow({
            // 	position : new Tmapv3.LatLng(n_lat,n_lon),
            // 	content : content,
            // 	type : 2,
            // 	map : map
            // });

            // 현위치
            now = new Tmapv3.LatLng(n_lat,n_lon);
            map.setCenter(now);
            map.setZoom(15);
        });
    }
    map.addListener("contextmenu", onContextmenu); // 지도 우클릭시, 이벤트 리스너 등록.
    map.addListener("click", onClick);

    // 2. POI 통합 검색 API 요청
    $("#bttn").click(function(){

        var searchKeyword = $('#searchInput').val();
        var headers = {};
        headers["appKey"]="gyeuBycm5d6tSeRpNvNyq1dm6YctkXF29812OT8D";

        $.ajax({
            method:"GET",
            headers : headers,
            url:"https://apis.openapi.sk.com/tmap/pois?version=1&format=json&callback=result",
            async:false,
            data:{
                "searchKeyword" : searchKeyword,
                "resCoordType" : "EPSG3857",
                "reqCoordType" : "WGS84GEO",
                "count" : 10
            },
            success:function(response){
                var resultpoisData = response.searchPoiInfo.pois.poi;

                // 기존 마커, 팝업 제거
                if(markers.length > 0){
                    for(var i in markers){
                        markers[i].setMap(null);
                    }
                }
                var innerHtml ="";	// searchResult 결과값 노출 위한 변수
                var positionBounds = new Tmapv3.LatLngBounds();		//맵에 결과물 확인 하기 위한 LatLngBounds객체 생성

                for(var k in resultpoisData){

                    var noorLat = Number(resultpoisData[k].noorLat);
                    var noorLon = Number(resultpoisData[k].noorLon);
                    var name = resultpoisData[k].name;

                    var pointCng = new Tmapv3.Point(noorLon, noorLat);
                    var projectionCng = new Tmapv3.Projection.convertEPSG3857ToWGS84GEO(pointCng);

                    var lat = projectionCng._lat;
                    var lon = projectionCng._lng;

                    var markerPosition = new Tmapv3.LatLng(lat, lon);

                    marker = new Tmapv3.Marker({
                        position : markerPosition,
                        //icon : "/upload/tmap/marker/pin_b_m_a.png",
                        icon : "/upload/tmap/marker/pin_b_m_" + k + ".png",
                        iconSize : new Tmapv3.Size(24, 38),
                        title : name,
                        map:map
                    });

                    innerHtml += "<li><img src='/upload/tmap/marker/pin_b_m_" + k + ".png' style='vertical-align:middle;'/><span>"+name+"</span></li>";

                    markers.push(marker);
                    positionBounds.extend(markerPosition);	// LatLngBounds의 객체 확장
                }

                $("#info_main").html(innerHtml);	//searchResult 결과값 노출
                map.panToBounds(positionBounds);	// 확장된 bounds의 중심으로 이동시키기
                map.zoomOut();

            },
            error:function(request,status,error){
                console.log("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
            }
        });
    });
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

function hideContextMenu() {
    $("#contextMenu").hide(); // 메뉴 숨기기
};

// 좌클릭
function onClick(e) {
    var result = '클릭한 위치의 좌표는' + e.latLng + '입니다.';
    var resultDiv = document.getElementById("result");
    resultDiv.innerHTML = result;

    $(document).ready(function() {
        hideContextMenu();
    });
}

// 우클릭
function onContextmenu(e) {
    // var result = '우클릭한 위치의 좌표는' + e.latLng._lat +', ' + e.latLng._lng + '입니다.';
    // var resultDiv = document.getElementById("result");
    // alert(result);
    lat = e.latLng._lat;
    lon = e.latLng._lng;

    // var lngLat = e.latLng;
    // console.log(lngLat);

    const menu = document.getElementById('contextMenu');
    menu.style.left = e.pageX + "px";
    menu.style.top = e.pageY + "px";
    menu.style.display = "block";
}

$(document).ready(function() {
    $("#setStart").click(function() {
        alert("출발지로 설정되었습니다.");
        if (s_marker != null) {
            s_marker.setMap(null);
        }
        s_marker = new Tmapv3.Marker({
            position: new Tmapv3.LatLng(lat,lon),
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
        e_marker = new Tmapv3.Marker({
            position: new Tmapv3.LatLng(lat,lon),
            map: map
        });
        hideContextMenu();
    });
});