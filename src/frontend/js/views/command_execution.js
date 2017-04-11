import Backbone from "backbone";
import * as _ from 'underscore';
import PodResultView from "./results/pod"
import TableResultView from "./results/table"
import K8ResourceList from "../models/k8resource_list"
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
    } else if (this.model.has('result')) {
      this.$('li').addClass('success');
      let resultView = this.$('.command-execution-results');
      let result = this.model.get('result');
      // Create specific view depending on result type
      switch (result.meta.dataType) {
        case "List":
          this.renderList(resultView, result);
          break;
        default:
          console.error("Can't find view for resource: " + result.kind);
      }
    }
  }

  renderList(parent, result) {
    let resultView = new TableResultView({ model: new K8ResourceList({
      type: result.meta.listType,
      items: result.data
    }) });
    resultView.render();
    parent.append(resultView.$el.html());
  }
}

export default CommandExecutionView;