package com.coremedia.labs.plugins.adapters.onedrive.service;

import com.microsoft.graph.models.extensions.Drive;
import com.microsoft.graph.models.extensions.DriveItem;
import com.microsoft.graph.models.extensions.Site;
import com.microsoft.graph.models.extensions.ThumbnailSet;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class OneDriveServiceIT {

  private OneDriveService testling;

  private static final String CLIENT_ID = "<CLIENT ID>";
  private static final String CLIENT_SECRET = "<SECRET>";
  private static final String TENANT_ID = "<TENANT ID>";
  private static final String SHARE_POINT_SITE = "cmpresales.sharepoint.com:/sites/Sample";
  private static final String SHARE_POINT_SITE_ID = "cmpresales.sharepoint.com,21c06144-5038-4ba3-8b99-115b661f9d09,4393c333-c93a-45e5-aaf7-39cf66c1de1d";
  private static final String DRIVE_ID = "b!RGHAIThQo0uLmRFbZh-dCTPDk0M6yeVFqvc5z2bB3h15UA6_MMe4T46v9u3_EE7S";

  // Example asset:
  // https://cmpresales.sharepoint.com/sites/Sample/Shared%20Documents/Assets/Summer%20Accessories%202.jpeg
  private static final String DRIVE_ITEM_ID = "01CL5ITZYQXUNCRR266VFJ7GYKWP22GNWF";

  @Before
  public void setUp() {
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
