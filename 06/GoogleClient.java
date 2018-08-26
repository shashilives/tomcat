/* The certificates received from Google are verified against
   the default truststore, typically JAVA_HOME/jre/lib/security/cacerts.
*/

import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.net.MalformedURLException;
import java.security.cert.Certificate;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GoogleClient {
    private static final String endpoint = "https://www.google.com:443/";

    // Send a GET request and print the response status code.
    public static void main(String[ ] args) {
	new GoogleClient().doIt();
    }
    private void doIt() {
	try {
	    URL url = new URL(endpoint);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
	    conn.setRequestMethod("GET");
	    conn.connect();
	    dumpDetails(conn);
	}
	catch(MalformedURLException e) { System.err.println(e); }
	catch(IOException e) { System.err.println(e); }
    }
    private void dumpDetails(HttpsURLConnection conn) {
	try {
	    print("Status code:  " + conn.getResponseCode());
	    print("Cipher suite: " + conn.getCipherSuite());
	    Certificate[ ] certs = conn.getServerCertificates();               //### the DCs
	    for (Certificate cert : certs) {                                   //### give details about each
		print("\tCert. type: " + cert.getType());
		print("\tHash code:  " + cert.hashCode());
		print("\tAlgorithm:  " + cert.getPublicKey().getAlgorithm());
		print("\tFormat:     " + cert.getPublicKey().getFormat());
		print("");
	    }
	    
	}
	catch(Exception e) { System.err.println(e); }
    }

    private void print(Object s) {
	System.out.println(s);
    }
}
/* Output from a sample run:

Status code:  200                                       ## HTTP status code, 200 == OK
Cipher suite: TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256     ## see below
	Cert. type: X.509                               ## 1st DC
	Hash code:  767508163
	Algorithm:  RSA
	Format:     X.509

	Cert. type: X.509                               ## 2nd DC
	Hash code:  771393018
	Algorithm:  RSA
	Format:     X.509

	Cert. type: X.509                               ## 3rd DC
	Hash code:  349192256
	Algorithm:  RSA
	Format:     X.509

  ## What the 'cipher suite' acronyms mean:

     TLS:         Transport Layer Security, basically the 'S' in HTTPS

     ECDHE:       Elliptic Curve Diffie-Hellman Key Exchange, the 'handshake' algorithm in play

     RSA:         Rivest-Shamir-Adleman, authors of the 'public key cryptography algorithm' in play

     AES_128_GCM: American Encryption Standard, the 'block cipher algorithm' in play with a 128-bit key in
                  Galois Counter Mode, a 'mode of operation' used in 'block ciphers'

     SHA256:      Secure Hash Algorithm, the 'cryptographic hash function' used here to generate a 
                  160-bit 'message digest' that serves as a certificate 'fingerprint'.

 Summary of the 'handshake':

 # Underlying algorithm is ECDHE from the cipher suite listing, shortened in general to Diffie-Hellman.

 1. Client (e.g., the Java GoogleClient app, a browser, 'curl') challenges the server (e.g., Google), which
    responds with one or more DCs.

    1a. The server may challenge the client, but for web sites this is unusual; for web services, the
        challenge is often 'mutual', however.

 2. The client checks the DCs against its truststore. If ok, continue. (The check may be indirect:
    check the vouching CA signature.)

 3. The client generates a "pre-master secret", for example, a 48-bit value with certain properties.

 4. The client uses the public key from a server DC to encrypt the "pre-master secret" before sending to the server.
    The server decrypts the secret with its private key.

 5. The client and the server then use this "secret" to generate the same "session key," which is used
    to encrypt and decrypt messages. (About 1K times faster than public-key-encryption-with-private-key-decryption.)

 6. Either side can call from a new handshake at any time.
*/
