import Backbone from "backbone";
import * as _ from 'underscore';
import $ from "jquery";
import EditorView from './editor';
import WorkspaceView from './workspace';

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
    this.editor = new EditorView(this.api);
    this.editor.setElement(this.$('#editor-form'));
    this.editor.render();
    this.editor.parseCommand();
    this.workspace = new WorkspaceView();
    this.workspace.setElement(this.$('#workspace'));
    this.workspace.render();
    return this;
  }
}

export default AppView;