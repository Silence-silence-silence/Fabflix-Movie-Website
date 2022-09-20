let add_form = $("#add_movie2");
let star_form = $("#add_star");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    console.log(resultDataString);
    var newData = JSON.stringify(resultDataString);
    let resultDataJson = JSON.parse(newData);

    console.log("handle add movie response");

    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    //  if (resultDataJson["status"] === "success") {

   //     window.location.replace("main.html");
   //  } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        add_form[0].reset();
        star_form[0].reset();
        $("#message").text(resultDataJson["message"]);

        popup.classList.add('open');
    // }
}

const popup = document.querySelector('.popup');


function closepop() {
    popup.classList.remove('open');
}
/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/dashboard", {
            method: "GET",
            // Serialize the login form to the data sent by POST request
            data: add_form.serialize(),
            success: handleLoginResult
        }
    );
}

function submitLoginForm1(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/dashboard", {
            method: "GET",
            // Serialize the login form to the data sent by POST request
            data: star_form.serialize(),
            success: handleLoginResult
        }
    );
}

function handleResult(resultData) {

    console.log("handleResult: handle price");

    let total = jQuery("#metadata");

     let res = "";

    for (let i = 0; i < resultData.length; i++) {
        res += "<h2>" + resultData[i]["table_title"] + " : </h2>";
        res += "<table> <tr> <th> Field </th> <th> Type </th> <th> Null </th> <th> Key </th> <th> Default </th> <th> Extra </th> </tr>" ;
        const f = resultData[i]["Field"].split("-");
        const t = resultData[i]["Type"].split("-");
        const n = resultData[i]["NULL"].split("-");
        const k = resultData[i]["Key"].split("-");
        const d = resultData[i]["Default"].split("-");
        const e = resultData[i]["Extra"].split("-");

        for (let j = 0; j < f.length; j++) {
            res+= "<tr>";
            res += "<td> " + f[j]+ "</td>";
            res += "<td> " + t[j]+ "</td>";
            res += "<td> " + n[j]+ "</td>";
            res += "<td> " + k[j]+ "</td>";
            res += "<td> " + d[j]+ "</td>";
            res += "<td> " + e[j]+ "</td>";
            res+= "</tr>";

        }


        res += "</table>";

    }


    total.append(res);

}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "POST", // Setting request method
    url: "api/dashboard",
    success: (resultData) => handleResult(resultData)
});

// Bind the submit action of the form to a handler function
add_form.submit(submitLoginForm);
star_form.submit(submitLoginForm1);
