package com.coremedia.labs.plugins.adapters.onedrive.service;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientCredential;
import com.microsoft.aad.msal4j.MsalException;
import com.microsoft.aad.msal4j.SilentParameters;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.http.IHttpRequest;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Set;

/**
 * Authentication provider for Microsoft Graph API.
 */
public class MSGraphAuthenticator implements IAuthenticationProvider {

  private static final Logger LOG = LoggerFactory.getLogger(MSGraphAuthenticator.class);

  private static final String TENANT_AUTHORITY_URI = "https://login.microsoftonline.com/%s/";
  private static final String DEFAULT_AUTHORITY = "common";
  private static final Set<String> DEFAULT_SCOPES = Set.of("https://graph.microsoft.com/.default");

  private String clientId;
  private String clientSecret;
  private String authority;
  private Set<String> scopes;
  private IAuthenticationResult authResult;

  public MSGraphAuthenticator(@NonNull String clientId, @NonNull String clientSecret, String tenantId) {
    this(clientId, clientSecret,
            String.format(TENANT_AUTHORITY_URI, StringUtils.isNotBlank(tenantId) ? tenantId : DEFAULT_AUTHORITY),
            DEFAULT_SCOPES);
  }

  public MSGraphAuthenticator(String clientId, String clientSecret, String tenantId, Set<String> scopes) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.authority = tenantId;
    this.scopes = scopes;
  }

  public String getAccessToken() {
    String accessToken = null;

    Date now = new Date();
    if (authResult == null || now.after(authResult.expiresOnDate())) {
      try {
        authResult = acquireToken();
      } catch (Exception e) {
        LOG.error("Cannot acquire access token.", e);
      }
    }

    if (authResult != null) {
      accessToken = authResult.accessToken();
    }

    return accessToken;
  }

  private IAuthenticationResult acquireToken() throws Exception {
    LOG.debug("Acquiring access token ...");
    IClientCredential credential = ClientCredentialFactory.createFromSecret(clientSecret);
    ConfidentialClientApplication cca = ConfidentialClientApplication
            .builder(clientId, credential)
            .authority(authority)
            .build();

    IAuthenticationResult result;
    try {
      SilentParameters silentParameters = SilentParameters.builder(scopes).build();
      result = cca.acquireTokenSilently(silentParameters).join();
    } catch (Exception ex) {
      if (ex.getCause() instanceof MsalException) {
        ClientCredentialParameters parameters = ClientCredentialParameters.builder(scopes).build();

        // Try to acquire a token.
        result = cca.acquireToken(parameters).join();
      } else {
        // Handle other exceptions accordingly
        throw ex;
      }
    }

    LOG.debug("Acquired access token: {}", result);
    return result;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public String getAuthority() {
    return authority;
  }

  public void setAuthority(String authority) {
    this.authority = authority;
  }

  public Set<String> getScopes() {
    return scopes;
  }

  public void setScopes(Set<String> scopes) {
    this.scopes = scopes;
  }

  @Override
  public void authenticateRequest(IHttpRequest request) {
    // Add the access token in the Authorization header
    LOG.debug("Authenticating request {}", request.getRequestUrl());
    request.addHeader("Authorization", "Bearer " + getAccessToken());
  }
}
