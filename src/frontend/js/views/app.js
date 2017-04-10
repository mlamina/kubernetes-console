import Backbone from "backbone";
import * as _ from 'underscore';
import $ from "jquery";
import EditorView from './editor';
import WorkspaceView from './workspace';
import CommandExecution from '../models/command_execution';

class AppView extends Backbone.View {

  constructor(api) {
    super();
    this.api = api;
  }

  initialize() {
    this.setElement($('#k8console'), true);
    this.template = _.template(`
      <div id="workspace"></div>
      <form id="editor-form"></form>
    `);
  }

  render() {
    this.$el.html( this.template({}));
    this.workspace = new WorkspaceView();
    this.workspace.setElement(this.$('#workspace'), true);
    this.workspace.render();
    this.editor = new EditorView(this.api);
    this.editor.setElement(this.$('#editor-form'), true);
    this.editor.render();
    this.editor.parseCommand();
    this.editor.on('submitCommand', (command) => {
      let commandExecution = new CommandExecution({ command: command });
      this.workspace.addCommandExecution(commandExecution);
    });

    return this;
  }
}

export default AppView;