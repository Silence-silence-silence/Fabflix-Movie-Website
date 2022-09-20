function handleResult(resultData) {

    console.log("handleResult: handle price");

    let total = jQuery("#total_price");

    let res = "<h1> Congratulations! Payment Received</h1>";
    res += "<h3>Total price: " + price + " </h3>";
    res += "<h3>Sale ID: " + sale + " </h3>";
    res += "<h3 align='center'>Oder Information </h3>";
    for (let i = 0; i < resultData.length; i++) {
        res += "<p>" + resultData[i]["movie_title"] + "</p>";

    }

    total.append(res);

}

function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

document.getElementById("home").addEventListener("click", () => {
    window.location.replace("main.html");
})

let price = getParameterByName('price');
let sale = getParameterByName('sale');
// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/confirmation?price=" + price + "&sale=" + sale, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});