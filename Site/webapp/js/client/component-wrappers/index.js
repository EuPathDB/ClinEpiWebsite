import { displayName } from 'eupathdb/wdkCustomization/js/client/config';
import Index from '../components/Index';

export default {
  IndexController: WdkIndexController => class IndexController extends WdkIndexController {

    getTitle() {
      return displayName;
    }

    renderView() {
      return (
        <Index displayName={displayName} />
      )
    }

  }
}
