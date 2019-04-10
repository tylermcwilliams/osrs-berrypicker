package scripts.BerryPicker.tasks;

import org.tribot.script.*;

import org.tribot.api.*;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import scripts.BerryPicker.tasks.*;
import scripts.BerryPicker.*;
import scripts.BerryPicker.settings.Settings;

public class TaskBerry {

	static RSArea berryArea = new RSArea(
			new RSTile(3264, 3375),
			new RSTile(3278, 3365)
			);
	
	public static boolean completed = false;
	
	public static void run() {
		
		// walk to berry patch
		if(!IsInBerryArea() && !WebWalking.walkTo(berryArea.getRandomTile())) {
			ArchBerryPicker.shouldRun = false;
			return; 
		}
		
		// wait for berry area
		if(!Timing.waitCondition(() -> {
				General.sleep(100);
				return IsInBerryArea();
			}, General.random(5000,7000))) {	
			ArchBerryPicker.shouldRun = false;
			return; 
		};
		
		while(!Inventory.isFull()) {
			General.sleep(300);
			
			RSObject[] bushes = GetBushes();
			
			if(bushes.length == 0) {
				ChangeWorld();
				continue; 
			} else {
				PickBerries(bushes);
			}
		}
		
		// finally, mark completed
		completed = true;
		
	}
	
	private static RSObject[] GetBushes() {
		
		RSObject[] bushes = Objects.findNearest(20, entry -> {
			if(IsFullBush(entry) && IsFree(entry)) {
				return true;
			}
			return false;
			});
		
		return bushes;
	}
	
	private static boolean IsFullBush(RSObject currentObj) {
		
		RSObjectDefinition objectDef = currentObj.getDefinition();
		
		if(objectDef.getName().endsWith(Settings.berriesToPick.bush) && objectDef.getModelIDs().length > 1) 
		{
			return true;
		}
		
		return false;
	}
	
	private static boolean IsFree(RSObject currentObj) {
		
		RSPlayer[] players = Players.findNearest(player -> {
			if(player == Player.getRSPlayer()) {
				return false;
			}
			return player.getPosition().distanceTo(currentObj.getPosition()) <= 1;
		});
		
		if(players.length == 0) {
			return true;
		}
		
		return false;
		
	}
 
	private static boolean ChangeWorld() {
		
		int newWorld = WorldHopper.getRandomWorld(false);
		
		if(WorldHopper.changeWorld(newWorld)) {
			return true;
		}
		return false;
	}

	private static boolean IsInBerryArea() {
		if(berryArea.contains(Player.getPosition())) {
			return true;
		}
		return false;
	}

	private static boolean PickBerries(RSObject[] bushes) {
			
		RSTile[] pathToBush = Walking.randomizePath(Walking.generateStraightPath(bushes[0]), 1, 1);

		if (!Walking.walkPath(pathToBush)) {
			return false;
		};
		
		if (!Timing.waitCondition(() -> {
				General.sleep(100);
				return bushes[0].isOnScreen();
			}, General.random(8000, 9300))) {
			
			return false;
		}
		
		if (!DynamicClicking.clickRSObject(bushes[0], "Pick-from")) {
			return false;
		};

		Timing.waitCondition(() -> {
				General.sleep(1000);
				return !IsPicking();
			}, General.random(8000, 9300));

		if (bushes[0].getDefinition().getModelIDs().length > 1) {
			if (!DynamicClicking.clickRSObject(bushes[0], "Pick-from")) {
				return false;
			};

			Timing.waitCondition(() -> {
					General.sleep(1000);
					return !IsPicking();
				}, General.random(8000, 9300));
		}

		return true;
		
	}
	
	private static boolean IsPicking() {
		return Player.getAnimation() > 0;
	}
}
