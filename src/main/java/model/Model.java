package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Model {
	private ArrayList<ActionListener> actionListenerList = new ArrayList<ActionListener>();
	
	public void addActionListener(ActionListener actionListener){
		actionListenerList.add(actionListener);
	}
	
	public void notifyListeners(){
		processEvent( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, null));
	}
	
	private void processEvent(ActionEvent e){
		for( ActionListener l : actionListenerList)
			l.actionPerformed( e );
	}
}
