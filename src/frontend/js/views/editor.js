import Backbone from "backbone";
import * as _ from 'underscore';

class EditorView extends Backbone.View {

  initialize() {
    this.template = _.template(`
      <form id="editor-form" target="#">
        <span id="editor-header">
          $
        </span>
        <input type="text" id="editor" />
        <input type="submit" value="Go" hidden>
      </form>
    `);
    this.events = {
      'submit #editor-form': 'submit'
    };
  }

  submit(event) {
    event.preventDefault();
    console.log('command');
  }

  render() {
    this.$el.html( this.template({}));
  }
}

export default EditorView;