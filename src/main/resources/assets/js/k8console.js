// import * as $ from 'jQuery';

export default class K8Console {

  constructor(element) {
    this.element = $(element);
  }

  currentCommand() {
    return this.element.val();
  }

}