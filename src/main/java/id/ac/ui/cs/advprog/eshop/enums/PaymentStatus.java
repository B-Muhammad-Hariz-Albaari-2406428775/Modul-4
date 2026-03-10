package id.ac.ui.cs.advprog.eshop.enums;

public enum PaymentStatus {
    SUCCESS, REJECTED;

    public static boolean contains(String param) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.name().equals(param)) return true;
        }
        return false;
    }
}
