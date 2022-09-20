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
import java.util.ArrayList;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

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
        response.setContentType("application/json");


        PrintWriter out = response.getWriter();
        String price = request.getParameter("price");

        String sale = request.getParameter("sale");


        request.getServletContext().log("getting sale: " + sale);



        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"

            String query = "SELECT m.title FROM sales as s, movies as m\n" +
                    " where s.id = ? and s.movieId = m.id;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, sale);

            System.out.println(statement);

            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            ArrayList<String> titles = new ArrayList<>();
            // Iterate through each row of rs
            while (rs.next()) {

                //String starId = rs.getString("starId");

                String movieTitle = rs.getString("title");
                int check = 0;
                for (int i = 0; i < titles.size(); i++)
                {
                    if ((titles.get(i)).contains(movieTitle))
                    {
                        String[] sp = (titles.get(i)).split(":");
                        int count = Integer.parseInt(sp[1]);
                        count++;
                        String update = movieTitle + ":" + count;
                        titles.set(i,update);
                        check = 1;
                        break;
                    }


                }

                if (check == 0)
                {
                    titles.add(movieTitle+ ":1" );
                }


                // Create a JsonObject based on the data we retrieve from rs
            }

            for (String title : titles)
            {

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_title", title);
                System.out.println(title);


                jsonArray.add(jsonObject);
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


    }


}