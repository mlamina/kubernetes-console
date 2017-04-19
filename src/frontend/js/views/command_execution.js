import Backbone from "backbone";
import * as _ from 'underscore';
import LogResultView from "./results/logs"
import TableResultView from "./results/table"
import PodResultView from "./results/pod"
import ErrorResultView from "./results/error"
import K8ResourceList from "../models/k8resource_list"
import K8Resource from "../models/k8resource"
import $ from "jquery";

class CommandExecutionView extends Backbone.View {

  initialize() {
    this.template = _.template(`
      <li class="loading command-execution-item">
      <span class="command-execution-header">
        <%= this.model.get('command').get('raw') %>
      </span>
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
      let errorList = new ErrorResultView({ model: new Backbone.Model({ errors: this.model.get('errors') })});
      errorList.render();
      this.$('li').append(errorList.$el);
    } else if (this.model.has('result')) {
      this.$('li').addClass('success');
      let resultList = this.$('.command-execution-results');
      let result = this.model.get('result');
      // Create specific view depending on result type
      let resultView;
      switch (result.meta.dataType) {
        case "List":
          resultView = buildListView(result.meta.listType, result.data);
          break;
        case "Pod":
          resultView = new PodResultView({ model: new K8Resource(result.data)});
          break;
        default:
          this.$('li').addClass('error').removeClass('success');
          resultView = new ErrorResultView({
            model: new Backbone.Model({ errors: ["View for " + result.meta.dataType + " not implemented yet"] })
          });
      }
      resultView.render();
      resultList.append(resultView.$el.html());
    }
  }
}

function buildListView(listType, data) {
  if (listType === 'Logs') {
    return new LogResultView({ model: new Backbone.Collection(data) })
  } else {
    return new TableResultView({
      model: new K8ResourceList({ type: listType, items: data })
    });
  }
}

export default CommandExecutionView;