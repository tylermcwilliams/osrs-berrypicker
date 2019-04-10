package scripts.BerryPicker.settings;

public enum EnumBerrySettings {
	CADAVA ("Cadava bush"),
	REDBERRY ("Redberry bush"),
	BOTH ("bush");
	
	public String bush;
	
	EnumBerrySettings(String string){
		this.bush = string;
	}
	
}
