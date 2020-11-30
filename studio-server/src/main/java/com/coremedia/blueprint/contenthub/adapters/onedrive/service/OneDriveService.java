package com.coremedia.blueprint.contenthub.adapters.onedrive.service;

import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.models.extensions.Drive;
import com.microsoft.graph.models.extensions.DriveItem;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.Site;
import com.microsoft.graph.models.extensions.ThumbnailSet;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.graph.requests.extensions.IDriveCollectionPage;
import com.microsoft.graph.requests.extensions.IDriveItemCollectionPage;
import com.microsoft.graph.requests.extensions.ISiteCollectionPage;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Service
public class OneDriveService {

  private static final Logger LOG = LoggerFactory.getLogger(OneDriveService.class);

  private String clientId;
  private String clientSecret;
  private String tenantId;

  private MSGraphAuthenticator authenticator;
  private IGraphServiceClient graphClient;


  public OneDriveService(String clientId, String clientSecret, String tenantId) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.tenantId = tenantId;

    authenticator = new MSGraphAuthenticator(clientId, clientSecret, tenantId);

    DefaultLogger logger = new DefaultLogger();
    logger.setLoggingLevel(LoggerLevel.DEBUG);
    graphClient = GraphServiceClient.builder()
            .authenticationProvider(authenticator)
            .logger(logger)
            .buildClient();
  }

  /**
   * Get the access token from the configured authenticator.
   *
   * @return access token or <code>null</code> if no token can be acquired.
   */
  public String getAccessToken() {
    return authenticator.getAccessToken();
  }


  // --- Sites ---

  /**
   * List all {@link Site}s on the configured organization.
   *
   * @return
   */
  public List<Site> getSites() {
    LOG.info("Fetching Sites.");
    ISiteCollectionPage sites = graphClient.sites()
            .buildRequest().get();
    return sites.getCurrentPage();
  }

  /**
   * Get a {@link Site} by it's site id.
   *
   * @param siteId site id
   * @return the {@link Site} or <code>null</code> if no site with the given id was found.
   */
  @Nullable
  public Site getSite(String siteId) {
    LOG.info("Fetching Site '{}'.", siteId);
    return graphClient.sites(siteId).buildRequest().get();
  }


  // --- Drives ---

  /**
   * List all {@link Drive}s for the given site id.
   *
   * @param siteId site id
   * @return list of {@link Drive}s for given site.
   */
  public List<Drive> getDrives(String siteId) {
    LOG.info("Fetching Drives for SharePoint site '{}'.", siteId);
    IDriveCollectionPage drives = graphClient.sites(siteId).drives()
            .buildRequest()
            .get();
    return drives.getCurrentPage();
  }

  /**
   * List all {@link Site}s for the given site name.
   *
   * @param siteName site name used for lookup
   * @return list of {@link Drive}s for given site.
   */
  public List<Drive> getDrivesForSiteName(String siteName) {
    LOG.info("Fetching Drives for SharePoint site '{}'.", siteName);
    Site site = getSite(siteName);
    if (site != null) {
      return getDrives(site.id);
    }
    return Collections.emptyList();
  }

  /**
   * Get a {@link Drive} by it's id.
   * @param driveId drive id
   * @return {@link Drive} or <code>null</code> if no drive with given id was found.
   */
  @Nullable
  public Drive getDrive(String driveId) {
    LOG.info("Fetching Drive drive-id={}", driveId);
    return graphClient.drives(driveId).buildRequest().get();
  }


  // --- DriveItems ---

  /**
   * Get the root {@link DriveItem} for the given drive id.
   *
   * @param driveId drive id
   * @return
   */
  public DriveItem getRootItem(String driveId) {
    LOG.info("Fetching root DriveItem for drive {}.", driveId);
    return graphClient.drives(driveId).root().buildRequest().get();
  }

  /**
   * Get a {@link DriveItem} from the given drive by it's id.
   * @param driveId drive id
   * @param itemId item id
   * @return {@link DriveItem} or <code>null</code> if no item with the given id was found.
   */
  @Nullable
  public DriveItem getItem(String driveId, String itemId) {
    LOG.info("Fetching DriveItem {} from drive {}.", itemId, driveId);
    return graphClient.drives(driveId).items(itemId).buildRequest().get();
  }

  /**
   * Get child {@link DriveItem}s for the given parent in the specified drive.
   *
   * @param driveId drive id
   * @param parent parent {@link DriveItem}
   * @return list of child {@link DriveItem}s
   */
  public List<DriveItem> getChildren(String driveId, DriveItem parent) {
    try {
      LOG.info("Fetching child DriveItems for {} (id={}). (drive-id={})", parent.name, parent.id, driveId);
      IDriveItemCollectionPage children = graphClient.drives(driveId).items(parent.id).children().buildRequest().get();
      return children.getCurrentPage();
    } catch (ClientException ce) {
      LOG.error("Error during fetch.", ce);
    }

    return Collections.emptyList();
  }

  /**
   * Get an {@link InputStream} for the given {@link DriveItem}.
   *
   * @param item {@link DriveItem}
   * @return {@link InputStream} or <code>null</code>
   */
  @Nullable
  public InputStream getDownloadStream(@NonNull DriveItem item) {
    String driveId = parseDriveId(item);
    if (driveId == null) {
      LOG.error("Cannot parse drive id from item {}.", item.id);
      return null;
    }

    LOG.info("Retrieving download stream for drive-id={}, item-id={}", driveId, item.id);
    return graphClient.drives(driveId).items(item.id).content().buildRequest().get();
  }

  /**
   * Get a {@link ThumbnailSet} for the given {@link DriveItem}.
   *
   * @param driveItem {@link DriveItem}
   * @return {@link ThumbnailSet}
   */
  public ThumbnailSet getThumbnailSet(@NonNull DriveItem driveItem) {
    return graphClient.drives(parseDriveId(driveItem))
            .items(driveItem.id)
            .thumbnails("0")
            .buildRequest().get();
  }

  // --- private ---

  private String parseDriveId(DriveItem item) {
    return item.parentReference.driveId;
  }

}
