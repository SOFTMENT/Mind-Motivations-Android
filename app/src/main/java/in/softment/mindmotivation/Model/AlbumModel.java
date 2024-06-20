package in.softment.mindmotivation.Model;

public class AlbumModel {
    public String id = "";
    public String name = "";
    public int tracks = 0;

    public AlbumModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTracks() {
        return tracks;
    }

    public void setTracks(int tracks) {
        this.tracks = tracks;
    }
}
