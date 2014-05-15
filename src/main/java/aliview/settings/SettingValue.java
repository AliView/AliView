package aliview.settings;

public class SettingValue {

	private String prefsKey;
	private String defaultStringValue;
	private int defaultIntValue;
	private boolean defaultBooleanValue;
	private int minIntVal;
	private int maxIntVal;
	

	public SettingValue(String prefsKey, String defaultStringValue) {
		this.prefsKey = prefsKey;
		this.defaultStringValue = defaultStringValue;
	}

	public SettingValue(String prefsKey, int defaultIntValue, int minIntVal, int maxIntVal) {
		this.prefsKey = prefsKey;
		this.defaultIntValue = defaultIntValue;
		this.minIntVal = minIntVal;
		this.maxIntVal = maxIntVal;
	}
	
	public SettingValue(String prefsKey, boolean defaultBooleanValue) {
		this.prefsKey = prefsKey;
		this.defaultBooleanValue = defaultBooleanValue;
	}

	public String getPrefsKey() {
		return prefsKey;
	}

	public void setPrefsKey(String prefsKey) {
		this.prefsKey = prefsKey;
	}

	public String getDefaultStringValue() {
		return defaultStringValue;
	}

	public void setDefaultStringValue(String defaultStringValue) {
		this.defaultStringValue = defaultStringValue;
	}

	public int getDefaultIntValue() {
		return defaultIntValue;
	}

	public void setDefaultIntValue(int defaultIntValue) {
		this.defaultIntValue = defaultIntValue;
	}

	public int getMinIntVal() {
		return minIntVal;
	}

	public void setMinIntVal(int minIntVal) {
		this.minIntVal = minIntVal;
	}

	public int getMaxIntVal() {
		return maxIntVal;
	}

	public void setMaxIntVal(int maxIntVal) {
		this.maxIntVal = maxIntVal;
	}

	public String getStringValue() {
		return Settings.getStringValue(this);
	}
	
	
	public void putStringValue(String stringValue) {
		Settings.putStringValue(this, stringValue);
	}

	public void putIntValue(int intValue) {
		int bounded = Math.min(maxIntVal, intValue);
		bounded = Math.max(minIntVal, bounded);
		intValue = bounded;
		Settings.putIntValue(this, bounded);
	}
	
	public int getIntValue() {
		return Settings.getIntValue(this);
	}
	
	public boolean getDefaultBooleanValue() {
		return defaultBooleanValue;
	}

	public void setDefaultBooleanValue(boolean defaultBooleanValue) {
		this.defaultBooleanValue = defaultBooleanValue;
	}

	public void putBooleanValue(boolean booleanValue) {
		Settings.putBooleanValue(this, booleanValue);
	}
	
	public boolean getBooleanValue() {
		return Settings.getBooleanValue(this);
	}

}
