import Backbone from "backbone";
import * as _ from 'underscore';
import API from "../api"

class CommandExecution extends Backbone.Model {

  initialize() {
    this.api = new API();
    this.reload();
    if (this.getFilter() === 'watch') {
      this.startWatching();
    }
  }

  isWatching() {
    return this.interval !== undefined;
  }

  startWatching() {
    let self = this;
    this.interval = setInterval(() => {
      self.reload();
    }, 1000);
  }

  stopWatching() {
    if (this.interval) {
      clearInterval(this.interval);
      this.interval = undefined;
    }

  }

  reload() {
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

  getFilter() {
    let splitCommand = this.get('command').get('raw').split('|');
    if (splitCommand.length > 1)
      return splitCommand[1].trim().toLowerCase();
    return undefined;
  }

  getCommand() {
    let currentCommand = this.get('command').get('raw').split('|');
    return currentCommand[0].trim();
  }


}

export default CommandExecution;