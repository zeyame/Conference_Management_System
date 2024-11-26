package util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CollectionUtils {
    public static <T> List<T> extractValidEntities(List<Optional<T>> optionals) {
        return optionals.stream()
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }
}
