package util;

import java.util.UUID;

public class IdGenerator {

    // private no-arg constructor to suppress instantiability
    private IdGenerator() {}

    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}
