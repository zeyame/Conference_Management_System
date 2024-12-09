package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.model.Certificate;

import java.util.Map;

public class CertificateRepository extends BaseRepository<Certificate> {

    private static CertificateRepository instance;

    private CertificateRepository() {
        super("src/main/resources/data/certificates.json");
    }


    // ensures only one instance of the repository is used throughout
    public static synchronized CertificateRepository getInstance() {
        if (instance == null) {
            instance = new CertificateRepository();
        }
        return instance;
    }

    @Override
    protected TypeReference<Map<String, Certificate>> getTypeReference() {
        return new TypeReference<Map<String, Certificate>>() {};
    }
}
