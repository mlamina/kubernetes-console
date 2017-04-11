import Backbone from "backbone";
import * as _ from 'underscore';

class PodTableRow extends Backbone.View {

  initialize() {
    this.template = _.template(`
      <tr>
        <td class="row-name"><%= name %></td>
        
      </tr>
    `);
  }

  render() {
    let data = {
      name: this.model.get('metadata').name
    };
    this.$el.html( this.template(data));
  }
}

export default PodTableRow;