let place_order = $("#place_order");


function handleResult(resultData) {

    console.log("handleResult: handle price");

    let total = $("#total_price");

    let res = "<h2>Total price: " + price + " </h2>";
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

document.getElementById("cart_button").addEventListener("click", () => {
    window.location.replace("index.html");
})

document.getElementById("home").addEventListener("click", () => {
    window.location.replace("main.html");
})

let price = getParameterByName('price');
let sale = getParameterByName('sale');
// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/payment?price=" + price + "&sale=" + sale, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

function submitForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: place_order.serialize(),
            success: handlePayResult
        }
    );
}

function handlePayResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle pay response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {

        window.location.replace("confirmation.html?price=" + price + "&sale=" + resultDataJson["message"]);
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#pay_error_message").text(resultDataJson["message"]);
    }
}



place_order.submit(submitForm);