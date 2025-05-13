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
        "format": 'json',
        "count": 10,
        "searchDttm": '202505131000'
    };

    try {
        const response = await fetch("https://apis.openapi.sk.com/transit/routes/", {
            method: "POST",
            headers: headers,
            body: JSON.stringify(params)
        });
        const data = await response.json();
        console.log(data);

    } catch (error) {
        console.error("car API 호출 중 오류 발생:", error);
    }
}