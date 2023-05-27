package org.lolicode.nekomusiccli.music;

import com.google.gson.annotations.SerializedName;

public class AlbumObj {
    public String name;
    public String id;
    @SerializedName("picUrl")
    public String picUrl;

    public String Hash() {
        return id;
    }
}
