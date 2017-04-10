import * as $ from 'jquery';

const DefaultEndpoint = 'http://localhost:8082/api';

class API {

  constructor(endpoint) {
    this.endpoint = endpoint || DefaultEndpoint;
  }

  parseCommand(command) {
    return this.sendPostRequest('/commands/parse', {
      command: command.trim()
    });
  }

  executeCommand(command) {
    return this.sendPostRequest('/commands/execute', {
      command: command.trim()
    });
  }

  sendPostRequest(path, data) {
    return $.ajax({
      url: this.endpoint + path,
      dataType: 'json',
      type: 'post',
      contentType: 'application/json',
      data: JSON.stringify( data ),
      processData: false
    });
  }

}

export default API;