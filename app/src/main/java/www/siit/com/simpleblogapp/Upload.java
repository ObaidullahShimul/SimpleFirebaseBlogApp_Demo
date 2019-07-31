package www.siit.com.simpleblogapp;

public class Upload {

    private String imageName;
    private String description;
    private String imgeUrl;

    //private String userName;

    //public String getUserName() {
        //return userName;
    //}

    //public void setUserName(String userName) {
        //this.userName = userName;
    //}

    public Upload(){

    }

    public Upload(String imageName, String description, String imgeUrl) {
        this.imageName = imageName;
        this.description = description;
        this.imgeUrl = imgeUrl;

    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getdescription() {
        return description;
    }

    public void setdescription(String description) {
        this.description = description;
    }

    public String getImgeUrl() {
        return imgeUrl;
    }

    public void setImgeUrl(String imgeUrl) {
        this.imgeUrl = imgeUrl;
    }
}
