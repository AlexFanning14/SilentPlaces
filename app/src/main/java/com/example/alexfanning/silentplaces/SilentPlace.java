package com.example.alexfanning.silentplaces;

/**
 * Created by alex.fanning on 20/10/2017.
 */

public class SilentPlace {


    private int _id;
    private String description;
    private String silentMode;
    public SilentPlace(int _id, String _desc, String _silentMode){
        this._id = _id;
        description = _desc;
        silentMode = _silentMode;
    }
    public SilentPlace(int _id){
        this._id = _id;

    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSilentMode() {
        return silentMode;
    }

    public void setSilentMode(String silentMode) {
        this.silentMode = silentMode;
    }
}
