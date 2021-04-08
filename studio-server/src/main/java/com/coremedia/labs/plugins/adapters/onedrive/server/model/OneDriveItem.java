package com.coremedia.labs.plugins.adapters.onedrive.server.model;

import com.coremedia.contenthub.api.BaseFileSystemItem;
import com.coremedia.contenthub.api.ContentHubBlob;
import com.coremedia.contenthub.api.ContentHubDefaultBlob;
import com.coremedia.contenthub.api.ContentHubMimeTypeService;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubType;
import com.coremedia.contenthub.api.Item;
import com.coremedia.contenthub.api.UrlBlobBuilder;
import com.coremedia.contenthub.api.preview.DetailsElement;
import com.coremedia.contenthub.api.preview.DetailsSection;
import com.coremedia.labs.plugins.adapters.onedrive.server.service.OneDriveService;
import com.microsoft.graph.models.extensions.DriveItem;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.io.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OneDriveItem extends BaseFileSystemItem implements Item, DriveItemAdapter {

  public static final String CLASSIFIER_PREVIEW = "preview";
  private static final int BLOB_SIZE_LIMIT = 10000000;
  private final DriveItem delegate;
  private final OneDriveService oneDriveService;
  private final ContentHubMimeTypeService mimeTypeService;

  public OneDriveItem(ContentHubObjectId id, DriveItem driveItem,
                      OneDriveService oneDriveService,
                      ContentHubMimeTypeService mimeTypeService,
                      Map<ContentHubType, String> itemTypeToContentTypeMapping) {
    super(id, driveItem.name, mimeTypeService, itemTypeToContentTypeMapping);
    this.delegate = driveItem;
    this.oneDriveService = oneDriveService;
    this.mimeTypeService = mimeTypeService;
  }

  @Nullable
  @Override
  public String getDescription() {
    return delegate.description;
  }

  @Override
  public DriveItem getDriveItem() {
    return delegate;
  }

  @NonNull
  @Override
  public List<DetailsSection> getDetails() {
    ContentHubBlob previewBlob = getPreviewBlob();
    boolean showPicture = previewBlob != null && previewBlob.getLength() < BLOB_SIZE_LIMIT;

    List<DetailsElement<?>> metadataElements = new ArrayList<>();
    metadataElements.add(new DetailsElement<>("id", getDriveItem().id));
    metadataElements.add(new DetailsElement<>("driveId", getDriveItem().parentReference.driveId));

    if (getDriveItem().image != null) {
      metadataElements.add(new DetailsElement<>("dimensions", String.format("%dx%d", getDriveItem().image.width, getDriveItem().image.height)));
    }

    metadataElements.add(new DetailsElement<>("size", FileUtils.byteCountToDisplaySize(getDriveItem().size)));
    metadataElements.add(new DetailsElement<>("lastModified", getDriveItem().lastModifiedDateTime));
    metadataElements.add(new DetailsElement<>("createdAt", getDriveItem().createdDateTime));

    if (getDriveItem().createdByUser != null) {
      metadataElements.add(new DetailsElement<>("createdBy", getDriveItem().createdByUser.displayName));
    }


    return List.of(
            // Details
            new DetailsSection("main", List.of(
                    new DetailsElement<>(getName(), false, showPicture ? previewBlob : SHOW_TYPE_ICON)
            ), false, false, false),

            // Metadata
            new DetailsSection("metadata", metadataElements)
    );
  }

  @Nullable
  @Override
  public ContentHubBlob getBlob(String classifier) {
    if (CLASSIFIER_PREVIEW.equals(classifier)) {
      return getPreviewBlob();
    }

    ContentHubBlob blob = new ContentHubDefaultBlob(
            this,
            classifier,
            mimeTypeService.mimeTypeForResourceName(getName()),
            getDriveItem().size,
            () -> oneDriveService.getDownloadStream(getDriveItem()),
            getDriveItem().eTag);
    return blob;
  }

  @Nullable
  public ContentHubBlob getPreviewBlob() {
    return Optional.ofNullable(oneDriveService.getThumbnailSet(getDriveItem()))
            .map(t -> t.large.url)
            .map(thumbUrl -> new UrlBlobBuilder(this, CLASSIFIER_PREVIEW)
                    .withUrl(thumbUrl)
                    .build())
            .orElse(null);
  }

}
