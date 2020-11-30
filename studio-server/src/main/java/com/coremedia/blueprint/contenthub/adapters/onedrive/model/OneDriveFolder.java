package com.coremedia.blueprint.contenthub.adapters.onedrive.model;

import com.coremedia.contenthub.api.BaseFileSystemHubObject;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.Folder;
import com.microsoft.graph.models.extensions.DriveItem;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;

public class OneDriveFolder extends BaseFileSystemHubObject implements Folder, DriveItemAdapter {

  private final DriveItem delegate;

  public OneDriveFolder(@NonNull ContentHubObjectId hubId, @NonNull DriveItem driveItem) {
    this(hubId, driveItem, driveItem.name);
  }

  public OneDriveFolder(@NonNull ContentHubObjectId hubId, @NonNull DriveItem driveItem, String displayName) {
    super(hubId, StringUtils.isNotBlank(displayName) ? displayName : driveItem.name);
    this.delegate = driveItem;
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
}
