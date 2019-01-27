public enum RentStatus {

    IN_RENT("In rent"),
    RETURNED("Returned");

    RentStatus(String status) {
        this.status = status;
    }

    String status;

    public String getStatus() {
        return status;
    }
}
