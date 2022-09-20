public class Star {

	private String id;
	private String name;
	private String birthYear;
	
	public Star(){
		name= "";
		birthYear = "";
	}
	
	public Star(String id, String name, String birthYear) {
		
		this.id=id;
		this.name = name;
		this.birthYear=birthYear;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(String birthYear) {
		this.birthYear = birthYear;
	}

    public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Star Details - ");
		sb.append("Id:" + getId());
		sb.append(", ");
		sb.append("Name:");
		if(getName()!=null)
			sb.append(getName());
		sb.append(", ");
		sb.append("Date of Birth:" + getBirthYear());
		sb.append(".");
		
		return sb.toString();
	}

	public String toCSV() {
		StringBuffer sb = new StringBuffer();
		sb.append(getId());
		sb.append("|");
		sb.append(getName());
		sb.append("|");
		sb.append(getBirthYear());

		return sb.toString();
	}


}