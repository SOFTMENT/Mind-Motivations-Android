package in.softment.mindmotivation.Model;

import java.util.Date;

public class UserModel {

    public String uid = "";
    public String fullName = "";
    public String email = "";
    public Date registredAt = new Date();
    public String regiType = "";


    public static UserModel data  = new UserModel();

    public UserModel() {
        data = this;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegistredAt() {
        return registredAt;
    }

    public void setRegistredAt(Date registredAt) {
        this.registredAt = registredAt;
    }

    public String getRegiType() {
        return regiType;
    }

    public void setRegiType(String regiType) {
        this.regiType = regiType;
    }
}
