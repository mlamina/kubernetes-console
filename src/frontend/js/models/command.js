import Backbone from "backbone";
import {LocalStorage} from 'backbone.localstorage';

class Command extends Backbone.Model {

  initialize() {
    // this.localStorage = new LocalStorage("Command");
  }

  defaults() {
    return {
      raw: '',
      tokens: []
    };
  }

}

export default Command;