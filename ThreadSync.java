//CS350
//Project #4 Thread Synchronization
//Janet Ruppert

//This program show how to use semaphores to synchronize multiple threads.
//Repeatedly prints out a '/' followed by three digits, then a '\' followed by two letters.

//creates three kinds of threads: 
//	PrintDigit for repeatedly printing out one digit 
//	PrintLetter for repeatedly printing letters
//	PrintSlashes for repeatedly printing '/' and '\'


import java.lang.Thread;
import java.util.concurrent.*;

public class ThreadSync
{
	private static Semaphore slashsem = new Semaphore(2);
	private static Semaphore digitsem = new Semaphore(0);
	private static Semaphore lettersem = new Semaphore(0);
	
    private static boolean runFlag = true;
	
    public static void main( String[] args ) {
     	Runnable[] tasks = new Runnable[37];
    	Thread[] threads = new Thread[37];
    	
    	// create a coordinator thread
		tasks[36] = new PrintSlashes();
		threads[36] = new Thread( tasks[36] );
		threads[36].start();

		
    	// create 26 letter threads
    	for (int d=0; d<26; d++) {
    		tasks[d+10] = new PrintLetter((char)('A'+d));
    		threads[d+10] = new Thread( tasks[d+10] );
    		threads[d+10].start();
    	}
       	
    	// create 10 digit threads
    	for (int d=0; d<10; d++) {
    		tasks[d] = new PrintDigit(d);
    		threads[d] = new Thread( tasks[d] );
    		threads[d].start();
    	}
    	

		// Let the threads to run for a period of time
        try {
        	Thread.sleep(50);
        }
        catch (InterruptedException ex) {
        	ex.printStackTrace();
        }
        runFlag = false;
        
        // Interrupt the threads
        for (int i=0; i<37; i++) threads[i].interrupt();
    }
    
    public static class PrintDigit implements Runnable 
    {
    	int digit;
    	public PrintDigit(int d) { digit=d; }
        public void run(){    	    
        	while (runFlag){    	    
        		try {    	    	
        			digitsem.acquire(1);
        	    	System.out.printf( "%d", digit);
        	    	slashsem.release(1);       	    
    	    	} 
    	    	catch (InterruptedException e) { }
    	    }
        }
    }
    public static class PrintLetter implements Runnable 
    {
    	char letter;
    	public PrintLetter(char c) { letter=c; }
        public void run(){
    	    while (runFlag) {
    	    	try{
    	    		lettersem.acquire(1);
    	    		System.out.printf( "%c", letter);
        	        slashsem.release(1);
    	    	}
    	    	catch (InterruptedException e) {}
    	        
    	    }
         }
    }
    public static class PrintSlashes implements Runnable 
    {
    	
        public void run(){
    	    while (runFlag) {
    	    	try {
        	    	slashsem.acquire(2);    	    		
        	    	System.out.printf( "%c", '/');
        	       
        	    	//start release of 3 numbers
        	        digitsem.release(3);        
        	        slashsem.acquire(3);
        	        System.out.printf( "%c", '\\');  
        	        
        	        //start release of letters
        	        lettersem.release(2);
    	    	}
    	    	catch (InterruptedException e) {}
    	        
    	        
    	    }
        }
    }
}
