import Backbone from "backbone";
import * as _ from 'underscore';

class Command extends Backbone.Model {

  defaults() {
    return {
      raw: '',
      tokens: []
    };
  }

}

export default Command;