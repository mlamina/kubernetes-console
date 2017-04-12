import Backbone from "backbone";
import * as _ from 'underscore';
import ProgressBarView from '../progress_bar'

class PodTableRow extends Backbone.View {

  initialize() {
    this.template = _.template(`
      <tr>
        <td class="row-name"><%= name %></td>
        <td class="row-progress"></td>
        <td class="row-deployment-replicas"><%= replicas %></td>
      </tr>
    `);
  }

  render() {
    let status = this.model.get('status');
    let spec = this.model.get('spec');
    let replicaStatus = status.replicas + '/' + spec.replicas;
    let data = {
      name: this.model.get('metadata').name,
      replicas: replicaStatus
    };
    this.$el.html( this.template(data));

    let progressBar = new ProgressBarView({
      model: new Backbone.Model({
        maximum: spec.replicas,
        current: status.replicas
      })
    });

    progressBar.render();
    this.$('.row-progress').append(progressBar.$el.html());
  }
}

export default PodTableRow;