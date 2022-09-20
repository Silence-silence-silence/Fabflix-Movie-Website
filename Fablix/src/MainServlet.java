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
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "MainServlet", urlPatterns = "/api/main")
public class MainServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public static HashMap<Integer, String> superHeroMap = new HashMap<>();

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
                String action = request.getParameter("action");
                // System.out.println("run");
                if ("Logout".equals(action)) {
                    HttpSession session = request.getSession(true);
                    System.out.println("run1");
    
                    // set the logged_in attribute
                    // Boolean logged_in = (Boolean) session.getAttribute("logged_in");
                    // logged_in = false;
                    session.setAttribute("logged_in", null);
                    session.invalidate();
                    // response.sendRedirect("login.html");
                }

            try {
                // setup the response json arrray
                JsonArray jsonArray = new JsonArray();

                // get the query string from parameter
                String query = request.getParameter("query");

                // return the empty json array if query is null or empty
                if (query == null || query.trim().isEmpty()) {
                    response.getWriter().write(jsonArray.toString());
                    return;
                }

                // search on superheroes and add the results to JSON Array
                // this example only does a substring match
                // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars

                String fullsearch = query;
                PrintWriter out = response.getWriter();
                try (Connection conn = dataSource.getConnection()) {
                    // Get a connection from dataSource

                    String[] parse = fullsearch.split(" ");
                    query = "SELECT id, title \n" +
                            "    from movies  \n" +
                            "   where MATCH (title) AGAINST (? IN boolean mode) ";
                    String tmp = "";

                    for (int i = 0; i < parse.length; i++)
                    {
                        tmp += "+" + parse[i] + "* ";
                    }



                    // Declare our statement
                    PreparedStatement statement = conn.prepareStatement(query);

                    System.out.println(statement);
                    // Set the parameter represented by "?" in the query to the id we get from url,
                    // num 1 indicates the first "?" in the query
                    statement.setString(1, tmp);

                    // Perform the query
                    System.out.println(statement);
                    ResultSet rs = statement.executeQuery();


                    // Iterate through each row of rs
                    while (rs.next()) {

                        String movie_id = rs.getString("id");
                        String movie_title = rs.getString("title");

                        // Create a JsonObject based on the data we retrieve from rs



                        jsonArray.add(generateJsonObject(movie_id,movie_title));
                    }



                    rs.close();
                    statement.close();



                    // Write JSON string to output
                    out.write(jsonArray.toString());
                    // Set response status to 200 (OK)
                    response.setStatus(200);


                } catch (Exception e) {
                    // Write error message JSON object to output
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("errorMessage", e.getMessage());
                    out.write(jsonObject.toString());

                    // Log error to localhost log
                    request.getServletContext().log("Error:", e);
                    // Set response status to 500 (Internal Server Error)
                    response.setStatus(500);
                } finally {
                    out.close();
                }



            } catch (Exception e) {
                System.out.println(e);
                response.sendError(500, e.getMessage());
            }






    }

    private static JsonObject generateJsonObject(String ID, String Name) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", Name);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("ID", ID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // String item = request.getParameter("item");
        // System.out.println(item);
        HttpSession session = request.getSession();
        User u = (User)session.getAttribute("user");
        String username = u.getUsername();

        session.setAttribute("prev_url","main.html");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection())
        {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            Statement statement = conn.createStatement();

            String query = "SELECT * FROM genres;";


            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();
            while (rs.next())
            {
                String g = rs.getString("name");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("genres", g);

                jsonArray.add(jsonObject);
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("genres", username);
            jsonArray.add(jsonObject);
            out.write(jsonArray.toString());
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