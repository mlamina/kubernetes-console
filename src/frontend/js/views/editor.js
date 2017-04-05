import Backbone from "backbone";
import * as _ from 'underscore';

class EditorView extends Backbone.View {

  /**
   * EditorView constructor.
   *
   * @param {!API} api Backend API
   */
  constructor(api) {
    super();
    this.api = api;
  }

  initialize() {
    this.template = _.template(`
      <form>
        <span id="editor-header">
          $
        </span>
        <input type="text" id="editor" />
        <input type="submit" value="Go" hidden>
      </form>
    `);
    this.events = {
      'submit': 'submitCommand',
      'keyup #editor': 'parseCommand'
    };
  }

  parseCommand() {
    this.api.parseCommand(this.getCurrentCommand()).then(
      (response) => console.log(response),
      (error) => console.error(error)
    );
  }

  /**
   * Issues the currently entered command to the backend.
   *
   * @param {!UIEvent} event Submit event
   */
  submitCommand(event) {
    event.preventDefault();
    // TODO: Issue command
  }

  getCurrentCommand() {
    return this.$el.find('#editor').val();
  }

  render() {
    this.$el.html( this.template({}));
  }
}

export default EditorView;