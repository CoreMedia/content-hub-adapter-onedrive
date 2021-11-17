import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";

/**
 * Interface values for ResourceBundle "ContentHubOneDrive".
 * @see ContentHubOneDrive_properties#INSTANCE
 */
interface ContentHubOneDrive_properties {

/**
 * Adapter
 */
  adapter_type_onedrive_name: string;
  adapter_type_onedrive_icon: string;
  item_type_video_name: string;
  item_type_video_icon: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "ContentHubOneDrive".
 * @see ContentHubOneDrive_properties
 */
const ContentHubOneDrive_properties: ContentHubOneDrive_properties = {
  adapter_type_onedrive_name: "OneDrive",
  adapter_type_onedrive_icon: CoreIcons_properties.type_video,
  item_type_video_name: "Video",
  item_type_video_icon: CoreIcons_properties.type_video,
};

export default ContentHubOneDrive_properties;
