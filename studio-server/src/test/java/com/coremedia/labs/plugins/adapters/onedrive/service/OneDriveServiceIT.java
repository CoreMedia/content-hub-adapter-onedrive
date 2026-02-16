package com.coremedia.labs.plugins.adapters.onedrive.service;

import com.microsoft.graph.models.extensions.Drive;
import com.microsoft.graph.models.extensions.DriveItem;
import com.microsoft.graph.models.extensions.Site;
import com.microsoft.graph.models.extensions.ThumbnailSet;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

public class OneDriveServiceIT {

  private OneDriveService testling;

  private static String CLIENT_ID;
  private static String CLIENT_SECRET;
  private static String TENANT_ID;
  private static String SHARE_POINT_SITE;
  private static String SHARE_POINT_SITE_ID;
  private static String DRIVE_ID;
  private static String DRIVE_ITEM_ID;

  private static boolean configurationAvailable = false;

  @BeforeClass
  public static void setUpClass() {
    Properties properties = new Properties();

    // Try to load from properties file first
    try (InputStream input = OneDriveServiceIT.class.getClassLoader()
            .getResourceAsStream("onedrive-test.properties")) {
      if (input != null) {
        properties.load(input);
      }
    } catch (IOException ex) {
      System.err.println("Warning: Could not load onedrive-test.properties: " + ex.getMessage());
    }

    // Override with system properties if provided (e.g., from Maven or IDE)
    CLIENT_ID = System.getProperty("onedrive.client.id",
                                     properties.getProperty("onedrive.client.id"));
    CLIENT_SECRET = System.getProperty("onedrive.client.secret",
                                        properties.getProperty("onedrive.client.secret"));
    TENANT_ID = System.getProperty("onedrive.tenant.id",
                                    properties.getProperty("onedrive.tenant.id"));
    SHARE_POINT_SITE = System.getProperty("onedrive.sharepoint.site",
                                           properties.getProperty("onedrive.sharepoint.site"));
    SHARE_POINT_SITE_ID = System.getProperty("onedrive.sharepoint.site.id",
                                              properties.getProperty("onedrive.sharepoint.site.id"));
    DRIVE_ID = System.getProperty("onedrive.drive.id",
                                   properties.getProperty("onedrive.drive.id"));
    DRIVE_ITEM_ID = System.getProperty("onedrive.drive.item.id",
                                        properties.getProperty("onedrive.drive.item.id"));

    // Check if all required properties are configured
    configurationAvailable = CLIENT_ID != null && !CLIENT_ID.isEmpty()
                          && CLIENT_SECRET != null && !CLIENT_SECRET.isEmpty()
                          && TENANT_ID != null && !TENANT_ID.isEmpty();

    if (!configurationAvailable) {
      System.err.println("=============================================================");
      System.err.println("OneDrive integration tests are SKIPPED!");
      System.err.println("To run these tests, either:");
      System.err.println("1. Create src/test/resources/onedrive-test.properties");
      System.err.println("   (use onedrive-test.properties.template as template)");
      System.err.println("2. Pass system properties via Maven:");
      System.err.println("   mvn test -Donedrive.client.id=<id> -Donedrive.client.secret=<secret> ...");
      System.err.println("=============================================================");
    }
  }

  @Before
  public void setUp() {
    // Skip test if configuration is not available
    assumeTrue("OneDrive test configuration is not available", configurationAvailable);
    testling = new OneDriveService(CLIENT_ID, CLIENT_SECRET, TENANT_ID);
  }

  @Test
  public void testGetAccessToken() throws Exception {
    String accessToken = testling.getAccessToken();
    assertNotNull(accessToken);
  }

  @Test
  public void testGetSites() throws Exception {
    List<Site> sites = testling.getSites();
    assertNotNull(sites);
  }

  @Test
  public void testGetSiteByName() throws Exception {
    Site site = testling.getSite(SHARE_POINT_SITE);
    assertNotNull(site);
  }

  @Test
  public void testGetDrives() throws Exception {
    // Get drives for given site id
    List<Drive> drives = testling.getDrives(SHARE_POINT_SITE_ID);
    assertNotNull(drives);
  }

  @Test
  public void testGetDrivesForSiteByName() throws Exception {
    List<Drive> drives = testling.getDrivesForSiteName(SHARE_POINT_SITE);
    assertNotNull(drives);
  }

  @Test
  public void testGetItem() throws Exception {
    DriveItem item = testling.getItem(DRIVE_ID, DRIVE_ITEM_ID);
    assertNotNull(item);
  }

  @Test
  public void testGetDownloadStream() throws Exception {
    DriveItem item = testling.getItem(DRIVE_ID, DRIVE_ITEM_ID);
    InputStream downloadStream = testling.getDownloadStream(item);
    assertNotNull(downloadStream);
  }

  @Test
  public void testGetThumbnailUrl() throws Exception {
    DriveItem item = testling.getItem(DRIVE_ID, DRIVE_ITEM_ID);
    ThumbnailSet thumbnailSet = testling.getThumbnailSet(item);
    assertNotNull(thumbnailSet);
    assertNotNull(thumbnailSet.large.url);
  }

}
