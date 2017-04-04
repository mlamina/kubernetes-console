// import * as $ from 'jQuery';

class K8Console {

  constructor(element) {
    this.element = $(element);
  }

  currentCommand() {
    return this.element.val();
  }

}

export default K8Console