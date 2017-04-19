import Backbone from "backbone";
import * as _ from 'underscore';

class ErrorResultView extends Backbone.View {

  initialize() {
    this.template = _.template(`<ul class="command-execution-errors"></ul>`);
  }

  render() {
    this.$el.html( this.template({}));
    let errorList = this.$('ul.command-execution-errors');
    _.each(this.model.get('errors'), (error) => {
      let errorElement = $('<li></li>');
      errorElement.text(error.message || error);
      errorList.append(errorElement);
    });
  }
}

export default ErrorResultView;