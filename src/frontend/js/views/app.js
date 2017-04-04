import Backbone from "backbone";
import * as _ from 'underscore';
import $ from "jquery";

class AppView extends Backbone.View {

  initialize() {
    this.setElement($('#k8console'), true);
    this.template =
      _.template(`
        <div class="pure-g" id="workspace">
        </div>
        <span id="console-header">
          $
        </span>
        <form id="console-form" target="#">
          <input type="text" id="console" />
          <input type="submit" value="Go">
        </form>
    `);
    this.render();
  }

  render() {
    this.$el.html( this.template({}));
  }
}

export default AppView;