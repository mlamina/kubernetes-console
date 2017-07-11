import Backbone from "backbone";
import * as _ from 'underscore';
import $ from 'jquery';

class BashOutputView extends Backbone.View {

  initialize() {
    this.template = _.template(`
      <div class="result-logs">
        <div class="result-logs-data"><%= data %></div>
      </div>
      
    `);

    this.listenTo(this.model, 'change', this.render);
  }

  render() {
    let output = this.model.get('output');
    let html = output.replace(new RegExp('\n', 'g'), '<br>');
    this.$el.html( this.template({ data: html }));
  }
}

export default BashOutputView;