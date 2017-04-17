import Backbone from "backbone";
import {LocalStorage} from 'backbone.localstorage';
import Command from './command';

class CommandHistory extends Backbone.Collection {

  initialize() {
    this.model = Command;
    this.localStorage = new LocalStorage("CommandHistory");
  }

  defaults() {
    return {
      commands: []
    };
  }

}

export default CommandHistory;