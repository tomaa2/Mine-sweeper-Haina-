package Model;

public class Player {
	private final String name;
	private boolean isTurn;
	
	
	public Player(String name) {
		this.name=name;
		this.isTurn=false;
	}


	public boolean isTurn() {
		return isTurn;
	}


	public void setTurn(boolean isTurn) {
		this.isTurn = isTurn;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name.equals(name);
	}
}
