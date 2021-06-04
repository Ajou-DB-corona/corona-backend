package database_java;

public class User {
	private String userID = null;
	private int posUser = -1;	//cityID

	public User(String userID) {
		this.userID= new String(userID);

	}
	public void setUserID(String userID) {
		this.userID = new String(userID);
	}
	
	public void setPos(int posUser) {
		this.posUser = posUser; 
	}
	
	public int getPosUser() {return this.posUser;}
	public String getUserID() {
		return new String(this.userID);
	}
	
}
