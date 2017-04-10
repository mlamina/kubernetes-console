import Backbone from "backbone";
import * as _ from 'underscore';
import PodResultView from "./results/pod"
import K8Resource from "../models/k8resource"
import $ from "jquery";

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
    this.$el.html( this.template());
    if (this.model.has('result') || this.model.has('errors'))
      this.$('li').removeClass('loading');
    if (this.model.has('errors')) {
      this.$('li').addClass('error');
      let errorList = $('<ul class="command-execution-errors"></ul>');
      _.each(this.model.get('errors'), (error) => {
        let errorElement = $('<li></li>');
        errorElement.text(error.message);
        errorList.append(errorElement);
      });
      this.$('li').append(errorList);
    } else {
      this.$('li').addClass('success');
      let resultList = this.$('.command-execution-results');
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
}

export default CommandExecutionView;