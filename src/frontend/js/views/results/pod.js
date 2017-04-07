import Backbone from "backbone";
import * as _ from 'underscore';

class PodResultView extends Backbone.View {

  initialize() {
    this.template = _.template(`
      <div class="result result-pod">
        <span class="result-title"><%= this.model.get('metadata').name %></span>
      </div>
    `);

    this.listenTo(this.model, 'change', this.render);
  }

  render() {
    this.$el.html( this.template({}));
  }
}

export default PodResultView;