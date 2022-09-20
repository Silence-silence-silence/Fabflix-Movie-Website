import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbmaster");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    String price = "";
    String sale = "";
    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");


        PrintWriter out = response.getWriter();
        price = request.getParameter("price");
        System.out.println("sadsa"+price);
        sale = request.getParameter("sale");
        System.out.println("sdasda"+sale);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("price", price);
        out.write(jsonObject.toString());



    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String fname = request.getParameter("fname");
        String lname = request.getParameter("lname");
        String card = request.getParameter("card");
        String exp = request.getParameter("exp");

        System.out.println(exp);
        request.getServletContext().log("pay with card: " + card);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        System.out.println(price);
        System.out.println(sale);
        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection())
        {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"

            String query = "SELECT * FROM creditcards where id = ? and firstName = ? and lastname = ? and expiration = ?;";



            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, card);
            statement.setString(2, fname);
            statement.setString(3, lname);
            statement.setString(4, exp);

            // statement.setString(2, password);

            System.out.println(statement);
            // Perform the query
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String maxquery = "SELECT max(id) FROM sales;";

                Statement maxstatement = conn.createStatement();

                ResultSet rsmax = maxstatement.executeQuery(maxquery);
                String id = "";

                if (rsmax.next()) {
                    id = rsmax.getString("max(id)");
                    System.out.println(id);
                }

                int realid = Integer.parseInt(id);
                realid++;

                String[] movies = sale.split(",");

                for (String a : movies)
                 {
                     String[] re = a.split("-");

                     for (int i = 0; i < Integer.parseInt(re[1]);i++)
                     {
                         String insquery = "INSERT INTO sales (id,customerId,movieId,saleDate)\n" +
                                 "VALUES (?,?,?,?);";
                         PreparedStatement insstatement = conn.prepareStatement(insquery);

                         DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                         LocalDateTime now = LocalDateTime.now();

                         // Set the parameter represented by "?" in the query to the id we get from url,
                         // num 1 indicates the first "?" in the query

                         HttpSession session = request.getSession();
                         User u = (User)session.getAttribute("user");
                         String username = u.getId();


                         insstatement.setString(1, Integer.toString(realid));

                         insstatement.setString(2, username);

                         insstatement.setString(3, re[0]);

                         insstatement.setString(4, dtf.format(now));


                         System.out.println(insstatement);
                          insstatement.executeUpdate();


                     }

                 }

                 //remove session cart
                 HttpSession session = request.getSession();
                 session.removeAttribute("previousItems");



                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", realid);

            } else {
                // Login fail
                System.out.println("xx");
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Pay failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                // responseJsonObject.addProperty("message", "user with email " + email + " doesn't exist");
                responseJsonObject.addProperty("message", "Incorrect Information");
            }

            out.write(responseJsonObject.toString());
            rs.close();
            statement.close();

            response.setStatus(200);

        }

        catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
    }
}