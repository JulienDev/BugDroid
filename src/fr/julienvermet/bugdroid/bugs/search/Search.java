package fr.julienvermet.bugdroid.bugs.search;

public class Search {

	protected int id;
	protected String name;
	protected String params;
	
	public Search(String name, String params) {
		super();
		this.name = name;
		this.params = params;
	}
	
	public Search(int id, String name, String params) {
		super();
		this.id = id;
		this.name = name;
		this.params = params;
	}
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getParams() {
		return params;
	}
	
	/*protected String status;
	protected String product;
	protected String words;
	protected int days;*/
	
	/*public Search(String name, String status, String product,
			String words, int days) {
		super();
		this.name = name;
		this.status = status;
		this.product = product;
		this.words = words;
		this.days = days;
	}

	public Search(int id, String name, String status, String product,
			String words, int days) {
		super();
		this.id = id;
		this.name = name;
		this.status = status;
		this.product = product;
		this.words = words;
		this.days = days;
	}



	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getStatus() {
		return status;
	}

	public String getProduct() {
		return product;
	}

	public String getWords() {
		return words;
	}

	public int getDays() {
		return days;
	}*/
}