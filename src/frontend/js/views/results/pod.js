import Backbone from "backbone";
import * as _ from 'underscore';
import $ from 'jquery';

class PodResultView extends Backbone.View {

  initialize() {
    this.template = _.template(`
      <div class="result-pod">
        <div class="result-header">
          <span class="result-title"><%= status.phase %></span>
          <span class="ip-address"><%= status.podIP %></span>
        </div>
        <table class="pod-containers result-table">
          <thead>
            <tr>
              <th>Container</th>
              <th>Image</th>
              <th>Restarts</th>
            </tr>
          </thead>
          <tbody>
            <% _.each(status.containerStatuses, function(container){ %>
            <tr>
               <% if (container.ready) { %>
               <td class="resource-status resource-status-green"><%= container.name %></td>
               <%} else { %>
               <td class="resource-status resource-status-red"><%= container.name %></td>
               <%} %>
               <td><%= container.image %></td>
               <td class="center"><%= container.restartCount %></td>
            </tr>
            <%}); %>
          </tbody>
        </table>
      </div>
    `);

    this.listenTo(this.model, 'change', this.render);
  }

  render() {
    this.$el.html( this.template(this.model.attributes));
    let status = this.model.get('status');
    if (status.phase === 'Failed') {
      this.$('table.result-table').hide();
      let error = $('<span></span>').addClass('error').text(status.message);
      this.$('.result-pod').append(error);
    }
  }
}

export default PodResultView;