# CS122B-Fall21-Team-6
This is a UCI CS122B Project4 made by **Fall 2021 Team 6** (Chunzhi Xu & Haoting Ni)
### Note: Commit History users Haoting Ni and Silence-silence-silence are done by the same person Haoting Ni. Only for project 1, 2 because of mistaken push from local desktop.
### Note2: Commit History users Ubuntu is our AWS Linux User, Chunzhi Xu accidentally push changes without checking the username when debugging on AWS.
---
## Video Demo Link
**https://youtu.be/ImmVItD0py4**
## Application URL
**https://ec2-54-151-116-40.us-west-1.compute.amazonaws.com:8443/fablix/**

## How to deploy your application with Tomcat
- On AWS Server clone the project1
 ```
 git clone https://github.com/UCI-Chenli-teaching/cs122b-fall21-team-6.git
 ```
- Direct to project folder
 ```
 cd cs122b-fall21-team-6/Fablix
 ```
- Build the war file
 ```
 mvn package
 ```
- Copy war file to tomcat to deploy
 ```
 sudo cp ./target/*.war /var/lib/tomcat9/webapps/
 ```
## Substring matching design
- To search Title, Director, Stars:
 ```
 Pattern: LIKE %ABC%  (ABC is key word enter in the text bar)
 Any movies contain all key words entered in the text bars.
 
 If title has key word A, director has key word B, stars has key word C:
 Mysql script: where movie.title Like %A% and movie.director LIKE %B% and stars.name LIKE %C%
 ```
## Prepared Statement
We use Prepared Statement mainly in MoviesServlet.java, every url parameter and user input will finally put into a prepared statement which protect the database from sql attack.
<a href="src/PaymentServlet.java">PaymentServlet.java</a>

Others:
<a href="src/DashboardServlet.java">DashboardServlet.java</a>
<a href="src/ConfirmationServlet.java">ConfirmationServlet.java</a>
<a href="src/IndexServlet.java">IndexServlet.java</a>
<a href="src/LoginServlet.java">LoginServlet.java</a>
<a href="src/MoviesServlet.java">MoviesServlet.java</a>
<a href="src/rDashboardServlet.java">rDashboardServlet.java</a>
<a href="src/SingleMovieServlet.java">SingleMovieServlet.java</a>
<a href="src/SingleStarServlet.java">SingleStarServlet.java</a>
## Two parsing time optimization strategies
1. I load the original data we need from database to help use check if the new data is already exist or not immediately when we finish reading each element. Thus, we need less query when we process the data.

2. I use the LOAD DATA LOCAL INFILE feature to load everything we need to add to the database. I first create several csv file for different tables in database and store the new data into these csv file. After creating the csv file, I only need to use LOAD DATA LOCAL INFILE feature to load the data at once, which hugely improve the parsing time.

## Inconsistent data report

[Inconsistent Report](xml_parser/inconsistency_report.txt)


## Contribution
- CurtisXuCAD (Chunzhi Xu)
```
  Build movie list page
  Beautify table
  GitHub setup
  AWS setup
  Create demo
  Bug fixing
  Beautify Login, Main Page, Movie List Page
  Jump Function using session
  Query optimization
  Pagination
  Sorting
  HTTPS
  Password Encryption
  XML Parsing
  Prepared Statedment
  Fixing Query & Statedment
  Android App -- Fablix Mobile
  Fixing Pagination & Sorting for full-text search result
```

- Silence-silence-silence (Haoting Ni)
```
  Single Movie Page
  Single Star Page
  Jump Function
  Readme Creation 
  Beautify Pages
  Creat_table.sql
  Main Page 
  Login Page
  Browsing and Search Functionality
  Shopping Cart
  Payment Page
  Place Order
  Confirmation Page
  Add to Cart
  Beautify Shopping Cart, Payment Page, Confirmation Page
  reCAPTCHA
  Prepared Statedment
  Employee Dashboard
  Full-text Search
  Autocomplete
```
