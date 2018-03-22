package dynamicdrillers.sih2018user;

/**
 * Created by Mayank on 22-03-2018.
 */

public class ComplaintModal {

    private String complaint_request_time;
    private String complaint_dis;
    private String complaint_full_address;
    private String complaint_votes;
    private String complaint_share;
    private String complaint_status;

    public ComplaintModal() {
    }

    public String getComplaint_status() {
        return complaint_status;
    }

    public void setComplaint_status(String complaint_status) {
        this.complaint_status = complaint_status;
    }

    public String getComplaint_request_time() {
        return complaint_request_time;
    }

    public ComplaintModal(String complaint_request_time, String complaint_dis, String complaint_full_address, String complaint_votes, String complaint_share, String complaint_status) {
        this.complaint_request_time = complaint_request_time;
        this.complaint_dis = complaint_dis;
        this.complaint_full_address = complaint_full_address;
        this.complaint_votes = complaint_votes;
        this.complaint_share = complaint_share;
        this.complaint_status = complaint_status;
    }


    public void setComplaint_request_time(String complaint_request_time) {
        this.complaint_request_time = complaint_request_time;
    }

    public String getComplaint_dis() {
        return complaint_dis;
    }

    public void setComplaint_dis(String complaint_dis) {
        this.complaint_dis = complaint_dis;
    }

    public String getComplaint_full_address() {
        return complaint_full_address;
    }

    public void setComplaint_full_address(String complaint_full_address) {
        this.complaint_full_address = complaint_full_address;
    }

    public String getComplaint_votes() {
        return complaint_votes;
    }

    public void setComplaint_votes(String complaint_votes) {
        this.complaint_votes = complaint_votes;
    }

    public String getComplaint_share() {
        return complaint_share;
    }

    public void setComplaint_share(String complaint_share) {
        this.complaint_share = complaint_share;
    }
}
