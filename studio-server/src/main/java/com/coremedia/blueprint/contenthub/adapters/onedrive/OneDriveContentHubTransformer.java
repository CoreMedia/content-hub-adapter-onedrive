package com.coremedia.blueprint.contenthub.adapters.onedrive;

import com.coremedia.blueprint.contenthub.adapters.onedrive.model.OneDriveItem;
import com.coremedia.blueprint.contenthub.adapters.onedrive.service.OneDriveService;
import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubBlob;
import com.coremedia.contenthub.api.ContentHubContentCreationException;
import com.coremedia.contenthub.api.ContentHubContext;
import com.coremedia.contenthub.api.ContentHubMimeTypeService;
import com.coremedia.contenthub.api.ContentHubTransformer;
import com.coremedia.contenthub.api.ContentModel;
import com.coremedia.contenthub.api.Item;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OneDriveContentHubTransformer implements ContentHubTransformer {

  private static final Logger LOG = LoggerFactory.getLogger(OneDriveContentHubTransformer.class);

  private OneDriveService oneDriveService;
  private final ContentHubMimeTypeService contentHubMimeTypeService;

  public OneDriveContentHubTransformer(OneDriveService oneDriveService, ContentHubMimeTypeService contentHubMimeTypeService) {
    this.oneDriveService = oneDriveService;
    this.contentHubMimeTypeService = contentHubMimeTypeService;
  }

  @Nullable
  @Override
  public ContentModel transform(Item item, ContentHubAdapter contentHubAdapter, ContentHubContext contentHubContext) throws ContentHubContentCreationException {
    if (!(item instanceof OneDriveItem)) {
      throw new IllegalArgumentException("Cannot transform item " + item.getClass());
    }
    return transformItem((OneDriveItem) item);
  }

  private ContentModel transformItem(OneDriveItem item) {
    LOG.debug("Transforming item {}", item);
    String contentName = FilenameUtils.removeExtension(item.getName());

    ContentModel model = ContentModel.createContentModel(
            contentName,
            item.getId(),
            item.getCoreMediaContentType());
    model.put("title", item.getName());

    ContentHubBlob fileBlob = item.getBlob("file");
    if (fileBlob != null) {
      model.put("data", fileBlob);
    }

    return model;
  }
}
