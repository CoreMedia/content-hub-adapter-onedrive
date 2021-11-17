import Config from "@jangaroo/runtime/Config";
import ContentHubOneDrive_properties from "./ContentHubOneDrive_properties";
import ContentHub_properties from "@coremedia/studio-client.main.content-hub-editor-components/ContentHub_properties";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
interface ContentHubOneDriveStudioPluginConfig extends Config<StudioPlugin> {
}



    class ContentHubOneDriveStudioPlugin extends StudioPlugin{
  declare Config: ContentHubOneDriveStudioPluginConfig;

  static readonly xtype:string = "com.coremedia.blueprint.studio.contenthub.onedrive.ContentHubOneDriveStudioPlugin";

  constructor(config:Config<ContentHubOneDriveStudioPlugin> = null){
    super( ConfigUtils.apply(Config(ContentHubOneDriveStudioPlugin, {

  configuration:[
    new CopyResourceBundleProperties({
            destination: resourceManager.getResourceBundle(null,ContentHub_properties),
            source: resourceManager.getResourceBundle(null,ContentHubOneDrive_properties)})
  ]

}),config));
  }}
export default ContentHubOneDriveStudioPlugin;
