package com.senthuran.mfa.azure;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import net.phonefactor.pfsdk.*;

public final class ExamplePhone {
	private final static String MFA_PIN = "1234";

	static public void main(String[] args) throws Exception {
		String mfaConfigurationFolder = System.getenv("MFA_CONFIGURATION_FOLDER");
		String mfaCertificatePassword = System.getenv("MFA_CERTIFICATE_PASSWORD");

		System.out.println("Please enter your phone number:");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String phoneNumber = reader.readLine();

		System.out.println(String.format("Dialing %s ...", phoneNumber));

		/*
		 * Create our PFAuth object. Generally you will want to make one
		 * globally accessible PFAuth instance and initialize it when your
		 * application initializes.
		 */
		PFAuth pf = new PFAuth();

		/*
		 * Initialize the object by pointing it to the directory where our
		 * license, cert, and private key reside. We also have to provide the
		 * password for the encrypted private key. This can be an empty string
		 * -- the decision to use an encrypted key and find a way to securely
		 * get the password here is your call. It is obviously superior to use a
		 * non blank password, but either method is supported.
		 */
		pf.initialize(mfaConfigurationFolder, mfaCertificatePassword);

		/*
		 * Now we transition to what should be done in the various threads in
		 * your application that need to use PhoneFactor. The code above this
		 * point should be done at application initialization or a similar point
		 * in your application.
		 */
		AuthModeInfo authModeInfo = new PlainTextPinInfo(MFA_PIN);

		PFAuthParams params = new PFAuthParams();
		params.setAuthInfo(authModeInfo);
		params.setUsername("senthuran");
		params.setCountryCode("1");
		params.setPhoneNumber(phoneNumber);

		/*
		 * Call PFAuth.authenticate. Catch all exceptions you wish to handle
		 * specifically and then use the PFException base class to catch the
		 * rest that are specific to the pfsdk in a generic fashion. If no
		 * exception is thrown, the result of the authentication request is
		 * available via the PFAuthResult object returned.
		 */
		PFAuthResult authResults = null;

		try {
			authResults = pf.authenticate(params);
		} catch (net.phonefactor.pfsdk.SecurityException e) {
			/*
			 * Perhaps log this?
			 */
			System.out.println("BAD AUTH -- Security issue!");
			throw e;
		} catch (TimeoutException e) {
			/*
			 * Perhaps log this and alert a network management system?
			 */
			System.out.println("BAD AUTH -- Server timeout");
			return;
		} catch (PFException e) {
			/*
			 * Catches all other exceptions authenticate throws via their common
			 * base class.
			 */
			System.out.println("BAD AUTH -- PFAuth failed with a PFException");
			throw e;
		}

		/*
		 * Obviously if an exception is thrown you should fail the auth that
		 * depended on it or take the correct action for that case.
		 *
		 * If an exception wasn't thrown, the PFAuthResult will be valid and you
		 * can consult it to determine the result of the authentication request.
		 */

		if (authResults.getAuthenticated()) {
			/*
			 * We were authenticated!
			 */
			System.out.println("GOOD AUTH");
			System.out.println("Call Status: " + authResults.getCallStatusString());

			switch (authResults.getCallStatus()) {
			case PFAuthResult.CALL_STATUS_PIN_ENTERED:
				System.out.println("I have detected that a PIN was entered.");
				break;

			case PFAuthResult.CALL_STATUS_NO_PIN_ENTERED:
				System.out.println("I have detected that NO PIN was entered.");
				break;

			default:
			}
		} else {
			/*
			 * We were not authenticated. Perhaps we should check if there is an
			 * error code or error message.
			 */
			System.out.println("BAD AUTH");
			System.out.println("Call Status: " + authResults.getCallStatusString());

			switch (authResults.getCallStatus()) {
			case PFAuthResult.CALL_STATUS_USER_HUNG_UP:
				System.out.println("I have detected that the user hung up.");
				break;

			case PFAuthResult.CALL_STATUS_PHONE_BUSY:
				System.out.println("I have detected that the phone was busy.");
				break;

			default:
			}

			if (authResults.getMessageErrorId() != 0) {
				System.out.println("Message Error ID: " + authResults.getMessageErrorId());

				String messageError = authResults.getMessageError();

				if (messageError != null) {
					System.out.println("Message Error: " + messageError);
				}
			}
		}
	}
}
