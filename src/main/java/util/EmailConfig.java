package util;

public enum EmailConfig {
    SMTP_HOST("smtp.gmail.com"),
    SMTP_PORT("587"),
    SENDER_EMAIL("zeyame14@gmail.com"),
    PASSWORD("qshj louz iwhc rygo"),
    DISPLAY_NAME("UH Scientific Conferences");

    private final String value;

    EmailConfig(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
