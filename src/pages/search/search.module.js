//Styles
import './stylesheets/default.less';

//Dependencies
import angular from 'angular';
import uiRouter from 'angular-ui-router';

import searchPageRoute from './search.route.js';
import searchCtrl from './controllers/search.controller.js';

export default angular.module('msl.containers.default.search', [uiRouter])
  .config(searchPageRoute)
  .controller('searchCtrl', searchCtrl)
  .name;