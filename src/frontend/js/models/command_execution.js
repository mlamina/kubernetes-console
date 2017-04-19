import Backbone from "backbone";
import * as _ from 'underscore';
import API from "../api"

class CommandExecution extends Backbone.Model {

  initialize() {
    this.api = new API();
    let self = this;
    this.api.executeCommand(this.getCommand()).then(
      (response) => {
        if (response.errors)
          self.set('errors', response.errors);
        else
          self.set('result', response);
      },
      (error) => {
        self.set('errors', [error.responseText])
      }
    );
  }

  getCommand() {
    return this.get('command').get('raw');
  }


}

export default CommandExecution;