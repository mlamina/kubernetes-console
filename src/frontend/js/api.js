import * as $ from 'jquery';
import * as Config from './config';

class API {

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
      url: Config.backendUrl + path,
      dataType: 'json',
      type: 'post',
      contentType: 'application/json',
      data: JSON.stringify( data ),
      processData: false
    });
  }

}

export default API;