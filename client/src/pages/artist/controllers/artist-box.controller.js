/**
 * Artist box controller
 */
export default class artistBoxCtrl {
  /*@ngInject*/

  /**
   * @constructor
   * @this {vm}
   * @param {ui.router.state.$state} $state
   * @param {authentication} authentication
   */
  constructor($state, authentication) {
    this.$state = $state;
    this.authentication = authentication;
  }

  isLibraryPage () {
    return this.$state.is('msl.library');
  }
}
