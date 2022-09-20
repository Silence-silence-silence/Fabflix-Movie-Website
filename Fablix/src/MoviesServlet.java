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
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;


// Declaring a WebServlet called MoviesServlet, which maps to url "/api/movies"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //TS TJ time test
        //------------------------------------------------------//
        long startTj;
        long endTj;
        long TJ;
        long startTS = System.nanoTime();
        ArrayList<Long> TJs = new ArrayList<>();
        //------------------------------------------------------//

        response.setContentType("application/json"); // Response mime type


        String name = (String)request.getParameter("name");
        String director = (String)request.getParameter("director");
        String stars = (String)request.getParameter("stars");
        String year =(String) request.getParameter("year");
        String genre = (String)request.getParameter("genre");
        String az = (String)request.getParameter("AZ");
        String numRecords = (String)request.getParameter("numRecords");
        String startIndex = (String)request.getParameter("startIndex");
        String totalResults = (String)request.getParameter("totalResults");
        String sortBy1 = (String)request.getParameter("sortBy1");
        String sortBy1_bk = sortBy1;
        String order1 = (String)request.getParameter("order1");
        String sortBy2 = (String)request.getParameter("sortBy2");
        String sortBy2_bk = sortBy2;
        String order2 = (String)request.getParameter("order2");
        String fullsearch = (String)request.getParameter("fullSearch");
        System.out.println(stars);

        //sort information to session
        HttpSession session = request.getSession();
        String current_url = "movie.html?name=" + name + "&director=" + director + "&stars=" +
            stars + "&year=" + year + "&genre=" + genre + "&AZ=" +
            az + "&numRecords=" + numRecords + "&startIndex=" +
            startIndex + "&totalResults=" + totalResults + "&sortBy1=" + sortBy1 + "&order1=" + order1 +
            "&sortBy2=" + sortBy2 + "&order2=" + order2 + "&fullSearch=" + fullsearch;
        session.setAttribute("prev_url", current_url);

        if(sortBy1.equals("rating")){ sortBy1 = "ISNULL(rating), "+sortBy1;}
        if(sortBy2.equals("rating")){ sortBy2 = "ISNULL(rating), "+sortBy2;}

        if(!(sortBy1_bk.equals("title") || sortBy1_bk.equals("rating") || sortBy1_bk == null || sortBy1_bk.equals("null"))){
            PrintWriter out = response.getWriter();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", "SQL Attack Alert");
            out.write(jsonObject.toString());
            response.setStatus(500);
            return;}
        if(!(sortBy2_bk.equals("title") || sortBy2_bk.equals("rating") || sortBy2_bk == null || sortBy2_bk.equals("null"))){
            PrintWriter out = response.getWriter();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", "SQL Attack Alert");
            out.write(jsonObject.toString());
            response.setStatus(500);
            return;}
        if(!(order1.equals("asc") || order1.equals("desc") || order1 == null || order1.equals("null"))){
            PrintWriter out = response.getWriter();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", "SQL Attack Alert");
            out.write(jsonObject.toString());
            response.setStatus(500);
            return;}
        if(!(order2.equals("asc") || order2.equals("desc") || order2 == null || order2.equals("null"))){
            PrintWriter out = response.getWriter();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", "SQL Attack Alert");
            out.write(jsonObject.toString());
            response.setStatus(500);
            return;}

        // System.out.println(current_url);
        
        String query = "";
        //start building query
        System.out.println(fullsearch);
        if(!(fullsearch.equals("null") || fullsearch == null || fullsearch.equals("")))
        {
            PrintWriter out = response.getWriter();
            try (Connection conn = dataSource.getConnection()) {
                // Get a connection from dataSource

                String[] parse = fullsearch.split(" ");
                query = "SELECT m.id, m.title as title, m.year, m.director, r.rating as rating\n" +
                        "                            from movies as m LEFT join ratings as r on r.movieId = m.id \n" +
                        "                            where MATCH (m.title) AGAINST (? IN boolean mode) ";

                if (!(sortBy1 == null || sortBy1.equals("null"))) {
                    query += " order by " + sortBy1;
                    if(!(order1 == null || order1.equals("null"))){
                        query += " " + order1;
                    }
                }

                if (!(sortBy2 == null || sortBy2.equals("null"))) {
                    query += ", " + sortBy2;
                    if(!(order2 == null || order2.equals("null"))){
                        query += " " + order2;
                    }
                }

                String tmp = "";

                for (int i = 0; i < parse.length; i++)
                {
                    tmp += "+" + parse[i] + "* ";
                }

                //get total number of results
                if(totalResults == null || totalResults.equals("null") || totalResults.equals("")){
                    String count_query = "select count(*) as c from (" + query + ") as fc";
                    System.out.println(count_query);
                    PreparedStatement count_statement = conn.prepareStatement(count_query);
                    count_statement.setString(1, tmp);
                    int ps_idex = 1;
                    if (!(sortBy1 == null || sortBy1.equals("null"))) {
                        count_statement.setString(++ps_idex,sortBy1);
                        System.out.print("pi: ");
                        System.out.println(ps_idex);
                        if(!(order1 == null || order1.equals("null"))){
                            count_statement.setString(++ps_idex,order1);
                            System.out.print("pi: ");
                            System.out.println(ps_idex);
                        }
                    }

                    if (!(sortBy2 == null || sortBy2.equals("null"))) {
                        count_statement.setString(++ps_idex,sortBy2);
                        if(!(order2 == null || order2.equals("null"))){
                            count_statement.setString(++ps_idex,order2);
                        }
                    }

                    //-----------------------------------------------------------//
                    startTj = System.nanoTime();
                    //-----------------------------------------------------------//

                    ResultSet rs1 = count_statement.executeQuery();

                    //-----------------------------------------------------------//
                    endTj = System.nanoTime();
                    TJ = endTj - startTj;
                    TJs.add(TJ);
                    //-----------------------------------------------------------//


                    while (rs1.next()) {
                        totalResults = rs1.getString("c");
                    }
                    rs1.close();
                }


                // String queryResultLimit = " limit " + numRecords + " offset " + startIndex + " ";
                query += " limit ? " + " offset ? " + " ";

                System.out.println(query);
                // Declare our statement
                PreparedStatement statement = conn.prepareStatement(query);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, tmp);
                int ps_idex = 1;
//                if (!(sortBy1 == null || sortBy1.equals("null"))) {
//                    statement.setString(++ps_idex,sortBy1);
//                    if(!(order1 == null || order1.equals("null"))){
//                        statement.setString(++ps_idex,order1);
//                    }
//                }
//
//                if (!(sortBy2 == null || sortBy2.equals("null"))) {
//                    statement.setString(++ps_idex,sortBy2);
//                    if(!(order2 == null || order2.equals("null"))){
//                        statement.setString(++ps_idex,order2);
//                    }
//                }

                System.out.print("pi: ");
                System.out.println(ps_idex);
                statement.setInt(++ps_idex,Integer.parseInt(numRecords));
                System.out.print("pi: ");
                System.out.println(ps_idex);
                statement.setInt(++ps_idex,Integer.parseInt(startIndex));


                // Perform the query

                //-----------------------------------------------------------//
                startTj = System.nanoTime();
                //-----------------------------------------------------------//

                ResultSet rs = statement.executeQuery();

                //-----------------------------------------------------------//
                endTj = System.nanoTime();
                TJ = endTj - startTj;
                TJs.add(TJ);
                //-----------------------------------------------------------//


                JsonArray jsonArray = new JsonArray();

                // Iterate through each row of rs
                while (rs.next()) {

                    String movie_id = rs.getString("id");
                    String movie_title = rs.getString("title");
                    String movie_year= rs.getString("year");
                    String movie_director = rs.getString("director");
                    String movie_rating = rs.getString("rating");

                    String sub_query_gnames = "select substring_index(group_concat(DISTINCT g.name order by g.name asc separator ', '), ', ' , 3) as gnames \n" +
                            "from genres as g, genres_in_movies as gim \n" +
                            "where gim.genreId = g.id and gim.movieId = ? ";
                    PreparedStatement gnames_statement = conn.prepareStatement(sub_query_gnames);
                    gnames_statement.setString(1, movie_id);

                    //-----------------------------------------------------------//
                    startTj = System.nanoTime();
                    //-----------------------------------------------------------//

                    ResultSet gnames_rs = gnames_statement.executeQuery();

                    //-----------------------------------------------------------//
                    endTj = System.nanoTime();
                    TJ = endTj - startTj;
                    TJs.add(TJ);
                    //-----------------------------------------------------------//


                    String movie_gnames = "null";
                    if(gnames_rs.next()){
                        movie_gnames= gnames_rs.getString("gnames");
                    }
                    gnames_rs.close();

                    String sub_query_snames = "select substring_index(group_concat(DISTINCT CONCAT_WS('-', s.id, s.name) order by s.name asc separator ', '), ', ' , 3) as snames \n" +
                            "from stars as s, stars_in_movies as sim \n" +
                            "where sim.starId = s.id and sim.movieId = ? ";
                    PreparedStatement snames_statement = conn.prepareStatement(sub_query_snames);
                    snames_statement.setString(1, movie_id);

                    //-----------------------------------------------------------//
                    startTj = System.nanoTime();
                    //-----------------------------------------------------------//

                    ResultSet snames_rs = snames_statement.executeQuery();

                    //-----------------------------------------------------------//
                    endTj = System.nanoTime();
                    TJ = endTj - startTj;
                    TJs.add(TJ);
                    //-----------------------------------------------------------//

                    String movie_snames = "null";
                    if(snames_rs.next()){
                        movie_snames= snames_rs.getString("snames");
                    }
                    snames_rs.close();

                    // Create a JsonObject based on the data we retrieve from rs
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_id", movie_id);
                    jsonObject.addProperty("movie_title", movie_title);
                    jsonObject.addProperty("movie_year", movie_year);
                    jsonObject.addProperty("movie_director", movie_director);
                    jsonObject.addProperty("movie_gnames", movie_gnames);
                    jsonObject.addProperty("movie_snames", movie_snames);
                    jsonObject.addProperty("movie_rating", movie_rating);

                    jsonArray.add(jsonObject);
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("numRecords", numRecords);
                jsonObject.addProperty("startIndex", startIndex);
                jsonObject.addProperty("totalResults", totalResults);
                jsonObject.addProperty("sortBy1", sortBy1_bk);
                jsonObject.addProperty("order1", order1);
                jsonObject.addProperty("sortBy2", sortBy2_bk);
                jsonObject.addProperty("order2", order2);
                jsonArray.add(jsonObject);
                rs.close();
                statement.close();

                // Log to localhost log
                request.getServletContext().log("getting " + jsonArray.size() + " results");

                // Write JSON string to output
                out.write(jsonArray.toString());
                // Set response status to 200 (OK)
                response.setStatus(200);

                //-----------------------------------------------------------//
                long endTS = System.nanoTime();
                long ts = (endTS - startTS);
                long tjTotal = 0;
                for(long d : TJs)
                    tjTotal += d;
                String result_str = String.valueOf(ts) + "," + String.valueOf(tjTotal)+"\n";
                Path path = Paths.get(request.getServletContext().getRealPath("/"),"logTSTJ.txt");
                try (
                        OutputStream logOut = new BufferedOutputStream(
                                Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND))){
                    logOut.write(result_str.getBytes());
                } catch (IOException e) {
                    request.getServletContext().log(e.getMessage());
                }
                //-----------------------------------------------------------//

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
        else
        {
            if(!(genre.equals("null") || genre == null || genre.equals(""))) {
                query += "SELECT m.id, m.title, m.year, m.director, r.rating \n" +
                        "from movies as m LEFT join ratings as r on r.movieId = m.id, genres as g, genres_in_movies as gim \n" +
                        "where g.name like ? and gim.genreId = g.id and m.id = gim.movieId \n" +
                        "group by m.id";
            }
            else if(!(az.equals("null") || az == null || az.equals(""))){
                if (az.equals("*"))
                {
                    query += "SELECT m.id, m.title, m.year, m.director, r.rating \n" +
                            "from movies as m LEFT join ratings as r on r.movieId = m.id \n" +
                            "where m.title not REGEXP \'^[0-9A-Za-z]\' " +
                            "group by m.id";
                }
                else{
                    query += "SELECT m.id, m.title, m.year, m.director, r.rating \n" +
                            "from movies as m LEFT join ratings as r on r.movieId = m.id \n" +
                            "where m.title like ? \n" +
                            "group by m.id";
                }
            }
            else{
                if(!(stars.equals("null") || stars == null || stars.equals(""))){
                    query += "SELECT m.id, m.title, m.year, m.director, r.rating \n" +
                            "from movies as m LEFT join ratings as r on r.movieId = m.id, (SELECT movieId FROM stars as s, stars_in_movies as sim \n" +
                            "where sim.starId = s.id and s.name LIKE ? group by movieId) as mid \n" +
                            "where mid.movieId = m.id \n" +
                            "and m.title LIKE ? \n" +
                            "and m.year like ? \n" +
                            "and m.director LIKE ? \n" +
                            "group by m.id";
                }
                else{
                    query += "SELECT m.id, m.title, m.year, m.director, r.rating \n" +
                            "from movies as m LEFT join ratings as r on r.movieId = m.id \n" +
                            "where m.title LIKE ? \n" +
                            "and m.year like ? \n" +
                            "and m.director LIKE ? \n" +
                            "group by m.id";
                }
            }

            if (year.equals("")){year = "%";}

            if (!(sortBy1 == null || sortBy1.equals("null"))) {
                query += " order by " + sortBy1;
                if(!(order1 == null || order1.equals("null"))){
                    query += " " + order1;
                }
            }

            if (!(sortBy2 == null || sortBy2.equals("null"))) {
                query += ", " + sortBy2;
                if(!(order2 == null || order2.equals("null"))){
                    query += " " + order2;
                }
            }

            // String queryResultLimit = " limit " + numRecords + " offset " + startIndex + " ";
            String queryResultLimit = " limit ? " + " offset ? " + " ";

            // Output stream to STDOUT
            PrintWriter out = response.getWriter();
            // Get a connection from dataSource and let resource manager close the connection after usage.
            try (Connection conn = dataSource.getConnection()) {

                //try to get total results at first time
                if(totalResults == null || totalResults.equals("null") || totalResults.equals("")){
                    String count_query = "select count(*) as c from (" + query + ") as fc";
                    System.out.println(count_query);
                    PreparedStatement count_statement = conn.prepareStatement(count_query);
                    int ps_idex = 0;
                    if (!(genre.equals("null") || genre == null || genre.equals(""))){
                        count_statement.setString(1, genre);
                        ps_idex = 1;
                    }
                    else if(!(az.equals("null") || az == null || az.equals(""))){
                        if (!az.equals("*"))
                        {
                            count_statement.setString(1, az+"%");
                            ps_idex = 1;
                        }
                    }
                    else
                    {
                        if(!(stars.equals("null") || stars == null || stars.equals(""))){
                            count_statement.setString(1, "%"+stars+"%");
                            count_statement.setString(2, "%"+name+"%");
                            count_statement.setString(3, year);
                            count_statement.setString(4, "%"+director+"%");
                            ps_idex = 4;
                        }
                        else{
                            count_statement.setString(1, "%"+name+"%");
                            count_statement.setString(2, year);
                            count_statement.setString(3, "%"+director+"%");
                            ps_idex = 3;
                        }
                    }

//                    if (!(sortBy1 == null || sortBy1.equals("null"))) {
//                        count_statement.setString(++ps_idex,sortBy1);
//                        System.out.print("pi: ");
//                        System.out.println(ps_idex);
//                        if(!(order1 == null || order1.equals("null"))){
//                            count_statement.setString(++ps_idex,order1);
//                            System.out.print("pi: ");
//                            System.out.println(ps_idex);
//                        }
//                    }
//
//                    if (!(sortBy2 == null || sortBy2.equals("null"))) {
//                        count_statement.setString(++ps_idex,sortBy2);
//                        if(!(order2 == null || order2.equals("null"))){
//                            count_statement.setString(++ps_idex,order2);
//                        }
//                    }

                    //-----------------------------------------------------------//
                    startTj = System.nanoTime();
                    //-----------------------------------------------------------//

                    ResultSet rs1 = count_statement.executeQuery();

                    //-----------------------------------------------------------//
                    endTj = System.nanoTime();
                    TJ = endTj - startTj;
                    TJs.add(TJ);
                    //-----------------------------------------------------------//

                    while (rs1.next()) {
                        totalResults = rs1.getString("c");
                    }
                    rs1.close();
                }

                System.out.println(totalResults);

                query += queryResultLimit;

                System.out.println(query);

                PreparedStatement statement = conn.prepareStatement(query);

                int ps_idex = 0;
                if (!(genre.equals("null") || genre == null || genre.equals(""))){
                    statement.setString(1, genre);
                    ps_idex = 1;
                }
                else if(!(az.equals("null") || az == null || az.equals(""))){
                    if (!az.equals("*"))
                    {
                        statement.setString(1, az+"%");
                        ps_idex = 1;
                    }
                }
                else
                {
                    if(!(stars.equals("null") || stars == null || stars.equals(""))){
                        statement.setString(1, "%"+stars+"%");
                        statement.setString(2, "%"+name+"%");
                        statement.setString(3, year);
                        statement.setString(4, "%"+director+"%");
                        ps_idex = 4;
                    }
                    else{
                        statement.setString(1, "%"+name+"%");
                        statement.setString(2, year);
                        statement.setString(3, "%"+director+"%");
                        ps_idex = 3;
                    }
                }

//                if (!(sortBy1 == null || sortBy1.equals("null"))) {
//                    statement.setString(++ps_idex,sortBy1);
//                    if(!(order1 == null || order1.equals("null"))){
//                        statement.setString(++ps_idex,order1);
//                    }
//                }
//
//                if (!(sortBy2 == null || sortBy2.equals("null"))) {
//                    statement.setString(++ps_idex,sortBy2);
//                    if(!(order2 == null || order2.equals("null"))){
//                        statement.setString(++ps_idex,order2);
//                    }
//                }

                System.out.print("pi: ");
                System.out.println(ps_idex);
                statement.setInt(++ps_idex,Integer.parseInt(numRecords));
                System.out.print("pi: ");
                System.out.println(ps_idex);
                statement.setInt(++ps_idex,Integer.parseInt(startIndex));

                //-----------------------------------------------------------//
                startTj = System.nanoTime();
                //-----------------------------------------------------------//

                ResultSet rs = statement.executeQuery();

                //-----------------------------------------------------------//
                endTj = System.nanoTime();
                TJ = endTj - startTj;
                TJs.add(TJ);
                //-----------------------------------------------------------//

                JsonArray jsonArray = new JsonArray();

                // Iterate through each row of rs
                while (rs.next()) {
                    String movie_id = rs.getString("id");
                    String movie_title = rs.getString("title");
                    String movie_year= rs.getString("year");
                    String movie_director = rs.getString("director");
                    String movie_rating = rs.getString("rating");

                    String sub_query_gnames = "select substring_index(group_concat(DISTINCT g.name order by g.name asc separator ', '), ', ' , 3) as gnames \n" +
                            "from genres as g, genres_in_movies as gim \n" +
                            "where gim.genreId = g.id and gim.movieId = ? ";
                    PreparedStatement gnames_statement = conn.prepareStatement(sub_query_gnames);
                    gnames_statement.setString(1, movie_id);

                    //-----------------------------------------------------------//
                    startTj = System.nanoTime();
                    //-----------------------------------------------------------//

                    ResultSet gnames_rs = gnames_statement.executeQuery();

                    //-----------------------------------------------------------//
                    endTj = System.nanoTime();
                    TJ = endTj - startTj;
                    TJs.add(TJ);
                    //-----------------------------------------------------------//

                    String movie_gnames = "null";
                    if(gnames_rs.next()){
                        movie_gnames= gnames_rs.getString("gnames");
                    }
                    gnames_rs.close();

                    String sub_query_snames = "select substring_index(group_concat(DISTINCT CONCAT_WS('-', s.id, s.name) order by s.name asc separator ', '), ', ' , 3) as snames \n" +
                            "from stars as s, stars_in_movies as sim \n" +
                            "where sim.starId = s.id and sim.movieId = ? ";
                    PreparedStatement snames_statement = conn.prepareStatement(sub_query_snames);
                    snames_statement.setString(1, movie_id);

                    //-----------------------------------------------------------//
                    startTj = System.nanoTime();
                    //-----------------------------------------------------------//

                    ResultSet snames_rs = snames_statement.executeQuery();

                    //-----------------------------------------------------------//
                    endTj = System.nanoTime();
                    TJ = endTj - startTj;
                    TJs.add(TJ);
                    //-----------------------------------------------------------//

                    String movie_snames = "null";
                    if(snames_rs.next()){
                        movie_snames= snames_rs.getString("snames");
                    }
                    snames_rs.close();

                    // Create a JsonObject based on the data we retrieve from rs
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_id", movie_id);
                    jsonObject.addProperty("movie_title", movie_title);
                    jsonObject.addProperty("movie_year", movie_year);
                    jsonObject.addProperty("movie_director", movie_director);
                    jsonObject.addProperty("movie_gnames", movie_gnames);
                    jsonObject.addProperty("movie_snames", movie_snames);
                    jsonObject.addProperty("movie_rating", movie_rating);

                    jsonArray.add(jsonObject);
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("numRecords", numRecords);
                jsonObject.addProperty("startIndex", startIndex);
                jsonObject.addProperty("totalResults", totalResults);
                jsonObject.addProperty("sortBy1", sortBy1);
                jsonObject.addProperty("order1", order1);
                jsonObject.addProperty("sortBy2", sortBy2);
                jsonObject.addProperty("order2", order2);
                jsonArray.add(jsonObject);
                rs.close();
                statement.close();

                // Log to localhost log
                request.getServletContext().log("getting " + jsonArray.size() + " results");

                // Write JSON string to output
                out.write(jsonArray.toString());
                // Set response status to 200 (OK)
                response.setStatus(200);

                //-----------------------------------------------------------//
                long endTS = System.nanoTime();
                long ts = (endTS - startTS);
                long tjTotal = 0;
                for(long d : TJs)
                    tjTotal += d;
                String result_str = String.valueOf(ts) + "," + String.valueOf(tjTotal) + "\n";
                Path path = Paths.get(request.getServletContext().getRealPath("/"),"logTSTJ.txt");
                try (
                        OutputStream logOut = new BufferedOutputStream(
                                Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND))){
                    logOut.write(result_str.getBytes());
                } catch (IOException e) {
                    request.getServletContext().log(e.getMessage());
                }
                //-----------------------------------------------------------//

            } catch (Exception e) {

                // Write error message JSON object to output
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("errorMessage", e.getMessage());
                out.write(jsonObject.toString());

                // Set response status to 500 (Internal Server Error)
                response.setStatus(500);
            } finally {
                out.close();
            }


        }



        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {


    }
}
