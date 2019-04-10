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
		name = "~arch~ Cadava Picker", 
		version = 1.00, 
		description = "Picks cadava berries south-east of Varrock", 
		gameMode = 1)

public class ArchBerryPicker extends Script{
	
	
	RSArea berryArea = new RSArea(
			new RSTile(3264, 3375),
			new RSTile(3278, 3365)
			);
	RSTile[] path = {
			new RSTile(3279, 3373, 0),
			new RSTile(3285, 3374, 0),
			new RSTile(3293, 3373, 0),
			new RSTile(3293, 3380, 0),
			new RSTile(3291, 3385, 0),
			new RSTile(3290, 3392, 0),
			new RSTile(3291, 3399, 0),
			new RSTile(3289, 3405, 0),
			new RSTile(3286, 3412, 0),
			new RSTile(3283, 3417, 0),
			new RSTile(3278, 3419, 0),
			new RSTile(3277, 3425, 0),
			new RSTile(3271, 3427, 0),
			new RSTile(3263, 3428, 0),
			new RSTile(3255, 3428, 0),
			new RSTile(3254, 3422, 0)	
	};
	
	public static boolean shouldRun = true;
	
	
	@Override
	public void run() {
		
		MainGUI gui = new MainGUI(this);
		
		gui.setVisible(true);
		
		while(gui.isVisible()) {
			General.sleep(1000);
		}
		
		// main loop
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
	
	// 
	
	private EnumTask NextTask() {
		
		if(Inventory.isFull()) {
			return EnumTask.BANK;
		}
		return EnumTask.BERRY;
		
	}
	
	// tasks, will probably create task system
	
	private void FirstGoBank() {
		
		if(!WebWalking.walkToBank()) {
			this.println("walking...");
			this.shouldRun = false;
			return;
		} 
		
		Timing.waitCondition(() -> {
			return Banking.isInBank();
		}, General.randomLong(7000, 9000));
		
		if(!TryUseBank()) {
			this.println("using...");
			this.shouldRun = false;
			return;
		};
		
	}
	
	private void FirstGoBerries() {
		
		if(!WebWalking.walkTo(berryArea.getRandomTile())) {
			this.shouldRun = false;
			return;
		};
		
		RSObject[] bushes = Objects.findNearest(50, "Cadava Bush");
		
		if(bushes.length == 0) {
			this.shouldRun = false;
			return;
		}
		
	}
	
	private void GoBankAndReturn() {
		// walk to the bank
		RSTile[] randomPath = Walking.randomizePath(path, 2, 2);
		RSTile[] returnPath = Walking.invertPath(randomPath);
		
		if(!Walking.walkPath(randomPath)) {
			this.shouldRun = false;
			return;
		}
		// bank
		if(!TryUseBank()) {
			this.shouldRun = false;
			return;
		}
		
		// walk back from the bank
		if(!Walking.walkPath(returnPath)) {
			this.shouldRun = false;
			return;
		}
	}
	
	private void PickBerries() {
		// check if any bushes are full
		RSObject[] bushes = GetBushes();
		
		if(bushes.length >= 1) {
			RSTile[] pathToBush = Walking.randomizePath(Walking.generateStraightPath(bushes[0]), 1, 1);
			if(!Walking.walkPath(pathToBush)) {
				return;
			};
			if(!Timing.waitCondition(() -> {
				General.sleep(100);
				return bushes[0].isOnScreen();
				}, 
					General.random(8000, 9300))) {
				return;
			}
			if(!DynamicClicking.clickRSObject(bushes[0], "Pick-from")) {
				return;
			};
			
			Timing.waitCondition(()->{
				General.sleep(1000);
				return !IsPicking();
			}, General.random(8000, 9300));
			
			if(bushes[0].getDefinition().getModelIDs().length > 1) {
				if(!DynamicClicking.clickRSObject(bushes[0], "Pick-from")) {
					return;
				};
				
				Timing.waitCondition(()->{
					General.sleep(1000);
					return !IsPicking();
				}, General.random(8000, 9300));
			}
		} 
		
	}
	
	// utility methods
	
	private RSObject[] GetBushes() {
		RSObject[] bushes = Objects.findNearest(20, bush -> {
			RSObjectDefinition objectDef = bush.getDefinition();
			// first check if full and cadava
			if(objectDef.getName().equals("Cadava bush") && objectDef.getModelIDs().length > 1) 
				{
					// then check if player adjacent
					RSPlayer[] players = Players.findNearest(player -> {
						return player.getPosition().distanceTo(bush.getPosition()) <= 1;
					});
					return players.length == 0;
				
				} else {
					return false;
				}
			});
		
		return bushes;
	}
	
	private boolean TryUseBank() {
		if(!Banking.isInBank()) {
			return false;
		};
		this.println("is in bank");
		
		if(!Banking.isBankScreenOpen()) {
			if(!Banking.openBank()) {
				return false;
			};
		}
		
		this.println("bank screen opened");
		
		if(Banking.isBankScreenOpen()) {
			Banking.depositAll();
			return true;
		} else {
			return false;
		}
		
		
	}

	private boolean IsPicking() {
		return Player.getAnimation() > 0;
	}
	
}
