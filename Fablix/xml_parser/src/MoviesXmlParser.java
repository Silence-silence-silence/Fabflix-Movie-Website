import org.apache.commons.io.FileUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MoviesXmlParser extends DefaultHandler{
	String url = "jdbc:mysql://localhost:3306/moviedb?allowLoadLocalInfile=true";
	String username = "mytestuser";
	String pawssword = "My6$Password";
	String beginMovieId = "nmo0";
	String beginStarId = "nst0";
	Integer beginGenreId = 100;
	private String tempVal;
	private String currentDir;
    private Integer myMovieSize;
    private Integer myStarSize;


    HashMap<String, Movie> currentMovies;
    ArrayList<Movie> myMovies;
    ArrayList<Movie> brokenMovies;
    ArrayList<String> genresOfMovie;
    HashSet<String> genresTable;
    HashMap<String, Integer> genreToGid;
    private Movie tempMovie;
    HashMap<String, String> movieTitledirToId;
    HashSet<String> gidmid;

    HashMap<String, String> starNameToId;
	// HashSet<String> sidmid;
	ArrayList<Star> myStars;
	HashMap<String, String> starNameYearToId;
	ArrayList<Star> brokenStars;
    private Star tempStar;

	ArrayList<Star> brokenSubStarsInRelation;
	String tempMovieTitle;
	ArrayList<String> tempStars;
	String tempDirector;
	HashMap<String, ArrayList<String>> movieToActors;

    public MoviesXmlParser(){
		currentMovies = new HashMap<>();
		myMovies = new ArrayList<>();
		brokenMovies = new ArrayList<>();
		genresOfMovie = new ArrayList<>();
		genresTable = new HashSet<>();
		genreToGid = new HashMap<>();
		movieTitledirToId = new HashMap<>();
		gidmid = new HashSet<>();

		starNameToId = new HashMap<>();
		// sidmid = new HashSet<>();
		myStars = new ArrayList<>();
		starNameYearToId = new HashMap<>();
		brokenStars = new ArrayList<>();

		movieToActors = new HashMap<String, ArrayList<String>>();
		tempMovieTitle = "";
		tempStars = new ArrayList<>();
		brokenSubStarsInRelation = new ArrayList<>();
		tempDirector = "";
    }

// 	public void initSqlInfo(){
// 		try(Connection conn = DriverManager.getConnection(url,username,pawssword)){
// 			String query = "SELECT MAX(id) FROM movies;";
// 			PreparedStatement statement = conn.prepareStatement(query);
// 			ResultSet rs = statement.executeQuery();

// 			if (rs.next()) {
// 				beginMovieId = rs.getString(1);
// 				System.out.println(beginMovieId);
// 			}

// 			query = "SELECT MAX(id) FROM stars;";
// 			statement = conn.prepareStatement(query);
// 			rs = statement.executeQuery();

// 			if (rs.next()) {
// 				beginStarId = rs.getString(1);
// 				System.out.println(beginStarId);
// 			}

// 			query = "SELECT id, title as t, director as dir FROM movies;";
// 			System.out.println("reading movies from current database......");
// 			statement = conn.prepareStatement(query);
// 			rs = statement.executeQuery();
// 			while (rs.next()){
// 				String id = rs.getString("id");
// 				String t = rs.getString("t");
// 				String dir = rs.getString("dir");
// 				movieTitledirToId.put(t+"|"+dir,id);
// 			}
// 			System.out.println(movieTitledirToId.size());

// // 			query = "SELECT id, name as n, birthYear as b FROM stars;";
// // 			System.out.println("reading stars from current database......");
// // 			statement = conn.prepareStatement(query);
// // 			rs = statement.executeQuery();
// // 			while (rs.next()){
// // 				String id = rs.getString("id");
// // 				String n = rs.getString("n");
// // 				String b = rs.getString("b");
// // 				if( b == null || b.equals("null")){
// // 					b = "";
// // 				}
// // //				if (starNameYearToId.containsKey(n+"|"+b)){
// // //					System.out.println(n+"|"+b);
// // //				}
// // 				starNameYearToId.put(n+"|"+b,id);
// // 				starNameToId.put(n,id);
// // 			}
// // 			System.out.println(starNameYearToId.size());


// 			query = "SELECT MAX(id) FROM genres;";
// 			System.out.println("reading id in genres from current database......");
// 			statement = conn.prepareStatement(query);
// 			rs = statement.executeQuery();

// 			if (rs.next()) {
// 				beginGenreId = rs.getInt(1);
// 				System.out.println(beginGenreId);
// 			}

// 			query = "SELECT id, name as g FROM genres;";
// 			System.out.println("reading genres from current database......");
// 			statement = conn.prepareStatement(query);
// 			rs = statement.executeQuery();
// 			while (rs.next()){
// 				Integer id = rs.getInt("id");
// 				String g = rs.getString("g");
// 				genreToGid.put(g,id);
// 			}
// 			System.out.println(genreToGid.size());

// 			query = "SELECT * FROM genres_in_movies;";
// 			System.out.println("reading genres_in_movies from current database......");
// 			statement = conn.prepareStatement(query);
// 			rs = statement.executeQuery();
// 			while (rs.next()){
// 				String gid = rs.getString("genreId");
// 				String mid = rs.getString("movieId");
// 				gidmid.add(gid+"|"+mid);
// 			}
// 			System.out.println(gidmid.size());

// 			// query = "SELECT * FROM stars_in_movies;";
// 			// System.out.println("reading stars_in_movies from current database......");
// 			// statement = conn.prepareStatement(query);
// 			// rs = statement.executeQuery();
// 			// while (rs.next()){
// 			// 	String sid = rs.getString("starId");
// 			// 	String mid = rs.getString("movieId");
// 			// 	sidmid.add(sid+"|"+mid);
// 			// }
// 			// System.out.println(sidmid.size());

// 		} catch (Exception e) {
// 			System.out.println("Connection Invalid"+  e.getMessage());
// 		}
// 	}

    public void runParser(){
        runMain();
        runStar();
        runMTS();

        // printInconsistencyData();
        // parseMain();
		// System.out.println("Main done");
		// // parseStar();
		// // System.out.println("Star done");
		// // parseMovieToStar();
		// // System.out.println("MTS done");

		// try{
		// 	processDatabase();
		// }
		// catch (SQLException e){
		// 	System.out.println("Connection Invalid"+  e.getMessage());
		// }

		printInconsistencyData();
    }

    public void runMain(){
        try(Connection conn = DriverManager.getConnection(url,username,pawssword)){
            String query = "SELECT MAX(id) FROM movies;";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                beginMovieId = rs.getString(1);
                System.out.println(beginMovieId);
            }

            query = "SELECT MAX(id) FROM stars;";
            statement = conn.prepareStatement(query);
            rs = statement.executeQuery();

            if (rs.next()) {
                beginStarId = rs.getString(1);
                System.out.println(beginStarId);
            }

            query = "SELECT id, title as t, director as dir FROM movies;";
            System.out.println("reading movies from current database......");
            statement = conn.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()){
                String id = rs.getString("id");
                String t = rs.getString("t");
                String dir = rs.getString("dir");
                movieTitledirToId.put(t+"|"+dir,id);
            }
            System.out.println(movieTitledirToId.size());

            query = "SELECT MAX(id) FROM genres;";
            System.out.println("reading id in genres from current database......");
            statement = conn.prepareStatement(query);
            rs = statement.executeQuery();

            if (rs.next()) {
                beginGenreId = rs.getInt(1);
                System.out.println(beginGenreId);
            }

            query = "SELECT id, name as g FROM genres;";
            System.out.println("reading genres from current database......");
            statement = conn.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()){
                Integer id = rs.getInt("id");
                String g = rs.getString("g");
                genreToGid.put(g,id);
            }
            System.out.println(genreToGid.size());

            query = "SELECT * FROM genres_in_movies;";
            System.out.println("reading genres_in_movies from current database......");
            statement = conn.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()){
                String gid = rs.getString("genreId");
                String mid = rs.getString("movieId");
                gidmid.add(gid+"|"+mid);
            }
            System.out.println(gidmid.size());

        } catch (Exception e) {
            System.out.println("Connection Invalid"+  e.getMessage());
        }

        parseMain();

        try{
            String dir = System.getProperty("user.dir");
            Path path = Paths.get(dir+"/csv");
            Files.createDirectory(path);
        }catch (IOException e){
            System.out.println("Dir error: "+ e.getMessage());
        }

		try {
			FileWriter csvWriter = new FileWriter("csv/genres.csv");

			for (String g : genresTable) {
				if (!genreToGid.containsKey(g)) {
					beginGenreId++;
					genreToGid.put(g, beginGenreId);
					csvWriter.append(String.valueOf(beginGenreId));
					csvWriter.append("|");
					csvWriter.append(g);
					csvWriter.append("\n");
					csvWriter.flush();
				}
			}

			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			System.out.println("File error: " + e.getMessage());
		}

		try {
			FileWriter csvWriter = new FileWriter("csv/movies.csv");
			FileWriter csvWriter2 = new FileWriter("csv/genres_in_movies.csv");

			for (Movie m : myMovies) {
				csvWriter.append(m.toCSV());
				csvWriter.append("\n");
				csvWriter.flush();

				for (String g : m.getGenres()) {
					if (!gidmid.contains(String.valueOf(genreToGid.get(g)) + "|" + m.getId())) {
						csvWriter2.append(String.valueOf(genreToGid.get(g)));
						csvWriter2.append("|");
						csvWriter2.append(m.getId());
						csvWriter2.append("\n");
						csvWriter2.flush();
					}
				}
			}

			csvWriter.flush();
			csvWriter.close();
			csvWriter2.flush();
			csvWriter2.close();
		} catch (IOException e) {
			System.out.println("File error: " + e.getMessage());
		}

        try {
            String dir = System.getProperty("user.dir");
            dir += "/csv";
            System.out.println(dir);
            Connection conn = DriverManager.getConnection(url, username, pawssword);
            Statement statement = conn.createStatement();
            String csvDir = dir + "/movies.csv";
//		csvDir = csvDir.replace("\\", "\\\\");
            System.out.println(csvDir);
            String sql = "LOAD DATA LOCAL INFILE '" + csvDir + "'\n" +
                    "REPLACE\n" +
                    "INTO TABLE movies\n" +
                    "FIELDS TERMINATED BY '|' \n" +
                    "ENCLOSED BY '\"' \n" +
                    "LINES TERMINATED BY '\\n'";

            System.out.println(sql);
            statement.execute("SET FOREIGN_KEY_CHECKS=0");
//		ResultSet rs = statement.executeQuery("SELECT count(*) from	movies;");
//		rs.next();
//		System.out.println(rs.getString(1));
            statement.execute(sql);
//		rs = statement.executeQuery("SELECT count(*) from	movies;");
//		rs.next();
//		System.out.println(rs.getString(1));
            statement.execute("SET FOREIGN_KEY_CHECKS=1");
            System.out.println("ok");


            csvDir = dir + "/genres.csv";
//		csvDir = csvDir.replace("\\", "\\\\");
            System.out.println(csvDir);
            sql = "LOAD DATA LOCAL INFILE '" + csvDir + "'\n" +
                    "REPLACE\n" +
                    "INTO TABLE genres\n" +
                    "FIELDS TERMINATED BY '|' \n" +
                    "ENCLOSED BY '\"' \n" +
                    "LINES TERMINATED BY '\\n'";

            System.out.println(sql);
            statement.execute(sql);
            System.out.println("ok");

            csvDir = dir + "/genres_in_movies.csv";
//		csvDir = csvDir.replace("\\", "\\\\");
            System.out.println(csvDir);
            sql = "LOAD DATA LOCAL INFILE '" + csvDir + "'\n" +
                    "REPLACE\n" +
                    "INTO TABLE genres_in_movies\n" +
                    "FIELDS TERMINATED BY '|' \n" +
                    "ENCLOSED BY '\"' \n" +
                    "LINES TERMINATED BY '\\n'";

            System.out.println(sql);
            statement.execute(sql);
            System.out.println("ok");
        }
        catch (SQLException e){
            System.out.println("SQL ERROR: "+e.getMessage());
        }

        currentMovies = new HashMap<>();
        myMovieSize = myMovies.size();
        myMovies = new ArrayList<>();
        genresOfMovie = new ArrayList<>();
        genresTable = new HashSet<>();
        genreToGid = new HashMap<>();
        gidmid = new HashSet<>();
    }

    public void runStar(){
        try(Connection conn = DriverManager.getConnection(url,username,pawssword)){
            String query = "SELECT id, name as n, birthYear as b FROM stars;";
			System.out.println("reading stars from current database......");
            PreparedStatement statement = conn.prepareStatement(query);
			ResultSet rs = statement.executeQuery();
			while (rs.next()){
				Thread.sleep(1);
				String id = rs.getString("id");
				String n = rs.getString("n");
				String b = rs.getString("b");
				if( b == null || b.equals("null")){
					b = "";
				}
//				if (starNameYearToId.containsKey(n+"|"+b)){
//					System.out.println(n+"|"+b);
//				}
				starNameYearToId.put(n+"|"+b,id);
				starNameToId.put(n,id);
			}
			System.out.println(starNameYearToId.size());
        } catch (Exception e) {
            System.out.println("Connection Invalid"+  e.getMessage());
        }

        parseStar();

		starNameYearToId = new HashMap<>();
    }

    public void runMTS(){
        // try(Connection conn = DriverManager.getConnection(url,username,pawssword)){
        //     String query = "SELECT * FROM stars_in_movies;";
        //     System.out.println("reading stars_in_movies from current database......");
        //     PreparedStatement statement = conn.prepareStatement(query);
        //     ResultSet rs = statement.executeQuery();
        //     while (rs.next()){
        //         String sid = rs.getString("starId");
        //         String mid = rs.getString("movieId");
        //         sidmid.add(sid+"|"+mid);
        //     }
        //     // System.out.println(sidmid.size());
        // } catch (Exception e) {
        //     System.out.println("Connection Invalid"+  e.getMessage());
        // }

        parseMovieToStar();

        try {
			FileWriter csvWriter = new FileWriter("csv/stars_in_movies.csv");
			Integer addionalStarNum = 0;
			for (var entry : movieToActors.entrySet()) {
				if (movieTitledirToId.containsKey(entry.getKey())) {
					String id = movieTitledirToId.get(entry.getKey());
					for (String s : entry.getValue()) {
						if (starNameToId.containsKey(s)) {
							// if (!sidmid.contains(starNameToId.get(s) + "|" + id)) {
								csvWriter.append(starNameToId.get(s));
								csvWriter.append("|");
								csvWriter.append(id);
								csvWriter.append("\n");
								csvWriter.flush();
							// }
						} else {
							String[] part = beginStarId.split("(?<=\\D)(?=\\d)");
							int idNum = Integer.parseInt(part[1]) + 1;
							String newId = part[0] + idNum;
							beginStarId = newId;
							starNameToId.put(s, newId);
							Star newStar = new Star();
							newStar.setId(newId);
							newStar.setName(s);
							myStars.add(newStar);
							addionalStarNum++;

							csvWriter.append(starNameToId.get(s));
							csvWriter.append("|");
							csvWriter.append(id);
							csvWriter.append("\n");
							csvWriter.flush();
						}
					}
				}
			}
			System.out.println("Addtional Star Num: " + addionalStarNum);
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			System.out.println("File error: " + e.getMessage());
		}


        try {
            String dir = System.getProperty("user.dir");
            dir += "/csv";
            System.out.println(dir);
            Connection conn = DriverManager.getConnection(url, username, pawssword);
            Statement statement = conn.createStatement();
            String csvDir = dir + "/stars_in_movies.csv";
    //		csvDir = csvDir.replace("\\", "\\\\");
            System.out.println(csvDir);
            String sql = "LOAD DATA LOCAL INFILE '" + csvDir + "'\n" +
                    "REPLACE\n" +
                    "INTO TABLE stars_in_movies\n" +
                    "FIELDS TERMINATED BY '|' \n" +
                    "ENCLOSED BY '\"' \n" +
                    "LINES TERMINATED BY '\\n'";

            System.out.println(sql);
            statement.execute("SET FOREIGN_KEY_CHECKS=0");
            statement.execute(sql);
            statement.execute("SET FOREIGN_KEY_CHECKS=1");
            System.out.println("ok");
        }
        catch (SQLException e){
            System.out.println("SQL ERROR: "+e.getMessage());
        }

		// try{
		// 	String fdir = System.getProperty("user.dir");
		// 	FileUtils.deleteDirectory(new File(fdir+"/csv"));

		// }catch (IOException e){
		// 	System.out.println("Delete dir error: "+e.getMessage());
		// }

		try {
			FileWriter csvWriter = new FileWriter("csv/stars.csv");

			for (Star s : myStars) {
				csvWriter.append(s.toCSV());
				csvWriter.append("\n");
				csvWriter.flush();
			}

			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			System.out.println("File error: " + e.getMessage());
		}

		try {
			String dir = System.getProperty("user.dir");
			dir += "/csv";
			System.out.println(dir);
			Connection conn = DriverManager.getConnection(url, username, pawssword);
			Statement statement = conn.createStatement();
			String csvDir = dir + "/stars.csv";
			//		csvDir = csvDir.replace("\\", "\\\\");
			System.out.println(csvDir);
			String sql = "LOAD DATA LOCAL INFILE '" + csvDir + "'\n" +
					"REPLACE\n" +
					"INTO TABLE stars\n" +
					"FIELDS TERMINATED BY '|' \n" +
					"ENCLOSED BY '\"' \n" +
					"LINES TERMINATED BY '\\n' \n" +
					"(id, name, @vbirthYear)\n" +
					"SET birthYear = NULLIF(@vbirthYear,'')";

			System.out.println(sql);
			statement.execute(sql);
			System.out.println("ok");
		}
		catch (SQLException e){
			System.out.println("SQL ERROR: "+e.getMessage());
		}

        myStarSize = myStars.size();
        myStars = new ArrayList<>();
        starNameToId = new HashMap<>();
        // sidmid = new HashSet<>();

        movieToActors = new HashMap<String, ArrayList<String>>();
        tempStars = new ArrayList<>();
    }

// 	public void processDatabase() throws SQLException {
// 		System.out.println("processing data");

// 		try{
// 			String dir = System.getProperty("user.dir");
// 			System.out.println(dir+"/csv");
// 			Path path = Paths.get(dir+"/csv");
// 			Files.createDirectory(path);
// 		}catch (IOException e){
// 			System.out.println("Dir error: "+ e.getMessage());
// 		}

// 		try {
// 			FileWriter csvWriter = new FileWriter("csv/genres.csv");

// 			for (String g : genresTable) {
// 				if (!genreToGid.containsKey(g)) {
// 					beginGenreId++;
// 					genreToGid.put(g, beginGenreId);
// 					csvWriter.append(String.valueOf(beginGenreId));
// 					csvWriter.append("|");
// 					csvWriter.append(g);
// 					csvWriter.append("\n");
// 					csvWriter.flush();
// 				}
// 			}

// 			csvWriter.flush();
// 			csvWriter.close();
// 		} catch (IOException e) {
// 			System.out.println("File error: " + e.getMessage());
// 		}

// 		try {
// 			FileWriter csvWriter = new FileWriter("csv/movies.csv");
// 			FileWriter csvWriter2 = new FileWriter("csv/genres_in_movies.csv");

// 			for (Movie m : myMovies) {
// 				csvWriter.append(m.toCSV());
// 				csvWriter.append("\n");
// 				csvWriter.flush();

// 				for (String g : m.getGenres()) {
// 					if (!gidmid.contains(String.valueOf(genreToGid.get(g)) + "|" + m.getId())) {
// 						csvWriter2.append(String.valueOf(genreToGid.get(g)));
// 						csvWriter2.append("|");
// 						csvWriter2.append(m.getId());
// 						csvWriter2.append("\n");
// 						csvWriter2.flush();
// 					}
// 				}
// 			}

// 			csvWriter.flush();
// 			csvWriter.close();
// 			csvWriter2.flush();
// 			csvWriter2.close();
// 		} catch (IOException e) {
// 			System.out.println("File error: " + e.getMessage());
// 		}

// // 		try {
// // 			FileWriter csvWriter = new FileWriter("csv/stars_in_movies.csv");
// // 			Integer addionalStarNum = 0;
// // 			for (var entry : movieToActors.entrySet()) {
// // 				if (movieTitledirToId.containsKey(entry.getKey())) {
// // 					String id = movieTitledirToId.get(entry.getKey());
// // 					for (String s : entry.getValue()) {
// // 						if (starNameToId.containsKey(s)) {
// // 							if (!sidmid.contains(starNameToId.get(s) + "|" + id)) {
// // 								csvWriter.append(starNameToId.get(s));
// // 								csvWriter.append("|");
// // 								csvWriter.append(id);
// // 								csvWriter.append("\n");
// // 							}
// // 						} else {
// // 							String[] part = beginStarId.split("(?<=\\D)(?=\\d)");
// // 							int idNum = Integer.parseInt(part[1]) + 1;
// // 							String newId = part[0] + idNum;
// // 							beginStarId = newId;
// // 							starNameToId.put(s, newId);
// // 							Star newStar = new Star();
// // 							newStar.setId(newId);
// // 							newStar.setName(s);
// // 							myStars.add(newStar);
// // 							addionalStarNum++;

// // 							csvWriter.append(starNameToId.get(s));
// // 							csvWriter.append("|");
// // 							csvWriter.append(id);
// // 							csvWriter.append("\n");
// // 						}
// // 					}
// // 				}
// // 			}
// // 			System.out.println("Addtional Star Num: " + addionalStarNum);
// // 			csvWriter.flush();
// // 			csvWriter.close();
// // 		} catch (IOException e) {
// // 			System.out.println("File error: " + e.getMessage());
// // 		}

// // 		try {
// // 			FileWriter csvWriter = new FileWriter("csv/stars.csv");

// // 			for (Star s : myStars) {
// // 				csvWriter.append(s.toCSV());
// // 				csvWriter.append("\n");
// // 			}

// // 			csvWriter.flush();
// // 			csvWriter.close();
// // 		} catch (IOException e) {
// // 			System.out.println("File error: " + e.getMessage());
// // 		}


// 		String dir = System.getProperty("user.dir");
// 		dir += "/csv";
// 		System.out.println(dir);
// 		Connection conn = DriverManager.getConnection(url, username, pawssword);
// 		Statement statement = conn.createStatement();
// 		String csvDir = dir + "/movies.csv";
// //		csvDir = csvDir.replace("\\", "\\\\");
// 		System.out.println(csvDir);
// 		String sql = "LOAD DATA LOCAL INFILE '" + csvDir + "'\n" +
// 				"REPLACE\n" +
// 				"INTO TABLE movies\n" +
// 				"FIELDS TERMINATED BY '|' \n" +
// 				"ENCLOSED BY '\"' \n" +
// 				"LINES TERMINATED BY '\\n'";

// 		System.out.println(sql);
// 		statement.execute("SET FOREIGN_KEY_CHECKS=0");
// //		ResultSet rs = statement.executeQuery("SELECT count(*) from	movies;");
// //		rs.next();
// //		System.out.println(rs.getString(1));
// 		statement.execute(sql);
// //		rs = statement.executeQuery("SELECT count(*) from	movies;");
// //		rs.next();
// //		System.out.println(rs.getString(1));
// 		statement.execute("SET FOREIGN_KEY_CHECKS=1");
// 		System.out.println("ok");


// 		csvDir = dir + "/genres.csv";
// //		csvDir = csvDir.replace("\\", "\\\\");
// 		System.out.println(csvDir);
// 		sql = "LOAD DATA LOCAL INFILE '" + csvDir + "'\n" +
// 				"REPLACE\n" +
// 				"INTO TABLE genres\n" +
// 				"FIELDS TERMINATED BY '|' \n" +
// 				"ENCLOSED BY '\"' \n" +
// 				"LINES TERMINATED BY '\\n'";

// 		System.out.println(sql);
// 		statement.execute(sql);
// 		System.out.println("ok");

// 		csvDir = dir + "/genres_in_movies.csv";
// //		csvDir = csvDir.replace("\\", "\\\\");
// 		System.out.println(csvDir);
// 		sql = "LOAD DATA LOCAL INFILE '" + csvDir + "'\n" +
// 				"REPLACE\n" +
// 				"INTO TABLE genres_in_movies\n" +
// 				"FIELDS TERMINATED BY '|' \n" +
// 				"ENCLOSED BY '\"' \n" +
// 				"LINES TERMINATED BY '\\n'";

// 		System.out.println(sql);
// 		statement.execute(sql);
// 		System.out.println("ok");

// // 		csvDir = dir + "/stars.csv";
// // //		csvDir = csvDir.replace("\\", "\\\\");
// // 		System.out.println(csvDir);
// // 		sql = "LOAD DATA INFILE '" + csvDir + "'\n" +
// // 				"REPLACE\n" +
// // 				"INTO TABLE stars\n" +
// // 				"FIELDS TERMINATED BY '|' \n" +
// // 				"ENCLOSED BY '\"' \n" +
// // 				"LINES TERMINATED BY '\\n' \n" +
// // 				"(id, name, @vbirthYear)\n" +
// // 				"SET birthYear = NULLIF(@vbirthYear,'')";

// // 		System.out.println(sql);
// // 		statement.execute(sql);
// // 		System.out.println("ok");

// // 		csvDir = dir + "/stars_in_movies.csv";
// // //		csvDir = csvDir.replace("\\", "\\\\");
// // 		System.out.println(csvDir);
// // 		sql = "LOAD DATA INFILE '" + csvDir + "'\n" +
// // 				"REPLACE\n" +
// // 				"INTO TABLE stars_in_movies\n" +
// // 				"FIELDS TERMINATED BY '|' \n" +
// // 				"ENCLOSED BY '\"' \n" +
// // 				"LINES TERMINATED BY '\\n'";

// // 		System.out.println(sql);
// // 		statement.execute("SET FOREIGN_KEY_CHECKS=0");
// // 		statement.execute(sql);
// // 		statement.execute("SET FOREIGN_KEY_CHECKS=1");
// // 		System.out.println("ok");

// // 		try{
// // 			String fdir = System.getProperty("user.dir");
// // 			FileUtils.deleteDirectory(new File(fdir+"/csv"));

// // 		}catch (IOException e){
// // 			System.out.println("Delete dir error: "+e.getMessage());
// // 		}
// 	}

    private void parseMain() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

	private void parseStar() {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse("actors63.xml", this);

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	private void parseMovieToStar() {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse("casts124.xml", this);

		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	private void printInconsistencyData() {
//		System.out.println("\n2600 Badly Formatted value Found\n");
		try{
			FileWriter reprotWriter = new FileWriter("inconsistency_report.txt");
			System.out.println("No of Movies " + myMovieSize);
			System.out.println("No of Stars " + myStarSize);


			String out = "Badly Formatted Stars (actor): " + brokenStars.size() + " Total\n";
			reprotWriter.append(out);
			for(Star s: brokenStars){
				reprotWriter.append(s.toString());
				reprotWriter.append("\n");
				reprotWriter.flush();
			}
			reprotWriter.append("\n");

			out = "Badly Formatted Movies Found: " + brokenMovies.size() + " Total\n";
			reprotWriter.append(out);
			for(Movie m: brokenMovies){
				reprotWriter.append(m.toString());
				reprotWriter.append("\n");
				reprotWriter.flush();
			}
			reprotWriter.append("\n");

			out = "Badly Formatted Sub Stars in Movie-Star Relation Found: " + brokenSubStarsInRelation.size() + " Total\n";
			reprotWriter.append(out);
			for(Star s: brokenSubStarsInRelation){
				reprotWriter.append(s.toString());
				reprotWriter.append("\n");
				reprotWriter.flush();
			}

//			out = genresTable.toString();
//			System.out.println(out);

			reprotWriter.flush();
			reprotWriter.close();

		}catch (IOException e){
			System.out.println("File error: "+ e.getMessage());
		}
	}

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of movie
            tempMovie = new Movie();
        }
		else if(qName.equalsIgnoreCase("actor")){
			tempStar = new Star();
		}
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
		tempVal = tempVal.replace("\\", "").replace("\n","");

		if (qName.equalsIgnoreCase("film")) {
			tempMovie.setDirector(currentDir);
//			if(tempMovie.getDirector().equals("R.Siodmak")){
//				System.out.println(tempMovie.getTitle()+"|"+tempMovie.getDirector());
//			}
			if(movieTitledirToId.containsKey(tempMovie.getTitle()+"|"+tempMovie.getDirector())){
				tempMovie.setId(movieTitledirToId.get(tempMovie.getTitle()+"|"+tempMovie.getDirector()));
//				System.out.println(tempMovie.getTitle()+"|"+tempMovie.getDirector());
//				System.out.println(tempMovie.toString());
			}

			if (tempMovie.getId().equals("")){
//				if (tempMovie.getTitle()+"|"+tempMovie.getDirector())
				String[] part = beginMovieId.split("(?<=\\D)(?=\\d)");
				int idNum = Integer.parseInt(part[1]) + 1;
				String newId = part[0]+ idNum;
				beginMovieId = newId;
				tempMovie.setId(newId);
//				System.out.println(newId);
				myMovies.add(tempMovie);
				currentMovies.put(currentDir,tempMovie);
			}
			else if(tempMovie.getTitle().equals("") || tempMovie.getYear() == -1){
                brokenMovies.add(tempMovie);
			}
			else {
				myMovies.add(tempMovie);
				currentMovies.put(currentDir,tempMovie);
			}
			movieTitledirToId.put(tempMovie.getTitle()+"|"+tempMovie.getDirector(),tempMovie.getId());

		}
		else if (qName.equalsIgnoreCase("fid")){
			tempMovie.setId(tempVal);
		}
        else if (qName.equalsIgnoreCase("t")){
			if(tempMovie != null){
				tempMovie.setTitle(tempVal);		
			}
		}
        else if (qName.equalsIgnoreCase("year")){
			try {
				tempMovie.setYear(Integer.parseInt(tempVal));
			}catch (NumberFormatException e){
//				System.out.println(tempVal);
				tempMovie.setYear(-1);
			}
		}
        else if (qName.equalsIgnoreCase("cat")) {
			if(!tempVal.equals("")){
				genresTable.add(tempVal);
				genresOfMovie.add(tempVal);
			}
		}
        else if (qName.equalsIgnoreCase("cats")) {
			tempMovie.setGenres(genresOfMovie);
			genresOfMovie = new ArrayList<String>();
		}
        else if (qName.equalsIgnoreCase("dirname")) {
			currentDir = tempVal;
		}
        // else if (qName.equalsIgnoreCase("t")) {
		// 	movie = tempVal;
		// }

		if (qName.equalsIgnoreCase("actor")) {
			if (tempStar.getName().equals("")){
				brokenStars.add(tempStar);
			}
			else if(!tempStar.getBirthYear().equals("") && !isNumeric(tempStar.getBirthYear())){
				brokenStars.add(tempStar);
			}
			else if(starNameYearToId.containsKey(tempStar.getName()+"|"+tempStar.getBirthYear())){
//				System.out.println(tempStar.getName());
			}
			else{
				String[] part = beginStarId.split("(?<=\\D)(?=\\d)");
				int idNum = Integer.parseInt(part[1]) + 1;
				String newId = part[0]+ idNum;
				beginStarId = newId;
//				System.out.println(newId);
				tempStar.setId(beginStarId);
				myStars.add(tempStar);
				starNameYearToId.put(tempStar.getName()+"|"+tempStar.getBirthYear(),beginStarId);
				starNameToId.put(tempStar.getName(),beginStarId);
			}
		}else if(qName.equalsIgnoreCase("stagename")){
				tempStar.setName(tempVal);
		}else if (qName.equalsIgnoreCase("dob")){
			if (!tempVal.equals("n.a.") && !tempVal.equals(" ") && !tempVal.equals("*")){
				tempStar.setBirthYear(tempVal);
			}
		}

		if (qName.equalsIgnoreCase("is")) {
			tempDirector = tempVal;
		}else if(qName.equalsIgnoreCase("t")) {
			tempMovieTitle = tempVal;
		}else if (qName.equalsIgnoreCase("a")) {
			if(tempVal.replace(" ","").length()>3){
				tempStars.add(tempVal);
			}
			else {
				Star ts = new Star();
				ts.setName(tempVal);
				brokenSubStarsInRelation.add(ts);
			}
		}else if (qName.equalsIgnoreCase("filmc")) {
			movieToActors.put(tempMovieTitle + "|" + tempDirector, tempStars);
			tempStars = new ArrayList<String>();
		}
    }

	private boolean isNumeric(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (NumberFormatException e){
			return false;
		}
	}



    public static void main(String[] args) {
        MoviesXmlParser mxp = new MoviesXmlParser();
        mxp.runParser();        
    }
}