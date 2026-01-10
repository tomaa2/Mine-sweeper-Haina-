package Controller;


/**
 * Observer interface for the Observer pattern.
 * Any UI/controller that wants to react to game state changes implements this.
 */
public interface GameObserver {
	void onGameStateChanged();
}








