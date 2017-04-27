import Backbone from "backbone";
import * as _ from 'underscore';
import ProgressBarView from '../progress_bar'
import K8ResourceList from "../../models/k8resource_list"
import TableResultView from "../results/table"

class DeploymentResultView extends Backbone.View {

  initialize() {
    this.template = _.template(`
      <div class="result-deployment">
        <div class="result-header">
          <span class="result-title"><%= deployment.status.replicas || pods.length %> / <%= deployment.spec.replicas %> Replicas</span>
          <span class="deployment-progress"></span>
        </div>
      </div>
    `);

    this.listenTo(this.model, 'change', this.render);
  }

  render() {
    this.$el.html( this.template(this.model.attributes));

    let progressBar = new ProgressBarView({
      model: new Backbone.Model({
        maximum: this.model.get('deployment').spec.replicas,
        current: this.model.get('deployment').status.replicas || this.model.get('pods').length
      })
    });
    progressBar.render();
    this.$('.deployment-progress').append(progressBar.$el.html());

    // Render pods in table
    let pods = new TableResultView({
      model: new K8ResourceList({ type: 'Pod', items: this.model.get('pods') })
    });
    pods.render();
    this.$('.result-deployment').append(pods.$el);
  }
}

export default DeploymentResultView;