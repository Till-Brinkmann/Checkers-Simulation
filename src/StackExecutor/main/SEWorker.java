package main;

import task.Task;

public class SEWorker extends Thread {
	//reference to the StackExecutor object associated with this worker
	private StackExecutor executor;
	
	public SEWorker(StackExecutor executor) {
		super(executor.namePrefix + ":Worker-" + executor.activeCount);
		this.executor = executor;
		//should always be a daemon thread because it waits without the guarantee to ever be notified
		setDaemon(true);
	}
	
	@Override
	public void run(){
		while(!executor.doShutdown){
			Task task = executor.scheduledTasks.pop();
			if(task != null) {
				try{
					task.compute();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			else {
				try {
					synchronized(executor.lock){
						executor.lock.wait();
					}
				} catch (InterruptedException ex2) {
					System.out.println(getName() + " got interrupted.");
					ex2.printStackTrace();
				}
			}
		}
	}
}
