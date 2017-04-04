import $ from "jquery";

class K8Console {

  constructor(element) {
    this.element = $(element);
    this.element.val('command');
  }

  currentCommand() {
    return this.element.val();
  }

  focus() {
    this.element.focus();
  }

}

export default K8Console