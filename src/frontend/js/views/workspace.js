import Backbone from "backbone";
import * as _ from 'underscore';

class WorkspaceView extends Backbone.View {


  initialize() {
    this.template = _.template('<div class="pure-g" id="workspace"></div>');
  }

  render() {
    this.$el.html(this.template({}));
  }
}

export default WorkspaceView;