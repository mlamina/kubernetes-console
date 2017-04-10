import Backbone from "backbone";
import * as _ from 'underscore';
import API from "../api"

class CommandExecution extends Backbone.Model {

  initialize() {
    this.api = new API();
    this.api.executeCommand(this.getCommand()).then(
      (response) => {
        if (response.errors)
          this.set('errors', response.errors);
        else
          this.set('result', response.data);
      },
      (error) => console.error(error)
    );
  }

  getCommand() {
    return this.get('command').get('raw');
  }


}

export default CommandExecution;