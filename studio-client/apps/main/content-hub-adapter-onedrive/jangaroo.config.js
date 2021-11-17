/** @type { import('@jangaroo/core').IJangarooConfig } */
module.exports = {
  type: "code",
  sencha: {
    name: "com.coremedia.labs.plugins__studio-client.content-hub-adapter-onedrive",
    namespace: "com.coremedia.labs.plugins.adapters.onedrive",
    studioPlugins: [
      {
        mainClass: "com.coremedia.labs.plugins.adapters.onedrive.ContentHubOneDriveStudioPlugin",
        name: "Content Hub - OneDrive",
      },
    ],
  },
  command: {
    build: {
      ignoreTypeErrors: true
    },
  },
};
