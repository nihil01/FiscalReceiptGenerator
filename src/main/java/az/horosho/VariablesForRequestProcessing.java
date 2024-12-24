package az.horosho;

import java.util.prefs.Preferences;

public class VariablesForRequestProcessing {
    Preferences preferences = Preferences.userNodeForPackage(this.getClass());

    public void setPreferences(String key, String value) {
        preferences.put(key, value);
    }

    public String getPreferences(String key) {
        return preferences.get(key, null);
    }

    public void removePreferences(String key){
        preferences.remove(key);
    }
}
