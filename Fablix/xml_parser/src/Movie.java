import java.util.ArrayList;
import java.util.List;

public class Movie {

    private String id;
	private String title;
	private int year;
	private String director;
	private List<String> genres;
	public Movie(){
		id = "";
		genres = new ArrayList<>();
	}
	
	public Movie(String id, String title, int year, String director) {
		this.setId(id);
		this.setTitle(title);
		this.setYear(year);
		this.setDirector(director);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}	
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Movie Details - ");
		sb.append("Id:" + getId());
		sb.append(", ");
		sb.append("Title:" + getTitle());
		sb.append(", ");
		sb.append("Year:" + getYear());
		sb.append(", ");
		sb.append("Director:" + getDirector());
		sb.append(", ");
		sb.append("Genres:" + getGenres());
		sb.append(".");
		
		return sb.toString();
	}

	public String toCSV() {
		StringBuffer sb = new StringBuffer();
		sb.append(getId());
		sb.append("|");
		sb.append(getTitle());
		sb.append("|");
		sb.append(getYear());
		sb.append("|");
		sb.append(getDirector());

		return sb.toString();
	}
}