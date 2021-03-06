import Backbone from "backbone";
import * as _ from 'underscore';
import PodResultView from "./results/pod"
import TableResultView from "./results/table"
import K8ResourceList from "../models/k8resource_list"
import $ from "jquery";

class ProgressBarView extends Backbone.View {

  initialize() {
    this.template = _.template(`
      <div class="progress-bar">
      <div class="progress-indicator" style="width: <%= percentage %>%"></div>
      </div>
    `);

    this.listenTo(this.model, 'change', this.render);
  }

  render() {
    this.$el.html( this.template({
      percentage: (this.model.get('current') / this.model.get('maximum')) * 100
    }));
    if (this.model.get('current') === 0)
      this.$('.progress-bar').addClass('error');
  }

}

export default ProgressBarView;