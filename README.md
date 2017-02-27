# Azure MFA for Java

The Azure Multi-Factor Authentication Software Development Kit (SDK) lets you build two-step verification directly into the sign-in or transaction processes of applications in your Azure AD tenant.+

The structure of the APIs in the Multi-Factor Authentication SDK is simple. Make a single function call to an API with the multi-factor option parameters (like verification mode) and user data (like the telephone number to call or the PIN number to validate). The APIs translate the function call into web services requests to the cloud-based Azure Multi-Factor Authentication Service. All calls must include a reference to the private certificate that is included in every SDK.+

Because the APIs do not have access to users registered in Azure Active Directory, you must provide user information in a file or database. Also, the APIs do not provide enrollment or user management features, so you need to build these processes into your application.+

## Important MFA Files

* License.xml
* cert.p12

These files can be found in the Azure MFA's home page:

1.  Sign in to the Azure classic portal as an Administrator.
2.  On the left, select Active Directory.
3.  On the Active Directory page, at the top select Multi-Factor Auth Providers
4.  At the bottom select Manage. A new page opens.
5.  On the left, at the bottom, click SDK.

Copy the license and the certificate to a secure location and set environment parameters to access the files.

The password for the certificate will also be provided.  You can modify the password using the notes located in resources\mfa\README.