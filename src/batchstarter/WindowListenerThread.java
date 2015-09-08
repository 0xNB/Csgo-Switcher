package batchstarter;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import static java.nio.file.StandardWatchEventKinds.*;

import java.io.IOException;
import java.nio.file.Path;

public class WindowListenerThread extends Thread{

	private WatchService watcher;
	private int folder;
	
	public WindowListenerThread(WatchService watcher,int folder){
		this.watcher = watcher;
		this.folder = folder;
	}
	
	@Override
	public void run() {
		while(!isInterrupted()){

				WatchKey key = null;
					try {
						key = watcher.take();
					} catch (InterruptedException e) {
						interrupt();
						break;
					}
		      
		    for (WatchEvent<?> event: key.pollEvents()) {
		        WatchEvent.Kind<?> kind = event.kind();
		        if (kind == OVERFLOW) {
		            continue;
		        }
		        WatchEvent<Path> ev = (WatchEvent<Path>)event;
		        Path filename = ev.context();
		        System.out.println(filename.toString());
		        if(folder == 1){
		        CsgoSounds.createRandomSubfolders();
		        }
		        else if(folder == 2){
		        	CsgoSounds.createSubFolders();
		        	CsgoSounds.updateLabels();
		        }
		    }
		    
		    boolean valid = key.reset();
		    if (!valid) {
		        break;
		    }
	}
		System.out.println("Thread beendet");

}
}
