package main;
import java.util.concurrent.ForkJoinPool;

import task.Task;

public class SETester {

	public static volatile int counter = 0;
	
	public static volatile int tasksStarted = 0;
	
	public static int sleepingCounter = 0;
	
	public static final int MAX = 10000000;

	public static void main(String[] args){
		StackExecutor e = new StackExecutor();
		long time = System.nanoTime();
		for(int i = 0; i < MAX; i++){
			e.execute(new Task(){
				@Override
				public void compute() {
					incrementCounter();
					e.execute(new Task(){

						@Override
						public void compute() {
							incrementCounter();
						}
						
					});
				}
				
			});
//			ForkJoinPool.commonPool().execute(new Runnable(){
//	
//				@Override
//				public void run() {
//					incrementCounter();
//				}
//				
//			});
			tasksStarted++;
		}
		while(counter < (MAX * 2)){;}
		print("The time: " + (System.nanoTime() - time));
		print("Sleepingcounter value: " + sleepingCounter);
		print("Tasks started: " + (tasksStarted*2));
		print("Counter value: " + counter);
		print("Stack length: " + e.scheduledTasks.length());
	}
	
	public static void print(String s){
		System.out.println(s);
	}
	
	public static synchronized void incrementCounter(){
		counter++;
	}
}
