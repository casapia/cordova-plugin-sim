module.exports = {
  /*
    * Get the SIM card information
    * Example: cordova.plugins.sim.getSimInfo(
    * function(info) { console.log('Sim info: ', info); }, 
    * function(err) { console.log('Error: ', err); });
    */
  getSimInfo: function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'Sim', 'getSimInfo', []);
  }
};
