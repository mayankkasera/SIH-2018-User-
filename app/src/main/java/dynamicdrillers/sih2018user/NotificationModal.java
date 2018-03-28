package dynamicdrillers.sih2018user;

/**
 * Created by Happy-Singh on 3/28/2018.
 */

public class NotificationModal {
    private String time;
    private String description;
    private String title;
    private String status;


    public NotificationModal(String time, String description, String title, String status) {
        this.time = time;
        this.description = description;
        this.title = title;
        this.status = status;
    }

    public NotificationModal() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
