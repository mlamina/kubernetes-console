import Backbone from "backbone";
import * as _ from 'underscore';
import Command from "../models/command"
import AutocompleteView from "./autocomplete"

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
        <div id="editor-autocomplete"></div>
        <input type="text" id="editor" />
        <input type="submit" value="Go" hidden>
      </form>
    `);
    this.events = {
      'submit': 'submitCommand',
      'keyup #editor': 'updateModel',
      'keydown #editor': 'keyPressed'
    };
    this.model = new Command();
    this.listenTo(this.model, 'change:raw', this.parseCommand);
  }

  keyPressed(event) {
    let code = event.keyCode || event.which;
    if (code === 9) { // Tab
      this.triggerAutocomplete();
      event.preventDefault();
    }
  }

  triggerAutocomplete() {
    if (this.model.has('tokens')) {
      let newCommand = '';
      for (let token of this.model.get('tokens')) {
        if (token.known)
          newCommand += token.value + ' ';
        else {
          if (token.completions.length > 0) {
            // Only complete the portion that all completions have in common
            let completionIndex = 0;
            let sameValueAtIndex = true;
            let completionToAdd = '';
            while (sameValueAtIndex && completionIndex < token.completions[0].length) {
              let valueAtIndex = token.completions[0][completionIndex];

              _.each(token.completions, (completion) => {
                sameValueAtIndex = valueAtIndex === completion[completionIndex] && sameValueAtIndex;
              });
              if (sameValueAtIndex)
                completionToAdd += valueAtIndex;
              completionIndex += 1;
            }

            newCommand += completionToAdd;
            if (completionToAdd === token.completions[0])
              newCommand += ' ';
            break;
          }
        }
      }
      this.$el.find('#editor').val(newCommand);
    }
  }

  updateModel() {
    this.model.set({ raw: this.getCurrentCommand() });
  }

  parseCommand() {
    this.api.parseCommand(this.getCurrentCommand()).then(
      (response) => this.model.set({ tokens: response.data }),
      (error) => console.error(error)
    );
  }

  reset() {
    this.model = new Command();
    this.listenTo(this.model, 'change:raw', this.parseCommand);
    this.$el.find('#editor').val('');
    this.render();
  }

  /**
   * Issues the currently entered command to the backend.
   *
   * @param {!UIEvent} event Submit event
   */
  submitCommand(event) {
    event.preventDefault();
    this.trigger('submitCommand', this.model);
    this.reset();
  }

  getCurrentCommand() {
    return this.$el.find('#editor').val();
  }

  render() {
    this.$el.html( this.template({}));
    this.autocomplete = new AutocompleteView({ model: this.model });
    this.autocomplete.setElement(this.$('#editor-autocomplete'));
    this.autocomplete.render();
    this.$('#editor').focus();
    return this;
  }
}

export default EditorView;