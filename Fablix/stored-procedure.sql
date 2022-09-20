DELIMITER //
CREATE PROCEDURE CreateMovies(in title varchar(100), in year integer,in director varchar(100),in star varchar(100), in genre varchar(32), out status varchar(500),  out generatedmovie varchar(500), out generatedgenre varchar(500), out generatedstar varchar(500))
BEGIN

DECLARE movieId varchar(15);
DECLARE starId varchar(15);

    if not exists (select * from movies as m where m.title = title and m.director = director and m.year = year ) then

		
			SET movieId =  (select concat( 'tt',  LPAD((convert((SELECT SUBSTRING_INDEX((select max(id) from movies where id like "tt%" and CHAR_LENGTH(id)=9 ),'t',-1)), unsigned ) + 1), 7 ,0)));
			INSERT INTO movies Value (movieId, title,year ,director);				
			SET generatedmovie = movieId;
    
			if not exists (select * from stars as s where s.name = star ) then
			SET starId = (select concat( 'nm',  LPAD((convert((SELECT SUBSTRING_INDEX((select max(id) from stars),'m',-1)), unsigned ) + 1), 7 ,0)));
			INSERT INTO stars  VALUES (starId,star, null);
			INSERT INTO stars_in_movies values(starId, movieId);
			SET generatedstar = starId;
			else
			INSERT INTO stars_in_movies values((select id from stars where name = star limit 1), movieId);
			SET generatedstar =(select id from stars where name = star limit 1);
			end if;
    
			if not exists (select * from genres as g where g.name = genre ) then
			INSERT INTO genres (name) VALUES (genre);
			INSERT INTO genres_in_movies values((select id from genres where name = genre), movieId);
            SET generatedgenre = (select id from genres where name = genre);
			else
			INSERT INTO genres_in_movies values((select id from genres where name = genre), movieId);
			SET generatedgenre = (select id from genres where name = genre);
			end if;
            SET status = "The movie is successfully added.";
    else
    
			
			SET status = "The movie already exists.";
			
    
    end if;
    
    
END //
    
DELIMITER ;
