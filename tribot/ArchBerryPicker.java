package scripts.BerryPicker;
    
import org.tribot.script.*;

import org.tribot.api.*;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import scripts.BerryPicker.tasks.*;
import scripts.BerryPicker.gui.*;

@ScriptManifest(
		authors = { "archpriest" }, 
		category = "Money Making", 
		name = "Arch Berry Picker", 
		version = 1.00, 
		description = "Picks berries south-east of Varrock", 
		gameMode = 1)

public class ArchBerryPicker extends Script{
	
	public static boolean shouldRun = true;
	
	@Override
	public void run() {
		
		MainGUI gui = new MainGUI(this);
		
		gui.setVisible(true);
		
		while(gui.isVisible()) {
			General.sleep(1000);
		}

		while(shouldRun) {
			
			General.sleep(2000);
			
			switch(NextTask()) {
				case BANK:
					TaskBank.run();
					break;
				case BERRY:
					TaskBerry.run();
					break;
				case EXIT:
				default:
					shouldRun = false;
			}
		}
		
	}

	private EnumTask NextTask() {
		
		if(Inventory.isFull()) {
			return EnumTask.BANK;
		}
		return EnumTask.BERRY;
		
	}
	
}
