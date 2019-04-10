package scripts.BerryPicker.tasks;

import org.tribot.script.*;

import org.tribot.api.*;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;

import scripts.BerryPicker.ArchBerryPicker;
import scripts.BerryPicker.tasks.*;

public class TaskBank {

	public static boolean completed = false;
	
	public static void run() {
		
		// walk to bank
		if(!WebWalking.walkToBank()) {
			ArchBerryPicker.shouldRun = false;
			return; 
		}
		
		// wait for inBank
		if(!Timing.waitCondition(() -> {
				General.sleep(100);
				return Banking.isInBank();
			}, General.random(5000,7000))) {	
			
			ArchBerryPicker.shouldRun = false;
			return; 
		};
		
		// open bank
		if(!Banking.openBank()) {
			ArchBerryPicker.shouldRun = false;
			return; 
		}
		
		// deposit all
		if(Banking.depositAll() < 1){
			ArchBerryPicker.shouldRun = false;
			return; 
		}
		
		// finally, mark completed
		completed = true;
		
	}

}
