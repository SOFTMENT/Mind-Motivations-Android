package in.softment.mindmotivation.Model;

import java.io.Serializable;
import java.util.Date;

public class CategoryModel implements Serializable {
    public String thumbnail = "";
    public String title = "";

    public String id = "";
    public Date createDate = new Date();
    public int tracks = 0;

    public CategoryModel() {
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getTracks() {
        return tracks;
    }

    public void setTracks(int tracks) {
        this.tracks = tracks;
    }
}
