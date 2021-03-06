package observer3;

import observer3.Observable.Observer;

public class Button{
		
	private Observable<String> observable;
	
	public Button() {
		this.observable = new Observable<String>();
	}

	public void addObserver(Observer<String> o) {
		observable.addObserver(o);
    }
	
	public void notifyObservers() {
		observable.notifyObservers(null);
    }

	public void onClick() {
		observable.setChanged();
		notifyObservers();
	}
}