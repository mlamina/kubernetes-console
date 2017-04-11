import Backbone from "backbone";
import * as _ from 'underscore';
import PodTableRow from './pod_row';
import DeploymentTableRow from './deployment_row';
import K8Resource from '../../models/k8resource';

class TableResultView extends Backbone.View {

  initialize() {
    this.template = _.template(`
      <table class="result-table">
        <thead>
          <tr class="result-table-header">
          <% _.each(headers, function(header){ %>
          <th><%= header %></th>
          <%}); %>
        </tr>
        </thead>
        <tbody>
        </tbody>
        
        
      </table>
    `);

    this.listenTo(this.model, 'change', this.render);
  }

  render() {
    let headers = [];
    switch (this.model.get('type')) {
      case 'Pod':
        headers = ['name', 'ready', 'status', 'restarts'];
        break;
      case 'Deployment':
        headers = ['name'];
        break;
    }
    this.$el.html( this.template({ headers: headers }));
    let table = this.$('tbody');
    _.each(this.model.get('items'), (item) => {
      let row;
      switch (this.model.get('type')) {
        case 'Pod':
          row = new PodTableRow({ model: new K8Resource(item) });
          break;
        case 'Deployment':
          row = new DeploymentTableRow({ model: new K8Resource(item) });
          break;
      }
      row.render();
      table.append(row.$el);
    });
  }
}

export default TableResultView;