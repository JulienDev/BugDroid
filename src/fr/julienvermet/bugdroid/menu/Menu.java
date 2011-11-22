package fr.julienvermet.bugdroid.menu;

public class Menu {
	
	protected String name;
	protected Class intent;
	protected int image;
	
	public Menu(String name, Class intent, int image) {
		super();
		this.name = name;
		this.intent = intent;
		this.image = image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Class getIntent() {
		return intent;
	}
	public void setIntent(Class intent) {
		this.intent = intent;
	}
	public int getImage() {
		return image;
	}
	public void setImage(int image) {
		this.image = image;
	}
}
