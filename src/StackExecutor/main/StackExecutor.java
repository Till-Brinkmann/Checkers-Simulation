package main;

import task.Task;
import util.TSStack;

public class StackExecutor{
	
	//public static final StackExecutor common = new StackExecutor("Common StackExecutor");
	//the stack that all incoming tasks are pushed on
	//also the object the workers invoke wait() on
	public final TSStack<Task> scheduledTasks;
	//start of the name that every worker of this executor is instantiated with
	protected final String namePrefix;
	protected final Object lock;
	//maximum number of workers to start
	private int parallelizationLevel;
	protected int activeCount;
	//indicates that the executor is shutting down
	protected boolean doShutdown;
	
	public StackExecutor() {
		this("StackExecutor");
	}
	public StackExecutor(String name){
		this(name, Runtime.getRuntime().availableProcessors());
	}
	public StackExecutor(String name, int parallelizationLevel){
		namePrefix = name;
		this.parallelizationLevel = parallelizationLevel;
		scheduledTasks = new TSStack<Task>();
		lock = new Object();
		//one worker is always existent to simplify code in execute()
		new SEWorker(this).start();
		activeCount = 1;
	}
	
	public void execute(Task task){
			if(scheduledTasks.length() == 0){
				scheduledTasks.push(task);
				synchronized(lock){
					lock.notify();
				}
			} else {
				scheduledTasks.push(task);
				//if there is more than one task, we will need more than one worker to optimize performance
				//only start as many workers as there are tasks or as the maximum parallelization level
				if(activeCount < Math.min(parallelizationLevel, scheduledTasks.length())){
					//we can start a new worker
					new SEWorker(this).start();
					activeCount++;
					return;
				}
				//if we can not start new workers but there is at least one task waiting on the stack
				//try waking all workers up
				synchronized(lock){
					lock.notifyAll();
				}
			}
	}
	
	public void start(){
		doShutdown = false;
		//if there are tasks on the stack start executing
		for(int i = 0, max = Math.min(scheduledTasks.length(), parallelizationLevel); i < max; i++){
			new SEWorker(this).start();
			activeCount++;
		}
	}
	public void shutdown(){
		doShutdown = true;
		synchronized(scheduledTasks){
			scheduledTasks.notifyAll();
		}
		activeCount = 0;
	}
	
	public void setParallelism(int amount){
		if(amount > 0) parallelizationLevel = amount;
	}
}
