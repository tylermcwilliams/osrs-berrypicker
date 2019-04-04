import org.tribot.script.*;

import java.util.function.Predicate;

import org.tribot.api.*;
import org.tribot.api2007.*;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;

@ScriptManifest(
		authors = { "archpriest" }, 
		category = "Money Making", 
		name = "~arch~ Cadava Picker", 
		version = 1.00, 
		description = "Picks cadava berries south-east of Varrock", 
		gameMode = 1)

public class core extends Script{

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
	
	boolean shouldRun = true;
	
	
	@Override
	public void run() {
		
		if(Inventory.getAll().length > 8) {
			FirstGoBank();
		} 
		if(!berryArea.contains(Player.getPosition())) {
			FirstGoBerries();
		}
		
		// main loop
		while(shouldRun) {
			if(Inventory.isFull()) {
				// go bank, this includes walking to , banking, walking back
				GoBankAndReturn();
			} else {
				// pick berries loop
				PickBerries();
			}
		}
		
	}
	
	// tasks, will probably create task system
	
	private void FirstGoBank() {
		
		if(!WebWalking.walkToBank()) {
			this.shouldRun = false;
			return;
		} 
		
		if(!TryUseBank()) {
			this.shouldRun = false;
			return;
		};
		
	}
	
	private void FirstGoBerries() {
		
		if(!WebWalking.walkTo(berryArea.getRandomTile())) {
			this.shouldRun = false;
			return;
		};
		
		RSObject[] bushes = Objects.findNearest(50, "Cadava Bushes");
		
		if(bushes.length == 0) {
			this.shouldRun = false;
			return;
		}
		
	}
	
	private void GoBankAndReturn() {
		// walk to the bank
		RSTile[] randomPath = Walking.randomizePath(path, 2, 2);
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
				return IsPicking();
			}, General.random(8000, 9300));
				
		} 
		
	}
	
	// utility methods
	
	private RSObject[] GetBushes() {
	
		RSObject[] bushes = Objects.findNearest(20, bush -> {
			RSObjectDefinition objectDef = bush.getDefinition();
			
			// first check if full and cadava
			if(objectDef.getName() == "Cadava Bush" && objectDef.getModelIDs().length > 1) 
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
		
		if(!Banking.isBankScreenOpen()) {
			Banking.openBank();
		}
		
		Banking.depositAll();
		
		return true;
	}

	private boolean IsPicking() {
		return Player.getAnimation() > 0;
	}
	
}
