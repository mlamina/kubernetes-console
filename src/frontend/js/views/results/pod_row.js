import Backbone from "backbone";
import * as _ from 'underscore';
import ProgressBarView from '../progress_bar'

class PodTableRow extends Backbone.View {

  initialize() {
    this.template = _.template(`
      <tr>
        <td class="row-name"><%= name %></td>
        <td class="row-status"><%= status %></td>
        <td class="row-progress"></td>
        <td class=""><%= ready %></td>
        <td class="row-restarts center"><%= restarts %></td>
      </tr>
    `);
  }

  render() {
    let data = {
      name: this.model.get('metadata').name,
      status: this.model.get('status').phase,
    };
    // Count ready containers
    let statuses = this.model.get('status').containerStatuses;
    let containersReady = _.countBy(statuses, (containerStatus) => {
      return containerStatus.ready ? 'ready': 'notready';
    }).ready || 0;
    data.ready = containersReady + '/' + statuses.length;
    // Sum up container restarts
    data.restarts = _.reduce(statuses, (num, status) => status.restartCount + num, 0);
    this.$el.html( this.template(data));

    let progressBar = new ProgressBarView({
      model: new Backbone.Model({
        maximum: statuses.length,
        current: containersReady
      })
    });

    progressBar.render();
    this.$('.row-progress').append(progressBar.$el.html());


    if (containersReady === 0)
      this.$('.row-progress').addClass('error');
    else if (containersReady < statuses.length)
      this.$('.row-progress').addClass('warning');
    if (data.restarts > 10)
      this.$('.row-restarts').addClass('error');
  }
}

export default PodTableRow;