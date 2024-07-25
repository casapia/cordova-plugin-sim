module.exports = {
  getSimInfoLite: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'Sim', 'getSimInfoLite', []);
  },
  hasReadPermission: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'Sim', 'hasReadPermission', []);
  },
  requestReadPermission: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'Sim', 'requestReadPermission', []);
  },
  shouldShowRequestPermissionRationale: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'Sim', 'shouldShowRequestPermissionRationale', []);
  }
};
