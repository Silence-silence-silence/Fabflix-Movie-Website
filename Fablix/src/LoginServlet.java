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
import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("GET");
    }
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {


        String email = request.getParameter("username");
        String password = request.getParameter("password");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        String deviceTag = request.getParameter("deviceTag");
        if(deviceTag == null) {
            deviceTag = "";
        }
        // System.out.println("Login Post");
        // The log message can be found in localhost log
        request.getServletContext().log("GetUser: " + email);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();


        JsonObject responseJsonObject = new JsonObject();
        if(!deviceTag.equals("Android")) {
            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                // responseJsonObject.addProperty("message", "user with email " + email + " doesn't exist");
                responseJsonObject.addProperty("message", "Verification Failed");
                out.write(responseJsonObject.toString());
                return;
            }
        }

        try (Connection conn = dataSource.getConnection())
        {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"


                String query = "SELECT * FROM moviedb.customers where email = ?";

                // Declare our statement
                PreparedStatement statement = conn.prepareStatement(query);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, email);
                // statement.setString(2, password);


                // Perform the query
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    // Have this user:
                    String encryptedPassword = rs.getString("password");
                    boolean success = false;
                    success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                    if(success){
                        System.out.println("Correct");
                        HttpSession session = request.getSession(true);

                        String id = rs.getString("id");
                        String firstName = rs.getString("firstName");
                        String lastName = rs.getString("lastName");
                        String ccId = rs.getString("ccId");
                        String address = rs.getString("address");
                        // set this user into the session
                        session.setAttribute("ID", id);
                        session.setAttribute("user", new User(id, email, firstName, lastName, ccId, address));

                        // set the logged_in attribute
//                        Boolean admin = (Boolean) session.getAttribute("logged_in");
                        Boolean admin = false;
                        session.setAttribute("admin", admin);

                        responseJsonObject.addProperty("status", "success");
                        responseJsonObject.addProperty("message", "success");
                    }
                    else{
                        //incorrect password
                        responseJsonObject.addProperty("status", "fail");
                        // Log to localhost log
                        request.getServletContext().log("Login failed");
                        responseJsonObject.addProperty("message", "Incorrect Username or Password");
                    }
                } else {
                    // Login fail

                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Login failed");
                    // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                    // responseJsonObject.addProperty("message", "user with email " + email + " doesn't exist");
                    responseJsonObject.addProperty("message", "Incorrect Username or Password");
                }
                rs.close();
                statement.close();





            out.write(responseJsonObject.toString());

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
