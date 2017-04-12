import Backbone from "backbone";
import * as _ from 'underscore';
import ProgressBarView from '../progress_bar'

class ServiceTableRow extends Backbone.View {

  initialize() {
    this.template = _.template(`
      <tr>
        <td class="row-name"><%= name %></td>
        <td><%= ip %></td>
        <td><%= ports %></td>
      </tr>
    `);
  }

  render() {
    let status = this.model.get('status');
    let spec = this.model.get('spec');
    let data = {
      name: this.model.get('metadata').name,
      ip: spec.clusterIP,
      ports: ''
    };
    _.each(spec.ports, (portSpec) => {
      data.ports += portSpec.port + '/' + portSpec.protocol + ' '
    });
    this.$el.html( this.template(data));
  }
}

export default ServiceTableRow;