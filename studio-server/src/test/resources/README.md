# OneDrive Integration Test Configuration

## Setup

To run the OneDrive integration tests, you need to provide valid Azure AD and OneDrive credentials.

### Option 1: Using a properties file (recommended for local development)

1. Copy the template file:
   ```bash
   cp onedrive-test.properties.template onedrive-test.properties
   ```

2. Edit `onedrive-test.properties` and fill in your actual values:
   - `onedrive.client.id`: Your Azure AD application client ID
   - `onedrive.client.secret`: Your Azure AD application client secret
   - `onedrive.tenant.id`: Your Azure AD tenant ID
   - `onedrive.sharepoint.site`: SharePoint site URL (optional for some tests)
   - `onedrive.sharepoint.site.id`: SharePoint site ID (optional for some tests)
   - `onedrive.drive.id`: OneDrive/SharePoint drive ID for testing
   - `onedrive.drive.item.id`: A test drive item ID for testing file operations

3. The `onedrive-test.properties` file is ignored by git to prevent accidental commits of credentials.

### Option 2: Using Maven system properties (recommended for CI/CD)

Pass the properties via command line:

```bash
mvn test -Donedrive.client.id=YOUR_CLIENT_ID \
         -Donedrive.client.secret=YOUR_CLIENT_SECRET \
         -Donedrive.tenant.id=YOUR_TENANT_ID \
         -Donedrive.sharepoint.site=YOUR_SITE \
         -Donedrive.sharepoint.site.id=YOUR_SITE_ID \
         -Donedrive.drive.id=YOUR_DRIVE_ID \
         -Donedrive.drive.item.id=YOUR_ITEM_ID
```

### Option 3: Using IDE configuration

Configure your IDE (IntelliJ IDEA, Eclipse, etc.) to pass system properties when running tests.

## Behavior when configuration is missing

If the required configuration properties are not provided, the integration tests will be **automatically skipped** with a clear message explaining how to configure them. This allows the build to succeed even without OneDrive credentials.

## Security Note

⚠️ **NEVER commit the `onedrive-test.properties` file or hardcode credentials in the test files!**

Always use one of the configuration methods described above to keep your credentials secure.

