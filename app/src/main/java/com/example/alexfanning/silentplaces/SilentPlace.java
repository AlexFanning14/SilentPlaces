package com.example.alexfanning.silentplaces;

/**
 * Created by alex.fanning on 20/10/2017.
 */

public class SilentPlace {


    private String _id;
    private String description;
    private int silentMode;
    public SilentPlace(String _id, String _desc, int _silentMode){
        this._id = _id;
        description = _desc;
        silentMode = _silentMode;
    }
    public SilentPlace(String _id){
        this._id = _id;

    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSilentMode() {
        return silentMode;
    }

    public void setSilentMode(int silentMode) {
        this.silentMode = silentMode;
    }
}
