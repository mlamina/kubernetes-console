import Backbone from "backbone";
import * as _ from 'underscore';

class CommandView extends Backbone.View {

  initialize() {
    this.template = _.template(`
      <div>
        <% _.each(this.model.get('tokens'), function(token){ %>
        <div class="token-wrapper">
          <% _.each(token.completions, function(completion, index, list){ %>
          <span class="token-completion"><%= completion %></span>
          <% if (index < list.length - 1 || index === list.length - 1 && token.value !== "") { %> <br> <% }; %> 
          <% }); %>
          <span class="token"><%= token.value %></span>
        </div>
        
        <% }); %>
      </div>
    `);

    this.listenTo(this.model, 'change:tokens', this.render);
  }

  render() {
    this.$el.html( this.template({}));
  }
}

export default CommandView;