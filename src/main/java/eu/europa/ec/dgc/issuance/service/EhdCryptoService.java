/*-
 * ---license-start
 * EU Digital Green Certificate Issuance Service / dgca-issuance-service
 * ---
 * Copyright (C) 2021 T-Systems International GmbH and all other contributors
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ---license-end
 */

package eu.europa.ec.dgc.issuance.service;

import COSE.AlgorithmID;
import COSE.CoseException;
import COSE.HeaderKeys;
import COSE.OneKey;
import com.upokecenter.cbor.CBORObject;
import ehn.techiop.hcert.kotlin.chain.CryptoService;
import ehn.techiop.hcert.kotlin.chain.VerificationResult;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.util.Arrays;
import java.util.List;
import kotlin.Pair;
import org.springframework.stereotype.Component;

@Component
public class EhdCryptoService implements CryptoService {
    private final X509Certificate cert;
    private final byte[] kid;
    private final List<Pair<HeaderKeys, CBORObject>> headers;
    private final PrivateKey privateKey;

    /**
     * the constructor.
     *
     * @param certificateService certificateService
     */
    public EhdCryptoService(CertificateService certificateService) {
        this.cert = certificateService.getCertficate();
        this.privateKey = certificateService.getPrivateKey();
        kid = certificateService.getKid();
        if (this.privateKey instanceof RSAPrivateCrtKey) {
            headers = Arrays.asList(new Pair<>(HeaderKeys.Algorithm, AlgorithmID.RSA_PSS_256.AsCBOR()),
                new Pair<>(HeaderKeys.KID, CBORObject.FromObject(kid)));
        } else {
            headers = Arrays.asList(new Pair<>(HeaderKeys.Algorithm, AlgorithmID.ECDSA_256.AsCBOR()),
                new Pair<>(HeaderKeys.KID, CBORObject.FromObject(kid)));
        }
    }

    @Override
    public List<Pair<HeaderKeys, CBORObject>> getCborHeaders() {
        return headers;
    }

    @Override
    public COSE.OneKey getCborSigningKey() {
        try {
            return new OneKey(cert.getPublicKey(), privateKey);
        } catch (CoseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public COSE.OneKey getCborVerificationKey(byte[] bytes, VerificationResult verificationResult) {
        if (Arrays.compare(this.kid, kid) == 0) {
            try {
                return new OneKey(cert.getPublicKey(), privateKey);
            } catch (CoseException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("unknown kid");
        }
    }

    @Override
    public X509Certificate getCertificate() {
        if (Arrays.compare(this.kid, kid) == 0) {
            return cert;
        } else {
            throw new IllegalArgumentException("unknown kid");
        }
    }

    @Override
    public String exportCertificateAsPem() {
        return null;
    }

    @Override
    public String exportPrivateKeyAsPem() {
        return null;
    }

}
