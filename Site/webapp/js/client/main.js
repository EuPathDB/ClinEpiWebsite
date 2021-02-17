import { initialize } from '@veupathdb/web-common/lib/bootstrap';

import '@veupathdb/web-common/lib/styles/client.scss';

import componentWrappers from './component-wrappers';
import wrapStoreModules from './wrapStoreModules';
import { wrapRoutes } from './routes';
import { reduxMiddleware } from '@veupathdb/web-common/lib/App/DataRestriction/DataRestrictionUtils'

import 'site/css/ClinEpiSite.scss';

initialize({
  componentWrappers,
  wrapStoreModules,
  wrapRoutes,
  additionalMiddleware: [ reduxMiddleware ]
});
