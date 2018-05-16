package cz.zamborsky.ontology.services;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.InMemoryDocument;
import eu.europa.esig.dss.client.crl.OnlineCRLSource;
import eu.europa.esig.dss.client.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.client.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.service.TSLRepository;
import eu.europa.esig.dss.tsl.service.TSLValidationJob;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.europa.esig.dss.validation.reports.SimpleReport;
import eu.europa.esig.dss.x509.CertificateToken;
import eu.europa.esig.dss.x509.KeyStoreCertificateSource;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Challenge pdf validator.
 */
@Service
public class PdfValidator {

    private CertificateVerifier cv;

    /**
     * Initializes thr validator with European certificate trusted lists.
     *
     */
    @PostConstruct
    public void init() {
        CommonsDataLoader dataLoader = new CommonsDataLoader();

        KeyStoreCertificateSource ojContentKeyStore = new KeyStoreCertificateSource(
                this.getClass().getClassLoader().getResourceAsStream("keystore.p12"),
                "PKCS12",
                "dss-password"
        );

        TrustedListsCertificateSource certificateSource = new TrustedListsCertificateSource();
        TSLRepository tslRepository = new TSLRepository();
        tslRepository.setTrustedListsCertificateSource(certificateSource);

        TSLValidationJob job = new TSLValidationJob();
        job.setDataLoader(dataLoader);
        job.setRepository(tslRepository);
        job.setLotlUrl("https://ec.europa.eu/information_society/policy/esignature/trusted-list/tl-mp.xml");
        job.setLotlRootSchemeInfoUri("https://ec.europa.eu/information_society/policy/esignature/trusted-list/tl.html");
        job.setLotlCode("EU");
        job.setOjUrl("http://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.2016.233.01.0001.01.ENG");
        job.setOjContentKeyStore(ojContentKeyStore);
        job.setCheckLOTLSignature(true);
        job.setCheckTSLSignatures(true);
        job.refresh();

        cv = new CommonCertificateVerifier();
        cv.setTrustedCertSource(certificateSource);
        cv.setDataLoader(dataLoader);
        cv.setOcspSource(new OnlineOCSPSource());
        cv.setCrlSource(new OnlineCRLSource());
    }

    /**
     * Validates the signature and extracts challenge into jwt file
     * @param signedPdf signed PDF
     */
    public Result validate(byte[] signedPdf) throws IOException {
        DSSDocument dssDocument = new InMemoryDocument(signedPdf);
        SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(dssDocument);
        validator.setCertificateVerifier(cv);

        Reports reports = validator.validateDocument();
        SimpleReport simpleReport = reports.getSimpleReport();

        List<AdvancedSignature> signatures = validator.getSignatures();
        if (signatures.size() == 0) {
            throw new IllegalArgumentException("No signature found.");
        }

        AdvancedSignature signature = signatures.get(0);

        if (simpleReport.isSignatureValid(signature.getId())) {
            Result result = extractSubjectInfo(signature);
            result.jwt = extractJwtChallenge(signedPdf);
            return result;

        } else {
            throw new IllegalArgumentException("Signature is invalid.");
        }
    }

    /**
     * Extracts subject information from bouncy castle signature.
     *
     * @param signature Signature
     * @return extracted info
     */
    private Result extractSubjectInfo(AdvancedSignature signature) {
        if (signature.getCertificates().size() == 0) {
            throw new IllegalArgumentException("No certificate found.");
        }

        CertificateToken token = signature.getCertificates().get(0);
        X509Certificate certificate = token.getCertificate();
        X500Principal principal = certificate.getSubjectX500Principal();
        X500Name name = new X500Name(principal.getName());


        Result result = new Result();
        result.fullName = extractValue(name, RFC4519Style.cn);
        result.country = extractValue(name, RFC4519Style.c);
        return result;
    }

    /**
     * Extracts first value for ASN1 identifier (cn, c, ...)
     *
     * @param name encoded object
     * @param attr attribute identifier
     * @return value
     */
    private String extractValue(X500Name name, ASN1ObjectIdentifier attr) {
        RDN[] values = name.getRDNs(attr);
        if (values == null || values.length == 0) {
            return null;
        }

        RDN value = values[0];
        return value.getFirst().getValue().toString();
    }

    /**
     * Extracts JWT
     * @param signedPdf PDF file
     * @return JWT challenge
     */
    private String extractJwtChallenge(byte[] signedPdf) throws IOException {
        PDDocument doc = PDDocument.load(signedPdf);
        PDDocumentInformation info = doc.getDocumentInformation();

        if (info == null) {
            throw new IllegalArgumentException("No info found.");
        }

        String challenge = info.getCustomMetadataValue("challenge");
        if (challenge == null) {
            throw new IllegalArgumentException("No challenge found.");
        }

        doc.close();
        return challenge;
    }
}
