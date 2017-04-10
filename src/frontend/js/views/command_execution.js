import Backbone from "backbone";
import * as _ from 'underscore';
import PodResultView from "./results/pod"
import K8Resource from "../models/k8resource"

class CommandExecutionView extends Backbone.View {

  initialize() {
    this.template = _.template(`
      <li class="loading">
      <span class="command-execution-header"><%= this.model.get('command').get('raw') %></span>
      <div class="command-execution-results"></div>  
      </li>
    `);

    this.listenTo(this.model, 'change', this.render);
  }

  render() {
    this.$el.html( this.template({}));
    let resultList = this.$('.command-execution-results');
    if (this.model.has('result'))
      this.$('li').removeClass('loading');
    _.each(this.model.get('result'), (result) => {
      // Create specific result view depending on resource type
      var resultView;
      switch (result.kind) {
        case "Pod":
          resultView = new PodResultView({ model: new K8Resource(result) });
          break;
        default:
          console.error("Can't find view for resource: " + result.kind);
      }
      resultView.render();
      resultList.append(resultView.$el.html());
    });

  }
}

export default CommandExecutionView;