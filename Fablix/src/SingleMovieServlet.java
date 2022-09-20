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
import java.util.ArrayList;
import java.util.Date;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
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

    static String movieName;
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {



        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String movie_id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + movie_id);
        System.out.println("getting id: " + movie_id);
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"

            // String query = "select m.title, m.year, m.director, \n" +
            //         "                    substring(group_concat(DISTINCT  CONCAT_WS('-', sn.id, sn.name) separator ', ' ),1) as stars,\n" +
            //         "                    substring(group_concat(DISTINCT gn.name separator ', '),1) as genres, r.rating\n" +
            //         "                    from movies as m,stars_in_movies as s, stars as sn, genres as gn, genres_in_movies as g, ratings as r\n" +
            //         "                    where m.id = ? and s.movieId = ?and \n" +
            //         "                    g.movieId = ? and g.genreId = gn.id and m.id = r.movieId\n" +
            //         "                    and s.starId = sn.id";
            String  query = "SELECT m.id, m.title, m.year, m.director \n" +
                    "from movies as m \n" +
                    "where m.id = ? \n";
            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, movie_id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                //String starId = rs.getString("starId");

                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");

                String sub_query_gnames = "select substring(group_concat(DISTINCT g.name order by g.name asc separator ', '), 1) as gnames \n" +
                "from genres as g, genres_in_movies as gim \n" +
                "where gim.genreId = g.id and gim.movieId = ? ";
                PreparedStatement gnames_statement = conn.prepareStatement(sub_query_gnames);
                gnames_statement.setString(1, movie_id);
                ResultSet gnames_rs = gnames_statement.executeQuery();
                String movie_gnames = "N/A";
                if(gnames_rs.next()){
                    movie_gnames= gnames_rs.getString("gnames");
                }
                gnames_rs.close();

                String sub_query_snames = "select substring(group_concat(DISTINCT CONCAT_WS('-', s.id, s.name) order by s.name asc separator ', '), 1) as snames \n" +
                "from stars as s, stars_in_movies as sim \n" +
                "where sim.starId = s.id and sim.movieId = ? ";
                PreparedStatement snames_statement = conn.prepareStatement(sub_query_snames);
                snames_statement.setString(1, movie_id);
                ResultSet snames_rs = snames_statement.executeQuery();
                String movie_snames = "N/A";
                if(snames_rs.next()){
                    movie_snames= snames_rs.getString("snames");
                }
                snames_rs.close();

                String sub_query_rating = "select r.rating as rating\n" +
                "from ratings as r \n" +
                "where r.movieId = ?";
                PreparedStatement rating_statement = conn.prepareStatement(sub_query_rating);
                rating_statement.setString(1, movie_id);
                ResultSet rating_rs = rating_statement.executeQuery();
                String movie_rating = "N/A";
                if(rating_rs.next()){
                    movie_rating= rating_rs.getString("rating");
                }
                rating_rs.close();
                
                // String movieStar = rs.getString("stars");
                // String movieGenres = rs.getString("genres");
                // String movieRating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("star_name", movie_snames);
                jsonObject.addProperty("genres_name", movie_gnames);
                jsonObject.addProperty("movie_title", movieTitle);
                movieName = movieTitle;
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);
                jsonObject.addProperty("movie_rating", movie_rating);

                jsonArray.add(jsonObject);
            }

            JsonObject json_prev_url = new JsonObject();
            HttpSession session = request.getSession();
            String prev_url = (String)session.getAttribute("prev_url");
            json_prev_url.addProperty("prev_url", prev_url);
            jsonArray.add(json_prev_url);
            
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

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String id = request.getParameter("id");



        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);



                HttpSession session = request.getSession();


                String item = "";

                ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
                if (previousItems == null) {
                    previousItems = new ArrayList<>();
                    item = movieName + "-"+id + "-" + "1" ;
                    previousItems.add(item);
                    System.out.println(item);
                    session.setAttribute("previousItems", previousItems);
                } else {
                    // prevent corrupted states through sharing under multi-threads
                    // will only be executed by one thread at a time
                    synchronized (previousItems) {
                        int check = 0;
                        for (int i  = 0; i < previousItems.size(); i++)
                        {
                            if ((previousItems.get(i)).contains(id))
                            {
                                String[] splited = (previousItems.get(i)).split("-");
                                int count = Integer.parseInt(splited[2]);
                                count ++;
                                item = movieName+"-"+id+"-"+ count;
                                previousItems.set(i,item);
                                check = 1;
                            }
                        }
                        System.out.println(item);
                        if (check ==0)
                        {
                            item = movieName + "-"+id + "-" + "1" ;
                            previousItems.add(item);
                        }

                    }
                }

                JsonObject responseJsonObject = new JsonObject();

                JsonArray previousItemsJsonArray = new JsonArray();
                previousItems.forEach(previousItemsJsonArray::add);
                responseJsonObject.add("previousItems", previousItemsJsonArray);

                response.getWriter().write(responseJsonObject.toString());

            }




    }


