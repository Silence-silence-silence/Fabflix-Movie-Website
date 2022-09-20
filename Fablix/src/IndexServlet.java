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

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "IndexServlet", urlPatterns = "/api/index")
public class IndexServlet extends HttpServlet {
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

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<>();
        }
        // Log to localhost log

        request.getServletContext().log("getting " + previousItems.size() + " items");
        JsonArray previousItemsJsonArray = new JsonArray();
        previousItems.forEach(previousItemsJsonArray::add);
        responseJsonObject.add("previousItems", previousItemsJsonArray);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        String condition = request.getParameter("condition");
        String item = "";
        HttpSession session = request.getSession();
        String title = "";



        try (Connection conn = dataSource.getConnection())
        {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"

            String query = "SELECT title FROM movies where id = ?;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);


            // statement.setString(2, password);


            // Perform the query
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                // Have this user:

                title = rs.getString("title");




            }


            rs.close();
            statement.close();

            response.setStatus(200);

        }

        catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());


            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }



        item = title + "-" + id;
        System.out.println(item);
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");


            if (previousItems == null) {
                previousItems = new ArrayList<>();
                System.out.println("before"+item);
                item = item + "-"+"1";
                previousItems.add(item);
                System.out.println("after"+item);
                session.setAttribute("previousItems", previousItems);
            } else {
                // prevent corrupted states through sharing under multi-threads
                // will only be executed by one thread at a time
                synchronized (previousItems) {
                    int check = 0;
                    for (int i  = 0; i < previousItems.size(); i++)
                    {
                        if ((previousItems.get(i)).contains(item))
                        {
                            System.out.println(previousItems.get(i));
                            String[] splited = (previousItems.get(i)).split("-");
                            int count = Integer.parseInt(splited[2]);
                            if (condition.equals("decrease"))
                            {
                                System.out.println("dsfds");
                            }
                            System.out.println(condition);
                            if (condition.equals("decrease"))
                            {
                                System.out.println(count + "cousadsant");
                                count--;
                                if (count ==0)
                                {
                                    previousItems.remove(i);
                                }
                                else
                                {
                                    item = item + "-"+count;
                                    previousItems.set(i,item);
                                }


                            }
                            else if (condition.equals("delete"))
                            {System.out.println(count + "cousadsant");
                                previousItems.remove(i);
                            }
                            else {
                                count ++;
                                System.out.println(previousItems.get(i)+"why?");
                                item = item + "-"+count;
                                System.out.println(item);
                                previousItems.set(i,item);
                            }


                            System.out.println(item+"sdasd");
                            check = 1;
                            break;
                        }
                    }

                    if (check ==0)
                    {
                        item = item + "-" + "1" ;
                        System.out.println(item);
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
