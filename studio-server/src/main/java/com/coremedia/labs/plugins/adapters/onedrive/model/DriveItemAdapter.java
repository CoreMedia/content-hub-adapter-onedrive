package com.coremedia.labs.plugins.adapters.onedrive.model;

import com.microsoft.graph.models.extensions.DriveItem;

/**
 * Delegate interface for classes working on {@link DriveItem}s.
 */
public interface DriveItemAdapter {

  DriveItem getDriveItem();

}
