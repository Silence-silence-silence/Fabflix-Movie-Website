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

let cart = $("#cart");
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

    let starInfoElement = jQuery("#movie_info");

    let snames = resultData[0]["star_name"];
    let html_content = "";
    if(snames == "null" || snames == null){
        const id_name_array = []
        html_content += "N/A";
    }
    else {
        const id_name_array = snames.split(', ');
        for (let j = 0; j < id_name_array.length; j++) {
            const star_id = id_name_array[j].split("-")[0];
            const star_name = id_name_array[j].split("-")[1];
            html_content +=
                '<a href="single-star.html?id=' + star_id + '">' +
                star_name + // display star_name for the link text
                '</a>' + ', '
        }
        html_content = html_content.slice(0, -2);
    }

    let gnames = resultData[0]["genres_name"];
    let gnames_html_content = "";
    if (gnames == null || gnames == "null") {
        gnames_html_content += "N/A";
    } else {
        const id_name_arrayg = gnames.split(', ');
        for (let j = 0; j < id_name_arrayg.length; j++) {
            const g_name = id_name_arrayg[j];
            gnames_html_content +=
                '<a href="movie.html?movies?name=&director=&stars=&year=&genre=' + g_name + '&AZ=' + '">' +
                g_name + // display movie_name for the link text
                '</a>' + ', '
        }
        gnames_html_content = gnames_html_content.slice(0, -2);
    }

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<h1>" + resultData[0]["movie_title"] + "</h1>" +
        "<h3>Year: " + "<span>" + resultData[0]["movie_year"] + "</span>" + "</h3>" +
        "<h3>Director: " + "<span>" + resultData[0]["movie_director"] + "</span>" + "</h3>" +
        "<h3>Genres: " + gnames_html_content + "</h3>" +
        "<h3>Stars: " + html_content + "</h3>" +
        "<h3>Rating: " + "<span>" + resultData[0]["movie_rating"] + "</span>" + "</h3>"
    );

    console.log("handleResult: populating movie info from resultData");

    prev_url = resultData[1]["prev_url"];
    document.getElementById("back-button-text").parentElement.href = prev_url;
    console.log(prev_url);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */
function handleCartInfo(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax("api/single-movie?id=" + movieId, {
        method: "POST",
        data: cart.serialize()

    });

    // clear input form
    cart[0].reset();
}

const popup = document.querySelector('.popup');

function openpop() {
    popup.classList.add('open');
}

function closepop() {
    popup.classList.remove('open');
}

document.getElementById("cart_button").addEventListener("click", () => {
    window.location.replace("index.html");
})

document.getElementById("home").addEventListener("click", () => {
        window.location.replace("main.html");
    })
    // Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

cart.submit(handleCartInfo);