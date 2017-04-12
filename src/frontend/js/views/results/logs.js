import Backbone from "backbone";
import * as _ from 'underscore';
import ProgressBarView from '../progress_bar'
import $ from 'jquery';
class LogsResultView extends Backbone.View {

  initialize() {
    this.template = _.template(`
      
      <div class="result-logs">
        <ul class="result-logs-container-select"></ul>
        <div class="result-logs-data"><%= data %></div>
      </div>
    `);
  }

  render() {
    let logs = this.model.toArray()[0].get('logs');
    let html = logs.replace(new RegExp('\n', 'g'), '<br>');
    this.$el.html( this.template({ data: html }));
    let tabList = this.$('.result-logs-container-select');
    this.model.each((logContainer, index) => {
      let tab = $('<li></li>').text(logContainer.get('container'));
      if (index === 0)
        tab.addClass('selected');
      tabList.append(tab);
    });
  }
}

export default LogsResultView;