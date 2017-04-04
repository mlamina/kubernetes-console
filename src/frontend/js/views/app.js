import Backbone from "backbone";
import * as _ from 'underscore';
import $ from "jquery";
import EditorView from './editor';
import WorkspaceView from './workspace';

class AppView extends Backbone.View {

  initialize() {
    this.setElement($('#k8console'), true);
    this.template = _.template(`
      <div id="workspace"></div>
      <form id="editor-form"></form>
    `);
  }

  render() {
    this.$el.html( this.template({}));
    this.editor = new EditorView({ el: this.$('#editor-form') });
    this.editor.render();
    this.workspace = new WorkspaceView({ el: this.$('#workspace') });
    this.workspace.render();
    return this;
  }
}

export default AppView;