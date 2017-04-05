import * as $ from 'jquery';

const DefaultEndpoint = 'http://localhost:8082/api';

class API {

  constructor(endpoint) {
    this.endpoint = endpoint || DefaultEndpoint;
  }

  parseCommand(command) {

    var request = {
      command: command
    };

    return $.ajax({
      url: this.endpoint + '/commands/parse',
      dataType: 'json',
      type: 'post',
      contentType: 'application/json',
      data: JSON.stringify( request ),
      processData: false
    });
  }

}

export default API;