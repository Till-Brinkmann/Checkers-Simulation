package gui;

import javax.swing.JFrame;

import training.TrainingSession;

public class TSSetupWindow extends JFrame {
	
	private Object waitLock;
	
	public TSSetupWindow(){
		waitLock = new Object();
	}
	
	public TrainingSession get(){
		synchronized(waitLock){
			try {
				waitLock.wait();
			} catch (InterruptedException e) {
				System.out.println("The Thread " + Thread.currentThread().getName() +
						"got interrupted while waiting on a new TrainingSession from TSSetupWindow.");
				e.printStackTrace();
			}
		}
		return null;//new TrainingSession();
	}

}
