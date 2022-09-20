/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    // show the session information 
    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);

    // show cart information
    handleCartArray(resultDataJson["previousItems"]);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    console.log(resultArray);
    let item_list = $("#item_list");
    let total_price = $("#total_price");
    let pay = $("#pay");
    // change it to html list
    let res = "<ul class='order_list'>";
    let sale = "";
    let total = 0;
    for (let i = 0; i < resultArray.length; i++) {
        const title = resultArray[i].split("-")[0];
        const id = resultArray[i].split("-")[1];
        const quantity = resultArray[i].split("-")[2];

        res += "<li class='movie_info'>" + title +  " : $" + 10 * quantity + " <BUTTON id='decrease' class='bu_back' onclick=\"handleCart('" + "decrease" + "','" + id + "')\">-</BUTTON>" + "     " + quantity  + "     " +"<BUTTON id='increase' class='bu_back' onclick=\"handleCart('" + "increase" + "','" + id + "')\">+</BUTTON>" + "     " +"<BUTTON id='delete'  class='bu_back' onclick=\"handleCart('" + "delete" + "','" + id + "')\">delete</BUTTON></li>";
        total = total + 10 * quantity;
        sale += id + "-" + quantity;
        if (i !== resultArray.length - 1) {
            sale += ",";
        }
    }
    res += "</ul>";

    let pes = "<h2 class = 'total_price1' align='center'>Total Price : " + total + "</h2>";
    // clear the old array and show the new array in the frontend
    item_list.html("");
    item_list.append(res);
    total_price.append(pes);

    let pyy = "<form id=\"payment\" method=\"get\" action = \"payment.html\">\n" +
        "<input type = \"hidden\" name = \"price\" value =" + total + " >\n" +
        "<input type = \"hidden\" name = \"sale\" value =" + sale + " >\n" +
        "        <input class = 'pay_style'  type=\"submit\" value=\"pay\">\n" +
        "        </form>";

    pay.append(pyy);



}

document.getElementById("home").addEventListener("click", () => {
    window.location.replace("main.html");
})


function handleCart(condition, item) {
    console.log("change quantity");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "POST", // Setting request method
        url: "api/index?id=" + item + "&condition=" + condition,


    });

    window.location.reload();


}

$.ajax("api/index", {
    method: "GET",
    success: handleSessionData
});