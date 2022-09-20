let login_form = $("#login_form");
let search_form = $("#search_form");
let startIndex = 0;
let numRecords = 20;
/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */

var cacheHash = {}

function handleLoginResult(resultDataString) {
    window.location.replace("login.html");

}

function handleUserInfo(resultData) {
    // console.log(resultData);

    let genre_link = $("#genres_links");
    let char_link = $("#char_links");
    let rowHTML = "";
    for (let i = 0; i < resultData.length - 1; i++) {

        rowHTML += "<a class=\"link-dec\" href=\"movie.html?name=&director=&stars=&year=&genre=" + resultData[i]["genres"] + "\">\n" +
            "                " + resultData[i]["genres"] + "\n" +
            "            </a>";



        // Append the row created to the table body, which will refresh the page
    }
    let res = "";
    for (var i = 65; i <= 90; i++) {
        res += "<a class=\"link-dec\" href=\"movie.html?name=&director=&stars=&year=&genre=&AZ=" + String.fromCharCode(i) + "\">\n" +
            "        " + String.fromCharCode(i) + "\n" +
            "    </a>";
    }

    for (var x = 0; x <= 9; x++) {
        res += "<a class=\"link-dec\" href=\"movie.html?name=&director=&stars=&year=&genre=&AZ=" + x + "\">\n" +
            "        " + x + "\n" +
            "    </a>";
    }


    genre_link.append(rowHTML);
    char_link.append(res);


    username = resultData[resultData.length - 1]["genres"];


    $("#username").text("Welcome to Fablix, " + username);
}


function doFunc(resultDataString) {
    resultDataString.preventDefault();
    $.ajax(
        "api/main", {
            method: "GET",
            data: { action: "Logout" },
            success: handleLoginResult
        }
    );
}

// function handleSearchResult(resultDataString) {
//     window.location.replace("api/movies?" + search_form.serialize() + "&startIndex=" + startIndex);
// }

function handleAdvanceSearch(resultDataString) {
    resultDataString.preventDefault();
    window.location.replace("movie.html?" + search_form.serialize() +
        "&numRecords=" + numRecords + "&startIndex=" + startIndex);
    // $.ajax(
    //     "api/movies", {
    //         method: "GET",
    //         data: search_form.serialize() + "&startIndex=" + startIndex,
    //         success: handleMovieResult
    //     }
    // );
}

document.getElementById("cart_button").addEventListener("click", () => {
    window.location.replace("index.html");
})

$.ajax(
    "api/main", {
        method: "Post",
        success: handleUserInfo
    }
);

// Bind the submit action of the form to a handler function
login_form.submit(doFunc);
search_form.submit(handleAdvanceSearch);

$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function(query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,

    minChars: 3
        // there are some other parameters that you might want to use to satisfy all the requirements
        // TODO: add other parameters, such as minimum characters
});

function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")


    // TODO: if you want to check past query results first, you can do it here

    if (query in cacheHash) {
        console.log("using cache in frontend")
        handleLookupAjaxSuccess(cacheHash[query], query, doneCallback)
    } else {
        console.log("sending AJAX request to backend Java Servlet")
            // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
            // with the query data
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "api/main?query=" + escape(query),
            "success": function(data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function(errorData) {
                console.log("lookup ajax error")
                console.log(errorData)
            }
        })
    }

}

function handleLookupAjaxSuccess(data, query, doneCallback) {
    // console.log("lookup ajax successful")

    // parse the string into JSON
    var jsonData = JSON.parse(data);

    console.log(jsonData)

    // TODO: if you want to cache the result into a global variable you can do it here
    if (!(query in cacheHash)) {
        cacheHash[query] = data;
    }

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    jsonData = jsonData.slice(0, 10);
    doneCallback({ suggestions: jsonData });
}

function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion
    // suggestion["data"]["heroID"]
    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"])


    var link = "single-movie.html?id=" + suggestion["data"]["ID"]
    window.location.replace(link);
    console.log(link)

}

function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    // TODO: you should do normal search here
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})