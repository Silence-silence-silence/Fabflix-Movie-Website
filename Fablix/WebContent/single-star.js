/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */

let prev_url = '';

/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
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

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#star_info");


    let mnames = resultData[0]["movie_title"];
    let html_content = "";
    const id_name_array = mnames.split(', ');
    for (let j = 0; j < id_name_array.length; j++) {
        const movie_id = id_name_array[j].split("-")[0];
        const movie_name = id_name_array[j].split("-")[1];
        html_content +=
            '<a href="single-movie.html?id=' + movie_id + '">' +
            movie_name + // display star_name for the link text
            '</a>' + ', '
    }
    html_content = html_content.slice(0, -2);

    // append two html <h3> created to the body, which will refresh the page
    if (resultData[0]["star_dob"] == null) {
        starInfoElement.append("<h1>" + resultData[0]["star_name"] + "</h1>" +
            "<h3>YEAR of Birth: " + "<span>N/A</span>" + "</h3>" +
            "<h3>Movies: " + html_content + "</h3>");
    } else {
        starInfoElement.append("<h1>" + resultData[0]["star_name"] + "</h1>" +
            "<h3>YEAR of Birth: " + "<span>" + resultData[0]["star_dob"] + "</span>" + "</h3>" +
            "<h3>Movies: " + html_content + "</h3>");
    }

    console.log("handleResult: populating movie table from resultData");

    prev_url = resultData[1]["prev_url"];
    document.getElementById("back-button-text").parentElement.href = prev_url;
    console.log(prev_url);
    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    // let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    // for (let i = 0; i < Math.min(10, resultData.length); i++) {
    //     let rowHTML = "";
    //     rowHTML += "<tr>";
    //     //rowHTML += "<th>" + resultData[i]["star_name"] + "</th>";
    //
    //     let mnames = resultData[i]["movie_title"];
    //     let html_content = "";
    //     const id_name_array = mnames.split(', ');
    //     for (let j = 0; j < id_name_array.length; j++) {
    //         const movie_id = id_name_array[j].split("-")[0];
    //         const movie_name = id_name_array[j].split("-")[1];
    //         html_content +=
    //             '<a href="single-movie.html?id=' + movie_id + '">' +
    //             movie_name + // display star_name for the link text
    //             '</a>' + ', '
    //     }
    //     rowHTML += "<th>" + html_content.slice(0, -2) + "</th>";
    //
    //     rowHTML += "</tr>";
    //
    //     // Append the row created to the table body, which will refresh the page
    //     movieTableBodyElement.append(rowHTML);
    // }
}

document.getElementById("back-button-text").addEventListener("click", () => {
    window.location.replace(prev_url);
});
/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

document.getElementById("cart_button").addEventListener("click", () => {
    window.location.replace("index.html");
});

document.getElementById("home").addEventListener("click", () => {
    window.location.replace("main.html");
});
// Get id from URL
let starId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/single-star?id=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});