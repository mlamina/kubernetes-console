import Backbone from "backbone";
import * as _ from 'underscore';
import CommandExecutionView from './command_execution'
import CommandExecution from '../models/command_execution'

class CommandExecutions extends Backbone.Collection {
  initialize() {
    this.model = CommandExecution;
  }
}

class WorkspaceView extends Backbone.View {


  initialize() {
    this.template = _.template(`
      <ul id="command-executions"></ul>
    `);
    this.model = new CommandExecutions();
    this.listenTo(this.model, 'change', this.render);
  }

  addCommandExecution(commandExecution) {
    this.model.add(commandExecution);
    this.render();
  }

  render() {
    this.$el.html(this.template({}));
    let list = this.$('#command-executions');
    let height = 0;
    this.model.each((execution) => {
      let view = new CommandExecutionView({ model: execution });
      view.render();
      list.append(view.$el.html());
      height += this.$el.height();
    });
    this.$el.scrollTop(height);
  }
}

export default WorkspaceView;