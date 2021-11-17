/** @type { import('@jangaroo/core').IJangarooConfig } */
module.exports = {
  type: "code",
  extName: "com.coremedia.labs.plugins__studio-client.content-hub-adapter-onedrive",
  extNamespace: "com.coremedia.labs.plugins.adapters.onedrive.client",
  sencha: {
    name: "studio-client",
    version: "1.0.0-SNAPSHOT",
    private: true,
    engines: {
      node: ">=16",
      pnpm: ">=6.14.6",
    },
    devDependencies: {
      "@coremedia/set-version": "^1.1.1",
      "@typescript-eslint/eslint-plugin": "4.31.2",
      "@typescript-eslint/parser": "4.31.2",
      eslint: "7.27.0",
      "eslint-plugin-import": "2.23.4",
      "eslint-plugin-jsdoc": "35.1.2",
      "eslint-plugin-unused-imports": "1.1.4",
    },
    scripts: {
      "set-version": "set-version",
    },
  },
  command: {
    build: {
      ignoreTypeErrors: true
    },
  },
};
